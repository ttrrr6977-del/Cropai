package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.ai.GeminiVisionService
import com.example.localization.AppLanguage
import com.example.localization.AppStrings
import com.example.weather.RiskLevel
import com.example.weather.WeatherRiskEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("CropShield AI", appName)
  }

  @Test
  fun `weather risk engine high humidity generates high risk`() {
    val state = WeatherRiskEngine.calculateRisk(85, 28, AppLanguage.ENGLISH)
    assertEquals(RiskLevel.HIGH, state.riskLevel)
    assertTrue(state.primaryThreat.contains("Blight") || state.primaryThreat.contains("Mildew"))
  }

  @Test
  fun `weather risk engine low humidity generates low risk`() {
    val state = WeatherRiskEngine.calculateRisk(45, 28, AppLanguage.ENGLISH)
    assertEquals(RiskLevel.LOW, state.riskLevel)
  }

  @Test
  fun `multilingual strings support all four languages`() {
    for (lang in AppLanguage.entries) {
      val title = AppStrings.get("app_title", lang)
      val scanTab = AppStrings.get("tab_scanner", lang)
      assertTrue(title.isNotBlank())
      assertTrue(scanTab.isNotBlank())
    }
  }

  @Test
  fun `agronomic fallback generates diagnostic result for tomato and corn`() {
    val service = GeminiVisionService()
    val tomatoDiag = service.getAgronomicFallback("tomato", AppLanguage.ENGLISH)
    assertEquals("Diseased", tomatoDiag.healthStatus)
    assertEquals("High", tomatoDiag.severityLevel)
    assertTrue(tomatoDiag.organicTreatment.isNotBlank())

    val cornDiag = service.getAgronomicFallback("corn", AppLanguage.ENGLISH)
    assertEquals("Healthy", cornDiag.healthStatus)
    assertEquals("Healthy", cornDiag.severityLevel)
    assertTrue(cornDiag.organicTreatment.isNotBlank())
  }
}
