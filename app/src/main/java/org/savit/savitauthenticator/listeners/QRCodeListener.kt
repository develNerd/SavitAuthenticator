package org.savit.savitauthenticator.listeners

interface QRCodeListener {
    fun isSuccessful(message:String?)
    fun isFailed(message: String?)
}