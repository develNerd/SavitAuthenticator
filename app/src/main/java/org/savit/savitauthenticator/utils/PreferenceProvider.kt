package org.savit.savitauthenticator.utils

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.security.KeyPairGeneratorSpec
import androidx.annotation.RequiresApi
import androidx.preference.PreferenceManager
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import java.math.BigInteger
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.util.*
import javax.security.auth.x500.X500Principal
import kotlin.math.abs


private const val KEY_SAVED_AT = "token"
private const val auth_uuid = "auth"
private const val user_json = "user"
private const val isrun = "isrun"
private const val isgenerate = "isgenerate"
private const val fingerprint = "fingerprint"
private const val isDashboard = "isDashboard"
private const val fingerprint2 = "fingerprint2"
private const val isDateChecked = "dateChecked"
private const val isGrid = "GRID"




class PreferenceProvider(
    context: Context
) {

    private val appContext = context.applicationContext

    var masterKeyAlias = createMasterKey(appContext)

    var preference = EncryptedSharedPreferences.create(
        "secret_shared_prefs",
        masterKeyAlias,
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )




    private fun createMasterKey(context: Context): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        } else {
            val alias = stringFromJNI()
            val start: Calendar = GregorianCalendar()
            val end: Calendar = GregorianCalendar()
            end.add(Calendar.YEAR, 30)

            val spec =
                KeyPairGeneratorSpec.Builder(context)
                    .setAlias(alias)
                    .setSubject(X500Principal("CN=$alias"))
                    .setSerialNumber(
                        BigInteger.valueOf(
                            abs(alias.hashCode()).toLong()
                        )
                    )
                    .setStartDate(start.time).setEndDate(end.time)
                    .build()

            val kpGenerator: KeyPairGenerator = KeyPairGenerator.getInstance(
                "RSA",
                "AndroidKeyStore"
            )
            kpGenerator.initialize(spec)
            val kp: KeyPair = kpGenerator.generateKeyPair()
            kp.public.toString()
        }
    }







    fun saveisGenerate(savedAt: Boolean?) {
        preference.edit().putBoolean(
            isgenerate,
            savedAt!!
        ).apply()
    }


    fun getIsDashboard(): Boolean {
        return preference.getBoolean(isDashboard, false)
    }

    fun saveIsDashboard(savedAt: Boolean) {
        preference.edit().putBoolean(
            isDashboard,
            savedAt
        ).apply()
    }

    fun getIsDateChecked(): Boolean {
        return preference.getBoolean(isDateChecked, false)
    }

    fun saveIsDateChecked(savedAt: Boolean) {
        preference.edit().putBoolean(
            isDateChecked,
            savedAt
        ).apply()
    }



    fun getIsGenerate(): Boolean? {
        return preference.getBoolean(isgenerate, false)
    }



    fun saveIsFingerprintEnabled(savedAt: Boolean?) {
        preference.edit().putBoolean(
            fingerprint,
            savedAt!!
        ).apply()
    }

    fun getFingerprint(): Boolean? {
        return preference.getBoolean(fingerprint, false)
    }




    fun savepasscode(savedAt: String) {
        preference.edit().putString(
            fingerprint2,
            savedAt
        ).apply()
    }

    fun passcode():CharArray?{
        val passcode = preference.getString(fingerprint2, null)
        return passcode?.toCharArray()
    }
    external fun stringFromJNI(): String


    companion object {
    init {
        System.loadLibrary("savitauthenticator")
     }
   }

}