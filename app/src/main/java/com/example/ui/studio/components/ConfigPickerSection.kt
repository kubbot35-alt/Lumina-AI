package com.example.ui.studio.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.domain.model.AiEngine
import com.example.domain.model.AspectRatioOption
import com.example.domain.model.ImageSizeOption
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.NeonCyan

@Composable
fun ConfigPickerSection(
    selectedEngine: AiEngine,
    selectedSize: ImageSizeOption,
    selectedRatio: AspectRatioOption,
    onEngineSelected: (AiEngine) -> Unit,
    onSizeSelected: (ImageSizeOption) -> Unit,
    onRatioSelected: (AspectRatioOption) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Engine Selector
        Column {
            Text(
                text = "Silnik AI",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AiEngine.values().forEach { engine ->
                    val isSelected = engine == selectedEngine
                    FilterChip(
                        selected = isSelected,
                        onClick = { onEngineSelected(engine) },
                        label = { Text(engine.title) },
                        shape = RoundedCornerShape(12.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NeonCyan.copy(alpha = 0.2f),
                            selectedLabelColor = NeonCyan,
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        border = BorderStroke(
                            width = if (isSelected) 1.5.dp else 1.dp,
                            color = if (isSelected) NeonCyan else MaterialTheme.colorScheme.outline
                        ),
                        modifier = Modifier.testTag("engine_chip_${engine.name.lowercase()}")
                    )
                }
            }
        }

        // Size Selector
        Column {
            Text(
                text = "Rozdzielczość (Jakość)",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ImageSizeOption.values().forEach { sizeOption ->
                    val isSelected = sizeOption == selectedSize
                    FilterChip(
                        selected = isSelected,
                        onClick = { onSizeSelected(sizeOption) },
                        label = { Text("${sizeOption.label} (${sizeOption.resolutionDescription})") },
                        shape = RoundedCornerShape(12.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NeonCyan.copy(alpha = 0.2f),
                            selectedLabelColor = NeonCyan,
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        border = BorderStroke(
                            width = if (isSelected) 1.5.dp else 1.dp,
                            color = if (isSelected) NeonCyan else MaterialTheme.colorScheme.outline
                        ),
                        modifier = Modifier.testTag("size_chip_${sizeOption.name.lowercase()}")
                    )
                }
            }
        }

        // Ratio Selector
        Column {
            Text(
                text = "Format / Proporcje",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AspectRatioOption.values().forEach { ratioOption ->
                    val isSelected = ratioOption == selectedRatio
                    FilterChip(
                        selected = isSelected,
                        onClick = { onRatioSelected(ratioOption) },
                        label = { Text(ratioOption.label) },
                        shape = RoundedCornerShape(12.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ElectricViolet.copy(alpha = 0.2f),
                            selectedLabelColor = ElectricViolet,
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        border = BorderStroke(
                            width = if (isSelected) 1.5.dp else 1.dp,
                            color = if (isSelected) ElectricViolet else MaterialTheme.colorScheme.outline
                        ),
                        modifier = Modifier.testTag("ratio_chip_${ratioOption.name.lowercase()}")
                    )
                }
            }
        }
    }
}
