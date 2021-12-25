package org.savit.savitauthenticator.ui.genericviews

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.savit.savitauthenticator.R
import org.savit.savitauthenticator.model.EncryptedDatabase
import org.savit.savitauthenticator.ui.genericviews.viewmodel.PinCameraViewmodel
import org.savit.savitauthenticator.ui.getstarted.EditScreen
import org.savit.savitauthenticator.ui.getstarted.MainQRScreen
import org.savit.savitauthenticator.ui.theme.Green500
import org.savit.savitauthenticator.ui.theme.GreenTrans200
import org.savit.savitauthenticator.ui.theme.SavitAuthenticatorTheme
import org.savit.savitauthenticator.ui.theme.textColorDark

class PinCameraActivity : AppCompatActivity() {

    private lateinit var requestPermissionLauncher:
            ActivityResultLauncher<String>

    private val model by inject<PinCameraViewmodel>()
    private val encryptedDatabase by inject<EncryptedDatabase>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val dao = encryptedDatabase.getUSerAccountDao()

        requestPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    model.grantPermission()
                } else {
                    model.disAblePermission()
                }
            }



        setContent {
            SavitAuthenticatorTheme {
                val listOfScreens = listOf(Screens.QRScreen,Screens.EditScreen)
                val navController = rememberNavController()
                val isPermissionGranted by model.isGranted.observeAsState()
                val isDark = isSystemInDarkTheme()
                Column() {

                }
                Scaffold(
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
                bottomBar = {
                    BottomAppBar(modifier = Modifier.fillMaxWidth(),contentPadding = PaddingValues(top = 5.dp,bottom = 5.dp)) {
                        val navBackStackEntry by navController.currentBackStackEntryAsState()
                        val currentRoute = navBackStackEntry?.destination
                        listOfScreens.forEach {screen ->
                            BottomNavigationItem(
                                icon = { screen.drawableResource() },
                                label = { Text(stringResource(screen.resourceId),color = androidx.compose.ui.graphics.Color.White) },
                                selected = currentRoute?.hierarchy?.any { it.route == screen.route } == true,
                                onClick = {
                                    navController.navigate(screen.route) {
                                        // Pop up to the start destination of the graph to
                                        // avoid building up a large stack of destinations
                                        // on the back stack as users select items
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = false
                                        }
                                        // Avoid multiple copies of the same destination when
                                        // reselecting the same item
                                        launchSingleTop = true
                                        // Restore state when reselecting a previously selected item
                                        restoreState = false
                                    }
                                }
                            )
                        }
                    }
                },modifier = Modifier.fillMaxSize())
                {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        when {
                            ContextCompat.checkSelfPermission(
                                this,
                                Manifest.permission.CAMERA
                            ) == PackageManager.PERMISSION_GRANTED -> {
                                model.grantPermission()
                            }
                            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                                model.disAblePermission()
                            }
                            else -> {
                                requestPermissionLauncher.launch(
                                    Manifest.permission.CAMERA)
                            }
                        }
                    }
                    if (isPermissionGranted != null && isPermissionGranted == true){

                        NavHost(navController, startDestination = Screens.QRScreen.route,modifier = Modifier.fillMaxSize()) {
                            composable(Screens.QRScreen.route) { MainQRScreen() }
                            composable(Screens.EditScreen.route) { EditScreen() }
                        }
                    }else if(isPermissionGranted != null && isPermissionGranted == false){
                        AlertDialog(
                            onDismissRequest = {
                            },
                            title = {
                                Box(Modifier.fillMaxWidth()) {
                                    Icon(
                                        Icons.Rounded.CameraAlt, contentDescription = "",tint = Green500,modifier = Modifier.align(
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
                                            model.disAblePermission()
                                        },modifier = Modifier.align(Alignment.Center)
                                    ) {
                                        Text("Grant Permission",fontSize = 14.sp,fontWeight = FontWeight.Bold,color = if (isDark) textColorDark else Green500)
                                    }
                                }

                            })
                    }



                }

            }
        }
    }
}

sealed class Screens(val route: String, @StringRes val resourceId: Int, val drawableResource : @Composable () -> Unit){
    object QRScreen : Screens ("QRCodeScreen",R.string.qrcode,{
        Icon(
            painter = painterResource(id = R.drawable.qrcode),
            contentDescription ="",modifier = Modifier
                .size(24.dp)
                .padding(bottom = 5.dp)
        )
    } )
    object EditScreen : Screens ("EnterKeyScreen",R.string.enterkey,{
        Icon(
            painter = painterResource(id = R.drawable.edit),
            contentDescription ="",modifier = Modifier
                .size(24.dp)
                .padding(bottom = 5.dp)
        )
    })
}

