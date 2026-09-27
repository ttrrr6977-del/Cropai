package com.example.weather

import com.example.localization.AppLanguage

data class FarmWeatherState(
    val location: String = "Farmland Sector #4",
    val temperatureC: Int = 28,
    val humidityPercent: Int = 84,
    val rainfallChancePercent: Int = 65,
    val windKmh: Int = 12,
    val weatherCondition: String = "Humid & Overcast",
    val riskLevel: RiskLevel = RiskLevel.HIGH,
    val primaryThreat: String = "Late Blight & Powdery Mildew",
    val alertAdvice: String = "Relative humidity > 80% creates critical conditions for fungal spore germination within 6 hours. Delay foliar spray until morning dew dries."
)

enum class RiskLevel {
    LOW,
    MODERATE,
    HIGH,
    CRITICAL
}

object WeatherRiskEngine {

    fun calculateRisk(humidity: Int, temp: Int, language: AppLanguage): FarmWeatherState {
        val (level, threatKey, adviceKey) = when {
            humidity >= 80 && temp in 18..32 -> Triple(
                RiskLevel.HIGH,
                "threat_fungal_high",
                "advice_fungal_high"
            )
            humidity >= 65 -> Triple(
                RiskLevel.MODERATE,
                "threat_spore_moderate",
                "advice_spore_moderate"
            )
            else -> Triple(
                RiskLevel.LOW,
                "threat_low",
                "advice_low"
            )
        }

        val threat = getLocalizedText(threatKey, language)
        val advice = getLocalizedText(adviceKey, language)

        return FarmWeatherState(
            location = getLocalizedText("field_station", language),
            temperatureC = temp,
            humidityPercent = humidity,
            rainfallChancePercent = if (humidity > 75) 70 else 20,
            windKmh = 14,
            weatherCondition = if (humidity > 75) getLocalizedText("cond_humid", language) else getLocalizedText("cond_clear", language),
            riskLevel = level,
            primaryThreat = threat,
            alertAdvice = advice
        )
    }

    private fun getLocalizedText(key: String, language: AppLanguage): String {
        return when (language) {
            AppLanguage.SPANISH -> when (key) {
                "field_station" -> "Estación Agronómica Norte"
                "cond_humid" -> "Húmedo y Nublado"
                "cond_clear" -> "Cálido y Despejado"
                "threat_fungal_high" -> "Alto Riesgo de Tizón Tardío y Mildiu"
                "advice_fungal_high" -> "La humedad superior al 80% activa la germinación de esporas fúngicas en 4 a 6 horas. Se recomienda aplicar Bacillus subtilis o Trichoderma preventivo."
                "threat_spore_moderate" -> "Riesgo Moderado de Roya y Manchas Foliares"
                "advice_spore_moderate" -> "Monitorear el envés de las hojas y asegurar buena ventilación entre surcos."
                "threat_low" -> "Condiciones Óptimas de Baja Infección"
                "advice_low" -> "Ambiente seco y ventilado. Buen momento para labores de poda y deshierbe."
                else -> key
            }
            AppLanguage.HINDI -> when (key) {
                "field_station" -> "खेत मौसम केंद्र #1"
                "cond_humid" -> "आर्द्र एवं बादल युक्त"
                "cond_clear" -> "धूप और साफ मौसम"
                "threat_fungal_high" -> "झुलसा और फफूंद रोग का उच्च खतरा"
                "advice_fungal_high" -> "80% से अधिक नमी फफूंद के बीजाणुओं को तेजी से बढ़ाती है। तुरंत नीम तेल या ट्राइकोडर्मा का छिड़काव करें और पानी निकासी सुधारें।"
                "threat_spore_moderate" -> "मध्यम जोखिम: पत्ती धब्बा रोग"
                "advice_spore_moderate" -> "पत्तियों के निचले हिस्से की जांच करें और पौधों के बीच हवा का प्रवाह बनाए रखें।"
                "threat_low" -> "रोग का कम जोखिम: स्वस्थ स्थिति"
                "advice_low" -> "सूखा और अनुकूल मौसम। निराई-गुड़ाई और पोषण देने के लिए उपयुक्त समय।"
                else -> key
            }
            AppLanguage.BENGALI -> when (key) {
                "field_station" -> "কৃষি আবহাওয়া কেন্দ্র"
                "cond_humid" -> "আর্দ্র ও মেঘলা"
                "cond_clear" -> "রৌদ্রোজ্জ্বল ও পরিষ্কার"
                "threat_fungal_high" -> "ব্লাইট ও ছত্রাকের উচ্চ ঝুঁকি"
                "advice_fungal_high" -> "বাতাসে আর্দ্রতা ৮০% এর বেশি হলে ছত্রাকের আক্রমণ দ্রুত বাড়ে। ট্রাইকোডার্মা স্প্রে করুন এবং গাছের গোড়ায় জল নিষ্কাশন নিশ্চিত করুন।"
                "threat_spore_moderate" -> "মাঝারি ঝুঁকি: পাতার দাগ ও মরিচা"
                "advice_spore_moderate" -> "পাতার নিচের অংশ পর্যবেক্ষণ করুন এবং ফসলের সারির মাঝে দূরত্ব বজায় রাখুন।"
                "threat_low" -> "স্বাভাবিক অনুকূল পরিবেশ"
                "advice_low" -> "শুষ্ক আবহাওয়া। সার প্রয়োগ ও আগাছা পরিষ্কারের উপযুক্ত সময়।"
                else -> key
            }
            else -> when (key) {
                "field_station" -> "Field Weather Station #4"
                "cond_humid" -> "Humid & Overcast"
                "cond_clear" -> "Clear & Warm"
                "threat_fungal_high" -> "High Risk: Blight & Powdery Mildew Spores"
                "advice_fungal_high" -> "Relative humidity > 80% triggers rapid fungal spore germination within 4-6 hours. Apply preventive organic bio-fungicide (Trichoderma or Neem formulation) and ensure furrow drainage."
                "threat_spore_moderate" -> "Moderate Risk: Cercospora & Leaf Spot"
                "advice_spore_moderate" -> "Inspect undersides of leaves during morning scouting. Maintain row airflow."
                "threat_low" -> "Low Pathogen Pressure"
                "advice_low" -> "Dry conditions limit fungal spore dispersal. Optimal time for weeding and foliar fertilizing."
                else -> key
            }
        }
    }
}
