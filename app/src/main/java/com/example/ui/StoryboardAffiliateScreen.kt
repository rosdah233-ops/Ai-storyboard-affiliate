package com.example.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.R
import com.example.ui.components.FullScreenStoryboardDialog
import com.example.ui.components.GenerateStoryboardButtonSection
import com.example.ui.components.NotesAndVariationSection
import com.example.ui.components.OptionalSinglePhotoSection
import com.example.ui.components.ProductLinkSection
import com.example.ui.components.ProductPhotosSection
import com.example.ui.components.StoryboardResultsHeader
import com.example.ui.components.StoryboardVariationCard
import com.example.ui.components.StudioHeaderSection
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.NavyBorder
import com.example.ui.theme.NavyCard
import com.example.ui.theme.NavyDeep
import com.example.ui.theme.TextMuted
import com.example.viewmodel.StoryboardViewModel

@Composable
fun StoryboardAffiliateScreen(
    viewModel: StoryboardViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()

    var activePickingSlotIndex by remember { mutableIntStateOf(0) }

    // 1. Single-slot product photo picker
    val singleProductPhotoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            viewModel.handlePickedProductPhotoForSlot(context, activePickingSlotIndex, uri)
        }
    }

    // 2. Multi-slot product photo picker (up to 5 photos)
    val multipleProductPhotosLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 5)
    ) { uris ->
        if (uris.isNotEmpty()) {
            viewModel.handlePickedMultipleProductPhotos(context, uris)
        }
    }

    // 3. Optional model photo picker
    val modelPhotoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            viewModel.handlePickedModelPhoto(context, uri)
        }
    }

    // 4. Optional background photo picker
    val backgroundPhotoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            viewModel.handlePickedBackgroundPhoto(context, uri)
        }
    }

    LaunchedEffect(uiState.snackbarMessage) {
        val msg = uiState.snackbarMessage
        if (!msg.isNullOrBlank()) {
            snackbarHostState.showSnackbar(msg)
            viewModel.clearSnackbar()
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyDeep),
        containerColor = NavyDeep,
        contentWindowInsets = WindowInsets.safeDrawing,
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = NavyCard,
                    contentColor = GoldLight,
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }
    ) { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            val isWideScreen = maxWidth >= 720.dp

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState),
                contentAlignment = Alignment.TopCenter
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 1040.dp)
                        .padding(horizontal = 16.dp, vertical = 16.dp)
                        .testTag("main_storyboard_scroll_column"),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 1. Header
                    StudioHeaderSection()

                    // 2. Mandatory Product Photos (5 Slots)
                    ProductPhotosSection(
                        productSlots = uiState.productSlots,
                        onPickForSlot = { slotIndex ->
                            activePickingSlotIndex = slotIndex
                            singleProductPhotoLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        onPickMultiple = {
                            multipleProductPhotosLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        onRemoveSlot = { slotIndex ->
                            viewModel.removeProductPhotoAtSlot(slotIndex)
                        },
                        onLoadSampleProduct = {
                            viewModel.loadSampleProductPhoto(context)
                        }
                    )

                    // 3. Optional Model Photo & Optional Background Photo (Side-by-side on wide screens, stacked on mobile)
                    if (isWideScreen) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            OptionalSinglePhotoSection(
                                title = stringResource(R.string.section_model_title),
                                description = stringResource(R.string.section_model_desc),
                                emptyButtonText = stringResource(R.string.btn_upload_model),
                                emptyStatusHint = "JPG, JPEG, PNG • Kosongkan untuk model konsep AI",
                                photo = uiState.modelPhoto,
                                onPickPhoto = {
                                    modelPhotoLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                                onRemovePhoto = { viewModel.removeModelPhoto() },
                                testTagPrefix = "model_photo",
                                isModelIcon = true,
                                modifier = Modifier.weight(1f)
                            )

                            OptionalSinglePhotoSection(
                                title = stringResource(R.string.section_bg_title),
                                description = stringResource(R.string.section_bg_desc),
                                emptyButtonText = stringResource(R.string.btn_upload_bg),
                                emptyStatusHint = "JPG, JPEG, PNG • Kosongkan untuk latar otomatis AI",
                                photo = uiState.backgroundPhoto,
                                onPickPhoto = {
                                    backgroundPhotoLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                                onRemovePhoto = { viewModel.removeBackgroundPhoto() },
                                testTagPrefix = "background_photo",
                                isModelIcon = false,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    } else {
                        OptionalSinglePhotoSection(
                            title = stringResource(R.string.section_model_title),
                            description = stringResource(R.string.section_model_desc),
                            emptyButtonText = stringResource(R.string.btn_upload_model),
                            emptyStatusHint = "JPG, JPEG, PNG • Kosongkan untuk model konsep AI",
                            photo = uiState.modelPhoto,
                            onPickPhoto = {
                                modelPhotoLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            onRemovePhoto = { viewModel.removeModelPhoto() },
                            testTagPrefix = "model_photo",
                            isModelIcon = true
                        )

                        OptionalSinglePhotoSection(
                            title = stringResource(R.string.section_bg_title),
                            description = stringResource(R.string.section_bg_desc),
                            emptyButtonText = stringResource(R.string.btn_upload_bg),
                            emptyStatusHint = "JPG, JPEG, PNG • Kosongkan untuk latar otomatis AI",
                            photo = uiState.backgroundPhoto,
                            onPickPhoto = {
                                backgroundPhotoLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            onRemovePhoto = { viewModel.removeBackgroundPhoto() },
                            testTagPrefix = "background_photo",
                            isModelIcon = false
                        )
                    }

                    // 4. Optional Product Link / Affiliate URL Detection
                    ProductLinkSection(
                        productUrl = uiState.productUrl,
                        isDetectingUrl = uiState.isDetectingUrl,
                        detectedInfo = uiState.detectedProductInfo,
                        onUrlChange = { viewModel.onProductUrlChange(it) },
                        onDetectClick = { viewModel.detectProductFromUrl() }
                    )

                    // 5 & 6. User Notes ("Mau Dibuat Seperti Apa?") & Variation Count (1..5, Default 3)
                    NotesAndVariationSection(
                        userNotes = uiState.userNotes,
                        onUserNotesChange = { viewModel.onUserNotesChange(it) },
                        variationCount = uiState.variationCount,
                        onVariationCountSelect = { viewModel.setVariationCount(it) },
                        isGenerating = uiState.isGeneratingBatch
                    )

                    // 7. Primary Gold "Generate Storyboard" Button
                    GenerateStoryboardButtonSection(
                        isGenerating = uiState.isGeneratingBatch,
                        generationStatusText = uiState.generationStatusText,
                        globalErrorMessage = uiState.globalErrorMessage,
                        onGenerateClick = { viewModel.generateStoryboards() }
                    )

                    // 8. Storyboard Results Section
                    if (uiState.variations.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))

                        StoryboardResultsHeader(
                            variationCount = uiState.variations.size,
                            productSummary = uiState.productAnalysisSummary,
                            onCopyAllPrompts = { viewModel.copyAllPrompts(context) }
                        )

                        if (isWideScreen) {
                            val chunked = uiState.variations.chunked(2)
                            chunked.forEach { rowItems ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    rowItems.forEach { variation ->
                                        StoryboardVariationCard(
                                            variation = variation,
                                            onToggleRevise = {
                                                viewModel.toggleRevisionInput(variation.variationNumber)
                                            },
                                            onRevisionTextChange = { text ->
                                                viewModel.updateRevisionInstruction(variation.variationNumber, text)
                                            },
                                            onSubmitRevision = {
                                                viewModel.reviseSingleStoryboard(variation.variationNumber)
                                            },
                                            onRegenerate = {
                                                viewModel.regenerateSingleStoryboard(variation.variationNumber)
                                            },
                                            onDownload = {
                                                viewModel.downloadStoryboard(context, variation)
                                            },
                                            onCopyPrompt = {
                                                viewModel.copySinglePrompt(context, variation)
                                            },
                                            onZoomImage = {
                                                viewModel.setZoomedVariation(variation)
                                            },
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                    if (rowItems.size == 1) {
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                        } else {
                            uiState.variations.forEach { variation ->
                                StoryboardVariationCard(
                                    variation = variation,
                                    onToggleRevise = {
                                        viewModel.toggleRevisionInput(variation.variationNumber)
                                    },
                                    onRevisionTextChange = { text ->
                                        viewModel.updateRevisionInstruction(variation.variationNumber, text)
                                    },
                                    onSubmitRevision = {
                                        viewModel.reviseSingleStoryboard(variation.variationNumber)
                                    },
                                    onRegenerate = {
                                        viewModel.regenerateSingleStoryboard(variation.variationNumber)
                                    },
                                    onDownload = {
                                        viewModel.downloadStoryboard(context, variation)
                                    },
                                    onCopyPrompt = {
                                        viewModel.copySinglePrompt(context, variation)
                                    },
                                    onZoomImage = {
                                        viewModel.setZoomedVariation(variation)
                                    }
                                )
                            }
                        }
                    }

                    // Prototype API Key Security Caution Notice
                    Surface(
                        color = NavyCard.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, NavyBorder.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = stringResource(R.string.security_warning_banner),
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(12.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }

    // Full-screen zoom modal for inspecting the 5-panel 9:16 storyboard sheet
    uiState.zoomedVariation?.let { zoomed ->
        FullScreenStoryboardDialog(
            variation = zoomed,
            onDismiss = { viewModel.setZoomedVariation(null) },
            onDownload = { viewModel.downloadStoryboard(context, zoomed) }
        )
    }
}
