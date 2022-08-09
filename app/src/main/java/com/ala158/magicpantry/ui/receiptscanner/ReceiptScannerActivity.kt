package com.ala158.magicpantry.ui.receiptscanner

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.*
import android.graphics.*
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.ala158.magicpantry.R
import com.ala158.magicpantry.Util
import com.ala158.magicpantry.ui.reviewingredients.ReviewIngredientsActivity
import com.ala158.magicpantry.ui.reviewingredients.ReviewIngredientsViewModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.io.File

//scan receipt if successful display results in ReviewIngredientsActivity
// used google mlkit
//  https://developers.google.com/ml-kit/vision/text-recognition
@Suppress("DEPRECATION")
class ReceiptScannerActivity : AppCompatActivity() {
    private lateinit var imageUri: Uri
    private var bitmap: Bitmap? = null

    private val tag = "MagicPantry"
    private var filePath = ""

    private val requestCamera = 1888
    private val requestGallery = 2222

    private lateinit var sharedPrefFile: SharedPreferences
    private lateinit var edit: SharedPreferences.Editor

    private var imageView: ImageView? = null
    private lateinit var textView: TextView

    private val helperPrice = mutableListOf<String>()
    private val filteredProducts = mutableListOf<String>()

    private lateinit var reviewIngredientsViewModel: ReviewIngredientsViewModel

    private lateinit var cameraBtn: Button
    private lateinit var scanBtn: Button

    private var imageToScan: File? = null

