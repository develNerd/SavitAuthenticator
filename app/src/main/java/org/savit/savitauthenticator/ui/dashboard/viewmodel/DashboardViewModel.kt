package org.savit.savitauthenticator.ui.dashboard.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.savit.savitauthenticator.model.EncryptedDatabase
import org.savit.savitauthenticator.network.APIERROR
import org.savit.savitauthenticator.network.ApiException
import org.savit.savitauthenticator.network.KtorClient
import org.savit.savitauthenticator.network.NoInternetException
import org.savit.savitauthenticator.utils.PreferenceProvider
import org.savit.savitauthenticator.utils.SavitDataStore
import java.lang.NullPointerException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class DashboardViewModel(private val encryptedDatabase: EncryptedDatabase, private val preferenceProvider: PreferenceProvider, private val savitDataStore: SavitDataStore,private val ktorClient: KtorClient) : ViewModel() {

    val userAccounts = encryptedDatabase.getUSerAccountDao().loadusers()

    private val _isGrid = MutableLiveData<Boolean>()
    val isGrid: LiveData<Boolean>
        get() = _isGrid




    private val _isTimeSync = MutableLiveData<Boolean>()
    val isTimeSync: LiveData<Boolean>
        get() = _isTimeSync


    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private val _isError = MutableLiveData<String>("")
    val isError: LiveData<String>
        get() = _isError

    private val _timeCorrectionMinutes = MutableLiveData<Int>()
    val timeCorrectionMinutes: LiveData<Int>
        get() = _timeCorrectionMinutes

    private val _hasFingerPrint = MutableLiveData<Boolean>()
    val hasFingerPrint: LiveData<Boolean>
        get() = _hasFingerPrint

    private val _isFingerPrint = MutableLiveData<Boolean>()
    val isFingerPrint: LiveData<Boolean>
        get() = _isFingerPrint

    private val _isMainFingerPrint = MutableLiveData<Boolean>()
    val isMainFingerPrint: LiveData<Boolean>
        get() = _isMainFingerPrint

    fun getIFingerPrint(){
        viewModelScope.launch {
            savitDataStore.isHaveFingerPrint.collect {
                _hasFingerPrint.value = it
            }
        }
    }

    fun getFingerprint(){
        viewModelScope.launch {
            savitDataStore.isFingerPrint.collect {
                _isFingerPrint.value = it
            }
        }
    }

    fun getMainFingerprint(){
        var isGet = false
        viewModelScope.launch {
            savitDataStore.isFingerPrint.collect {
                if (!isGet){
                    isGet = true
                    _isMainFingerPrint.value = it
                }
            }
        }
    }


    fun saveIsFingerPrint(value: Boolean){
        viewModelScope.launch {
            savitDataStore.saveIsFingerPrint(value)
            _isFingerPrint.value = value
        }
    }

    fun getIsTimeSync(){
        viewModelScope.launch {
            savitDataStore.isTimeSync.collect {
                _isTimeSync.value = it
            }
        }
    }


    init {
        getMainFingerprint()
        getIFingerPrint()
        getFingerprint()
        getIsGrid()
        getIsTimeSync()
    }

    fun getIsGrid(){
        if (!preferenceProvider.getIsDashboard()) preferenceProvider.saveIsDashboard(true)
        viewModelScope.launch {
            savitDataStore.isGrid.collect {
                _isGrid.value = it
            }
        }
    }

    fun setGrid(value:Boolean){
        viewModelScope.launch {
            _isGrid.value = value
            savitDataStore.saveIsGrid(value)
        }

    }



    fun getTime(){
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val dateTimeStampResponse = ktorClient.getDateHeader()
                val dateFormmatter = SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z")
                val timeCorrectionMillis =  dateFormmatter.parse(dateTimeStampResponse.headers["Date"]!!)!!.time - Calendar.getInstance().timeInMillis
                    val timeInMinutes = (timeCorrectionMillis / 60000).toDouble().roundToInt()
                    //Log.d("Calculation","${ Calendar.getInstance().timeInMillis} - ${dateFormmatter.parse(dateTimeStampResponse.headers["Date"]).time} $timeInMinutes")
                    //Log.d("Google Date In minutes","Date $timeInMinutes")
                    if (timeInMinutes != 0){
                        savitDataStore.saveTimeCorrectionminute(timeInMinutes)
                        setisTimeSyncy(true)
                    }
                    _timeCorrectionMinutes.value = timeInMinutes
                    _isLoading.value = false
                _isError.value = ""

            }catch (e:NoInternetException){
                _isLoading.value = false
                _isError.value = "${e.message}"
            }catch (e:ApiException){
                _isLoading.value = false
                _isError.value = "Something went Wrong"
                Log.e("ApiException","${e.message}")
            }catch (e:APIERROR){
                _isLoading.value = false
                _isError.value = "Something went Wrong"
                Log.e("APIERROR","${e.message}")
            }catch (e:NullPointerException){
                _isLoading.value = false
                _isError.value = "Something went Wrong"
                Log.e("APIERROR","${e.message}")
            }
        }
    }

    private val _showDeleteSnackbar = MutableLiveData<Boolean>()
    val showDeleteSnackbar: LiveData<Boolean>
        get() = _showDeleteSnackbar

    fun setShowDeleteSnackBar(value: Boolean){
        _showDeleteSnackbar.value = value
    }

    fun deleteAccount(sharedKey:String){
        viewModelScope.launch {
            encryptedDatabase.getUSerAccountDao().deleteAccount(sharedKey = sharedKey)
        }
    }

    fun resetTimeSync(){
        viewModelScope.launch {
            _isTimeSync.value = false
            savitDataStore.saveTimeCorrectionminute(0)
            savitDataStore.saveIsTimeSync(false)
        }
    }

    fun setisTimeSyncy(value: Boolean){
        viewModelScope.launch {
            _isTimeSync.value = value
            savitDataStore.saveIsTimeSync(value)
        }
    }



}