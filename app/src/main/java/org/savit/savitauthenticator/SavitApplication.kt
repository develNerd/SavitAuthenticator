package org.savit.savitauthenticator

import android.app.Application
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.savit.savitauthenticator.model.*
import org.savit.savitauthenticator.network.KtorClient
import org.savit.savitauthenticator.network.NetworkConnectionInterceptor
import org.savit.savitauthenticator.ui.dashboard.viewmodel.DashboardViewModel
import org.savit.savitauthenticator.ui.genericviews.viewmodel.PinCameraViewmodel
import org.savit.savitauthenticator.utils.PreferenceProvider
import org.savit.savitauthenticator.utils.SavitDataStore
import java.security.SecureRandom
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey


val appModule = module {
    single { PreferenceProvider(get()) }
    single <KeyService>{ KeyServiceImpl(get()) }
    single { EncryptedDatabase(get(),get()) }
    single { SavitDataStore(get()) }
    single <TimeService>{ TimeServiceImplementation(get()) }
    single { NetworkConnectionInterceptor(get()) }
    single { KtorClient(get()) }
    viewModel { DashboardViewModel(get(),get(),get(),get()) }
    viewModel { PinCameraViewmodel(get()) }

}

class SavitApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        val s = generateKey()

        val pref = PreferenceProvider(this)
        if (pref.getIsGenerate() == false){
            pref.savepasscode(s!!)
            pref.saveisGenerate(true)
        }
        // Start Koin
        startKoin{
            androidLogger()
            androidContext(this@SavitApplication)
            modules(appModule)
        }
    }

    private fun generateKey():String{
        var key: SecretKey
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            val builder = KeyGenParameterSpec.Builder(
                "key1",
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
            val keySpec = builder
                .setKeySize(256)
                .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                .setRandomizedEncryptionRequired(true)
                .setUserAuthenticationRequired(false)
                .build()

            val kg: KeyGenerator = KeyGenerator.getInstance("AES", "AndroidKeyStore")
            kg.init(keySpec)
            key = kg.generateKey()
        }
        else{
            val outputKeyLength = 256
            val secureRandom = SecureRandom()
            val keyGenerator = KeyGenerator.getInstance("AES")
            keyGenerator.init(outputKeyLength, secureRandom)
            key = keyGenerator.generateKey()
        }

        return key.toString()
    }

}