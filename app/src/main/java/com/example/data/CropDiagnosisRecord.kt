package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "crop_diagnoses")
data class CropDiagnosisRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val cropName: String,
    val healthStatus: String, // "Healthy" or "Diseased"
    val diseaseName: String,
    val confidenceScore: Int, // e.g. 96 (%)
    val severityLevel: String, // "Healthy", "Low", "Medium", "High"
    val symptoms: String,
    val organicTreatment: String,
    val chemicalTreatment: String,
    val preventionSteps: String,
    val timestamp: Long = System.currentTimeMillis(),
    val imagePath: String? = null,
    val sampleResId: Int? = null,
    val notes: String? = null
)
