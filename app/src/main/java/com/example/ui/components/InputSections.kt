package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ViewCarousel
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Landscape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.DetectedProductInfo
import com.example.model.UploadedPhoto
import com.example.model.VariationGuide
import com.example.ui.theme.BlueAccent
import com.example.ui.theme.BlueSurface
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.GoldChampagne
import com.example.ui.theme.GoldDark
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StudioHeaderSection(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("studio_header_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = NavyCard),
        border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.35f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            NavySurface,
                            NavyCard,
                            NavyDeep
                        )
                    )
                )
                .padding(horizontal = 20.dp, vertical = 22.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        color = GoldSurface,
                        shape = RoundedCornerShape(50),
                        border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = GoldChampagne,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = stringResource(R.string.header_badge),
                                style = MaterialTheme.typography.labelSmall,
                                color = GoldLight,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Surface(
                        color = BlueSurface,
                        shape = RoundedCornerShape(50),
                        border = BorderStroke(1.dp, BlueAccent.copy(alpha = 0.4f))
                    ) {
                        Text(
                            text = "9:16 • 5 PANEL • 10 DETIK",
                            style = MaterialTheme.typography.labelSmall,
                            color = BlueAccent,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                }

                Text(
                    text = stringResource(R.string.header_title),
                    style = MaterialTheme.typography.displaySmall,
                    color = WhitePure
                )

                Text(
                    text = stringResource(R.string.header_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(2.dp))

                // 5-panel timecode strip indicator
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val timecodes = listOf(
                        "Panel 1: 0–2 dtk",
                        "Panel 2: 2–4 dtk",
                        "Panel 3: 4–6 dtk",
                        "Panel 4: 6–8 dtk",
                        "Panel 5: 8–10 dtk"
                    )
                    timecodes.forEach { label ->
                        Surface(
                            color = NavyDeep.copy(alpha = 0.75f),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, NavyBorder)
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall,
                                color = GoldLight,
                                modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProductPhotosSection(
    productSlots: List<UploadedPhoto?>,
    onPickForSlot: (Int) -> Unit,
    onPickMultiple: () -> Unit,
    onRemoveSlot: (Int) -> Unit,
    onLoadSampleProduct: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uploadedCount = productSlots.count { it != null }

    SectionContainerCard(
        title = stringResource(R.string.section_product_title),
        badgeText = "${stringResource(R.string.section_product_badge)} • $uploadedCount/5",
        isMandatory = true,
        description = stringResource(R.string.section_product_desc),
        modifier = modifier.testTag("product_photos_section")
    ) {
        // Quick action row: Multi-upload + Sample product helper
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilledTonalButton(
                onClick = onPickMultiple,
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = GoldSurface,
                    contentColor = GoldLight
                ),
                border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.55f)),
                modifier = Modifier
                    .heightIn(min = 48.dp)
                    .testTag("add_multiple_product_photos_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Collections,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.btn_add_product_photos),
                    style = MaterialTheme.typography.labelLarge
                )
            }

            OutlinedButton(
                onClick = onLoadSampleProduct,
                border = BorderStroke(1.dp, BlueAccent.copy(alpha = 0.5f)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = BlueAccent),
                modifier = Modifier
                    .heightIn(min = 48.dp)
                    .testTag("load_sample_product_button")
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = stringResource(R.string.btn_load_sample_product),
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 5-Slot Product Grid (Responsive rows)
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                for (slotIndex in 0..2) {
                    ProductSlotCard(
                        slotIndex = slotIndex,
                        photo = productSlots[slotIndex],
                        onSelectOrReplace = { onPickForSlot(slotIndex) },
                        onRemove = { onRemoveSlot(slotIndex) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                for (slotIndex in 3..4) {
                    ProductSlotCard(
                        slotIndex = slotIndex,
                        photo = productSlots[slotIndex],
                        onSelectOrReplace = { onPickForSlot(slotIndex) },
                        onRemove = { onRemoveSlot(slotIndex) },
                        modifier = Modifier.weight(1f)
                    )
                }
                // Info card in the 6th cell of the 3x2 grid to keep proportions balanced
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(0.82f),
                    shape = RoundedCornerShape(14.dp),
                    color = NavyDeep.copy(alpha = 0.6f),
                    border = BorderStroke(1.dp, NavyBorder.copy(alpha = 0.6f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(10.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = null,
                            tint = GoldChampagne,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Produk Dijaga 100% Asli",
                            style = MaterialTheme.typography.labelSmall,
                            color = GoldLight,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Warna, motif, logo & bahan tidak diubah",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = TextMuted,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProductSlotCard(
    slotIndex: Int,
    photo: UploadedPhoto?,
    onSelectOrReplace: () -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .aspectRatio(0.82f)
            .clip(RoundedCornerShape(14.dp))
            .clickable { onSelectOrReplace() }
            .testTag("product_slot_${slotIndex + 1}"),
        shape = RoundedCornerShape(14.dp),
        color = if (photo != null) NavySurface else NavyDeep,
        border = BorderStroke(
            width = if (photo != null) 1.5.dp else 1.dp,
            color = if (photo != null) GoldPrimary else NavyBorder
        )
    ) {
        if (photo != null) {
            Box(modifier = Modifier.fillMaxSize()) {
                Image(
                    bitmap = photo.bitmap.asImageBitmap(),
                    contentDescription = "Foto Produk Slot ${slotIndex + 1}",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Top slot number badge
                Surface(
                    color = NavyDeep.copy(alpha = 0.82f),
                    shape = RoundedCornerShape(bottomEnd = 10.dp),
                    modifier = Alignment.TopStart.let { Modifier.align(it) }
                ) {
                    Text(
                        text = "#${slotIndex + 1}",
                        style = MaterialTheme.typography.labelSmall,
                        color = GoldChampagne,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                // Bottom Replace & Delete controls
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .background(NavyDeep.copy(alpha = 0.88f))
                        .padding(horizontal = 4.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = GoldSurface,
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier
                            .clickable { onSelectOrReplace() }
                            .testTag("replace_product_slot_${slotIndex + 1}")
                    ) {
                        Text(
                            text = stringResource(R.string.btn_replace_photo),
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = GoldLight,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                        )
                    }

                    Surface(
                        color = ErrorRed.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier
                            .clickable { onRemove() }
                            .testTag("remove_product_slot_${slotIndex + 1}")
                    ) {
                        Text(
                            text = stringResource(R.string.btn_remove_photo),
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = ErrorRed,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    shape = CircleShape,
                    color = NavySurface,
                    border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.35f)),
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.AddPhotoAlternate,
                            contentDescription = "Tambah Foto Slot ${slotIndex + 1}",
                            tint = GoldChampagne,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = stringResource(R.string.slot_empty_label, slotIndex + 1),
                    style = MaterialTheme.typography.labelMedium,
                    color = WhiteSoft
                )
                Text(
                    text = if (slotIndex == 0) "Wajib" else "Tambah",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = if (slotIndex == 0) GoldChampagne else TextMuted
                )
            }
        }
    }
}

@Composable
fun OptionalSinglePhotoSection(
    title: String,
    description: String,
    emptyButtonText: String,
    emptyStatusHint: String,
    photo: UploadedPhoto?,
    onPickPhoto: () -> Unit,
    onRemovePhoto: () -> Unit,
    testTagPrefix: String,
    isModelIcon: Boolean = true,
    modifier: Modifier = Modifier
) {
    SectionContainerCard(
        title = title,
        badgeText = stringResource(R.string.section_optional_badge),
        isMandatory = false,
        description = description,
        modifier = modifier.testTag("${testTagPrefix}_section")
    ) {
        if (photo != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(NavyDeep, RoundedCornerShape(14.dp))
                    .border(1.dp, GoldPrimary.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Image(
                    bitmap = photo.bitmap.asImageBitmap(),
                    contentDescription = title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(84.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, GoldPrimary, RoundedCornerShape(12.dp))
                )

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = photo.label.ifBlank { "Foto Referensi Aktif" },
                        style = MaterialTheme.typography.titleSmall,
                        color = WhitePure
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = onPickPhoto,
                            border = BorderStroke(1.dp, GoldPrimary),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = GoldLight),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier
                                .heightIn(min = 48.dp)
                                .testTag("${testTagPrefix}_replace_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = stringResource(R.string.btn_replace_photo))
                        }

                        OutlinedButton(
                            onClick = onRemovePhoto,
                            border = BorderStroke(1.dp, ErrorRed.copy(alpha = 0.6f)),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorRed),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier
                                .heightIn(min = 48.dp)
                                .testTag("${testTagPrefix}_remove_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteOutline,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = stringResource(R.string.btn_remove_photo))
                        }
                    }
                }
            }
        } else {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .clickable { onPickPhoto() }
                    .testTag("${testTagPrefix}_upload_button"),
                color = NavyDeep,
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, NavyBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = NavySurface,
                        border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.4f)),
                        modifier = Modifier.size(44.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (isModelIcon) Icons.Default.Person else Icons.Outlined.Landscape,
                                contentDescription = null,
                                tint = GoldChampagne,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = emptyButtonText,
                            style = MaterialTheme.typography.titleSmall,
                            color = GoldLight
                        )
                        Text(
                            text = emptyStatusHint,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProductLinkSection(
    productUrl: String,
    isDetectingUrl: Boolean,
    detectedInfo: DetectedProductInfo?,
    onUrlChange: (String) -> Unit,
    onDetectClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    SectionContainerCard(
        title = stringResource(R.string.section_link_title),
        badgeText = stringResource(R.string.section_optional_badge),
        isMandatory = false,
        description = stringResource(R.string.section_link_desc),
        modifier = modifier.testTag("product_link_section")
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = productUrl,
                    onValueChange = onUrlChange,
                    placeholder = {
                        Text(
                            text = stringResource(R.string.hint_product_url),
                            color = TextMuted
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Link,
                            contentDescription = null,
                            tint = GoldChampagne
                        )
                    },
                    trailingIcon = {
                        if (productUrl.isNotEmpty()) {
                            IconButton(onClick = { onUrlChange("") }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Bersihkan URL",
                                    tint = TextMuted
                                )
                            }
                        }
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = NavyBorder,
                        focusedContainerColor = NavyDeep,
                        unfocusedContainerColor = NavyDeep,
                        focusedTextColor = WhitePure,
                        unfocusedTextColor = WhiteSoft
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("product_url_input")
                )

                Button(
                    onClick = onDetectClick,
                    enabled = !isDetectingUrl && productUrl.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GoldPrimary,
                        contentColor = NavyDeep,
                        disabledContainerColor = NavyElevated,
                        disabledContentColor = TextMuted
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .heightIn(min = 54.dp)
                        .testTag("detect_product_button")
                ) {
                    if (isDetectingUrl) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp,
                            color = NavyDeep
                        )
                    } else {
                        Text(
                            text = stringResource(R.string.btn_detect_product),
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }

            AnimatedVisibility(visible = detectedInfo != null) {
                detectedInfo?.let { info ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("detected_product_info_card"),
                        shape = RoundedCornerShape(12.dp),
                        color = if (info.isSuccess) BlueSurface.copy(alpha = 0.65f) else NavyDeep,
                        border = BorderStroke(
                            1.dp,
                            if (info.isSuccess) BlueAccent.copy(alpha = 0.5f) else GoldPrimary.copy(alpha = 0.4f)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = if (info.isSuccess) Icons.Default.CheckCircle else Icons.Outlined.Info,
                                    contentDescription = null,
                                    tint = if (info.isSuccess) SuccessGreen else GoldChampagne,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = info.statusMessage,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = if (info.isSuccess) WhitePure else GoldLight
                                )
                            }
                            if (info.isSuccess) {
                                Text(
                                    text = info.title,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = GoldLight
                                )
                                if (info.description.isNotBlank()) {
                                    Text(
                                        text = info.description,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextSecondary,
                                        maxLines = 3,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                if (info.variationsOrDetails.isNotBlank()) {
                                    Text(
                                        text = "Variasi / Detail: ${info.variationsOrDetails}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = BlueAccent
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NotesAndVariationSection(
    userNotes: String,
    onUserNotesChange: (String) -> Unit,
    variationCount: Int,
    onVariationCountSelect: (Int) -> Unit,
    isGenerating: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Section 5: Mau Dibuat Seperti Apa?
        SectionContainerCard(
            title = stringResource(R.string.section_notes_title),
            badgeText = stringResource(R.string.section_optional_badge),
            isMandatory = false,
            description = "Jelaskan gaya model, suasana latar, pencahayaan, atau instruksi khusus yang diinginkan. Jika dikosongkan, AI menentukan konsep terbaik sesuai produk."
        ) {
            OutlinedTextField(
                value = userNotes,
                onValueChange = onUserNotesChange,
                placeholder = {
                    Text(
                        text = stringResource(R.string.hint_user_notes),
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMuted
                    )
                },
                minLines = 3,
                maxLines = 5,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GoldPrimary,
                    unfocusedBorderColor = NavyBorder,
                    focusedContainerColor = NavyDeep,
                    unfocusedContainerColor = NavyDeep,
                    focusedTextColor = WhitePure,
                    unfocusedTextColor = WhiteSoft
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("user_notes_input")
            )
        }

        // Section 6: Jumlah Variasi (1, 2, 3, 4, 5 - Default 3)
        SectionContainerCard(
            title = stringResource(R.string.section_variations_title),
            badgeText = "$variationCount STORYBOARD",
            isMandatory = true,
            description = stringResource(R.string.section_variations_desc)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    for (count in 1..5) {
                        val isSelected = variationCount == count
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 52.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable(enabled = !isGenerating) { onVariationCountSelect(count) }
                                .testTag("variation_count_$count"),
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) GoldPrimary else NavyDeep,
                            border = BorderStroke(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) GoldLight else NavyBorder
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "$count",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) NavyDeep else WhiteSoft
                                )
                                Text(
                                    text = if (count == 3) "Default" else "Variasi",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    color = if (isSelected) NavyDeep.copy(alpha = 0.8f) else TextMuted
                                )
                            }
                        }
                    }
                }

                // Active variation angle preview list
                Surface(
                    color = NavyDeep.copy(alpha = 0.8f),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, NavyBorder)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "Konsep & Angle yang Akan Dibuat ($variationCount Lembar Storyboard 9:16):",
                            style = MaterialTheme.typography.labelMedium,
                            color = GoldChampagne
                        )
                        for (i in 1..variationCount) {
                            Row(
                                verticalAlignment = Alignment.Top,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "• Storyboard $i:",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = GoldLight,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${VariationGuide.getGuideTitle(i)} — ${VariationGuide.getGuideSubtitle(i)}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GenerateStoryboardButtonSection(
    isGenerating: Boolean,
    generationStatusText: String,
    globalErrorMessage: String?,
    onGenerateClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        AnimatedVisibility(visible = !globalErrorMessage.isNullOrBlank()) {
            globalErrorMessage?.let { errorMsg ->
                Surface(
                    color = ErrorRed.copy(alpha = 0.14f),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, ErrorRed.copy(alpha = 0.6f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("global_error_banner")
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ErrorOutline,
                            contentDescription = null,
                            tint = ErrorRed,
                            modifier = Modifier.size(22.dp)
                        )
                        Text(
                            text = errorMsg,
                            style = MaterialTheme.typography.bodyMedium,
                            color = WhiteSoft,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        val goldBrush = Brush.horizontalGradient(
            colors = if (isGenerating) {
                listOf(NavyElevated, NavySurface)
            } else {
                listOf(GoldChampagne, GoldPrimary, GoldDark)
            }
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 60.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(goldBrush)
                .border(
                    width = 1.dp,
                    color = if (isGenerating) GoldPrimary.copy(alpha = 0.4f) else GoldLight,
                    shape = RoundedCornerShape(16.dp)
                )
                .clickable(enabled = !isGenerating) { onGenerateClick() }
                .testTag("generate_storyboard_button"),
            contentAlignment = Alignment.Center
        ) {
            if (isGenerating) {
                Row(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 2.5.dp,
                        color = GoldChampagne
                    )
                    Text(
                        text = generationStatusText.ifBlank { stringResource(R.string.generating_storyboard) },
                        style = MaterialTheme.typography.titleSmall,
                        color = GoldLight,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            } else {
                Row(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ViewCarousel,
                        contentDescription = null,
                        tint = NavyDeep,
                        modifier = Modifier.size(22.dp)
                    )
                    Text(
                        text = stringResource(R.string.btn_generate_storyboard),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.4.sp
                        ),
                        color = NavyDeep
                    )
                }
            }
        }
    }
}

@Composable
fun SectionContainerCard(
    title: String,
    badgeText: String,
    isMandatory: Boolean,
    description: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = NavyCard),
        border = BorderStroke(
            width = 1.dp,
            color = if (isMandatory) GoldPrimary.copy(alpha = 0.35f) else NavyBorder
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = WhitePure,
                    modifier = Modifier.weight(1f)
                )

                Surface(
                    color = if (isMandatory) GoldSurface else NavySurface,
                    shape = RoundedCornerShape(50),
                    border = BorderStroke(
                        1.dp,
                        if (isMandatory) GoldPrimary.copy(alpha = 0.5f) else NavyBorder
                    )
                ) {
                    Text(
                        text = badgeText,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isMandatory) GoldLight else TextSecondary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(2.dp))

            content()
        }
    }
}
