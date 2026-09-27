package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.localization.AppLanguage
import com.example.ui.theme.HealthGreen
import com.example.ui.theme.WarningAmber

data class AgronomyGuideItem(
    val crop: String,
    val disease: String,
    val identification: String,
    val organicRemedy: String,
    val prevention: String
)

@Composable
fun CropLibraryScreen(currentLanguage: AppLanguage) {
    val guides = listOf(
        AgronomyGuideItem(
            crop = "Tomato & Potato",
            disease = "Late Blight (Phytophthora infestans)",
            identification = "Water-soaked dark lesions on leaf tips turning necrotic brown with white cottony spore fuzz under high humidity.",
            organicRemedy = "Bordeaux mixture 1% spray or copper hydroxide. Biological foliar spray of Trichoderma harzianum.",
            prevention = "Ensure 75cm row spacing, plant certified blight-resistant seeds, avoid wet evening sprinkler irrigation."
        ),
        AgronomyGuideItem(
            crop = "Rice / Paddy",
            disease = "Rice Blast (Magnaporthe oryzae)",
            identification = "Diamond or spindle-shaped lesions with gray-white centers and brownish borders along leaf blades.",
            organicRemedy = "Pseudomonas fluorescens 0.2% foliar application at tillering and panicle emergence. Neem cake soil drench.",
            prevention = "Avoid excessive split urea nitrogen doses. Maintain continuous 3-5cm water level during vegetative stage."
        ),
        AgronomyGuideItem(
            crop = "Corn / Maize",
            disease = "Northern Corn Leaf Blight (Exserohilum turcicum)",
            identification = "Elongated cigar-shaped grayish-green lesions (2-15cm long) turning tan with dark spore clusters.",
            organicRemedy = "Bacillus subtilis bio-fungicide drench combined with seaweed bio-stimulant foliar spray.",
            prevention = "Deep autumn tillage to bury crop debris. Strict 2-year rotation with soybean or pulses."
        ),
        AgronomyGuideItem(
            crop = "Wheat & Barley",
            disease = "Yellow Stripe Rust (Puccinia striiformis)",
            identification = "Linear rows of bright yellow-orange powdery pustules resembling stripes between leaf veins.",
            organicRemedy = "Spray 5% sour buttermilk (whey) or cow urine distillate (10%) mixed with neem oil (5ml/L).",
            prevention = "Early sowing in autumn, balanced potassium application to harden leaf cuticle cellular walls."
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("agronomy_guide_screen")
    ) {
        Text(
            text = "Field Agronomy & Pathology Reference",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "Verified biological remedies & early identification protocols for field officers & farmers",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(14.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(guides) { guide ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = guide.crop,
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Text(
                                    text = "BIO-DEFENSE",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Text(
                            text = guide.disease,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Identification: ${guide.identification}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(verticalAlignment = Alignment.Top) {
                            Text(text = "🌱 ", fontSize = 14.sp)
                            Text(
                                text = guide.organicRemedy,
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.primary,
                                lineHeight = 18.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(verticalAlignment = Alignment.Top) {
                            Text(text = "🛡️ ", fontSize = 14.sp)
                            Text(
                                text = guide.prevention,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
