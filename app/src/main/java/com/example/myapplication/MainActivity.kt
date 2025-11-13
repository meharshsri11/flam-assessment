package com.example.flamassessment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.flamassessment.databinding.ActivityMainBinding
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import android.util.Size
import java.util.concurrent.Executors
import android.media.Image
import android.util.Log

import org.opencv.core.Mat
import org.opencv.core.CvType
import org.opencv.imgproc.Imgproc
import java.nio.ByteBuffer

// Constants for permission requests
private const val REQUEST_CODE_PERMISSIONS = 10
private val REQUIRED_PERMISSIONS = arrayOf(android.Manifest.permission.CAMERA)

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // This is the check that kicks off the process
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            requestPermissions(REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }
    }

    external fun stringFromJNI(): String

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        checkSelfPermission(it) == android.content.pm.PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(this,
                    "Camera permission is required, you know?",
                    Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
            }

            val imageAnalyzer = ImageAnalysis.Builder()
                .setTargetResolution(Size(640, 480))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_LATEST)
                .build()
                .also {
                    it.setAnalyzer(Executors.newSingleThreadExecutor(), ImageProcessor())
                }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageAnalyzer)

            } catch(exc: Exception) {
                Log.e("V_LOG", "Use case binding failed", exc)
                Toast.makeText(this, "Binding failed: ${exc.message}", Toast.LENGTH_SHORT).show()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    companion object {
        init {
            System.loadLibrary("flamassessment")
        }
    }
}

// ⭐ IMAGE PROCESSOR CLASS (outside MainActivity)
class ImageProcessor : ImageAnalysis.Analyzer {

    override fun analyze(image: ImageProxy) {
        image.image?.let { cameraImage ->
            val mat = cameraImage.toMat()
            ImageProcessor.Companion.processFrameNative(mat.nativeObjAddr, mat.width(), mat.height())
            mat.release()
        }
        image.close()
    }

    // JNI BRIDGE DEFINITION
    companion object {
        external fun processFrameNative(frameBufferAddress: Long, width: Int, height: Int)
    }
}

// ⭐ IMAGE CONVERSION UTILITY (outside all classes)
fun Image.toMat(): Mat {
    val planes = this.planes
    val yBuffer = planes[0].buffer
    val uBuffer = planes[1].buffer
    val vBuffer = planes[2].buffer

    val ySize = yBuffer.remaining()
    val uSize = uBuffer.remaining()
    val vSize = vBuffer.remaining()

    val nv21 = ByteArray(ySize + uSize + vSize)

    yBuffer.get(nv21, 0, ySize)
    vBuffer.get(nv21, ySize, vSize)
    uBuffer.get(nv21, ySize + vSize, uSize)

    val yuvMat = Mat(this.height + this.height / 2, this.width, CvType.CV_8UC1)
    yuvMat.put(0, 0, nv21)

    val mat = Mat()
    Imgproc.cvtColor(yuvMat, mat, Imgproc.COLOR_YUV2BGR_NV21, 3)
    yuvMat.release()

    return mat
}