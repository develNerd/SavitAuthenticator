package org.savit.savitauthenticator.model

import kotlinx.coroutines.flow.collect
import org.savit.savitauthenticator.utils.Coroutines
import org.savit.savitauthenticator.utils.SavitDataStore

class TimeServiceImplementation(private val savitDataStore: SavitDataStore) : TimeService {
    override fun getCurrentTime(): Long {
        var timeToCorrect:Long? = null
        Coroutines.main {
            savitDataStore.isTimeSync.collect { isTimeSync->
                if (isTimeSync){
                    savitDataStore.timeCorrectioninMinutes.collect {
                        timeToCorrect =  minutesToMillis(it)
                    }
                }else{
                    timeToCorrect =  0
                }
            }

        }
        return if (timeToCorrect != null) timeToCorrect!! else minutesToMillis(-2)
    }

    fun minutesToMillis(timeInMinutes: Int): Long {
        return (timeInMinutes * 60000).toLong()
    }
}