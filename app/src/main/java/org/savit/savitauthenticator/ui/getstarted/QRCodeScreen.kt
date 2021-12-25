package org.savit.savitauthenticator.ui.getstarted

import android.content.Intent
import android.net.Uri
import android.net.UrlQuerySanitizer
import android.os.Looper
import android.util.Log
import android.util.Size
import android.view.ViewGroup
import androidx.camera.core.CameraControl
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.delay
import org.koin.androidx.compose.getViewModel
import org.savit.savitauthenticator.R
import org.savit.savitauthenticator.model.useraccounts.UserAccount
import org.savit.savitauthenticator.ui.dashboard.DashboardActivity
import org.savit.savitauthenticator.ui.genericviews.CameraOverlay
import org.savit.savitauthenticator.ui.genericviews.viewmodel.PinCameraViewmodel
import org.savit.savitauthenticator.ui.theme.Green500
import org.savit.savitauthenticator.ui.theme.textColorDark
import org.savit.savitauthenticator.utils.QRCodeAnalyzer
import java.util.*
import java.util.concurrent.Executor
import java.util.logging.Handler


private lateinit var qrAnalyzer:ImageAnalysis
private  val  ISSUER_PARAM = "issuer"
private val SECRET_PARAM = "secret"
private val OTP_SCHEME = "otpauth"
private val TOTP = "totp" // time-based
private lateinit var cameraControl: CameraControl
@Composable
fun MainQRScreen() {

    val isDark = isSystemInDarkTheme()
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val viewModel = getViewModel<PinCameraViewmodel>()
    var (isDetected,setDetected) = remember {
        mutableStateOf(false)
    }
    var qrerror by remember {
        mutableStateOf("")
    }
    var qrCode by remember {
        mutableStateOf("")
    }

    var (isLightOn,setLightOn) = remember {
        mutableStateOf(false)
    }


    var isDone by remember {
        mutableStateOf(false)
    }
    val eventID by remember {
        mutableStateOf("")
    }

    LaunchedEffect(eventID){
        delay(100)
        isDone = true
    }

    if(isDone){
        Box(modifier = Modifier.fillMaxHeight().fillMaxWidth()) {


            AndroidView(modifier = Modifier.fillMaxWidth(1F).fillMaxHeight(1F),
                factory = { ctx ->
                    val previewView = PreviewView(ctx)
                    val executor = ContextCompat.getMainExecutor(ctx)

                    cameraProviderFuture.addListener({
                        val cameraProvider = cameraProviderFuture.get()
                        val preview = Preview.Builder().build().also {
                            it.setSurfaceProvider(previewView.surfaceProvider)
                        }

                        qrAnalyzer = ImageAnalysis.Builder()
                            .setTargetResolution(Size(720, 1280))
                            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                            .build()
                            .apply {
                                this.setAnalyzer(executor,QRCodeAnalyzer{barcode: String?, error: String? ->
                                    if (!barcode.isNullOrEmpty() && error == null && !isDetected){
                                        setDetected(true)
                                        qrCode = barcode
                                        qrerror = ""
                                    }else if (!error.isNullOrEmpty()){
                                        setDetected(true)
                                        qrerror = error
                                    }
                                })
                            }


                        val cameraSelector = CameraSelector.Builder()
                            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                            .build()

                        cameraProvider.unbindAll()
                        val cam =  cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            cameraSelector,
                            qrAnalyzer,
                            preview
                        )
                        previewView.implementationMode = PreviewView.ImplementationMode.COMPATIBLE

                        cameraControl = cam.cameraControl


                    }, executor)
                    previewView
                }
            ){view ->
                view.layoutParams = ViewGroup.LayoutParams(view.width,view.minimumHeight)
            }


            if(isDetected && qrerror == ""){

                var myMessage:String by remember {
                    mutableStateOf("")
                }

                val userAccountInfo = parseUri(qrCode.toUri()){
                    myMessage = it?:""
                }

                if (myMessage.isEmpty() && userAccountInfo != null){
                    viewModel.saveUserAccount(userAccountInfo).also {
                        context.startActivity(Intent(context,DashboardActivity::class.java))
                    }
                }else{
                    AlertDialog(
                        onDismissRequest = {
                        },
                        title = {
                            Box(Modifier.fillMaxWidth()) {
                                Icon(
                                    Icons.Rounded.QrCodeScanner, contentDescription = "",tint = Green500,modifier = Modifier.align(
                                        Alignment.Center))
                            }
                        },
                        text = {
                            Text(text = "$myMessage",modifier = Modifier.fillMaxWidth(),textAlign = TextAlign.Center)
                        },
                        confirmButton = {
                            Box(modifier = Modifier.wrapContentSize()) {
                                TextButton(
                                    onClick = {
                                        qrerror = ""
                                        setDetected(false)
                                    },modifier = Modifier.align(Alignment.Center)
                                ) {
                                    Text("Dismiss",fontSize = 14.sp,fontWeight = FontWeight.Bold,color = if (isDark) textColorDark else Green500)
                                }
                            }

                        }
                    )
                }



            }
            else if (qrerror.isNotEmpty()){
                AlertDialog(
                    onDismissRequest = {
                    },
                    title = {
                        Box(Modifier.fillMaxWidth()) {
                            Icon(
                                Icons.Rounded.QrCodeScanner, contentDescription = "",tint = Green500,modifier = Modifier.align(
                                    Alignment.Center))
                        }
                    },
                    text = {
                        Text(text = "Error Identifying QR Code",modifier = Modifier.fillMaxWidth(),textAlign = TextAlign.Center)
                    },
                    confirmButton = {
                        Box(modifier = Modifier.wrapContentSize()) {
                            TextButton(
                                onClick = {
                                    qrerror = ""
                                    setDetected(false)
                                },modifier = Modifier.align(Alignment.Center)
                            ) {
                                Text("Dismiss",fontSize = 14.sp,fontWeight = FontWeight.Bold,color = if (isDark) textColorDark else Green500)
                            }
                        }

                    }
                )
            }

            AndroidView(modifier = Modifier.fillMaxSize(), factory = {context ->
                CameraOverlay(context).apply {
                }
            }) {view ->

            }

            IconToggleButton(checked = isLightOn, onCheckedChange = { isOn ->
                setLightOn(isOn)
                if (isOn){
                    cameraControl?.enableTorch(true)
                }else{
                    cameraControl?.enableTorch(false)
                }
            },modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 130.dp)) {
                if (!isLightOn){
                    Icon(
                        Icons.Rounded.FlashlightOn, contentDescription = "" ,modifier = Modifier
                            .size(32.dp),tint = textColorDark
                    )
                }else{
                    Icon(
                        Icons.Rounded.FlashlightOff, contentDescription = "" ,modifier = Modifier
                            .size(32.dp),tint = textColorDark
                    )
                }
            }

            TextButton(onClick = {
                context.startActivity(Intent(context,DashboardActivity::class.java))
            },modifier = Modifier
                .align(Alignment.BottomCenter).padding(bottom = 80.dp)) {
                Text(text = "Skip for Now",fontStyle = FontStyle.Italic,fontWeight = FontWeight.SemiBold,fontSize = 14.sp,textAlign = TextAlign.Center)
            }

        }

    }


}



