package com.ala158.magicpantry.ui.dashboard

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.*
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.ala158.magicpantry.databinding.FragmentDashboardBinding
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.io.IOException
import java.io.InputStream


@Suppress("DEPRECATION")
class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null

    private lateinit var imageUri: Uri
    private var bitmap : Bitmap? = null

    private val requestCamera = 1888
    private val requestGallery = 2222
    private var imageView : ImageView? = null
    private lateinit var textView : TextView

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imageView = binding.imageviewReceipt
        textView = binding.textviewReceipt

        // button to select receipt image
        binding.cameraButton.setOnClickListener {
            val choices = arrayOf("Open Camera", "Select from Gallery")

            val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
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
                        imageUri = requireContext().contentResolver.insert(
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

        // scan text
        binding.scanButton.setOnClickListener {
            recognize()
        }
    }

    // recognizing text
    private fun recognize() {
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        textView.text = "ok?"

        if (bitmap != null) {
            // process image
            val image = InputImage.fromBitmap(bitmap!!, 0)
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
        val resultBlocks = mutableListOf<Text.TextBlock>()
        val helperProducts = mutableListOf<String>()
        val filteredProducts = mutableListOf<String>()
        val helperPrice = mutableListOf<String>()
        val filteredList = mutableListOf<String>()

        val mutableBitmap = bitmap!!.copy(bitmap!!.config, true)

        for (block in result.textBlocks) {
            val blockText = block.text
            val blockCornerPoints = block.cornerPoints
            val blockFrame = block.boundingBox
            // textView.text = "${textView.text} \n block: $blockText \n"

            resultBlocks.add(block)
            drawRect(mutableBitmap, blockFrame!!)

            for (line in block.lines) {
                val lineText = line.text
                val lineCornerPoints = line.cornerPoints
                val lineFrame = line.boundingBox
                // textView.text = "${textView.text} \n line: $lineText \n"

                for (element in line.elements) {
                    val elementText = element.text
                    val elementCornerPoints = element.cornerPoints
                    val elementFrame = element.boundingBox
                    // textView.text = "${textView.text} \n element: $elementText \n"
                }
            }
        }

        for (i in 0 until resultBlocks.size) {
            if (resultBlocks[i].text.contains(".")
                && !resultBlocks[i].text.lowercase().contains("phone")
                && !resultBlocks[i].text.contains("/")
                && !resultBlocks[i].text.lowercase().contains("sav")) {

                for (line in resultBlocks[i - 1].lines) {
                    helperProducts.add(line.text)
                }
                for (line in resultBlocks[i].lines) {
                    helperPrice.add(line.text)
                }
            }
        }

        if (helperProducts.size != helperPrice.size) {
            for (i in 0 until helperProducts.size) {
                Log.d("product", "${helperProducts.size}  =  ${helperProducts[i]}")
                Log.d("price", "${helperPrice.size}")

                if ((helperProducts[i] != helperProducts[i].uppercase())
                    && !(helperProducts[i].lowercase().contains("gluten free item"))
                    && !(helperProducts[i].contains("/") && helperProducts[i].contains(Regex("[^A-Za-z]")))
                ) {
                    filteredProducts.add(helperProducts[i])
                }

                /*if (helperProducts[i] == helperProducts[i].uppercase()) {
                        *//*if (helperProducts[i].lowercase().contains("subtotal")) {
                            Log.d("line", "continued")
                            continue
                        }
                        else {*//*
                            helperProducts.removeAt(i)
                            Log.d("line", "removed 1")
//                        }
                    }
                    else if (helperProducts[i].lowercase().contains("gluten free item")) {
                        helperProducts.removeAt(i)
                        Log.d("line", "removed 2")
                    }
                    else if (helperProducts[i].contains("/") && helperProducts[i].contains(Regex("[^A-Za-z]"))) {
                        helperProducts[i - 1] = helperProducts[i - 1] + " quantity: " + helperProducts[i]
                        Toast.makeText(requireContext(), helperProducts[i - 1], Toast.LENGTH_SHORT).show()
                        helperProducts.removeAt(i)
                        Log.d("line", "removed 4")
                    }
                    if (i + 1 == helperPrice.size) {
                        break
                    }*/
            }
        }
        for (item in 0 until filteredProducts.size) {
            Log.d("product size", "${filteredProducts.size}")
            Log.d("price size", "${helperPrice.size}")

            Toast.makeText(requireContext(), "${filteredProducts.size} and {helperPrice.size}", Toast.LENGTH_SHORT).show()

            filteredList.add(filteredProducts[item])
            filteredList.add(helperPrice[item])
        }

        for (i in 0 until filteredList.size) {
            if (filteredList[i].lowercase().contains("subtotal") || filteredList[i].lowercase() == "c")
                filteredList.subList(0, i)
        }

        textView.text = "${textView.text} \n list: $filteredList"

        textView.movementMethod = ScrollingMovementMethod()
        imageView!!.setImageBitmap(mutableBitmap)
    }

    // add rect around text blocks
    private fun drawRect(mutableBitmap : Bitmap, location : Rect) {
        val paint = Paint()
        paint.color = Color.RED
        paint.style = Paint.Style.STROKE
        with(Canvas(mutableBitmap)) {
            drawRect(location, paint)
        }
    }

    // when camera or gallery chosen, update photo
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("CommitPrefEdits")
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // if camera selected, check request code
        if (requestCode == requestCamera && resultCode == Activity.RESULT_OK) {

            // get image uri and set imageBitmap to display it
            var myBitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, imageUri)
            myBitmap = handleSamplingAndRotationBitmap(requireActivity(),imageUri)
            imageView!!.setImageBitmap(myBitmap)
            bitmap = myBitmap
        }

        // if gallery selected, check request code
        else if (requestCode == requestGallery && resultCode == Activity.RESULT_OK && data != null) {

            // get image uri and set imageBitmap to display it
            val myData = data.data
            val myBitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, myData)
            imageView!!.setImageBitmap(myBitmap)

            bitmap = myBitmap
        }
    }

    @Throws(IOException::class)
    fun handleSamplingAndRotationBitmap(context: Context, selectedImage: Uri?): Bitmap? {
        val MAX_HEIGHT = 1024
        val MAX_WIDTH = 1024

        // First decode with inJustDecodeBounds=true to check dimensions
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        var imageStream: InputStream? = selectedImage?.let {
            context.contentResolver.openInputStream(
                it
            )
        }
        BitmapFactory.decodeStream(imageStream, null, options)
        imageStream?.close()

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, MAX_WIDTH, MAX_HEIGHT)

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false
        imageStream = selectedImage?.let { context.contentResolver.openInputStream(it) }
        var img = BitmapFactory.decodeStream(imageStream, null, options)
        img = selectedImage?.let { img?.let { it1 -> rotateImageIfRequired(context, it1, it) } }
        return img
    }

    private fun calculateInSampleSize(
        options: BitmapFactory.Options,
        reqWidth: Int, reqHeight: Int
    ): Int {
        // Raw height and width of image
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and width
            val heightRatio = Math.round(height.toFloat() / reqHeight.toFloat())
            val widthRatio = Math.round(width.toFloat() / reqWidth.toFloat())

            // Choose the smallest ratio as inSampleSize value, this will guarantee a final image
            // with both dimensions larger than or equal to the requested height and width.
            inSampleSize = if (heightRatio < widthRatio) heightRatio else widthRatio

            // This offers some additional logic in case the image has a strange
            // aspect ratio.
            val totalPixels = (width * height).toFloat()

            // Anything more than 2x the requested pixels we'll sample down further
            val totalReqPixelsCap = (reqWidth * reqHeight * 2).toFloat()
            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize++
            }
        }
        return inSampleSize
    }

    @Throws(IOException::class)
    private fun rotateImageIfRequired(context: Context, img: Bitmap, selectedImage: Uri): Bitmap? {
        val input = context.contentResolver.openInputStream(selectedImage)
        val ei: ExifInterface = if (Build.VERSION.SDK_INT > 23) input?.let { ExifInterface(it) }!! else selectedImage.path?.let { ExifInterface(it) }!!
        return when (ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(img, 90)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(img, 180)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(img, 270)
            else -> img
        }
    }

    private fun rotateImage(img: Bitmap, degree: Int): Bitmap? {
        val matrix = Matrix()
        matrix.postRotate(degree.toFloat())
        val rotatedImg = Bitmap.createBitmap(img, 0, 0, img.width, img.height, matrix, true)
        img.recycle()
        return rotatedImg
    }

    // on destroy remove binding
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}