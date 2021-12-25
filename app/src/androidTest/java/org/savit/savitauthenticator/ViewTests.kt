package org.savit.savitauthenticator

import androidx.compose.foundation.layout.Box
import androidx.compose.material.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import junit.framework.Assert.assertEquals
import junit.framework.Assert.fail
import org.junit.Rule
import org.junit.Test
import org.koin.androidx.compose.getViewModel
import org.savit.savitauthenticator.ui.genericviews.CustomSwitch
import org.savit.savitauthenticator.ui.genericviews.InputField
import org.savit.savitauthenticator.ui.genericviews.Screens
import org.savit.savitauthenticator.ui.genericviews.viewmodel.PinCameraViewmodel
import org.savit.savitauthenticator.ui.getstarted.EditScreen
import org.savit.savitauthenticator.ui.getstarted.MainQRScreen

class ViewTests {

    @get:Rule
    val composeTestRule = createComposeRule()
    lateinit var navController: NavHostController


    @Test
    fun inputFieldTest() {
        composeTestRule.setContent {
            Box() {
                InputField(inputFieldValue = "", inputFieldHint = "SampleTestText") {

                }
            }
        }
        composeTestRule.onNodeWithText("SampleTestText").assertIsDisplayed()
    }

    @Test
    fun customSwitchTest(){
        var isSC = false
        composeTestRule.setContent {
            val (ischecked,setSwitchChecked) = remember {
                mutableStateOf(false)
            }
            CustomSwitch(colorWhenDisabled = Color.White, isChecked = ischecked) {
                setSwitchChecked(it)
                isSC = it
            }
        }
        composeTestRule.onNode(hasClickAction()).performClick().assertExists()
        assertEquals(isSC,true
        )

    }



}