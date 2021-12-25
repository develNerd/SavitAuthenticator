package org.savit.savitauthenticator.utils

import android.annotation.SuppressLint
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage

typealias QRListener = (barcode: String?,error:String?) -> Unit


class QRCodeAnalyzer(private val qrListener: QRListener) : ImageAnalysis.Analyzer {

    override fun analyze(image: ImageProxy) {
       scanBarcode(imageProxy = image)
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
                        val barcodes = it.result as List<Barcode>
                        for (barcode in barcodes) {
                            when (barcode.valueType) {
                                Barcode.TYPE_URL -> {
                                    val url = barcode.url?.url
                                    qrListener(url,null)
                                    Log.d("QR Code :" ,"$url")
                                }
                                else -> {
                                    val barcodeText = barcode.rawValue
                                    qrListener(barcodeText,null)
                                    Log.d("QR Code :" ,"$barcodeText")
                                }
                            }
                        }
                        Log.d("QR Code :" ,"QR Code Identified")

                    } else {
                        it.exception?.printStackTrace()
                        qrListener(null,it.exception.toString())
                        Log.d("QR Code :" ,"${it.exception}")
                    }
                }
        }
    }



}