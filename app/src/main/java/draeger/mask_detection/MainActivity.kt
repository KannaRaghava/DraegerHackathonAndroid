package draeger.mask_detection

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.request.SimpleMultiPartRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    companion object {
        const val CAMERA_PERMISSION_CODE = 42
        const val REQUEST_CAPTURE_IMAGE = 100
        const val URL = "http://35.234.103.97:8888/"
    }

     // View elements
    lateinit var scanButton: Button
    lateinit var takenImageView: ImageView
    lateinit var maskResult: TextView

    // Image path for the taken image
    var imageFilePath: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        scanButton = findViewById(R.id.scanBtn)
        takenImageView = findViewById(R.id.takenImageView)
        takenImageView.visibility = View.INVISIBLE
        maskResult = findViewById(R.id.maskResult)

        scanButton.setOnClickListener {
            openCameraIntent()
        }

        requestPermissions()
    }

    private fun requestPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.CAMERA)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.CAMERA,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        CAMERA_PERMISSION_CODE)
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(Date())
        val imageFileName = "IMG_" + timeStamp + "_"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
                imageFileName, /* prefix */
                ".jpg", /* suffix */
                storageDir      /* directory */
        )

        imageFilePath = image.absolutePath
        return image
    }

    private fun openCameraIntent() {
        maskResult.text = ""
        val pictureIntent = Intent(
                MediaStore.ACTION_IMAGE_CAPTURE
        )
        if (pictureIntent.resolveActivity(packageManager) != null) {
            var file: File? = null
            try {
                file = createImageFile()
            } catch (ex: IOException) {
                Toast.makeText(applicationContext, "Unable to create file!", Toast.LENGTH_LONG).show()
            }

            if (file != null) {
                val photoUri: Uri = FileProvider.getUriForFile(this,
                        "draeger.mask_detection.provider", file
                )
                pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                startActivityForResult(pictureIntent,
                        REQUEST_CAPTURE_IMAGE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int,
                                  data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            takenImageView.visibility = View.VISIBLE
            Glide.with(this).load(imageFilePath).into(takenImageView)
            uploadImage(imageFilePath!!)
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Toast.makeText(applicationContext, "No picture taken!", Toast.LENGTH_LONG).show()
        }
    }

    private fun uploadImage(imagePath: String) {
        val smr = SimpleMultiPartRequest(Request.Method.POST, URL, Response.Listener<String> {
            maskResult.text = it
        },
        Response.ErrorListener {
            Toast.makeText(applicationContext, "Internal Server Error", Toast.LENGTH_LONG).show()
        })
        smr.addFile("file", imagePath)

        val requestQueue = Volley.newRequestQueue(applicationContext)
        requestQueue.add(smr)
    }
}
