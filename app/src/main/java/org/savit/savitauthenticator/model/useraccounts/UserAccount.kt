package org.savit.savitauthenticator.model.useraccounts

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class UserAccount(
     val id: Int = 0,
     @PrimaryKey
     val sharedKey:String,
     val image:Int,
     val issuer:String?,
     val name:String?,
     val code:String?
    )
