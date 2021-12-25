package org.savit.savitauthenticator.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SavitDataStore(
    private val context: Context
) {
    val Context.datastore:DataStore<Preferences> by preferencesDataStore(name = "UserSettings")
    val ISGRID = booleanPreferencesKey("is_grid")
    val ISTIMESYNC =  booleanPreferencesKey("is_time_sync")
    val TIMECORRECTIONMINUTES = intPreferencesKey("time_correction_minutes")
    val ISFINGERPRINT =  booleanPreferencesKey("is_fingerprint")
    val ISHAVEFINGERPRINT =  booleanPreferencesKey("is_have_fingerprint")


    val timeCorrectioninMinutes:Flow<Int> = context.datastore.data
        .map { preferences ->
            preferences[TIMECORRECTIONMINUTES]?:0
        }

    suspend fun saveTimeCorrectionminute(value:Int){
        context.datastore.edit {
            it[TIMECORRECTIONMINUTES] = value
        }
    }

    val isTimeSync:Flow<Boolean> = context.datastore.data
        .map {preferences ->
            preferences[ISTIMESYNC]?:false
        }

    suspend fun saveIsTimeSync(value:Boolean){
        context.datastore.edit { mutablePrefrence ->
            mutablePrefrence[ISTIMESYNC] = value
        }
    }

    val isHaveFingerPrint:Flow<Boolean> = context.datastore.data
        .map {preferences ->
            preferences[ISHAVEFINGERPRINT]?:false
        }

    suspend fun saveIsHaveFingerPrint(value:Boolean){
        context.datastore.edit { mutablePrefrence ->
            mutablePrefrence[ISHAVEFINGERPRINT] = value
        }
    }

    val isFingerPrint:Flow<Boolean> = context.datastore.data
        .map {preferences ->
            preferences[ISFINGERPRINT]?:false
        }

    suspend fun saveIsFingerPrint(value:Boolean){
        context.datastore.edit { mutablePrefrence ->
            mutablePrefrence[ISFINGERPRINT] = value
        }
    }

    val isGrid:Flow<Boolean> = context.datastore.data
        .map {preferences ->
            preferences[ISGRID]?:false
        }

    suspend fun saveIsGrid(value:Boolean){
        context.datastore.edit { mutablePrefrence ->
            mutablePrefrence[ISGRID] = value
        }
    }

}