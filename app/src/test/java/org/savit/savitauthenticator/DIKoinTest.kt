package org.savit.savitauthenticator

import org.junit.Rule
import org.junit.Test
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule
import org.koin.test.inject
import org.savit.savitauthenticator.model.KeyServiceImpl

class DIKoinTest : KoinTest {

    val service by  inject<KeyServiceImpl>()

    @get:Rule
    val koinTestRule = KoinTestRule.create {
        printLogger()
        modules(appModule)
    }

    @Test
    fun keyServiceTest(){

    }


}