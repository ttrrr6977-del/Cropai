package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CropDao {
    @Query("SELECT * FROM crop_diagnoses ORDER BY timestamp DESC")
    fun getAllDiagnoses(): Flow<List<CropDiagnosisRecord>>

    @Query("SELECT * FROM crop_diagnoses WHERE id = :id LIMIT 1")
    suspend fun getDiagnosisById(id: Long): CropDiagnosisRecord?

    @Query("SELECT * FROM crop_diagnoses WHERE cropName LIKE '%' || :query || '%' OR diseaseName LIKE '%' || :query || '%' ORDER BY timestamp DESC")
    fun searchDiagnoses(query: String): Flow<List<CropDiagnosisRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDiagnosis(record: CropDiagnosisRecord): Long

    @Query("DELETE FROM crop_diagnoses WHERE id = :id")
    suspend fun deleteDiagnosis(id: Long)

    @Query("DELETE FROM crop_diagnoses")
    suspend fun clearAllDiagnoses()
}
