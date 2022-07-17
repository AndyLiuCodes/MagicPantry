package com.ala158.magicpantry.ui.receiptscanner

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ala158.magicpantry.R
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.text.method.ScrollingMovementMethod
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions


class ReceiptScannerActivity : AppCompatActivity() {
    private var checkIfPicTaken = 0

    private lateinit var imageUri: Uri
    private lateinit var bitmap : Bitmap

    private val requestCamera = 1888
    private val requestGallery = 2222
    private var imageView: ImageView? = null
    private lateinit var textView : TextView
    private lateinit var cameraBtn: Button
    private lateinit var scanBtn: Button
    private lateinit var reviewItemsBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receipt_scanner)

        imageView = findViewById(R.id.imageview_receipt)
        textView = findViewById(R.id.textview_receipt)
        cameraBtn = findViewById(R.id.cameraButton)
        scanBtn = findViewById(R.id.scanButton)
        reviewItemsBtn = findViewById(R.id.btn_review_scanner_items)

        cameraBtn.setOnClickListener {
            val choices = arrayOf("Open Camera", "Select from Gallery")

            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder.setTitle("Pick Profile Picture")
                .setCancelable(true)

                // set 2 choices, open camera or open gallery
                .setItems(choices)
                { _: DialogInterface, myId: Int ->
                    if (myId == 0) {
                        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

                        // store image once it is taken. includes a file name and date/time taken
                        val values = ContentValues()
                        values.put(MediaStore.Images.Media.TITLE, "MyPicture")
                        values.put(
                            MediaStore.Images.Media.DESCRIPTION,
                            "Photo taken on " + System.currentTimeMillis()
                        )
                        imageUri = this.contentResolver.insert(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)!!

                        // open camera and add image to photo gallery if one is taken
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                        startActivityForResult(cameraIntent, requestCamera)
                    } else {
                        // open photo gallery to choose an image
                        val galleryIntent =
                            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        startActivityForResult(galleryIntent, requestGallery)
                    }
                }
            val alert: AlertDialog = builder.create()
            alert.show()
        }

        scanBtn.setOnClickListener {
            recognize()
        }

        reviewItemsBtn.setOnClickListener {

        }
    }

    // recognizing text
    private fun recognize() {
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        textView.text = "ok?"

        if (imageView != null) {
            // process image
            val image = InputImage.fromBitmap(bitmap, 0)
            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    // Task completed successfully
                    // ...
                    textView.text = "success"
                    success(visionText)
                }
                .addOnFailureListener {
                    // Task failed with an exception
                    // ...
                    textView.text = "failed to recognize text"
                }
        }
    }

    // successfully processed image
    private fun success(result : Text) {
        val resultText = result.text

        for (block in result.textBlocks) {
            val blockText = block.text
            val blockCornerPoints = block.cornerPoints
            val blockFrame = block.boundingBox
            textView.text = "${textView.text} \n block: $blockText \n"

            for (line in block.lines) {
                val lineText = line.text
                val lineCornerPoints = line.cornerPoints
                val lineFrame = line.boundingBox
                textView.text = "${textView.text} \n line: $lineText \n"

                for (element in line.elements) {
                    val elementText = element.text
                    val elementCornerPoints = element.cornerPoints
                    val elementFrame = element.boundingBox
                    textView.text = "${textView.text} \n element: $elementText \n"
                }
            }
        }
        textView.text = "${textView.text} \n result: $resultText"
        textView.movementMethod = ScrollingMovementMethod()
    }

    // when camera or gallery chosen, update photo
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("CommitPrefEdits")
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        checkIfPicTaken += 1

        // if camera selected, check request code
        if (requestCode == requestCamera && resultCode == Activity.RESULT_OK && data != null) {

            // get image uri and set imageBitmap to display it
            val myBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)
            imageView!!.setImageBitmap(myBitmap)

            bitmap = myBitmap
        }

        // if gallery selected, check request code
        else if (requestCode == requestGallery && resultCode == Activity.RESULT_OK && data != null) {

            // get image uri and set imageBitmap to display it
            val myData = data.data
            val myBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, myData)
            imageView!!.setImageBitmap(myBitmap)

            bitmap = myBitmap
        }
    }
}