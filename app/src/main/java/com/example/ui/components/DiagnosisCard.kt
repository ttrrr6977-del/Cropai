package com.example.ui.components

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ai.DiagnosisResult
import com.example.localization.AppLanguage
import com.example.localization.AppStrings
import com.example.ui.theme.HealthGreen
import com.example.ui.theme.SevereRed
import com.example.ui.theme.WarningAmber

@Composable
fun DiagnosisCard(
    diagnosis: DiagnosisResult,
    currentLanguage: AppLanguage,
    selectedBitmap: Bitmap? = null,
    selectedImageUri: Uri? = null,
    sampleResId: Int? = null,
    isSpeaking: Boolean,
    isSaved: Boolean,
    onReadAloudClick: () -> Unit,
    onStopAudioClick: () -> Unit,
    onSaveToHistoryClick: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    // Color determination based on severity
    val (severityColor, severityBg, severityIcon) = when {
        diagnosis.healthStatus.equals("Healthy", ignoreCase = true) || diagnosis.severityLevel.equals("Healthy", ignoreCase = true) -> {
            Triple(HealthGreen, HealthGreen.copy(alpha = 0.15f), Icons.Default.CheckCircle)
        }
        diagnosis.severityLevel.equals("Low", ignoreCase = true) -> {
            Triple(HealthGreen, HealthGreen.copy(alpha = 0.15f), Icons.Default.CheckCircle)
        }
        diagnosis.severityLevel.equals("Medium", ignoreCase = true) -> {
            Triple(WarningAmber, WarningAmber.copy(alpha = 0.15f), Icons.Default.Warning)
        }
        else -> {
            Triple(SevereRed, SevereRed.copy(alpha = 0.15f), Icons.Default.Warning)
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("diagnosis_result_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        border = BorderStroke(1.dp, severityColor.copy(alpha = 0.4f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header: Scanned Image Thumbnail alongside Crop Name & Severity Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Scanned Specimen Thumbnail
                if (selectedBitmap != null || selectedImageUri != null || sampleResId != null) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .size(72.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        border = BorderStroke(1.5.dp, severityColor.copy(alpha = 0.6f))
                    ) {
                        when {
                            selectedBitmap != null -> {
                                Image(
                                    bitmap = selectedBitmap.asImageBitmap(),
                                    contentDescription = "Scanned specimen leaf",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                            selectedImageUri != null -> {
                                AsyncImage(
                                    model = selectedImageUri,
                                    contentDescription = "Scanned specimen leaf",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                            sampleResId != null -> {
                                Image(
                                    painter = painterResource(id = sampleResId),
                                    contentDescription = "Scanned specimen leaf",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = diagnosis.cropName,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = diagnosis.diseaseName,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Severity Badge
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = severityBg
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = severityIcon,
                            contentDescription = null,
                            tint = severityColor,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = diagnosis.severityLevel.uppercase(),
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Black),
                            color = severityColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Confidence & AI Source bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Text(
                        text = "${AppStrings.get("confidence", currentLanguage)}: ${diagnosis.confidenceScore}%",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                // Audio Read-Aloud Button
                Button(
                    onClick = {
                        if (isSpeaking) onStopAudioClick() else onReadAloudClick()
                    },
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSpeaking) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primaryContainer,
                        contentColor = if (isSpeaking) MaterialTheme.colorScheme.onError else MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    modifier = Modifier
                        .height(40.dp)
                        .testTag("audio_read_aloud_button")
                ) {
                    Icon(
                        imageVector = if (isSpeaking) Icons.Default.Stop else Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = "Read Aloud",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isSpeaking) AppStrings.get("stop_speech", currentLanguage) else AppStrings.get("read_aloud", currentLanguage),
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tabbed Sections for Agronomic Breakdown
            val tabTitles = listOf(
                Pair(AppStrings.get("symptoms", currentLanguage), Icons.Default.Warning),
                Pair(AppStrings.get("organic_remedies", currentLanguage), Icons.Default.Eco),
                Pair(AppStrings.get("chemical_treatments", currentLanguage), Icons.Default.Science),
                Pair(AppStrings.get("prevention_steps", currentLanguage), Icons.Default.Shield)
            )

            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                edgePadding = 0.dp,
                containerColor = Color.Transparent,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = MaterialTheme.colorScheme.primary,
                        height = 3.dp
                    )
                },
                divider = {}
            ) {
                tabTitles.forEachIndexed { index, (title, icon) ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = if (selectedTab == index) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = title,
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedTab == index) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Tab Content Body
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    when (selectedTab) {
                        0 -> { // Symptoms
                            Text(
                                text = diagnosis.symptoms,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                lineHeight = 22.sp
                            )
                        }
                        1 -> { // Organic & Biological Remedies
                            Row(verticalAlignment = Alignment.Top) {
                                Text(
                                    text = "🌱 ",
                                    fontSize = 18.sp
                                )
                                Text(
                                    text = diagnosis.organicTreatment,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                                    color = MaterialTheme.colorScheme.onSurface,
                                    lineHeight = 22.sp
                                )
                            }
                        }
                        2 -> { // Chemical Treatments
                            Row(verticalAlignment = Alignment.Top) {
                                Text(
                                    text = "🧪 ",
                                    fontSize = 18.sp
                                )
                                Text(
                                    text = diagnosis.chemicalTreatment,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    lineHeight = 22.sp
                                )
                            }
                        }
                        3 -> { // Prevention Steps
                            Row(verticalAlignment = Alignment.Top) {
                                Text(
                                    text = "🛡️ ",
                                    fontSize = 18.sp
                                )
                                Text(
                                    text = diagnosis.preventionSteps,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    lineHeight = 22.sp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Save to Field History CTA
            OutlinedButton(
                onClick = onSaveToHistoryClick,
                enabled = !isSaved,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("save_to_history_button"),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, if (isSaved) HealthGreen else MaterialTheme.colorScheme.outline)
            ) {
                Icon(
                    imageVector = if (isSaved) Icons.Default.CheckCircle else Icons.Default.BookmarkBorder,
                    contentDescription = null,
                    tint = if (isSaved) HealthGreen else MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isSaved) AppStrings.get("saved_success", currentLanguage) else AppStrings.get("save_to_history", currentLanguage),
                    fontWeight = FontWeight.Bold,
                    color = if (isSaved) HealthGreen else MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
