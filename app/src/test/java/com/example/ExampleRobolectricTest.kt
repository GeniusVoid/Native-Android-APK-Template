package com.example

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.core.app.ApplicationProvider
import com.example.data.InMemoryTemplateRepository
import com.example.viewmodel.TemplateViewModel
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36], qualifiers = "w411dp-h891dp")
class ExampleRobolectricTest {

  @get:Rule
  val composeTestRule = createComposeRule()

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Blueprint", appName)
  }

  @Test
  fun `counter increments and navigation tabs switch cleanly`() {
    val viewModel = TemplateViewModel(InMemoryTemplateRepository())
    composeTestRule.setContent {
      BlueprintApp(viewModel = viewModel)
    }

    composeTestRule.onNodeWithTag("overview_screen").assertIsDisplayed()
    composeTestRule
      .onNodeWithTag("counter_increment_button")
      .performScrollTo()
      .performClick()
    composeTestRule.waitForIdle()
    composeTestRule
      .onNodeWithTag("counter_value_text", useUnmergedTree = true)
      .assertTextEquals("1")

    composeTestRule.onNodeWithTag("nav_tab_components").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("components_screen").assertIsDisplayed()

    composeTestRule.onNodeWithTag("nav_tab_architecture").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("architecture_screen").assertIsDisplayed()

    composeTestRule.onNodeWithTag("nav_tab_settings").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("settings_screen").assertIsDisplayed()
  }
}
