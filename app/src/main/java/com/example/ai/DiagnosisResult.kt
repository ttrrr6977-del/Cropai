package com.example.ai

data class DiagnosisResult(
    val cropName: String,
    val healthStatus: String, // "Healthy" or "Diseased"
    val diseaseName: String,
    val confidenceScore: Int,
    val severityLevel: String, // "Low", "Medium", "High", or "Healthy"
    val symptoms: String,
    val organicTreatment: String,
    val chemicalTreatment: String,
    val preventionSteps: String,
    val isAiGenerated: Boolean = true
)
