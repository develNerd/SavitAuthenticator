package org.savit.savitauthenticator.ui.dashboard

import android.R.attr
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.text.style.ParagraphStyle
import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.MoreHoriz
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat.startActivity
import org.koin.androidx.compose.get
import org.koin.androidx.compose.getViewModel
import org.savit.savitauthenticator.R
import org.savit.savitauthenticator.ui.dashboard.viewmodel.DashboardViewModel
import org.savit.savitauthenticator.ui.genericviews.CustomProgressBar
import org.savit.savitauthenticator.ui.genericviews.PinCameraActivity
import org.savit.savitauthenticator.ui.theme.*
import org.savit.savitauthenticator.utils.Coroutines
import org.savit.savitauthenticator.utils.SavitDataStore
import org.savit.savitauthenticator.utils.TotpCounter
import org.savit.savitauthenticator.utils.otp.CountDownListener
import org.savit.savitauthenticator.utils.otp.OtpProvider
import org.savit.savitauthenticator.utils.otp.TotpCountdownTask
import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput
import android.R.attr.text

import android.R.attr.label

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.InteractionSource
import androidx.core.content.ContextCompat

import androidx.core.content.ContextCompat.getSystemService





private  val DEFAULT_INTERVAL:Int = 30
private val TOTP_COUNTDOWN_REFRESH_PERIOD_MILLIS = 100L
@ExperimentalFoundationApi
@Composable
fun DashboardScreen(viewModel: DashboardViewModel){
    val isGrid by viewModel.isGrid.observeAsState()
    val userAccounts by viewModel.userAccounts.observeAsState()
    val setShowDeleteSnackbar by viewModel.showDeleteSnackbar.observeAsState(false)
    val isDark = isSystemInDarkTheme()
    val context = LocalContext.current
    val clipboard = context.applicationContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager


    Box(modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 70.dp)) {
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(top = 3.dp, start = 3.dp, end = 3.dp))
        {
            if (!userAccounts.isNullOrEmpty()){
                Card(modifier = Modifier
                    .fillMaxWidth()
                    .padding(7.dp)) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Text(text = "Accounts",fontWeight = FontWeight.Bold,modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(top = 15.dp, start = 10.dp, bottom = 15.dp))
                        Text(text = if (userAccounts.isNullOrEmpty()) "" else userAccounts!!.size.toString(),fontWeight = FontWeight.Bold,modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(top = 15.dp, end = 10.dp, bottom = 15.dp))
                    }
                }

                if (isGrid != null){
                    if (!isGrid!! && !userAccounts.isNullOrEmpty()){
                        LazyColumn(modifier = Modifier.fillMaxHeight()) {
                            items(userAccounts!!){userAccount ->
                                RowAccountItem(isDark = isDark,userAccount.name?:"",userAccount.issuer?:"",userAccount.sharedKey,icon = userAccount.image,viewModel,context,clipboard)
                            }
                        }
                    }else if(isGrid!! && !userAccounts.isNullOrEmpty()){
                        LazyVerticalGrid(cells = GridCells.Fixed(2)) {
                            items(userAccounts!!){userAccount ->
                                GridAccountItem(isDark = isDark,userAccount.name?:"",userAccount.issuer?:"",userAccount.sharedKey,icon = userAccount.image,viewModel,context,clipboard)
                            }
                        }

                    }
                }
            }else if (userAccounts != null && userAccounts!!.isEmpty()){
                Box(modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 80.dp)) {
                    Column(modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxSize(),horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(painter = painterResource(id = R.drawable.intro_pic), contentDescription = "",modifier = Modifier.weight(0.4F))
                        Spacer(modifier = Modifier.size(10.dp))
                        Text(text = "No Accounts Added")
                        Spacer(modifier = Modifier.size(10.dp))
                        OutlinedButton(onClick = {
                            val intent = Intent(context as Activity, PinCameraActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            (context as Activity).startActivity(intent)
                        }) {
                            Icon(Icons.Default.Add, contentDescription = "",modifier = Modifier.padding(end = 10.dp))
                            Text(text = "Add User Account",color = if (isDark) Green201 else Green500)
                        }
                    }
                }

            }

        }

        if (setShowDeleteSnackbar){
            ShwDeleteSnackBar(modifier = Modifier.align(Alignment.BottomCenter)) {
                viewModel.setShowDeleteSnackBar(it)
            }
        }
    }




}