private fun parseUri(uri: Uri, message:(String?) -> Unit) : UserAccount?{
    val scheme = uri.scheme!!.toLowerCase()
    val path = uri.path
    val authority = uri.authority

    if (!OTP_SCHEME.equals(scheme)){
        message("Wrong Url Scheme")
        Log.d("MyQR","Wrong Url Scheme")
        return null
    }

    val name = validateAndGetNameInPath(path)
    if (name == null){
        message("Wrong Url Scheme")
        Log.d("MyQR","Name Null")
        return null
    }

    val sanitizer = UrlQuerySanitizer()
    sanitizer.allowUnregisteredParamaters = true
    sanitizer.parseUrl(uri.toString())



    val secret = sanitizer.getValue(SECRET_PARAM)
    if (secret == null){
        message("Something Went Wrong, No Secret")
        Log.d("MyQR","Secret Null")
        return  null
    }

    val issuer = sanitizer.getValue("issuer")?:""
    val image = when{
        issuer.lowercase(Locale.getDefault()).contains("slack") -> R.drawable.slack
        issuer.lowercase(Locale.getDefault()).contains("github") -> R.drawable.github
        issuer.lowercase(Locale.getDefault()).contains("microsoft") -> R.drawable.microsoft
        issuer.lowercase(Locale.getDefault()).contains("google") -> R.drawable.google
        issuer.lowercase(Locale.getDefault()).contains("bitbucket") -> R.drawable.bitbucket
        issuer.lowercase(Locale.getDefault()).contains("linkedin") -> R.drawable.linkedin
        issuer.lowercase(Locale.getDefault()).contains("gmail") -> R.drawable.gmail
        issuer.lowercase(Locale.getDefault()).contains("dropbox") -> R.drawable.dropbox
        issuer.lowercase(Locale.getDefault()).contains("bitcoin") -> R.drawable.bitcoin
        issuer.lowercase(Locale.getDefault()).contains("discord") -> R.drawable.discord
        issuer.lowercase(Locale.getDefault()).contains("gitlab") -> R.drawable.gitlab
        issuer.lowercase(Locale.getDefault()).contains("aws") -> R.drawable.aws
        else -> R.drawable.logo_vector
    }

    return UserAccount(0,secret,image,issuer,name,null)

}

private fun validateAndGetNameInPath(path: String?): String? {
    if (path == null || !path.startsWith("/")) {
        return null
    }
    // path is "/name", so remove leading "/", and trailing white spaces
    // path is "/name", so remove leading "/", and trailing white spaces
    val name = path.substring(1).trim()
    return if (name.length == 0) {
        null // only white spaces.
    } else name
}
