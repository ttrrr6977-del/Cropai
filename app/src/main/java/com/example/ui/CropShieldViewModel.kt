package com.example.ui

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.R
import com.example.ai.DiagnosisResult
import com.example.ai.GeminiVisionService
import com.example.data.CropDatabase
import com.example.data.CropDiagnosisRecord
import com.example.data.CropRepository
import com.example.localization.AppLanguage
import com.example.localization.AppStrings
import com.example.tts.TextToSpeechHelper
import com.example.weather.FarmWeatherState
import com.example.weather.WeatherRiskEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

enum class CropNavTab {
    SCANNER,
    WEATHER,
    HISTORY,
    GUIDE
}

class CropShieldViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: CropRepository
    private val visionService = GeminiVisionService()
    private val ttsHelper = TextToSpeechHelper(application)

    // Language & Accessibility
    private val _selectedLanguage = MutableStateFlow(AppLanguage.ENGLISH)
    val selectedLanguage: StateFlow<AppLanguage> = _selectedLanguage.asStateFlow()

    private val _isOutdoorHighContrast = MutableStateFlow(false)
    val isOutdoorHighContrast: StateFlow<Boolean> = _isOutdoorHighContrast.asStateFlow()

    private val _activeTab = MutableStateFlow(CropNavTab.SCANNER)
    val activeTab: StateFlow<CropNavTab> = _activeTab.asStateFlow()

    // Scanner State
    private val _selectedBitmap = MutableStateFlow<Bitmap?>(null)
    val selectedBitmap: StateFlow<Bitmap?> = _selectedBitmap.asStateFlow()

    private val _selectedImageUri = MutableStateFlow<Uri?>(null)
    val selectedImageUri: StateFlow<Uri?> = _selectedImageUri.asStateFlow()

    private val _sampleResId = MutableStateFlow<Int?>(null)
    val sampleResId: StateFlow<Int?> = _sampleResId.asStateFlow()

    private val _sampleHint = MutableStateFlow<String?>("tomato")
    val sampleHint: StateFlow<String?> = _sampleHint.asStateFlow()

    private val _isAnalyzing = MutableStateFlow(false)
    val isAnalyzing: StateFlow<Boolean> = _isAnalyzing.asStateFlow()

    private val _activeDiagnosis = MutableStateFlow<DiagnosisResult?>(null)
    val activeDiagnosis: StateFlow<DiagnosisResult?> = _activeDiagnosis.asStateFlow()

    private val _isSaved = MutableStateFlow(false)
    val isSaved: StateFlow<Boolean> = _isSaved.asStateFlow()

    // Weather Risk
    private val _simulatedHumidity = MutableStateFlow(84)
    val simulatedHumidity: StateFlow<Int> = _simulatedHumidity.asStateFlow()

    private val _weatherState = MutableStateFlow(
        WeatherRiskEngine.calculateRisk(84, 28, AppLanguage.ENGLISH)
    )
    val weatherState: StateFlow<FarmWeatherState> = _weatherState.asStateFlow()

    // History & Search Filter
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _filterStatus = MutableStateFlow("ALL")
    val filterStatus: StateFlow<String> = _filterStatus.asStateFlow()

    private val _selectedDetailRecord = MutableStateFlow<CropDiagnosisRecord?>(null)
    val selectedDetailRecord: StateFlow<CropDiagnosisRecord?> = _selectedDetailRecord.asStateFlow()

    // Connectivity
    private val _isOnline = MutableStateFlow(checkNetworkConnectivity())
    val isOnline: StateFlow<Boolean> = _isOnline.asStateFlow()

    val isSpeaking: StateFlow<Boolean> = ttsHelper.isSpeaking

    // Room Database Records
    private val _allDiagnosesFlow = MutableStateFlow<List<CropDiagnosisRecord>>(emptyList())
    val filteredDiagnoses: StateFlow<List<CropDiagnosisRecord>>

    init {
        val database = CropDatabase.getDatabase(application)
        repository = CropRepository(database.cropDao())

        viewModelScope.launch {
            repository.allDiagnoses.collect { list ->
                if (list.isEmpty()) {
                    repository.seedInitialDataIfEmpty()
                } else {
                    _allDiagnosesFlow.value = list
                }
            }
        }

        // Initialize with default sample image (Tomato Blight) for instant demonstration
        loadSamplePreset("tomato")

        filteredDiagnoses = combine(
            _allDiagnosesFlow,
            _searchQuery,
            _filterStatus
        ) { records, query, filter ->
            records.filter { record ->
                val matchesQuery = query.isBlank() ||
                        record.cropName.contains(query, ignoreCase = true) ||
                        record.diseaseName.contains(query, ignoreCase = true)

                val matchesFilter = when (filter) {
                    "HEALTHY" -> record.healthStatus.equals("Healthy", ignoreCase = true)
                    "DISEASED" -> record.healthStatus.equals("Diseased", ignoreCase = true)
                    else -> true
                }
                matchesQuery && matchesFilter
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    }

    fun setLanguage(language: AppLanguage) {
        _selectedLanguage.value = language
        _weatherState.value = WeatherRiskEngine.calculateRisk(
            _simulatedHumidity.value,
            28,
            language
        )
        // If there's an active diagnosis from fallback, re-translate it
        val current = _activeDiagnosis.value
        if (current != null && !current.isAiGenerated) {
            _activeDiagnosis.value = visionService.getAgronomicFallback(_sampleHint.value, language)
        }
    }

    fun toggleOutdoorHighContrast() {
        _isOutdoorHighContrast.value = !_isOutdoorHighContrast.value
    }

    fun setActiveTab(tab: CropNavTab) {
        _activeTab.value = tab
    }

    fun setSimulatedHumidity(humidity: Int) {
        _simulatedHumidity.value = humidity
        _weatherState.value = WeatherRiskEngine.calculateRisk(
            humidity,
            28,
            _selectedLanguage.value
        )
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setFilterStatus(status: String) {
        _filterStatus.value = status
    }

    fun setSelectedDetailRecord(record: CropDiagnosisRecord?) {
        _selectedDetailRecord.value = record
    }

    fun loadSamplePreset(preset: String) {
        _sampleHint.value = preset
        _selectedImageUri.value = null
        _isSaved.value = false
        val context = getApplication<Application>()

        val (resId, sampleHintName) = when (preset) {
            "corn", "healthy" -> Pair(R.drawable.sample_healthy_leaf_1790444703161, "corn")
            else -> Pair(R.drawable.sample_tomato_leaf_1790444686321, "tomato")
        }

        _sampleResId.value = resId
        _sampleHint.value = sampleHintName

        val bmp = BitmapFactory.decodeResource(context.resources, resId)
        _selectedBitmap.value = bmp
        _activeDiagnosis.value = null
    }

    fun onImageSelected(uri: Uri) {
        _selectedImageUri.value = uri
        _sampleResId.value = null
        _sampleHint.value = null
        _isSaved.value = false
        _activeDiagnosis.value = null

        val context = getApplication<Application>()
        try {
            context.contentResolver.openInputStream(uri)?.use { stream ->
                val bitmap = BitmapFactory.decodeStream(stream)
                _selectedBitmap.value = bitmap
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun onBitmapCaptured(bitmap: Bitmap) {
        _selectedBitmap.value = bitmap
        _selectedImageUri.value = null
        _sampleResId.value = null
        _sampleHint.value = null
        _isSaved.value = false
        _activeDiagnosis.value = null
    }

    fun clearActiveScan() {
        _selectedBitmap.value = null
        _selectedImageUri.value = null
        _sampleResId.value = null
        _sampleHint.value = null
        _activeDiagnosis.value = null
        _isSaved.value = false
        ttsHelper.stop()
    }

    fun diagnoseCrop() {
        val bitmap = _selectedBitmap.value
        if (bitmap == null && _sampleResId.value == null) return

        _isAnalyzing.value = true
        _isSaved.value = false

        viewModelScope.launch {
            _isOnline.value = checkNetworkConnectivity()
            val result = visionService.analyzePlantLeaf(
                bitmap = bitmap,
                sampleHint = _sampleHint.value,
                language = _selectedLanguage.value
            )
            _activeDiagnosis.value = result
            _isAnalyzing.value = false
        }
    }

    fun saveDiagnosisToHistory() {
        val diagnosis = _activeDiagnosis.value ?: return
        if (_isSaved.value) return

        viewModelScope.launch {
            // Save local image if from bitmap
            val savedImagePath: String? = _selectedBitmap.value?.let { bmp ->
                try {
                    val file = File(
                        getApplication<Application>().filesDir,
                        "scan_${System.currentTimeMillis()}.jpg"
                    )
                    FileOutputStream(file).use { out ->
                        bmp.compress(Bitmap.CompressFormat.JPEG, 85, out)
                    }
                    file.absolutePath
                } catch (e: Exception) {
                    null
                }
            }

            val record = CropDiagnosisRecord(
                cropName = diagnosis.cropName,
                healthStatus = diagnosis.healthStatus,
                diseaseName = diagnosis.diseaseName,
                confidenceScore = diagnosis.confidenceScore,
                severityLevel = diagnosis.severityLevel,
                symptoms = diagnosis.symptoms,
                organicTreatment = diagnosis.organicTreatment,
                chemicalTreatment = diagnosis.chemicalTreatment,
                preventionSteps = diagnosis.preventionSteps,
                timestamp = System.currentTimeMillis(),
                imagePath = savedImagePath,
                sampleResId = _sampleResId.value,
                notes = "Scanned with CropShield AI"
            )

            repository.insertDiagnosis(record)
            _isSaved.value = true
        }
    }

    fun deleteHistoryRecord(id: Long) {
        viewModelScope.launch {
            repository.deleteDiagnosis(id)
            if (_selectedDetailRecord.value?.id == id) {
                _selectedDetailRecord.value = null
            }
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            repository.clearAll()
            _selectedDetailRecord.value = null
        }
    }

    fun readDiagnosisAloud() {
        val diag = _activeDiagnosis.value ?: return
        val lang = _selectedLanguage.value

        val textToSpeak = buildString {
            append("${diag.cropName}. ")
            append("${diag.diseaseName}. ")
            append("${AppStrings.get("severity", lang)}: ${diag.severityLevel}. ")
            append("${AppStrings.get("symptoms", lang)}: ${diag.symptoms}. ")
            append("${AppStrings.get("organic_remedies", lang)}: ${diag.organicTreatment}")
        }

        ttsHelper.speak(textToSpeak, lang)
    }

    fun stopAudio() {
        ttsHelper.stop()
    }

    private fun checkNetworkConnectivity(): Boolean {
        val cm = getApplication<Application>().getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            ?: return false
        val activeNetwork = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(activeNetwork) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    override fun onCleared() {
        super.onCleared()
        ttsHelper.shutdown()
    }
}