fun deleteAccount(sharedKey:String,viewModel: DashboardViewModel){
    viewModel.setShowDeleteSnackBar(true)

    Handler(Looper.getMainLooper())
        .postDelayed({
            val delete = viewModel.showDeleteSnackbar.value?:false
                     if (delete){
                         viewModel.deleteAccount(sharedKey)
                         viewModel.setShowDeleteSnackBar(false)

                     }
        },3000)
}

@Composable
fun ShwDeleteSnackBar(modifier: Modifier = Modifier,setShowSnackBar:(Boolean) -> Unit){

    Box(modifier = modifier
        .height(80.dp)
        .fillMaxWidth()
        .padding(15.dp)
        .background(shape = RoundedCornerShape(3.dp), color = darkBg)) {
        Row(modifier = modifier.fillMaxSize(),verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier
                .fillMaxHeight()
                .background(
                    color = red,
                    shape = RoundedCornerShape(topStart = 3.dp, bottomStart = 3.dp)
                )
                .width(3.dp))
            Text(text = "Account Deleted",modifier = Modifier.padding(start = 10.dp,end = 5.dp),color = textColorDark,fontSize = 16.sp)
        }
        TextButton(onClick = { setShowSnackBar(false) },modifier = Modifier.align(Alignment.CenterEnd)) {
            Text(text = "Undo",modifier = Modifier.padding(5.dp),color = Green200,fontSize = 16.sp)
        }
    }
}


@Preview(showBackground = true)
@Composable
fun RowAccountItemPreview(){
    SavitAuthenticatorTheme() {
        Box(modifier = Modifier
            .padding(7.dp)
            .fillMaxWidth()
            .background(color = UserAccountBg, shape = RoundedCornerShape(10))) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier
                    .wrapContentWidth()
                    .padding(end = 71.dp),verticalAlignment = Alignment.CenterVertically) {
                    Image(painter = painterResource(id = R.drawable.slack), contentDescription = "",modifier = Modifier
                        .padding(top = 15.dp, bottom = 15.dp, start = 10.dp, end = 10.dp)
                        .size(48.dp))
                    Column(verticalArrangement = Arrangement.Center,modifier = Modifier
                        .wrapContentSize()
                        .padding(top = 10.dp, bottom = 10.dp)) {
                        Text(text = "Slack",fontWeight = FontWeight.Bold,fontSize = 14.sp)
                        Text(text = "isaackakpo4@gmail.com",fontWeight = FontWeight.SemiBold,fontSize = 14.sp,modifier = Modifier.padding(top = 6.dp))
                        Text(text = "908765",fontWeight = FontWeight.Bold,fontSize = 18.sp,modifier = Modifier.padding(top = 6.dp))

                    }
                }
                IconButton(onClick = { /*TODO*/ },modifier = Modifier.align(Alignment.CenterHorizontally)) {
                    Icon(Icons.Rounded.MoreHoriz, contentDescription = "")
                }

            }

            Box(modifier = Modifier
                .size(70.dp)
                .align(Alignment.CenterEnd)
                .padding(end = 5.dp)) {
                CustomProgressBar(progressMutiplyingFactor = 1F,"")
            }
        }
    }

}


