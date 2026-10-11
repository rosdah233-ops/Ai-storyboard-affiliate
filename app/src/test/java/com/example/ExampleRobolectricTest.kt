package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.viewmodel.StoryboardViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read app name and verify default storyboard state`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Ai Storyboard Affiliate", appName)

        val viewModel = StoryboardViewModel()
        val state = viewModel.uiState.value
        assertEquals(3, state.variationCount)
        assertEquals(5, state.productSlots.size)

        viewModel.setVariationCount(5)
        assertEquals(5, viewModel.uiState.value.variationCount)

        viewModel.generateStoryboards()
        assertNotNull(viewModel.uiState.value.globalErrorMessage)
    }
}