    private lateinit var receiptScannerViewModel: ReceiptScannerViewModel
    private var isCameraChosen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receipt_scanner)

        //set up shared pref
        sharedPrefFile = getSharedPreferences("MySharedPrefs", Context.MODE_PRIVATE)
        edit = sharedPrefFile.edit()

        reviewIngredientsViewModel = Util.createViewModel(
            this,
            ReviewIngredientsViewModel::class.java,
            Util.DataType.INGREDIENT
        )

        imageView = findViewById(R.id.imageview_receipt)
        textView = findViewById(R.id.textview_receipt)
        cameraBtn = findViewById(R.id.cameraButton)
        scanBtn = findViewById(R.id.scanButton)

        receiptScannerViewModel = ViewModelProvider(this)[ReceiptScannerViewModel::class.java]
        receiptScannerViewModel.userImage.observe(this) {
            imageView!!.setImageBitmap(it)
            bitmap = it
        }

        cameraBtn.setOnClickListener {
            val choices = arrayOf("Open Camera", "Select from Gallery")

            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
                .setCancelable(true)

                // set 2 choices, open camera or open gallery
                .setItems(choices)
                { _: DialogInterface, myId: Int ->
                    if (myId == 0) {
                        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

                        // store image once it is taken. includes a file name and date/time taken
                        val values = ContentValues()

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            val file =
                                File(
                                    Environment.getExternalStorageDirectory().toString() + "/$tag/"
                                )
                            if (!file.exists()) {
                                file.mkdirs()
                            }
                            values.put(MediaStore.Images.Media.TITLE, "Receipt.jpg")
                            values.put(MediaStore.Images.Media.DATA, "Receipt")
                            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                            values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/$tag/")
                            values.put(
                                MediaStore.Images.Media.DESCRIPTION,
                                "Photo taken on " + System.currentTimeMillis()
                            )
                        } else {
                            values.put(MediaStore.Images.Media.TITLE, "Receipt")
                            values.put(
                                MediaStore.Images.Media.DESCRIPTION,
                                "Photo taken on " + System.currentTimeMillis()
                            )
                        }
                        imageUri = contentResolver.insert(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values
                        )!!

                        // open camera and add image to photo gallery if one is taken
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                        isCameraChosen = true
                        startActivityForResult(cameraIntent, requestCamera)
                    } else {
                        // open photo gallery to choose an image
                        val galleryIntent =
                            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        isCameraChosen = false
                        startActivityForResult(galleryIntent, requestGallery)
                    }
                }
            val alert: AlertDialog = builder.create()
            alert.show()
        }

        // on click scan image for text
        scanBtn.setOnClickListener {
            recognize()
        }

    }

    // recognizing text
    private fun recognize() {
        if (imageView != null && bitmap != null) {
            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
            textView.text = "Processing . ."

            // process image
            val image = InputImage.fromBitmap(bitmap!!, 0)
            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    // Task completed successfully
                    textView.text = "Success"
                    success(visionText)
                }
                .addOnFailureListener {
                    // Task failed with an exception
                    textView.text = "Failed to recognize text"
                }
        } else if (imageView == null || bitmap == null) {
            Toast.makeText(
                this, "You must first select/capture an image before scanning.", Toast.LENGTH_SHORT
            ).show()
        }
    }

    // successfully processed image
    private fun success(result: Text) {
        val resultBlocks = mutableListOf<Text.TextBlock>()
        val helperProducts = mutableListOf<String>()
        val filteredList = mutableListOf<String>()

        val mutableBitmap = bitmap!!.copy(bitmap!!.config, true)

        // for blocks in result text
        for (block in result.textBlocks) {
            val blockFrame = block.boundingBox

            // add all blocks into a list
            resultBlocks.add(block)
            drawRect(mutableBitmap, blockFrame!!)
        }

        // add lines from block with prices and products to respective lists
        for (i in 0 until resultBlocks.size) {
            if (resultBlocks[i].text.contains(".")
                && resultBlocks[i].text.any { it.isDigit() }
                && !resultBlocks[i].text.lowercase().contains("phone")
                && !resultBlocks[i].text.contains("/")
                && !resultBlocks[i].text.lowercase().contains("sav")
            ) {
                if (resultBlocks[i].lines[0].text.count { it == '.' } == 1) {
                    if (i > 0 && resultBlocks[i - 1].text.count { it.isLetter() } > 1
                    ) {
                        if (!resultBlocks[i - 1].text.lowercase().contains("miles")
                            && !resultBlocks[i - 1].text.lowercase().contains("total")
                            && !resultBlocks[i - 1].text.lowercase().contains("debit")
                            && !resultBlocks[i - 1].text.lowercase().contains("cash")
                            && !resultBlocks[i - 1].text.lowercase().contains("visa")) {
                            for (line in resultBlocks[i - 1].lines) {
                                helperProducts.add(line.text)
                            }
                        }
                    }
                    for (line in resultBlocks[i].lines) {
                        if (!line.text.contains("-") && !line.text.all { it.isLetter() }) {
                            helperPrice.add(line.text)
                        }
                    }
                }
            }
        }

        // filter through product block lines to get product name and
        //  get amount of product bought if says
        for (i in 0 until helperProducts.size) {
            if ((helperProducts[i].lowercase().contains("sav"))
                && !(helperProducts[i].lowercase().contains("saving"))
                && helperProducts[i].contains("$") && helperProducts[i].contains("/")
                && helperProducts[i].any { it.isDigit() }
            ) {
                filteredProducts[filteredProducts.size - 1] =
                    filteredProducts[filteredProducts.size - 1] + ";;" + helperProducts[i]
            } else if ((helperProducts[i] != helperProducts[i].uppercase())
                && !(helperProducts[i].lowercase().contains("gluten free ltem"))
                && !(helperProducts[i].lowercase().contains("gluten free item"))
                && !(helperProducts[i].lowercase().contains("saving"))
                && !(helperProducts[i].lowercase().contains("$"))
            ) {
                filteredProducts.add(helperProducts[i])
            }
        }

        // add the two lists to make one
        for (item in 0 until filteredProducts.size) {
            filteredList.add(filteredProducts[item])
            filteredList.add(helperPrice[item])
        }
        textView.movementMethod = ScrollingMovementMethod()
        imageView!!.setImageBitmap(mutableBitmap)

        val bundle = Bundle()
        bundle.putStringArray("arrayList", filteredProducts.toTypedArray())
        bundle.putStringArray("priceList", helperPrice.toTypedArray())
        val intent = Intent(this, ReviewIngredientsActivity::class.java)
        intent.putExtras(bundle)
        // Close this activity and start the new one (review ingredients activity)
        // https://stackoverflow.com/questions/11308198/start-new-activity-and-finish-current-one-in-android
        finish()
        startActivity(intent)
    }

    // add rect around text blocks
    private fun drawRect(mutableBitmap: Bitmap, location: Rect) {
        val paint = Paint()
        paint.color = Color.RED
        paint.style = Paint.Style.STROKE
        with(Canvas(mutableBitmap)) {
            drawRect(location, paint)
        }
    }

    // when camera or gallery chosen, update photo
    @Deprecated("Deprecated in Java")
    @RequiresApi(Build.VERSION_CODES.Q)
    @SuppressLint("CommitPrefEdits")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // if camera selected, check request code
        if (requestCode == requestCamera && resultCode == Activity.RESULT_OK) {
            deleteImageFromGallery(filePath)

            // get image uri, convert it to bitmap and rotate if necessary, then
            // set imageBitmap to display it
            // Help from https://developer.android.com/training/data-storage/shared/media
            var imagePath = ""
            val projections = arrayOf(MediaStore.Images.Media.DATA)
            this.contentResolver.query(
                imageUri,
                projections,
                null,
                null,
                null
            )?.use { cursor ->
                val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                cursor.moveToFirst()
                imagePath = cursor.getString(columnIndex)
            }

            imageToScan = File(imagePath)
            val myBitmap = Util.getBitmap(this, imageUri, imageToScan!!)

            imageView!!.setImageBitmap(myBitmap)

            bitmap = myBitmap

            imageUri = Uri.parse(imagePath)
            filePath = imageUri.path!!
        }

        // if gallery selected, check request code
        else if (requestCode == requestGallery && resultCode == Activity.RESULT_OK && data != null) {
            deleteImageFromGallery(filePath)

            // get image uri and set imageBitmap to display it
            val myData = data.data

            // get image uri, convert it to bitmap and rotate if necessary, then
            // set imageBitmap to display it
            // Help from https://developer.android.com/training/data-storage/shared/media
            var imagePath = ""
            val projections = arrayOf(MediaStore.Images.Media.DATA)
            this.contentResolver.query(
                myData!!,
                projections,
                null,
                null,
                null
            )?.use { cursor ->
                val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)

                cursor.moveToFirst()
                imagePath = cursor.getString(columnIndex)
            }

            imageToScan = File(imagePath)
            val myBitmap = Util.getBitmap(this, myData, imageToScan!!)

            imageView!!.setImageBitmap(myBitmap)

            bitmap = myBitmap

            imageUri = Uri.parse(imagePath)
            filePath = imageUri.path!!
        }
        val imageScanned = if (bitmap != null) {
            imageUri.path.toString()
        } else {
            ""
        }
        edit.putString("scan_image", imageScanned).apply()

    }

    // Delete image once we are done with it
    fun deleteImageFromGallery(fPath: String) {
        val deleteFile = File(fPath)
        if (deleteFile.exists()) {
            if (deleteFile.delete()) {
                Log.d("RECEIPT_SCANNER", "deleteImageFromGallery: File deleted: $fPath ")
            } else {
                Log.d("RECEIPT_SCANNER", "deleteImageFromGallery: File not deleted: $fPath ")
            }
        }
    }

    //reload image on activity resumed
    override fun onResume() {
        super.onResume()

        if (sharedPrefFile.contains("scan_image")) {
            val savedUri = sharedPrefFile.getString("scan_image", "")
            if (savedUri == "") {
                imageView!!.setImageResource(R.drawable.magic_pantry_app_logo)
            } else {
                imageView!!.setImageURI(Uri.parse(savedUri))
            }
        }
    }

    //delete image if back pressed
    override fun onBackPressed() {
        super.onBackPressed()

        edit.remove("scan_image").apply()
        deleteImageFromGallery(filePath)
    }

    //delete image if activity destroyed
    override fun onDestroy() {
        super.onDestroy()

        edit.remove("scan_image").apply()
        deleteImageFromGallery(filePath)
    }
}