package org.savit.savitauthenticator.model

import org.savit.savitauthenticator.utils.PreferenceProvider

class KeyServiceImpl(private val preferenceProvider: PreferenceProvider) : KeyService {
    override fun getKey(): CharArray {
       return preferenceProvider.passcode()?:"".toCharArray()
    }
}