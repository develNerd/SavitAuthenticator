package org.savit.savitauthenticator.test

import android.content.Context
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.Assert
import kotlinx.coroutines.flow.collect
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.savit.savitauthenticator.R
import org.savit.savitauthenticator.model.EncryptedDatabase
import org.savit.savitauthenticator.model.KeyService
import org.savit.savitauthenticator.model.KeyServiceImpl
import org.savit.savitauthenticator.model.useraccounts.UserAccount
import org.savit.savitauthenticator.network.KtorClient
import org.savit.savitauthenticator.network.NetworkConnectionInterceptor
import org.savit.savitauthenticator.ui.dashboard.viewmodel.DashboardViewModel
import org.savit.savitauthenticator.utils.Coroutines
import org.savit.savitauthenticator.utils.PreferenceProvider
import org.savit.savitauthenticator.utils.SavitDataStore
import java.io.IOException
import java.security.SecureRandom
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

class DashboardViewModelTest {
    /*
    private lateinit var encryptedDatabase: EncryptedDatabase
    private lateinit var viewModel: DashboardViewModel
    private lateinit var keyService: KeyService
    private lateinit var preferenceProvider: PreferenceProvider
    private lateinit var dataStore: SavitDataStore
    private lateinit var ktorClient: KtorClient
    private lateinit var networkConnectionInterceptor: NetworkConnectionInterceptor

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        preferenceProvider = PreferenceProvider(context = context)
        preferenceProvider.savepasscode(generateKey())
        keyService = KeyServiceImpl(preferenceProvider)
        encryptedDatabase = Room.inMemoryDatabaseBuilder(
            context,
            EncryptedDatabase::class.java).build()
        dataStore = SavitDataStore(context)
        networkConnectionInterceptor = NetworkConnectionInterceptor(context)
        ktorClient = KtorClient(networkConnectionInterceptor = networkConnectionInterceptor)
        viewModel = DashboardViewModel(encryptedDatabase,preferenceProvider,dataStore,ktorClient)
    }*/

  /*  @Test
    fun saveIsFingerPrint() {
        Coroutines.main {
            dataStore.saveIsFingerPrint(true)
            dataStore.isFingerPrint.collect {
                assertEquals(it,true)
            }

        }
    }

    @Test
    fun saveISGrid() {

        Coroutines.main {
            viewModel.setGrid(true)
            dataStore.isGrid.collect {
                assertEquals(it,true)
            }
        }
    }

    @Test
    fun setTimeSync(){
        Coroutines.main {
            dataStore.saveIsTimeSync(true)
            dataStore.isTimeSync.collect {
                assertEquals(it,true)
            }
        }
    }

   */


    /*

    @After
    @Throws(IOException::class)
    fun closeDb(){
        encryptedDatabase.close()
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
    } */

}