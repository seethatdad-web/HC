package com.example.hc

import android.app.Activity
import android.hardware.Camera
import android.os.Bundle
import android.view.KeyEvent
import android.widget.Toast
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Suppress("DEPRECATION")
class MainActivity : Activity() {

    private var camera: Camera? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val background = android.view.View(this)
        background.setBackgroundColor(android.graphics.Color.BLACK)
        setContentView(background)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            takeSecretPicture()
            return true 
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun takeSecretPicture() {
        try {
            camera = Camera.open()
            val parameters = camera?.parameters
            camera?.parameters = parameters
            
            val dummy = android.graphics.SurfaceTexture(10)
            camera?.setPreviewTexture(dummy)
            camera?.startPreview()

            camera?.takePicture(null, null, Camera.PictureCallback { data, _ ->
                savePhotoToStorage(data)
                releaseCamera()
            })
        } catch (e: Exception) {
            Toast.makeText(this, "Ошибка: ${e.message}", Toast.LENGTH_SHORT).show()
            releaseCamera()
        }
    }

    private fun savePhotoToStorage(data: ByteArray) {
        try {
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val outputDir = getExternalFilesDir(null)
            val photoFile = File(outputDir, "IMG_$timeStamp.jpg")
            
            val fos = FileOutputStream(photoFile)
            fos.write(data)
            fos.close()
            
            Toast.makeText(this, "Снимок: ${photoFile.name}", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Ошибка сохранения: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun releaseCamera() {
        camera?.stopPreview()
        camera?.release()
        camera = null
    }

    override fun onDestroy() {
        super.onDestroy()
        releaseCamera()
    }
}
