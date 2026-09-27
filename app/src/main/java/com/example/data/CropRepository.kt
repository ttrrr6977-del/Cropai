package com.example.data

import com.example.R
import kotlinx.coroutines.flow.Flow

class CropRepository(private val cropDao: CropDao) {

    val allDiagnoses: Flow<List<CropDiagnosisRecord>> = cropDao.getAllDiagnoses()

    fun searchDiagnoses(query: String): Flow<List<CropDiagnosisRecord>> {
        return cropDao.searchDiagnoses(query)
    }

    suspend fun insertDiagnosis(record: CropDiagnosisRecord): Long {
        return cropDao.insertDiagnosis(record)
    }

    suspend fun deleteDiagnosis(id: Long) {
        cropDao.deleteDiagnosis(id)
    }

    suspend fun clearAll() {
        cropDao.clearAllDiagnoses()
    }

    suspend fun seedInitialDataIfEmpty() {
        // Pre-populate with realistic agritech diagnostic history
        cropDao.insertDiagnosis(
            CropDiagnosisRecord(
                cropName = "Tomato (Solanum lycopersicum)",
                healthStatus = "Diseased",
                diseaseName = "Early Blight (Alternaria solani)",
                confidenceScore = 95,
                severityLevel = "High",
                symptoms = "Circular target-like dark brown lesions with concentric rings on lower leaves, surrounded by yellow chlorotic margins.",
                organicTreatment = "Apply 2% cold-pressed neem oil mixed with potassium bicarbonate (3g/L). Treat root zone with Trichoderma harzianum bio-fungicide.",
                chemicalTreatment = "Spray Mancozeb 75% WP (2.5g/L) or Copper Oxychloride 50% WP.",
                preventionSteps = "Practice 3-year crop rotation away from solanaceous crops. Prune lower foliage to prevent soil splash.",
                sampleResId = R.drawable.sample_tomato_leaf_1790444686321,
                notes = "Field North Plot B - morning scout"
            )
        )
        cropDao.insertDiagnosis(
            CropDiagnosisRecord(
                cropName = "Corn / Maize (Zea mays)",
                healthStatus = "Healthy",
                diseaseName = "Healthy Leaf Tissue",
                confidenceScore = 98,
                severityLevel = "Healthy",
                symptoms = "Uniform vibrant chlorophyll green coloration, crisp venation, zero necrotic spots or chlorosis.",
                organicTreatment = "Maintain regular vermicompost tea foliar drench every 14 days.",
                chemicalTreatment = "No chemical intervention necessary.",
                preventionSteps = "Ensure adequate spacing (60cm x 20cm) and balanced nitrogen fertilization.",
                sampleResId = R.drawable.sample_healthy_leaf_1790444703161,
                notes = "Field South Plot A - weekly health check"
            )
        )
    }
}
