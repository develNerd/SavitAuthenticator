package org.savit.savitauthenticator.ui.getstarted

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.getViewModel
import org.savit.savitauthenticator.R
import org.savit.savitauthenticator.model.useraccounts.UserAccount
import org.savit.savitauthenticator.ui.dashboard.DashboardActivity
import org.savit.savitauthenticator.ui.genericviews.InputField
import org.savit.savitauthenticator.ui.genericviews.viewmodel.PinCameraViewmodel
import org.savit.savitauthenticator.ui.theme.textColorDark
import java.util.*

@Composable
fun EditScreen(){
    val context = LocalContext.current
    val viewModel = getViewModel<PinCameraViewmodel>()
    val name by viewModel.name.observeAsState("")
    val key by viewModel.key.observeAsState("")
    val issuer by viewModel.issuer.observeAsState("")
    fun validate():Boolean = name.trim().isNotEmpty() && key.trim().length >= 6

    Column(modifier = Modifier.fillMaxSize()) {


        InputField(inputFieldValue = name, inputFieldHint = "Name or Email*") {
            viewModel.setName(it)
        }
        Spacer(modifier = Modifier.size(5.dp))
        InputField(inputFieldValue = key, inputFieldHint = "Key (Enter key not less than 6 characters)") {
            viewModel.setKey(it)
        }
        Spacer(modifier = Modifier.size(5.dp))
        InputField(inputFieldValue = issuer, inputFieldHint = "Issuer") {
            viewModel.setIssuer(it)
        }

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
        Spacer(modifier = Modifier.size(10.dp))
        Button(
            onClick = {
                         viewModel.saveUserAccount(UserAccount(id = 0,key,image,issuer = issuer,name,code = null)).also {
                             context.startActivity(Intent(context, DashboardActivity::class.java))
                         }
        },modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),enabled = validate()) {
            Text(text = "Add Account",color = Color.White)
        }

    }

}