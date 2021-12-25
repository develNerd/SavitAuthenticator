package org.savit.savitauthenticator.ui.genericviews.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.savit.savitauthenticator.model.EncryptedDatabase
import org.savit.savitauthenticator.model.useraccounts.UserAccount

class PinCameraViewmodel(private val encryptedDatabase: EncryptedDatabase) : ViewModel() {

    val userAccounts = encryptedDatabase.getUSerAccountDao().loadusers()


    private val _name = MutableLiveData<String>()
    val name: LiveData<String>
        get() = _name

    private val _key = MutableLiveData<String>()
    val key: LiveData<String>
        get() = _key


    private val _issuer = MutableLiveData<String>()
    val issuer: MutableLiveData<String>
        get() = _issuer

    fun setIssuer(value:String){
        _issuer.value = value
    }

    fun setName(value: String){
        _name.value = value
    }

    fun setKey(value: String){
        _key.value = value
    }


    private val _isGranted = MutableLiveData<Boolean>()
    val isGranted: MutableLiveData<Boolean>
        get() = _isGranted

    fun grantPermission(){
        _isGranted.value = true
    }

    fun disAblePermission(){
        _isGranted.value = false
    }

    fun saveUserAccount(userAccount:UserAccount){
        viewModelScope.launch {
            encryptedDatabase.getUSerAccountDao().inserUserData(userAccount)
        }
    }

    /*fun saveUserAccount(password:String,context: Context,userAccount: UserAccount){
        val encryptedDatabase = EncryptedDatabase(password.toCharArray(),context)
        viewModelScope.launch {
            encryptedDatabase.getUSerAccountDao().inserUserData(userAccount)
        }

    } */
}