@Composable
fun RowAccountItem(isDark:Boolean,name:String,issuer:String,key:String,icon:Int,viewModel: DashboardViewModel,context: Context,clipboard: ClipboardManager){
    var totpCountdownTask: TotpCountdownTask? = null
    var totpCounter: TotpCounter
    var otpProvider: OtpProvider
    val timeService = get<SavitDataStore>()
    val realName = if (name.contains(":")) name.split(":")[1] else name
    totpCounter = TotpCounter(DEFAULT_INTERVAL.toLong())
    totpCountdownTask = TotpCountdownTask(totpCounter,TOTP_COUNTDOWN_REFRESH_PERIOD_MILLIS)
    otpProvider = OtpProvider(key,timeService)
    var progress by remember {
        mutableStateOf(1F)
    }
    var pin by remember {
        mutableStateOf(otpProvider.getNextCode(key))
    }
    var secondsRemaining by remember {
        mutableStateOf("")
    }
    //val color =  if (progress <= 0.33) red else if (progress <= 0.66) lightblue else Green500

    val countDownListener = object : CountDownListener {
        override fun onTotpCountdown(millisRemaining: Long) {
            val progressPhase =
                millisRemaining.toDouble() / secondsToMillis(totpCounter.getTimeStep())
            secondsRemaining = millisToSeconds(millisRemaining).toString()
            progress = progressPhase.toFloat()
        }

        override fun onTotpCounterValueChanged() {
            Coroutines.main {
                try {
                    progress = 1F
                    pin = otpProvider.getNextCode(key)
                } catch (e: Exception) {
                    Log.e("Error Message", e.message.toString())
                    totpCountdownTask!!.stop()
                    totpCountdownTask!!.setListener(null)
                    totpCountdownTask!!.setListener(this)
                    totpCountdownTask!!.start()

                }
            }

        }
    }
    totpCountdownTask!!.setListener(countDownListener)
    totpCountdownTask!!.startAndNotifyListener()

    val lastcolor = if (isDark) myGreen else Green500

    val (showDeleteDialog,setShowDeleteDialog) = remember {
        mutableStateOf(false)
    }



    SavitAuthenticatorTheme() {
        Box(modifier = Modifier
            .padding(7.dp)
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = {
                        setShowDeleteDialog(true)
                    },
                    onTap = {
                        val clip: ClipData = ClipData.newPlainText("SP", pin)
                        clipboard.setPrimaryClip(clip)
                        Toast
                            .makeText(context, "Pin Copied", Toast.LENGTH_SHORT)
                            .show()
                    }
                )
            }
            .background(
                color = if (isDark) GreenTrans200 else UserAccountBg,
                shape = RoundedCornerShape(8)
            )) {
            Row(modifier = Modifier.wrapContentWidth(),verticalAlignment = Alignment.CenterVertically) {
                Image(painter = painterResource(id = icon), contentDescription = "",modifier = Modifier
                    .padding(top = 15.dp, bottom = 15.dp, start = 10.dp, end = 10.dp)
                    .size(48.dp))
                Column(verticalArrangement = Arrangement.Center,modifier = Modifier
                    .wrapContentSize()
                    .padding(top = 10.dp, bottom = 10.dp)) {
                    Text(text = issuer?:"-",fontWeight = FontWeight.Bold,fontSize = 14.sp)
                    Text(text = realName?:"-",fontWeight = FontWeight.SemiBold,fontSize = 14.sp,modifier = Modifier.padding(top = 6.dp))
                    Text(text = pin?:"",fontWeight = FontWeight.SemiBold,fontSize = 24.sp,modifier = Modifier.padding(top = 6.dp),color =  if (progress <= 0.33) red else if (progress <= 0.66) lightblue else lastcolor)
                }
            }

            Box(modifier = Modifier
                .size(70.dp)
                .align(Alignment.CenterEnd)
                .padding(end = 5.dp)) {
                CustomProgressBar(progressMutiplyingFactor = progress,secondsRemaining)
            }
        }
    }



    if (showDeleteDialog){
        AlertDialog(
            title = {
                Text(text = "Delete Account $realName",modifier = Modifier.fillMaxWidth(),textAlign = TextAlign.Center)
            },
            text = {
                Text(text = "You are about to delete account for $issuer:$realName",modifier = Modifier.fillMaxWidth(),textAlign = TextAlign.Center)
            },onDismissRequest = {
                setShowDeleteDialog(false)
            },
            properties = DialogProperties(true, dismissOnClickOutside = true),
            confirmButton = {
                TextButton(onClick = {
                    deleteAccount(key,viewModel)
                    setShowDeleteDialog(false)
                }) {
                    Text(text = "Delete",color = red)
                }
            },dismissButton = {
                TextButton(onClick = {
                    setShowDeleteDialog(false)
                }) {
                    Text(text = "Cancel",color = if (!isDark) Green500 else Green201)
                }
            },modifier = Modifier.padding(15.dp))
    }
}


