package com.udemycourses.kidsdrawingapp

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.get

class MainActivity : AppCompatActivity() {

    private var drawingView: DrawingView? = null
    private var mImageButtonCurrentPaint: ImageButton? = null

    // constant to use in order to request camera permission
    private val cameraResultLauncher : ActivityResultLauncher<String> =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()){
                isGranted ->
                if(isGranted){
                    Toast.makeText(this,"Permission granted for camera.", Toast.LENGTH_LONG).show()
                }else {
                    Toast.makeText(this,"Permission denied for camera.", Toast.LENGTH_LONG).show()
                }
            }

    private val cameraAndLocationResultLauncher : ActivityResultLauncher<Array<String>> =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()){
             permissions ->
            permissions.entries.forEach{
                val permissionName = it.key
                val isGranted = it.value
                if(isGranted){
                    if(permissionName == Manifest.permission.ACCESS_FINE_LOCATION){
                        Toast.makeText(this, "Permission granted for FINE Location", Toast.LENGTH_SHORT).show()

                    }else if (permissionName == Manifest.permission.ACCESS_COARSE_LOCATION){
                        Toast.makeText(this, "Permission granted COARSE Location", Toast.LENGTH_SHORT).show()
                    }else {
                        Toast.makeText(this, "Permission granted for Camera", Toast.LENGTH_SHORT).show()
                    }
                }else{
                    if(permissionName == Manifest.permission.ACCESS_FINE_LOCATION){
                        Toast.makeText(this, "Permission denied for Location", Toast.LENGTH_SHORT).show()

                    }else if (permissionName == Manifest.permission.ACCESS_COARSE_LOCATION){
                        Toast.makeText(this, "Permission granted COARSE Location", Toast.LENGTH_SHORT).show()
                    }
                    else{
                        Toast.makeText(this, "Permission denied for Camera", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Request for camera permission
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M &&
                shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)){
            showRationaleDialog("Kids Drawing App requires camera access",
                "Camera cannot be used because Camera access is denied")
        }else {
            cameraAndLocationResultLauncher.launch(
                arrayOf(Manifest.permission.CAMERA,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
            )
        }

        drawingView = findViewById(R.id.drawing_view)
        drawingView?.setSizeForBrush(20.toFloat())

        val linearLayoutPaintColors = findViewById<LinearLayout>(R.id.ll_paint_colors)
        mImageButtonCurrentPaint = linearLayoutPaintColors[1] as ImageButton
        mImageButtonCurrentPaint!!.setImageDrawable(
            ContextCompat.getDrawable(this,R.drawable.pallet_pressed)
        )

        val ib_brush : ImageButton = findViewById(R.id.ib_brush)
        ib_brush.setOnClickListener{
            showBrushSizeChooserDialog()
        }
    }

    /*
     *Shows rationale dialog for displaying why the app needs permission
     *Only shown if the user has denied the permission request previously
    */
    private fun showRationaleDialog(
        title: String,
        message: String,
    ){
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle(title)
            .setMessage(message)
            .setPositiveButton("Cancel"){dialog, _->
                dialog.dismiss()
            }
        builder.create().show()
    }

    private fun showBrushSizeChooserDialog(){
        var brushDialog = Dialog(this)
        brushDialog.setContentView(R.layout.dialog_brush_size)
        brushDialog.setTitle("Brush size: ")
        val smallBtn : ImageButton = brushDialog.findViewById(R.id.ib_small_brush)
        smallBtn.setOnClickListener{
            drawingView?.setSizeForBrush(10.toFloat())
            brushDialog.dismiss()
        }
        val mediumBtn : ImageButton = brushDialog.findViewById(R.id.ib_medium_brush)
        mediumBtn.setOnClickListener{
            drawingView?.setSizeForBrush(20.toFloat())
            brushDialog.dismiss()
        }
        val largeBtn : ImageButton = brushDialog.findViewById(R.id.ib_large_brush)
        largeBtn.setOnClickListener{
            drawingView?.setSizeForBrush(30.toFloat())
            brushDialog.dismiss()
        }
        brushDialog.show()
    }

    fun paintClicked(view: View){
       if(view !== mImageButtonCurrentPaint){
           val imageButton = view as ImageButton
           val colorTag = imageButton.tag.toString()
           drawingView?.setColor(colorTag)

           imageButton.setImageDrawable(
               ContextCompat.getDrawable(this,R.drawable.pallet_pressed)
           )
           mImageButtonCurrentPaint?.setImageDrawable(
               ContextCompat.getDrawable(this,R.drawable.pallet_normal)
           )

           mImageButtonCurrentPaint = view
       }
    }
}