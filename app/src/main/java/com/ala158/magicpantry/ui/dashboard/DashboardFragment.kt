package com.ala158.magicpantry.ui.dashboard

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.content.DialogInterface
import android.content.Intent
import android.graphics.*
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

                    if (helperProducts[i] == helperProducts[i].uppercase()) {
                        /*if (helperProducts[i].lowercase().contains("subtotal")) {
                            Log.d("line", "continued")
                            continue
                        }
                        else {*/
                            helperProducts.removeAt(i)
                            Log.d("line", "removed 1")
//                        }
                    }
                    else if (helperProducts[i].lowercase().contains("gluten free item")) {
                        helperProducts.removeAt(i)
                        Log.d("line", "removed 2")
                    }
                    else if (helperProducts[i].uppercase() == "C") {
                        helperProducts.removeAt(i)
                        Log.d("line", "removed 3")
                    }
                    else if (helperProducts[i].contains("/") && helperProducts[i].contains(Regex("[^A-Za-z]"))) {
                        //helperProducts[i][line - 1].text.plus(helperProducts[i][line])
                        //Toast.makeText(requireContext(), helperProducts[i], Toast.LENGTH_SHORT).show()

                        helperProducts[i - 1] = helperProducts[i - 1] + " quantity: " + helperProducts[i]
                        Toast.makeText(requireContext(), helperProducts[i - 1], Toast.LENGTH_SHORT).show()
                        helperProducts.removeAt(i)
                        Log.d("line", "removed 4")
                    }
                    if (i + 1 == helperPrice.size) {
                        break
                    }
                }
            }
            for (item in 0 until helperProducts.size) {
                Log.d("product line", "${helperProducts.size}")
                Log.d("price line", "${helperPrice.size}")

                filteredList.add(helperProducts[item])
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
            val myBitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, imageUri)
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

    // on destroy remove binding
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}