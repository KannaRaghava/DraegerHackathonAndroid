package draeger.mask_detection

import android.view.View
import android.widget.Toast

class ScanListener : View.OnClickListener {

    override fun onClick(v: View) {
        // Open camera
        Toast.makeText(v.context.applicationContext, "Test", Toast.LENGTH_LONG).show()

    }
}