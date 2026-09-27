package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.localization.AppStrings
import com.example.ui.CropNavTab
import com.example.ui.CropShieldViewModel
import com.example.ui.components.CropBottomNavBar
import com.example.ui.components.CropLibraryScreen
import com.example.ui.components.CropShieldTopBar
import com.example.ui.components.DiagnosisCard
import com.example.ui.components.FieldHistoryScreen
import com.example.ui.components.ScanHeroSection
import com.example.ui.components.WeatherRiskWidget
import com.example.ui.theme.CropShieldTheme

class MainActivity : ComponentActivity() {

    private val viewModel: CropShieldViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val isOutdoorMode by viewModel.isOutdoorHighContrast.collectAsState()

            CropShieldTheme(darkTheme = isOutdoorMode) {
                CropShieldApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun CropShieldApp(viewModel: CropShieldViewModel) {
    val activeTab by viewModel.activeTab.collectAsState()
    val currentLanguage by viewModel.selectedLanguage.collectAsState()
    val isOutdoorMode by viewModel.isOutdoorHighContrast.collectAsState()
    val isOnline by viewModel.isOnline.collectAsState()

    val selectedBitmap by viewModel.selectedBitmap.collectAsState()
    val selectedImageUri by viewModel.selectedImageUri.collectAsState()
    val sampleResId by viewModel.sampleResId.collectAsState()
    val sampleHint by viewModel.sampleHint.collectAsState()
    val isAnalyzing by viewModel.isAnalyzing.collectAsState()
    val activeDiagnosis by viewModel.activeDiagnosis.collectAsState()
    val isSaved by viewModel.isSaved.collectAsState()
    val isSpeaking by viewModel.isSpeaking.collectAsState()

    val weatherState by viewModel.weatherState.collectAsState()
    val simulatedHumidity by viewModel.simulatedHumidity.collectAsState()

    val filteredDiagnoses by viewModel.filteredDiagnoses.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val filterStatus by viewModel.filterStatus.collectAsState()
    val selectedDetailRecord by viewModel.selectedDetailRecord.collectAsState()

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val screenWidth = maxWidth
        val isMobile = screenWidth < 640.dp
        val isTablet = screenWidth in 640.dp..1024.dp
        val isDesktop = screenWidth > 1024.dp

        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .testTag("cropshield_scaffold"),
            topBar = {
                CropShieldTopBar(
                    currentLanguage = currentLanguage,
                    onLanguageSelected = { viewModel.setLanguage(it) },
                    isOutdoorMode = isOutdoorMode,
                    onToggleOutdoorMode = { viewModel.toggleOutdoorHighContrast() },
                    isOnline = isOnline,
                    showNavTabs = !isMobile,
                    activeTab = activeTab,
                    onTabSelected = { viewModel.setActiveTab(it) }
                )
            },
            bottomBar = {
                // Bottom tab bar on mobile screens, top header navigation on tablet and desktop
                if (isMobile) {
                    CropBottomNavBar(
                        activeTab = activeTab,
                        onTabSelected = { viewModel.setActiveTab(it) },
                        currentLanguage = currentLanguage
                    )
                }
            }
        ) { innerPadding ->
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                color = MaterialTheme.colorScheme.background
            ) {
                when (activeTab) {
                    CropNavTab.SCANNER -> {
                        when {
                            isDesktop -> {
                                // 3-column dashboard on desktop (>1024px)
                                Row(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    // Column 1: Scanner Section
                                    Column(
                                        modifier = Modifier
                                            .weight(1.1f)
                                            .verticalScroll(rememberScrollState())
                                    ) {
                                        AgritechHeroBanner()
                                        ScanHeroSection(
                                            currentLanguage = currentLanguage,
                                            selectedBitmap = selectedBitmap,
                                            selectedImageUri = selectedImageUri,
                                            sampleResId = sampleResId,
                                            sampleHint = sampleHint,
                                            isAnalyzing = isAnalyzing,
                                            onBitmapCaptured = { viewModel.onBitmapCaptured(it) },
                                            onImageSelected = { viewModel.onImageSelected(it) },
                                            onSelectSamplePreset = { viewModel.loadSamplePreset(it) },
                                            onClearScan = { viewModel.clearActiveScan() },
                                            onDiagnoseClick = { viewModel.diagnoseCrop() }
                                        )
                                    }

                                    // Column 2: Detailed Diagnosis & Treatment Card
                                    Column(
                                        modifier = Modifier
                                            .weight(1.3f)
                                            .verticalScroll(rememberScrollState())
                                    ) {
                                        if (activeDiagnosis != null) {
                                            DiagnosisCard(
                                                diagnosis = activeDiagnosis!!,
                                                currentLanguage = currentLanguage,
                                                selectedBitmap = selectedBitmap,
                                                selectedImageUri = selectedImageUri,
                                                sampleResId = sampleResId,
                                                isSpeaking = isSpeaking,
                                                isSaved = isSaved,
                                                onReadAloudClick = { viewModel.readDiagnosisAloud() },
                                                onStopAudioClick = { viewModel.stopAudio() },
                                                onSaveToHistoryClick = { viewModel.saveDiagnosisToHistory() }
                                            )
                                        } else {
                                            AwaitingDiagnosisCard(currentLanguage)
                                        }
                                    }

                                    // Column 3: Weather, Humidity & Disease Warnings
                                    Column(
                                        modifier = Modifier
                                            .weight(1.0f)
                                            .verticalScroll(rememberScrollState())
                                    ) {
                                        WeatherRiskWidget(
                                            weather = weatherState,
                                            currentLanguage = currentLanguage,
                                            currentHumidity = simulatedHumidity,
                                            onHumidityPresetSelected = { viewModel.setSimulatedHumidity(it) }
                                        )
                                    }
                                }
                            }
                            isTablet -> {
                                // 2-column on tablet (640px - 1024px)
                                Row(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    // Left Column: Hero & Scanner
                                    Column(
                                        modifier = Modifier
                                            .weight(1f)
                                            .verticalScroll(rememberScrollState())
                                    ) {
                                        AgritechHeroBanner()
                                        ScanHeroSection(
                                            currentLanguage = currentLanguage,
                                            selectedBitmap = selectedBitmap,
                                            selectedImageUri = selectedImageUri,
                                            sampleResId = sampleResId,
                                            sampleHint = sampleHint,
                                            isAnalyzing = isAnalyzing,
                                            onBitmapCaptured = { viewModel.onBitmapCaptured(it) },
                                            onImageSelected = { viewModel.onImageSelected(it) },
                                            onSelectSamplePreset = { viewModel.loadSamplePreset(it) },
                                            onClearScan = { viewModel.clearActiveScan() },
                                            onDiagnoseClick = { viewModel.diagnoseCrop() }
                                        )
                                    }

                                    // Right Column: Diagnosis & Weather Alert
                                    Column(
                                        modifier = Modifier
                                            .weight(1f)
                                            .verticalScroll(rememberScrollState())
                                    ) {
                                        if (activeDiagnosis != null) {
                                            DiagnosisCard(
                                                diagnosis = activeDiagnosis!!,
                                                currentLanguage = currentLanguage,
                                                selectedBitmap = selectedBitmap,
                                                selectedImageUri = selectedImageUri,
                                                sampleResId = sampleResId,
                                                isSpeaking = isSpeaking,
                                                isSaved = isSaved,
                                                onReadAloudClick = { viewModel.readDiagnosisAloud() },
                                                onStopAudioClick = { viewModel.stopAudio() },
                                                onSaveToHistoryClick = { viewModel.saveDiagnosisToHistory() }
                                            )
                                        }
                                        WeatherRiskWidget(
                                            weather = weatherState,
                                            currentLanguage = currentLanguage,
                                            currentHumidity = simulatedHumidity,
                                            onHumidityPresetSelected = { viewModel.setSimulatedHumidity(it) }
                                        )
                                    }
                                }
                            }
                            else -> {
                                // Single-column on mobile (<640px)
                                val scrollState = rememberScrollState()
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .verticalScroll(scrollState)
                                ) {
                                    AgritechHeroBanner()

                                    // Core Scanner View (Camera, Gallery, Presets, CTA)
                                    ScanHeroSection(
                                        currentLanguage = currentLanguage,
                                        selectedBitmap = selectedBitmap,
                                        selectedImageUri = selectedImageUri,
                                        sampleResId = sampleResId,
                                        sampleHint = sampleHint,
                                        isAnalyzing = isAnalyzing,
                                        onBitmapCaptured = { viewModel.onBitmapCaptured(it) },
                                        onImageSelected = { viewModel.onImageSelected(it) },
                                        onSelectSamplePreset = { viewModel.loadSamplePreset(it) },
                                        onClearScan = { viewModel.clearActiveScan() },
                                        onDiagnoseClick = { viewModel.diagnoseCrop() }
                                    )

                                    // Detailed Diagnosis & Treatment Result Card (if diagnosed)
                                    activeDiagnosis?.let { diag ->
                                        DiagnosisCard(
                                            diagnosis = diag,
                                            currentLanguage = currentLanguage,
                                            selectedBitmap = selectedBitmap,
                                            selectedImageUri = selectedImageUri,
                                            sampleResId = sampleResId,
                                            isSpeaking = isSpeaking,
                                            isSaved = isSaved,
                                            onReadAloudClick = { viewModel.readDiagnosisAloud() },
                                            onStopAudioClick = { viewModel.stopAudio() },
                                            onSaveToHistoryClick = { viewModel.saveDiagnosisToHistory() }
                                        )
                                    }

                                    // Local Weather & Disease Warning Widget
                                    WeatherRiskWidget(
                                        weather = weatherState,
                                        currentLanguage = currentLanguage,
                                        currentHumidity = simulatedHumidity,
                                        onHumidityPresetSelected = { viewModel.setSimulatedHumidity(it) }
                                    )

                                    Spacer(modifier = Modifier.height(24.dp))
                                }
                            }
                        }
                    }

                    CropNavTab.WEATHER -> {
                        val scrollState = rememberScrollState()
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(scrollState)
                        ) {
                            WeatherRiskWidget(
                                weather = weatherState,
                                currentLanguage = currentLanguage,
                                currentHumidity = simulatedHumidity,
                                onHumidityPresetSelected = { viewModel.setSimulatedHumidity(it) }
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                        }
                    }

                    CropNavTab.HISTORY -> {
                        FieldHistoryScreen(
                            diagnoses = filteredDiagnoses,
                            currentLanguage = currentLanguage,
                            searchQuery = searchQuery,
                            filterStatus = filterStatus,
                            selectedRecord = selectedDetailRecord,
                            onSearchQueryChanged = { viewModel.setSearchQuery(it) },
                            onFilterStatusChanged = { viewModel.setFilterStatus(it) },
                            onSelectRecord = { viewModel.setSelectedDetailRecord(it) },
                            onDeleteRecord = { viewModel.deleteHistoryRecord(it) },
                            onClearAll = { viewModel.clearAllHistory() }
                        )
                    }

                    CropNavTab.GUIDE -> {
                        CropLibraryScreen(currentLanguage = currentLanguage)
                    }
                }
            }
        }
    }
}

@Composable
fun AgritechHeroBanner() {
    Surface(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(16.dp))
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = R.drawable.hero_agritech_banner_1790444671156),
                contentDescription = "Agritech Hero Field",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.85f)
                ) {
                    Text(
                        text = "SMART AGRONOMY VISION",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "Scan Leaves for Instant Disease & Organic Cures",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                    color = androidx.compose.ui.graphics.Color.White
                )
            }
        }
    }
}

@Composable
fun AwaitingDiagnosisCard(currentLanguage: com.example.localization.AppLanguage) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Psychology,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(54.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "AI Vision Diagnostics Ready",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Select a sample leaf on the left or capture/upload your crop photo to trigger real-time AI pathology analysis.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 20.sp
            )
        }
    }
}