@Composable
fun GridAccountItem(isDark:Boolean,name:String,issuer:String,key:String,icon:Int,viewModel: DashboardViewModel,context: Context,clipboard: ClipboardManager){
    var totpCountdownTask: TotpCountdownTask? = null
    val totpCounter: TotpCounter
    val otpProvider: OtpProvider
    val timeService = get<SavitDataStore>()
    val realName = if (name.contains(":")) name.split(":")[1] else name
    totpCounter = TotpCounter(DEFAULT_INTERVAL.toLong())
    totpCountdownTask = TotpCountdownTask(totpCounter,TOTP_COUNTDOWN_REFRESH_PERIOD_MILLIS)
    otpProvider = OtpProvider(key,timeService)


    var progress by remember {
        mutableStateOf(1F)
    }
    var pin by remember {
        mutableStateOf(otpProvider.getNextCode(key))
    }
    var secondsRemaining by remember {
        mutableStateOf("")
    }
    //val color =  if (progress <= 0.33) red else if (progress <= 0.66) lightblue else Green500

    val countDownListener = object : CountDownListener {
        override fun onTotpCountdown(millisRemaining: Long) {
            val progressPhase =
                millisRemaining.toDouble() / secondsToMillis(totpCounter.getTimeStep())
            secondsRemaining = millisToSeconds(millisRemaining).toString()
            progress = progressPhase.toFloat()

        }

        override fun onTotpCounterValueChanged() {
            Coroutines.main {
                try {
                    progress = 1F
                    pin = otpProvider.getNextCode(key)
                } catch (e: Exception) {
                    Log.e("Error Message", e.message.toString())
                    totpCountdownTask!!.stop()
                    totpCountdownTask!!.setListener(null)
                    totpCountdownTask!!.setListener(this)
                    totpCountdownTask!!.start()

                }
            }

        }
    }
    totpCountdownTask!!.setListener(countDownListener)
    totpCountdownTask!!.startAndNotifyListener()

    val lastcolor = if (isDark) myGreen else Green500

    val (showDeleteDialog,setShowDeleteDialog) = remember {
        mutableStateOf(false)
    }

    SavitAuthenticatorTheme() {
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(7.dp),horizontalAlignment = Alignment.CenterHorizontally) {
            Image(painter = painterResource(id = icon), contentDescription = "",modifier = Modifier
                .padding(top = 15.dp, bottom = 15.dp, start = 10.dp, end = 10.dp)
                .size(48.dp))

            Box(modifier = Modifier
                .background(
                    color = if (isDark) GreenTrans200 else UserAccountBg,
                    shape = RoundedCornerShape(8)
                )
                .pointerInput(Unit) {
                    detectTapGestures(
                        onLongPress = {
                            setShowDeleteDialog(true)
                        },
                        onTap = {
                            val clip: ClipData = ClipData.newPlainText("SP", pin)
                            clipboard.setPrimaryClip(clip)
                            Toast
                                .makeText(context, "Pin Copied", Toast.LENGTH_SHORT)
                                .show()
                        }
                    )
                }
                .wrapContentHeight())
            {
                Column(verticalArrangement = Arrangement.Center,horizontalAlignment = Alignment.CenterHorizontally,modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, bottom = 10.dp)) {
                    Text(text = issuer,fontWeight = FontWeight.Bold,fontSize = 14.sp,textAlign = TextAlign.Center,modifier = Modifier.align(
                        Alignment.CenterHorizontally))
                    Text(text = realName,fontWeight = FontWeight.SemiBold,fontSize = 14.sp,modifier = Modifier
                        .padding(top = 6.dp)
                        .align(
                            Alignment.CenterHorizontally
                        ),textAlign = TextAlign.Center)
                    Text(text =  pin?:"",fontWeight = FontWeight.SemiBold,fontSize = 24.sp,modifier = Modifier
                        .padding(top = 6.dp)
                        .align(
                            Alignment.CenterHorizontally
                        ),textAlign = TextAlign.Center,color =  if (progress <= 0.33) red else if (progress <= 0.66) lightblue else lastcolor)

                    Box(modifier = Modifier
                        .size(70.dp)
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 5.dp)) {
                        CustomProgressBar(progressMutiplyingFactor = progress,secondsRemaining)
                    }

                }
            }

        }
    }
    if (showDeleteDialog){

        AlertDialog(
            title = {
                Text(text = "Delete Account $realName",modifier = Modifier.fillMaxWidth(),textAlign = TextAlign.Center)
            },
            text = {
                Text(text = "You are about to delete account for $issuer:$realName",modifier = Modifier.fillMaxWidth(),textAlign = TextAlign.Center)
            },onDismissRequest = {

            },
            properties = DialogProperties(true, dismissOnClickOutside = true),
            confirmButton = {
                TextButton(onClick = {
                    deleteAccount(key,viewModel = viewModel)
                    setShowDeleteDialog(false)
                }) {
                    Text(text = "Delete",color = red)
                }
            },dismissButton = {
                TextButton(onClick = {
                    setShowDeleteDialog(false)
                }) {
                    Text(text = "Cancel",color = if (!isDark) Green500 else Green201)
                }
            },modifier = Modifier.padding(15.dp))
    }

}

