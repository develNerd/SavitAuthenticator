package org.savit.savitauthenticator.utils

import android.annotation.SuppressLint
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import org.savit.savitauthenticator.listeners.QRCodeListener

class QRCodeAnalyser1: ImageAnalysis.Analyzer {
    var listener : QRCodeListener? = null

    override fun analyze(imageProxy: ImageProxy) {
        scanBarcode(imageProxy)
    }

    @SuppressLint("UnsafeExperimentalUsageError", "UnsafeOptInUsageError")
    private fun scanBarcode(imageProxy: ImageProxy) {
        imageProxy.image?.let { image ->
            val inputImage = InputImage.fromMediaImage(image, imageProxy.imageInfo.rotationDegrees)
            val scanner = BarcodeScanning.getClient()

            scanner.process(inputImage)
                    .addOnCompleteListener {
                        imageProxy.close()
                        if (it.isSuccessful) {
                            readBarcodeData(it.result as List<Barcode>,listener)
                            Log.d("QR Code :" ,"QR Code Identified")

                        } else {
                            it.exception?.printStackTrace()
                            listener?.isFailed(it.exception?.message.toString())
                            Log.d("QR Code :" ,"${it.exception}")
                        }
                    }
        }
    }

    private fun readBarcodeData(barcodes: List<Barcode>,listener: QRCodeListener?){

        for (barcode in barcodes) {
            when (barcode.valueType) {
                Barcode.TYPE_URL -> {
                    val url = barcode.url?.url
                    listener?.isSuccessful(url)
                    Log.d("QR Code :" ,"$url")

                }
                else -> {
                    val barcodeText = barcode.rawValue
                    listener?.isSuccessful(barcodeText)
                    Log.d("QR Code :" ,"$barcodeText")

                }
            }
        }
    }
}