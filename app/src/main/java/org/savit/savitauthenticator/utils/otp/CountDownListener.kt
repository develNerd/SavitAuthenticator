package org.savit.savitauthenticator.utils.otp

interface CountDownListener {

    fun onTotpCountdown(millisRemaining: Long)

    /** Invoked when the TOTP counter changes its value.  */
    fun onTotpCounterValueChanged()
}