@Preview(showBackground = true)
@Composable
fun GridAccountItemP(){
    val isDark = false
    SavitAuthenticatorTheme() {
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(7.dp),horizontalAlignment = Alignment.CenterHorizontally) {
            Image(painter = painterResource(id = R.drawable.slack), contentDescription = "",modifier = Modifier
                .padding(top = 15.dp, bottom = 15.dp, start = 10.dp, end = 10.dp)
                .size(48.dp))

            Box(modifier = Modifier
                .background(
                    color = if (isDark) GreenTrans200 else UserAccountBg,
                    shape = RoundedCornerShape(8)
                )
                .wrapContentHeight()) {
                Column(verticalArrangement = Arrangement.Center,horizontalAlignment = Alignment.CenterHorizontally,modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, bottom = 10.dp)) {
                    Text(text = "Slack",fontWeight = FontWeight.Bold,fontSize = 14.sp,textAlign = TextAlign.Center,modifier = Modifier.align(
                        Alignment.CenterHorizontally))
                    Text(text = "isaackakpo4@gmail.com",fontWeight = FontWeight.SemiBold,fontSize = 14.sp,modifier = Modifier
                        .padding(top = 6.dp)
                        .align(
                            Alignment.CenterHorizontally
                        ),textAlign = TextAlign.Center)
                    Text(text = "908765",fontWeight = FontWeight.Bold,fontSize = 18.sp,modifier = Modifier
                        .padding(top = 6.dp)
                        .align(
                            Alignment.CenterHorizontally
                        )
                        ,textAlign = TextAlign.Center)

                    Box(modifier = Modifier
                        .size(70.dp)
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 5.dp)) {
                        CustomProgressBar(progressMutiplyingFactor = 1F,"")
                    }
                }
            }
        }
    }
}

private fun secondsToMillis(timeSeconds: Long): Long {
    return timeSeconds * 1000
}

private fun millisToSeconds(timeinMillis: Long): Long {
    return timeinMillis / 1000
}