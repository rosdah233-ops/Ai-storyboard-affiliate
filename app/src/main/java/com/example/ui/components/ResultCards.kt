package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R
import com.example.model.StoryboardVariation
import com.example.ui.theme.BlueAccent
import com.example.ui.theme.BlueSurface
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.GoldChampagne
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.GoldSurface
import com.example.ui.theme.NavyBorder
import com.example.ui.theme.NavyCard
import com.example.ui.theme.NavyDeep
import com.example.ui.theme.NavyElevated
import com.example.ui.theme.NavySurface
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WhitePure
import com.example.ui.theme.WhiteSoft

@Composable
fun StoryboardResultsHeader(
    variationCount: Int,
    productSummary: String,
    onCopyAllPrompts: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("storyboard_results_header"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.results_header_title),
                    style = MaterialTheme.typography.headlineMedium,
                    color = WhitePure
                )
                Text(
                    text = "$variationCount variasi lembar storyboard siap digunakan sebagai acuan video 10 detik.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }

            if (variationCount > 1) {
                Spacer(modifier = Modifier.width(10.dp))
                FilledTonalButton(
                    onClick = onCopyAllPrompts,
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = GoldSurface,
                        contentColor = GoldLight
                    ),
                    border = BorderStroke(1.dp, GoldPrimary),
                    modifier = Modifier
                        .heightIn(min = 48.dp)
                        .testTag("copy_all_prompts_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = stringResource(R.string.btn_copy_all_prompts),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }

        if (productSummary.isNotBlank()) {
            Surface(
                color = NavySurface,
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Verified,
                        contentDescription = null,
                        tint = GoldChampagne,
                        modifier = Modifier.size(20.dp)
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = "Detail Produk yang Dipertahankan AI:",
                            style = MaterialTheme.typography.labelMedium,
                            color = GoldLight
                        )
                        Text(
                            text = productSummary,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StoryboardVariationCard(
    variation: StoryboardVariation,
    onToggleRevise: () -> Unit,
    onRevisionTextChange: (String) -> Unit,
    onSubmitRevision: () -> Unit,
    onRegenerate: () -> Unit,
    onDownload: () -> Unit,
    onCopyPrompt: () -> Unit,
    onZoomImage: () -> Unit,
    modifier: Modifier = Modifier
) {
    val num = variation.variationNumber

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("storyboard_card_$num"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = NavyCard),
        border = BorderStroke(1.2.dp, GoldPrimary.copy(alpha = 0.45f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. Variation Number & Title + Concept & Angle
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Surface(
                    color = GoldPrimary,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "STORYBOARD #$num",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = NavyDeep,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                Surface(
                    color = BlueSurface,
                    shape = RoundedCornerShape(50),
                    border = BorderStroke(1.dp, BlueAccent.copy(alpha = 0.4f))
                ) {
                    Text(
                        text = if (variation.imageWidth > 0 && variation.imageHeight > 0) {
                            "${variation.imageWidth} × ${variation.imageHeight} px • 9:16"
                        } else {
                            "Kanvas 9:16 • 5 Panel"
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = BlueAccent,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Text(
                text = variation.title,
                style = MaterialTheme.typography.titleLarge,
                color = WhitePure
            )

            Text(
                text = variation.conceptAndAngle,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )

            // 2. Single Composite 9:16 Storyboard Sheet Image (5 panels in 1 canvas)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(9f / 16f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(NavyDeep)
                    .border(1.5.dp, GoldPrimary.copy(alpha = 0.55f), RoundedCornerShape(16.dp))
                    .testTag("storyboard_image_container_$num"),
                contentAlignment = Alignment.Center
            ) {
                val bmp = variation.compositeBitmap

                if (bmp != null && !variation.isGeneratingImage) {
                    Image(
                        bitmap = bmp.asImageBitmap(),
                        contentDescription = "Gambar Komposit 5-Panel Storyboard #$num: ${variation.title}",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable { onZoomImage() }
                    )

                    // Top-right Fullscreen Zoom button
                    Surface(
                        color = NavyDeep.copy(alpha = 0.8f),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.6f)),
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(10.dp)
                            .clickable { onZoomImage() }
                            .testTag("zoom_storyboard_button_$num")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Fullscreen,
                                contentDescription = "Perbesar Gambar",
                                tint = GoldLight,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Perbesar",
                                style = MaterialTheme.typography.labelSmall,
                                color = GoldLight
                            )
                        }
                    }

                    // Bottom subtle 5-panel timecode reference bar
                    Surface(
                        color = NavyDeep.copy(alpha = 0.85f),
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Lembar Utuh 5 Panel (0–2s • 2–4s • 4–6s • 6–8s • 8–10s)",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = GoldLight
                            )
                            Text(
                                text = "${variation.imageWidth}×${variation.imageHeight}",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = BlueAccent
                            )
                        }
                    }
                } else if (variation.isGeneratingImage) {
                    // 5-panel 9:16 loading layout
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val timeLabels = listOf(
                            "Panel 1 • 0–2 detik",
                            "Panel 2 • 2–4 detik",
                            "Panel 3 • 4–6 detik",
                            "Panel 4 • 6–8 detik",
                            "Panel 5 • 8–10 detik"
                        )
                        timeLabels.forEachIndexed { idx, timeLabel ->
                            val panelInfo = variation.panels.getOrNull(idx)
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                color = NavySurface.copy(alpha = 0.7f),
                                border = BorderStroke(1.dp, NavyBorder)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(10.dp),
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = timeLabel,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = GoldChampagne,
                                        fontWeight = FontWeight.Bold
                                    )
                                    if (panelInfo != null) {
                                        Text(
                                            text = "${panelInfo.sceneTitle} (${panelInfo.cameraAngle})",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = WhiteSoft,
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.5.dp,
                                color = GoldPrimary
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = if (variation.isRevising) {
                                    "Memperbaiki lembar storyboard #$num..."
                                } else {
                                    "Merender kanvas 5-panel 9:16..."
                                },
                                style = MaterialTheme.typography.labelMedium,
                                color = GoldLight
                            )
                        }
                    }
                } else {
                    // Error state inside the image frame
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.ErrorOutline,
                            contentDescription = null,
                            tint = ErrorRed,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = variation.errorMessage ?: "Gambar belum berhasil dibuat.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = WhiteSoft,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = onRegenerate,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = GoldPrimary,
                                contentColor = NavyDeep
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = stringResource(R.string.btn_regenerate))
                        }
                    }
                }
            }

            // 3. Four Mandatory Action Buttons Under the Storyboard Image:
            // "Perbaiki", "Generate Ulang", "Download Storyboard", "Salin Prompt"
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onToggleRevise,
                        enabled = !variation.isGeneratingImage,
                        border = BorderStroke(1.dp, GoldPrimary),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = GoldLight),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 48.dp)
                            .testTag("revise_button_$num")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = stringResource(R.string.btn_revise),
                            style = MaterialTheme.typography.labelLarge
                        )
                    }

                    OutlinedButton(
                        onClick = onRegenerate,
                        enabled = !variation.isGeneratingImage,
                        border = BorderStroke(1.dp, BlueAccent.copy(alpha = 0.7f)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = BlueAccent),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 48.dp)
                            .testTag("regenerate_button_$num")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = stringResource(R.string.btn_regenerate),
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onDownload,
                        enabled = variation.compositeBitmap != null && !variation.isGeneratingImage,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GoldPrimary,
                            contentColor = NavyDeep,
                            disabledContainerColor = NavyElevated,
                            disabledContentColor = TextMuted
                        ),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 48.dp)
                            .testTag("download_button_$num")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = stringResource(R.string.btn_download_storyboard),
                            style = MaterialTheme.typography.labelLarge
                        )
                    }

                    FilledTonalButton(
                        onClick = onCopyPrompt,
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = NavySurface,
                            contentColor = WhitePure
                        ),
                        border = BorderStroke(1.dp, NavyBorder),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 48.dp)
                            .testTag("copy_prompt_button_$num")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = null,
                            tint = GoldChampagne,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = stringResource(R.string.btn_copy_prompt),
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }

            // Inline Revision Box ("Perbaiki")
            AnimatedVisibility(visible = variation.revisionInputVisible) {
                Surface(
                    color = NavyDeep,
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.6f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("revision_box_$num")
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Perbaiki Storyboard #$num (Tidak mengubah variasi lain)",
                            style = MaterialTheme.typography.titleSmall,
                            color = GoldLight
                        )

                        OutlinedTextField(
                            value = variation.revisionInstruction,
                            onValueChange = onRevisionTextChange,
                            placeholder = {
                                Text(
                                    text = stringResource(R.string.hint_revision_input),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextMuted
                                )
                            },
                            minLines = 2,
                            maxLines = 4,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldPrimary,
                                unfocusedBorderColor = NavyBorder,
                                focusedContainerColor = NavyCard,
                                unfocusedContainerColor = NavyCard,
                                focusedTextColor = WhitePure,
                                unfocusedTextColor = WhiteSoft
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("revision_input_$num")
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedButton(
                                onClick = onToggleRevise,
                                border = BorderStroke(1.dp, NavyBorder),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary),
                                modifier = Modifier.heightIn(min = 48.dp)
                            ) {
                                Text(text = stringResource(R.string.btn_cancel))
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = onSubmitRevision,
                                enabled = variation.revisionInstruction.isNotBlank() && !variation.isGeneratingImage,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = GoldPrimary,
                                    contentColor = NavyDeep
                                ),
                                modifier = Modifier
                                    .heightIn(min = 48.dp)
                                    .testTag("submit_revision_button_$num")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Send,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = stringResource(R.string.btn_apply_revision))
                            }
                        }
                    }
                }
            }

            HorizontalDivider(color = NavyBorder.copy(alpha = 0.6f))

            // 4. Urutan 5 Panel Adegan (0-10 Detik)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Urutan 5 Panel Adegan (Video 10 Detik):",
                    style = MaterialTheme.typography.titleSmall,
                    color = GoldChampagne
                )
                variation.panels.forEach { panel ->
                    Surface(
                        color = NavyDeep.copy(alpha = 0.7f),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, NavyBorder.copy(alpha = 0.7f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Panel ${panel.panelNumber} (${panel.timeRange}) • ${panel.sceneTitle}",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = GoldLight,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = panel.cameraAngle,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = BlueAccent
                                )
                            }
                            Text(
                                text = panel.visualDescription,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }

            // 5. Prompt Lengkap untuk Google Flow
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Prompt Lengkap (Siap Pakai di Google Flow):",
                        style = MaterialTheme.typography.titleSmall,
                        color = WhitePure
                    )
                    Text(
                        text = "10 Detik • 9:16",
                        style = MaterialTheme.typography.labelSmall,
                        color = GoldChampagne
                    )
                }

                Surface(
                    color = NavyDeep,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, NavyBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("full_prompt_box_$num")
                ) {
                    SelectionContainer {
                        Text(
                            text = variation.fullPrompt,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FullScreenStoryboardDialog(
    variation: StoryboardVariation,
    onDismiss: () -> Unit,
    onDownload: () -> Unit
) {
    val bmp = variation.compositeBitmap ?: return
    var scale by remember { mutableFloatStateOf(1f) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(NavyDeep.copy(alpha = 0.96f))
                .padding(16.dp)
        ) {
            Image(
                bitmap = bmp.asImageBitmap(),
                contentDescription = "Layar Penuh Storyboard #${variation.variationNumber}",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTransformGestures { _, pan, zoom, _ ->
                            scale = (scale * zoom).coerceIn(1f, 4f)
                            if (scale > 1f) {
                                offsetX += pan.x
                                offsetY += pan.y
                            } else {
                                offsetX = 0f
                                offsetY = 0f
                            }
                        }
                    }
                    .graphicsLayer(
                        scaleX = scale,
                        scaleY = scale,
                        translationX = offsetX,
                        translationY = offsetY
                    )
            )

            // Top bar with title & close button
            Row(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .background(NavyCard.copy(alpha = 0.9f), RoundedCornerShape(14.dp))
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Storyboard #${variation.variationNumber}: ${variation.title}",
                        style = MaterialTheme.typography.titleSmall,
                        color = WhitePure
                    )
                    Text(
                        text = "Resolusi Asli: ${variation.imageWidth} × ${variation.imageHeight} px • Cubit untuk zoom",
                        style = MaterialTheme.typography.labelSmall,
                        color = GoldLight
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(48.dp)
                        .background(NavySurface, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Tutup Pratinjau",
                        tint = WhitePure
                    )
                }
            }

            // Bottom download button
            Button(
                onClick = onDownload,
                colors = ButtonDefaults.buttonColors(
                    containerColor = GoldPrimary,
                    contentColor = NavyDeep
                ),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .heightIn(min = 52.dp)
                    .padding(bottom = 8.dp)
            ) {
                Icon(imageVector = Icons.Default.Download, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.btn_download_storyboard),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
