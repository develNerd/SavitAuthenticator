package org.savit.savitauthenticator.ui.dashboard

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.rounded.Fingerprint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
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
import org.koin.androidx.compose.getViewModel
import org.savit.savitauthenticator.R
import org.savit.savitauthenticator.ui.dashboard.viewmodel.DashboardViewModel
import org.savit.savitauthenticator.ui.genericviews.PinCameraActivity
import org.savit.savitauthenticator.ui.theme.*
import java.util.*
import java.util.concurrent.Executor

class DashboardActivity : AppCompatActivity() {
    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo
    private val viewModel by inject<DashboardViewModel>()
    @ExperimentalFoundationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            var errorMessage by remember {
                mutableStateOf("")
            }
            val (isAuthenticated,setAuthenticated) = remember {
                mutableStateOf(false)
            }

            executor = ContextCompat.getMainExecutor(this)
            biometricPrompt = BiometricPrompt(this, executor,
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationError(errorCode: Int,
                                                       errString: CharSequence) {
                        super.onAuthenticationError(errorCode, errString)
                        errorMessage = "Something Went Wrong : $errString"
                    }

                    override fun onAuthenticationSucceeded(
                        result: BiometricPrompt.AuthenticationResult) {
                        super.onAuthenticationSucceeded(result)
                        setAuthenticated(true)
                    }

                    override fun onAuthenticationFailed() {
                        super.onAuthenticationFailed()
                        errorMessage = "Something Went Wrong"
                    }
                })

            promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle("Savit Authenticator")
                .setSubtitle("Log in using your biometric credential")
                .setNegativeButtonText("Cancel")
                .build()

            val isFingerprint by viewModel.isMainFingerPrint.observeAsState()


            SavitAuthenticatorTheme {

                if (isFingerprint != null){
                    if (isAuthenticated  && isFingerprint!!)
                    {
                        Dash()
                    }
                    else if(!isFingerprint!!){
                        setAuthenticated(true)
                        Dash()
                    }
                    else if(!isAuthenticated  && isFingerprint!!){
                        biometricPrompt.authenticate(promptInfo)
                        FingerPrintScreen()
                    }
                }


            }

       }


    }

    @ExperimentalFoundationApi
    @Composable
    fun Dash(){
        val listOfScreens = listOf(DashboardScreens.DashboardScreen, DashboardScreens.PrefenenceScreen)
        val navController = rememberNavController()
        val isDark = isSystemInDarkTheme()
        var expanded by remember {
            mutableStateOf(false)
        }
        Scaffold(
            topBar = {
                BoxWithConstraints(modifier = Modifier
                    .fillMaxWidth()
                    .background(color = if (isDark) Green500 else Green201)) {
                    Row(modifier = Modifier.padding(start =  10.dp,end = 20.dp,top = 10.dp,bottom = 10.dp),verticalAlignment = Alignment.CenterVertically) {
                        Image(painter = painterResource(id = R.drawable.logo_vector), contentDescription = "logoTopAppBar",modifier = Modifier
                            .size(36.dp)
                            .padding(start = 3.dp, end = 10.dp))
                        Text(text = "Savit Authenticator",fontSize = 16.sp,fontWeight = FontWeight.Bold,fontStyle = FontStyle.Italic)
                    }
                    val menuItems = listOf("Add Account")
                    IconButton(onClick = { expanded = !expanded },modifier = Modifier.align(
                        Alignment.CenterEnd)) {
                        Icon(Icons.Default.MoreVert, contentDescription = "menuIconButton",)
                    }
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false},offset = DpOffset(this.maxWidth,0.dp),modifier = Modifier.align(
                        Alignment.CenterEnd)) {
                        menuItems.forEach {
                            DropdownMenuItem(onClick = {
                                if (it.lowercase(Locale.getDefault()).contains("account")){
                                    startActivity(Intent(this@DashboardActivity,PinCameraActivity::class.java))
                                    expanded = false
                                }
                            },modifier = Modifier.align(Alignment.CenterHorizontally)) {
                                Text(text = it)
                            }
                        }
                    }
                }
            },
            bottomBar = {
                BottomAppBar(modifier = Modifier.fillMaxWidth(),contentPadding = PaddingValues(top = 5.dp,bottom = 5.dp),backgroundColor =if (isDark) Green500 else Green201,contentColor = if (isDark) Green201 else Green500) {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = navBackStackEntry?.destination
                    listOfScreens.forEach {screen ->
                        BottomNavigationItem(
                            icon = { screen.drawableResource() },
                            label = { Text(stringResource(screen.resourceId)) },
                            selected = currentRoute?.hierarchy?.any { it.route == screen.route } == true,
                            onClick = {
                                navController.navigate(screen.route) {

                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    // reselecting the same item
                                    launchSingleTop = true
                                    // Restore state when reselecting a previously selected item
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            })
        {
            NavHost(navController, startDestination = DashboardScreens.DashboardScreen.route) {
                composable(DashboardScreens.DashboardScreen.route) { DashboardScreen(viewModel) }
                composable(DashboardScreens.PrefenenceScreen.route) { PreferenceScreen(this@DashboardActivity) }
            }
        }
    }
    @Composable
    fun FingerPrintScreen(){
        val isDark = isSystemInDarkTheme()
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize(),horizontalAlignment = Alignment.CenterHorizontally) {
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .background(color = GreenTrans200))
                {
                    Row(modifier = Modifier.padding(start =  10.dp,end = 20.dp,top = 10.dp,bottom = 10.dp),verticalAlignment = Alignment.CenterVertically) {
                        Image(painter = painterResource(id = R.drawable.logo_vector), contentDescription = "logoTopAppBar",modifier = Modifier
                            .size(36.dp)
                            .padding(start = 3.dp, end = 10.dp))
                        Text(text = "Savit Authenticator",fontSize = 16.sp,fontWeight = FontWeight.Bold,fontStyle = FontStyle.Italic)
                    }
                }
                Box(modifier = Modifier
                    .fillMaxSize()){
                    Icon(Icons.Rounded.Fingerprint,tint = Green200, contentDescription = "Fingerprint",modifier = Modifier
                        .size(72.dp)
                        .align(
                            Alignment.Center
                        ))
                }
            }
            Column(modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(15.dp),horizontalAlignment = Alignment.CenterHorizontally) {
                
                Text(text = "Fingerprint Required to Unlock App",modifier = Modifier.padding(20.dp))
                
                OutlinedButton(onClick = {
                    biometricPrompt.authenticate(promptInfo)
                }) {
                    Text(text = "Unlock",modifier = Modifier.padding(start = 5.dp, end = 5.dp),color = if (isDark) Green201 else Green500 )
                }
            }
   
        }

    }
}



sealed class DashboardScreens(val route: String, @StringRes val resourceId: Int, val drawableResource : @Composable () -> Unit){
    object DashboardScreen : DashboardScreens ("Dashboard",R.string.dashboard,{
        Icon(
            painter = painterResource(id = R.drawable.dashboard),
            contentDescription ="",modifier = Modifier
                .size(24.dp)
                .padding(bottom = 5.dp)
        )
    } )
    object PrefenenceScreen : DashboardScreens ("Preferences",R.string.preferences,{
        Icon(
            painter = painterResource(id = R.drawable.preferences),
            contentDescription ="",modifier = Modifier
                .size(24.dp)
                .padding(bottom = 5.dp)
        )
    })
}