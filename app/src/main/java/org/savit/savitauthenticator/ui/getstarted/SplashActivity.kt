package org.savit.savitauthenticator.ui.getstarted

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import org.koin.android.ext.android.inject
import org.savit.savitauthenticator.ui.dashboard.DashboardActivity
import org.savit.savitauthenticator.utils.PreferenceProvider

class SplashActivity : AppCompatActivity() {
    private val preferenceProvider by inject<PreferenceProvider>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (preferenceProvider.getIsDashboard()){
            startActivity(Intent(this,DashboardActivity::class.java))
            finish()
        }else{
            startActivity(Intent(this,GetStartedActivity::class.java))
            finish()
        }

    }
}