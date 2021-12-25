package org.savit.savitauthenticator.utils.otp

import org.savit.savitauthenticator.utils.TotpCounter


interface OtpSource {

    @Throws(OtpSourceException::class)
     fun getNextCode(sharedKey:String): String?

    @Throws(OtpSourceException::class)
      fun   respondToChallenge(sharedKey: String, challenge: String?): String?

    fun getTotpCounter(): TotpCounter?


}