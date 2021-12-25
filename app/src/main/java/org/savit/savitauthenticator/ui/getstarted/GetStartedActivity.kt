package org.savit.savitauthenticator.ui.getstarted

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.content.ContextCompat
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.savit.savitauthenticator.R
import org.savit.savitauthenticator.ui.genericviews.PinCameraActivity
import org.savit.savitauthenticator.ui.theme.*
import org.savit.savitauthenticator.utils.Coroutines
import org.savit.savitauthenticator.utils.SavitDataStore
import java.io.InputStreamReader

import java.io.BufferedReader
import android.net.Uri







class GetStartedActivity : AppCompatActivity() {

    private lateinit var requestPermissionLauncher:
            ActivityResultLauncher<String>

    private val savitDataStore by inject<SavitDataStore>()

    @ExperimentalMaterialApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    startActivity(Intent(this@GetStartedActivity,PinCameraActivity::class.java))
                } else {

                }
            }

        Coroutines.main {
            savitDataStore.saveIsHaveFingerPrint(checkIfFingerprintAvailable().first)
        }
        setContent { 
            SavitAuthenticatorTheme {
                val isDark = isSystemInDarkTheme()
                val scope = rememberCoroutineScope()
                val scaffoldState = rememberBottomSheetScaffoldState()
                val isCollapsed = scaffoldState.bottomSheetState.isCollapsed
                BottomSheetScaffold(
                    sheetContent = {
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .height(60.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically,modifier = Modifier.align(
                                Alignment.Center)) {
                                TextButton(onClick = {
                                    scope.launch {
                                        if (scaffoldState.bottomSheetState.isCollapsed) scaffoldState.bottomSheetState.expand() else  scaffoldState.bottomSheetState.collapse()  }
                                }) {
                                    Text("Read More",color = if(isDark) textColorDark else Green500)
                                    if (isCollapsed){
                                        Icon(Icons.Default.KeyboardArrowUp, contentDescription = "",modifier = Modifier
                                            .padding(start = 10.dp)
                                            .align(
                                                Alignment.CenterVertically
                                            ))
                                    }else{
                                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = "",modifier = Modifier
                                            .padding(start = 10.dp)
                                            .align(
                                                Alignment.CenterVertically
                                            ))
                                    }
                                }
                            }
                        }
                        Column(
                            Modifier
                                .fillMaxWidth()
                                .padding(start = 20.dp, end = 20.dp, top = 10.dp, bottom = 10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            AnnotatedClickableText()
                            Spacer(modifier = Modifier.size(10.dp))
                            Text(text = stringResource(id = R.string.totpPhrase2))
                            Spacer(modifier = Modifier.size(15.dp))
                            Text(text = "Some applications that currently make use of TOTP include",fontSize = 13.sp,textAlign = TextAlign.Center)
                            Spacer(modifier = Modifier.size(7.dp))

                            Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                                Image(painter = painterResource(id = R.drawable.slack), contentDescription = "",modifier = Modifier.padding(end = 10.dp))
                                Image(painter = painterResource(id = R.drawable.google), contentDescription = "",modifier = Modifier.padding(end = 10.dp))
                                Image(painter = painterResource(id = R.drawable.github), contentDescription = "",modifier = Modifier.padding(end = 10.dp))
                                Image(painter = painterResource(id = R.drawable.microsoft), contentDescription = "",modifier = Modifier.padding(end = 10.dp))
                            }
                            Spacer(modifier = Modifier.size(15.dp))




                        }
                    },
                    topBar = {
                        Box(modifier = Modifier
                            .fillMaxWidth()
                            .background(color = GreenTrans200)) {
                            Row(modifier = Modifier.padding(start =  10.dp,end = 20.dp,top = 10.dp,bottom = 10.dp),verticalAlignment = Alignment.CenterVertically) {
                                Image(painter = painterResource(id = R.drawable.logo_vector), contentDescription = "logoTopAppBar",modifier = Modifier
                                    .size(36.dp)
                                    .padding(start = 3.dp, end = 10.dp))
                                Text(text = "Savit Authenticator",fontSize = 16.sp,fontWeight = FontWeight.Bold,fontStyle = FontStyle.Italic)
                            }
                            /* IconButton(onClick = {  },modifier = Modifier.align(
                                 Alignment.CenterEnd)) {
                                 Icon(Icons.Default.Menu, contentDescription = "menuIconButton",)
                             }*/
                        }
                    },
                    scaffoldState = scaffoldState,
                    sheetPeekHeight = 60.dp,
                    sheetElevation = 2.dp,
                    sheetBackgroundColor = if (isDark) black else GreenTrans300,
                    sheetShape = RoundedCornerShape(topStart = 10.dp,topEnd = 10.dp),
                    content = {
                        var (showPermissonRationale,setPermissionRationale) = remember {
                            mutableStateOf(false)
                        }

                        ConstraintLayout(modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = 65.dp)) {

                            val (button,Image) = createRefs()


                            Column(verticalArrangement = Arrangement.Center,modifier = Modifier.constrainAs(Image){
                                top.linkTo(parent.top)
                                bottom.linkTo(button.top)
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                            }) {
                                Image(painter = painterResource(id = R.drawable.intro_pic), contentDescription = "",modifier = Modifier.size(400.dp))
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally,modifier = Modifier.constrainAs(button){
                                bottom.linkTo(parent.bottom)
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                            }) {
                                Text(text = "Savit Authenticator fully secures you accounts with a ready OTP for Two Factor Authentication",fontSize = 15.sp,modifier = Modifier
                                    .padding(10.dp),textAlign = TextAlign.Center)
                                Button(onClick = {


                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        when {
                                            ContextCompat.checkSelfPermission(
                                                this@GetStartedActivity,
                                                Manifest.permission.CAMERA
                                            ) == PackageManager.PERMISSION_GRANTED -> {
                                                startActivity(Intent(this@GetStartedActivity,PinCameraActivity::class.java))
                                            }
                                            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                                                setPermissionRationale(true)
                                            }
                                            else -> {
                                                // You can directly ask for the permission.
                                                // The registered ActivityResultCallback gets the result of this request.
                                                requestPermissionLauncher.launch(
                                                    Manifest.permission.CAMERA)
                                            }
                                        }
                                    }else{
                                        startActivity(Intent(this@GetStartedActivity,PinCameraActivity::class.java))
                                    }



                                },elevation = ButtonDefaults.elevation(defaultElevation = 0.dp),modifier = Modifier
                                    .padding(10.dp)
                                    .fillMaxWidth()) {
                                    Text(text = "Get Started")
                                }
                            }

                            if (showPermissonRationale){
                                AlertDialog(
                                    onDismissRequest = {
                                    },
                                    title = {
                                        Box(Modifier.fillMaxWidth()) {
                                            Icon(Icons.Rounded.CameraAlt, contentDescription = "",tint = Green500,modifier = Modifier.align(
                                                Alignment.Center))
                                        }
                                    },
                                    text = {
                                        Text(text = "Camera permission is needed for Savit Authenticator to operate on this device",modifier = Modifier.fillMaxWidth(),textAlign = TextAlign.Center)
                                    },
                                    confirmButton = {
                                        Box(modifier = Modifier.wrapContentSize()) {
                                            TextButton(
                                                onClick = {
                                                    requestPermissionLauncher.launch(
                                                        Manifest.permission.CAMERA)
                                                    setPermissionRationale(false)
                                                },modifier = Modifier.align(Alignment.Center)
                                            ) {

                                                Text("Grant Permission",fontSize = 14.sp,fontWeight = FontWeight.Bold,color = if (isDark) textColorDark else Green500)
                                            }
                                        }

                                    },dismissButton = {
                                        Box(modifier = Modifier.wrapContentSize()) {
                                            TextButton(
                                                onClick = {

                                                    setPermissionRationale(false)
                                                },modifier = Modifier.align(Alignment.Center)
                                            ) {
                                                Text("Dissmiss",fontSize = 14.sp,fontWeight = FontWeight.Bold,color =  if (isDark) textColorDark else Green500)
                                            }
                                        }
                                    },modifier = Modifier.padding(15.dp)
                                )
                            }

                        }

                    }
                )
            }
        }
    }

    @Preview(showBackground = true)
    @ExperimentalMaterialApi
    @Composable
    fun MainScreen(){

    }

    @Composable
    fun AnnotatedClickableText() {
        val isDark = isSystemInDarkTheme()
        val annotatedText = buildAnnotatedString {
            withStyle(style = SpanStyle(fontSize = 16.sp,color = if (isDark) textColorDark else Green500)){
                append(" Two factor authentication (2FA) via sms has been deprecated by")

                // We attach this *URL* annotation to the following content
                // until `pop()` is called
                pushStringAnnotation(tag = "URL",
                    annotation = "https://www.nist.gov/")
                withStyle(style = SpanStyle(color = lightblue,
                    fontWeight = FontWeight.Bold,fontSize = 17.sp,textDecoration = TextDecoration.Underline)) {
                    append("  NIST  ")
                }
                append(stringResource(id = R.string.totpPhrase01))
                pop()
            }


        }

        ClickableText(
            text = annotatedText,
            onClick = { offset ->
                // We check if there is an *URL* annotation attached to the text
                // at the clicked position
                annotatedText.getStringAnnotations(tag = "URL", start = offset,
                    end = offset)
                    .firstOrNull()?.let { annotation ->
                        // If yes, we log its value
                        val browse = Intent(Intent.ACTION_VIEW, Uri.parse(annotation.item))
                        startActivity(browse)
                        Log.d("Clicked URL", annotation.item)
                    }
            }
        )

    }

    private fun checkCameraPermission() {

        val requestPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {

                } else {

                }
            }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED -> {

                                }
                shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {

                }
                else -> {
                    // You can directly ask for the permission.
                    // The registered ActivityResultCallback gets the result of this request.
                    requestPermissionLauncher.launch(
                        Manifest.permission.CAMERA)
                }
            }
        }

    }

    private fun checkRootMethod3(): Boolean {
        var process: Process? = null
        return try {
            process = Runtime.getRuntime().exec(arrayOf("/system/xbin/which", "su"))
            val `in` = BufferedReader(InputStreamReader(process.inputStream))
            if (`in`.readLine() != null) true else false
        } catch (t: Throwable) {
            false
        } finally {
            process?.destroy()
        }
    }

    private fun checkIfFingerprintAvailable():Pair<Boolean,String?>{
        val biometricManager = BiometricManager.from(this)
        when (biometricManager.canAuthenticate(BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS ->
                return Pair(true,null)
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE ->
            {
                return Pair(false,null)
            }

            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                return Pair(false,null)
            }

            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED ->{
                return Pair(true,"Enroll Fingerprint")
            }

            BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED -> {
                return Pair(false,null)
            }
            BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED -> {
                return Pair(false,null)
            }
            BiometricManager.BIOMETRIC_STATUS_UNKNOWN -> {
                return Pair(false,null)
            }
        }
        return Pair(false,null)
    }



}