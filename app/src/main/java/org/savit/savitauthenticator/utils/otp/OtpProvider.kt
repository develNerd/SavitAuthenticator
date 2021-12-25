package org.savit.savitauthenticator.utils.otp

import android.util.Log
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.flow.collect
import org.savit.savitauthenticator.crypto.PasscodeGenerator
import org.savit.savitauthenticator.utils.Coroutines
import org.savit.savitauthenticator.utils.SavitDataStore
import org.savit.savitauthenticator.utils.TotpCounter
import java.io.UnsupportedEncodingException
import java.security.GeneralSecurityException
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

class OtpProvider : OtpSource {

    companion object{
        private const val PIN_LENGTH:Int = 6

        private const val REFLECTIVE_PIN_LENGTH:Int = 9

        private const val DEFAULT_INTERVAL:Int = 30

    }

    private var mTotpCounter: TotpCounter? = null
    private var mTimeCorrection = MutableLiveData<Int>()
    private var misTimeSync = MutableLiveData<Boolean>(false)

    constructor(sharedKey: String,savitDataStore: SavitDataStore): this(DEFAULT_INTERVAL,sharedKey,savitDataStore) {}

    constructor(interval: Int, sharedKey: String,savitDataStore:SavitDataStore) {
        mTotpCounter = TotpCounter(interval.toLong())
        Coroutines.main {
            savitDataStore.timeCorrectioninMinutes.collect {
                mTimeCorrection.value = it
            }
            savitDataStore.isTimeSync.collect {
                misTimeSync.value = it
            }
        }
    }


    @Throws(OtpSourceException::class)
    fun getCurrentCode(
        sharedKey: String?,
        challenge: ByteArray?
    ): String? {
        // Account is required.
        if (sharedKey == null) {
            throw OtpSourceException("No account")
        }

        val secret = getSecret(sharedKey)
        var otpState: Long = 0
            // For time-based OTP, the state is derived from clock.

        Log.d("correction Millis","${mTimeCorrection.value}")
        otpState = if (misTimeSync.value!!)
            mTotpCounter!!.getValueAtTime(millisToSeconds(minutesToMillis(mTimeCorrection.value!!?:0) + System.currentTimeMillis()))
        else
            mTotpCounter!!.getValueAtTime(millisToSeconds( System.currentTimeMillis()))


        return computePin(secret, otpState, challenge)
    }


    @Throws(OtpSourceException::class)
    private fun computePin(secret: String?, otpState: Long, challenge: ByteArray?): String? {
        if (secret == null || secret.isEmpty()) {
            throw OtpSourceException("Null or empty secret")
        }
        return try {
            val signer: PasscodeGenerator.Signer = getSigningOracle(secret)!!
            val pcg = PasscodeGenerator(
                signer,
                if (challenge == null) PIN_LENGTH else REFLECTIVE_PIN_LENGTH
            )
            if (challenge == null) pcg.generateResponseCode(otpState) else pcg.generateResponseCode(
                otpState,
                challenge
            )
        } catch (e: GeneralSecurityException) {
            throw OtpSourceException("Crypto failure", e)
        }
    }

    private fun getSigningOracle(secret: String): PasscodeGenerator.Signer? {
        try {
            val keyBytes = decodeKey(secret)
            val mac = Mac.getInstance("HMACSHA1")
            mac.init(SecretKeySpec(keyBytes, ""))

            // Create a signer object out of the standard Java MAC implementation.
            return object : PasscodeGenerator.Signer {
                override fun sign(data: ByteArray?): ByteArray? {
                    return mac.doFinal(data)
                }
            }
        } catch (error: Base32String.DecodingException) {
            Log.e("DECODE_TAG", error.message!!)
        } catch (error: NoSuchAlgorithmException) {
            Log.e("LOCAL_TAG", error.message!!)
        } catch (error: InvalidKeyException) {
            Log.e("LOCAL_TAG", error.message!!)
        } catch (error: IllegalArgumentException) {
            Log.e("LOCAL_TAG", error.message!!)
        }
        return null
    }

    @Throws(Base32String.DecodingException::class)
    private fun decodeKey(secret: String): ByteArray {
        return Base32String.decode(secret)
    }


    private  fun getSecret(sharedKey:String):String?{
        return try {
            sharedKey
        }catch (e:Exception){
            null
        }
    }

    override  fun getNextCode(sharedKey: String): String? {
       return getCurrentCode(sharedKey,null)
    }

    @Throws(OtpSourceException::class)
    override   fun respondToChallenge(sharedKey: String, challenge: String?): String? {
        return if (challenge == null) {
            getCurrentCode(sharedKey,null)
        } else try {
            val challengeBytes: ByteArray = challenge.toByteArray(charset("UTF-8"))
            getCurrentCode(sharedKey,challengeBytes)
        } catch (e: UnsupportedEncodingException) {
            ""
        }    }

    override fun getTotpCounter(): TotpCounter? {
        return mTotpCounter
    }

    fun millisToSeconds(timeMillis: Long): Long {
        return timeMillis / 1000
    }

    fun minutesToMillis(timeInMinutes: Int): Long {
        return (timeInMinutes * 60000).toLong()
    }


}