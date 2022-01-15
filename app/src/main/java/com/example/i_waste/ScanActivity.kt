package com.example.i_waste

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.i_waste.databinding.ActivityScanBinding
import com.example.i_waste.ml.MobilenetV110224Quant
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer

private const val REQUEST_CODE = 42
class ScanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScanBinding

    lateinit var btnCamera:Button
    lateinit var btnScan:Button
    lateinit var btnGallery:Button

    lateinit var bitmap : Bitmap
    lateinit var imgview:ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val btnCamera: Button = findViewById(R.id.btnCamera)
        btnCamera.setOnClickListener{
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

            if(takePictureIntent.resolveActivity(this.packageManager) !=null){
                startActivityForResult(takePictureIntent, REQUEST_CODE)
            }else{
                Toast.makeText(this, "Unable to open Camera", Toast.LENGTH_SHORT).show()
            }

        }

        imgview = findViewById(R.id.imgView)
        val fileName = "label.txt"
        val inputString = application.assets.open(fileName).bufferedReader().use { it.readText() }
        var townList = inputString.split("\n")

        var gclassif: TextView = findViewById(R.id.gclassif)

        btnGallery = findViewById(R.id.btnGallery)
        btnGallery.setOnClickListener(View.OnClickListener{


            var intent:Intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"

            startActivityForResult(intent, 100)
        })

        btnScan = findViewById(R.id.btnScan)
        btnScan.setOnClickListener(View.OnClickListener {

            var resized: Bitmap = Bitmap.createScaledBitmap(bitmap, 224, 224, true)
            val model = MobilenetV110224Quant.newInstance(this)

            // Creates inputs for reference.
            val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 224, 224, 3), DataType.UINT8)

            var tbuffer = TensorImage.fromBitmap(resized)
            //var bytebuffer = tbuffer.buffer

            val byteBuffer = tbuffer.buffer
            inputFeature0.loadBuffer(byteBuffer)

            // Runs model inference and gets result.
            val outputs = model.process(inputFeature0)
            val outputFeature0 = outputs.outputFeature0AsTensorBuffer

            var max = getMax(outputFeature0.floatArray)

            gclassif.text = townList[max]

            // Releases model resources if no longer used.
            model.close()

        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK){
            val takenImage = data?.extras?.get("data") as Bitmap
            imgview.setImageBitmap(takenImage)
        }else{
            super.onActivityResult(requestCode, resultCode, data)

            imgview.setImageURI(data?.data)

            var uri: Uri?= data?.data

            bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
        }

    }

    private fun getMax(arr:FloatArray) : Int{

        var ind = 0
        var min = 0.0f

        for(i in 0..1000)
        {
            if(arr[i]>min)
            {
                ind = i
                min = arr[i]
            }
        }

        return ind
    }
}
