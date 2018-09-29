package draeger.mask_detection

import android.Manifest
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.widget.Button

class MainActivity : AppCompatActivity() {

    companion object {
        const val CAMERA_PERMISSION_CODE = 45738
    }

    lateinit var scanButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.READ_CONTACTS)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_CODE)
            }
        }

        scanButton = findViewById(R.id.scanBtn)

        scanButton.setOnClickListener(ScanListener())
    }
}
