package org.savit.savitauthenticator.test

import android.content.Context
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.annotation.VisibleForTesting
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import junit.framework.Assert.assertEquals
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.savit.savitauthenticator.R
import org.savit.savitauthenticator.model.EncryptedDatabase
import org.savit.savitauthenticator.model.KeyService
import org.savit.savitauthenticator.model.KeyServiceImpl
import org.savit.savitauthenticator.model.useraccounts.UserAccount
import org.savit.savitauthenticator.ui.genericviews.viewmodel.PinCameraViewmodel
import org.savit.savitauthenticator.utils.Coroutines
import org.savit.savitauthenticator.utils.PreferenceProvider
import java.io.IOException
import java.security.SecureRandom
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

@RunWith(AndroidJUnit4::class)
class PinCameraViewmodelTest {

    private lateinit var encryptedDatabase: EncryptedDatabase
    private lateinit var viewModel: PinCameraViewmodel
    private lateinit var keyService: KeyService
    private lateinit var preferenceProvider:PreferenceProvider




    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        preferenceProvider = PreferenceProvider(context = context)
        preferenceProvider.savepasscode(generateKey())
        keyService = KeyServiceImpl(preferenceProvider)
        encryptedDatabase = Room.inMemoryDatabaseBuilder(
            context,
            EncryptedDatabase::class.java).build()
        viewModel = PinCameraViewmodel(encryptedDatabase)
    }



    @Test
    fun saveUserAccount() {
        Coroutines.main {
            encryptedDatabase.getUSerAccountDao().inserUserData(UserAccount(id = 0,"ERER343DDSE3VFVL",R.drawable.bitbucket,"Bitbucker","isaacakakpo4@gmail.com",null))
            val userAccount = encryptedDatabase.getUSerAccountDao().loadUserAccount("ERER343DDSE3VFVL")
            if (userAccount != null)  assertEquals(userAccount.issuer == "Bitbucker",true)

        }
    }


    @Test
    fun saveUserAccountViewModel() {
        Coroutines.main {
            viewModel.saveUserAccount(UserAccount(id = 0,"ERER343DDSE3VFVLVIEWMODEL",R.drawable.bitbucket,"Bitbucker","isaacakakpo4@gmail.com",null))
            val userAccount = encryptedDatabase.getUSerAccountDao().loadUserAccount("ERER343DDSE3VFVLVIEWMODEL")
            if (userAccount != null){
                assertEquals(userAccount.name == "isaacakakpo4@gmail.com",true)
                assertEquals(userAccount.issuer == "Bitbucker",true)
            }

        }
    }

    @Test
    fun deleteAccount() {
        Coroutines.main {
            viewModel.saveUserAccount(UserAccount(id = 0,"ERER343DDSE3VFVLVIEWMODEL",R.drawable.bitbucket,"Bitbucker","isaacakakpo4@gmail.com",null))
            val userAccount = encryptedDatabase.getUSerAccountDao().loadUserAccount("ERER343DDSE3VFVLVIEWMODEL")
            if (userAccount != null){
                assertEquals(userAccount.name == "isaacakakpo4@gmail.com",true)
                assertEquals(userAccount.issuer == "Bitbucker",true)
                encryptedDatabase.getUSerAccountDao().deleteAccount("ERER343DDSE3VFVLVIEWMODEL")
                val userAccount2 = encryptedDatabase.getUSerAccountDao().loadUserAccount("ERER343DDSE3VFVLVIEWMODEL")
                assertEquals(userAccount2 == null,true)
            }


        }
    }

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
    }

}