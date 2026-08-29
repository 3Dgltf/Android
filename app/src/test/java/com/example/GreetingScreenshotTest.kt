package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.data.local.CalculationEntity
import com.example.data.repository.HistoryRepository
import com.example.ui.CalculatorApp
import com.example.ui.viewmodel.CalculatorViewModel
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class CalculatorScreenshotTest {

    @get:Rule val composeTestRule = createComposeRule()

    @Test
    fun calculator_screenshot() {
        val fakeDao = object : com.example.data.local.HistoryDao {
            override fun getAllHistory(): Flow<List<CalculationEntity>> = flowOf(emptyList())
            override fun getFavoriteHistory(): Flow<List<CalculationEntity>> = flowOf(emptyList())
            override fun searchHistory(query: String): Flow<List<CalculationEntity>> = flowOf(emptyList())
            override suspend fun insertCalculation(entity: CalculationEntity): Long = 1L
            override suspend fun updateCalculation(entity: CalculationEntity) {}
            override suspend fun deleteCalculation(entity: CalculationEntity) {}
            override suspend fun deleteById(id: Long) {}
            override suspend fun clearAllHistory() {}
            override suspend fun clearNonFavorites() {}
        }
        val repository = HistoryRepository(fakeDao)
        val viewModel = CalculatorViewModel(repository)

        composeTestRule.setContent {
            CalculatorApp(viewModel = viewModel)
        }

        composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/calculator.png")
    }
}
