package org.savit.savitauthenticator.model.useraccounts

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface UserAccountDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserUserData(vararg userAccount: UserAccount)

    @Update
    suspend fun updateUserAccountData(vararg userAccount: UserAccount)

    @Delete
    suspend  fun deleteuserAccount(vararg userAccount: UserAccount)

    @Query("SELECT * FROM useraccount WHERE id LIKE :userAccountId")
    suspend  fun loadUserAccountData(userAccountId:Int): UserAccount

    @Query("SELECT * FROM useraccount WHERE sharedKey LIKE :mySharedKey")
    suspend  fun loadUserAccount(mySharedKey:String): UserAccount

    @Query("DELETE FROM useraccount WHERE sharedKey LIKE :sharedKey")
    suspend  fun deleteAccount(sharedKey:String)



    @Query("SELECT * FROM useraccount")
    fun loadusers(): LiveData<List<UserAccount>>

}