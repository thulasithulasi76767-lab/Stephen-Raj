package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.PortfolioEntity
import com.example.ui.components.LuxuryCard
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PortfolioGalleryScreen(
    portfolioItems: List<PortfolioEntity>,
    onAddPortfolioItem: (title: String, category: String, client: String, loc: String, year: String, sqft: Int, desc: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf("All") }
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedItemForDetail by remember { mutableStateOf<PortfolioEntity?>(null) }

    val categories = listOf("All", "Living Room", "Modular Kitchen", "Bedroom", "Commercial")

    val filteredItems = remember(portfolioItems, selectedCategory) {
        if (selectedCategory == "All") portfolioItems
        else portfolioItems.filter { it.category.equals(selectedCategory, ignoreCase = true) }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = ImperialGold,
                contentColor = ObsidianBlack,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.testTag("fab_add_portfolio")
            ) {
                Icon(Icons.Default.AddPhotoAlternate, contentDescription = "Upload Project")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            // Category Filter Bar
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(vertical = 12.dp)
            ) {
                items(categories) { cat ->
                    val isSelected = cat == selectedCategory
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedCategory = cat },
                        label = {
                            Text(
                                text = cat,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = CharcoalSurface,
                            labelColor = TextSecondaryDark,
                            selectedContainerColor = ImperialGold,
                            selectedLabelColor = ObsidianBlack
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            borderColor = CharcoalBorder,
                            selectedBorderColor = ChampagneGold,
                            enabled = true,
                            selected = isSelected
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )
                }
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(18.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(filteredItems, key = { it.id }) { item ->
                    PortfolioItemCard(
                        item = item,
                        onClick = { selectedItemForDetail = item }
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        AddPortfolioDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { title, cat, client, loc, year, sqft, desc ->
                onAddPortfolioItem(title, cat, client, loc, year, sqft, desc)
                showAddDialog = false
            }
        )
    }

    selectedItemForDetail?.let { item ->
        PortfolioDetailModal(
            item = item,
            onDismiss = { selectedItemForDetail = null }
        )
    }
}

@Composable
fun PortfolioItemCard(
    item: PortfolioEntity,
    onClick: () -> Unit
) {
    LuxuryCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(12.dp))
        ) {
            Image(
                painter = painterResource(id = item.coverImageRes),
                contentDescription = item.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f))
                        )
                    )
            )
            Row(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(ImperialGold)
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = item.category.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = ObsidianBlack,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${item.areaSqFt} SQ.FT • ${item.location}",
                    style = MaterialTheme.typography.labelSmall,
                    color = SoftBeige
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimaryDark,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif
                )
                Text(
                    text = "Client: ${item.clientName} • Delivered ${item.completionYear}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = ImperialGold
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = item.description,
            style = MaterialTheme.typography.bodyMedium,
            color = SoftBeige,
            maxLines = 3
        )

        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(Icons.Default.Compare, contentDescription = null, tint = ChampagneGold, modifier = Modifier.size(16.dp))
                Text(
                    text = "Before & After Available",
                    style = MaterialTheme.typography.labelSmall,
                    color = ChampagneGold
                )
            }
            Text(
                text = "Inspect Case Study →",
                style = MaterialTheme.typography.labelSmall,
                color = PaleGold,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun PortfolioDetailModal(
    item: PortfolioEntity,
    onDismiss: () -> Unit
) {
    var showAfterImage by remember { mutableStateOf(true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DeepCharcoal,
        title = {
            Column {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleLarge,
                    color = ChampagneGold,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${item.category} • ${item.location} (${item.areaSqFt} sq.ft)",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondaryDark
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Before / After Toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(CharcoalSurface)
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Button(
                        onClick = { showAfterImage = false },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (!showAfterImage) ImperialGold else Color.Transparent,
                            contentColor = if (!showAfterImage) ObsidianBlack else TextSecondaryDark
                        ),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Before State")
                    }
                    Button(
                        onClick = { showAfterImage = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (showAfterImage) ImperialGold else Color.Transparent,
                            contentColor = if (showAfterImage) ObsidianBlack else TextSecondaryDark
                        ),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("After Transformation")
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(12.dp))
                ) {
                    Image(
                        painter = painterResource(
                            id = if (showAfterImage) (item.afterImageRes ?: item.coverImageRes)
                                 else (item.beforeImageRes ?: R.drawable.img_luxury_living)
                        ),
                        contentDescription = "Project State",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color.Black.copy(alpha = 0.75f))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (showAfterImage) "COMPLETED LUXURY RESIDENCE" else "RAW CIVIL HANDOVER STATE",
                            style = MaterialTheme.typography.labelSmall,
                            color = ChampagneGold,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = SoftBeige
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = ImperialGold, contentColor = ObsidianBlack)
            ) {
                Text("Close Case Study")
            }
        }
    )
}

@Composable
fun AddPortfolioDialog(
    onDismiss: () -> Unit,
    onConfirm: (title: String, category: String, client: String, loc: String, year: String, sqft: Int, desc: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Living Room") }
    var client by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("Bengaluru") }
    var year by remember { mutableStateOf("2026") }
    var sqftStr by remember { mutableStateOf("3200") }
    var desc by remember { mutableStateOf("") }

    val categories = listOf("Living Room", "Modular Kitchen", "Bedroom", "Commercial", "Full Home")

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DeepCharcoal,
        title = {
            Text("Publish Completed Portfolio Project", color = ChampagneGold, fontWeight = FontWeight.Bold)
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Project Showcase Title") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = luxuryFieldColors()
                    )
                }

                item {
                    Text(text = "Category:", style = MaterialTheme.typography.labelSmall, color = TextSecondaryDark)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(categories) { cat ->
                            FilterChip(
                                selected = category == cat,
                                onClick = { category = cat },
                                label = { Text(cat, style = MaterialTheme.typography.labelSmall) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = ImperialGold,
                                    selectedLabelColor = ObsidianBlack,
                                    containerColor = CharcoalSurface,
                                    labelColor = TextPrimaryDark
                                )
                            )
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = client,
                        onValueChange = { client = it },
                        label = { Text("Client / Estate Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = luxuryFieldColors()
                    )
                }

                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = location,
                            onValueChange = { location = it },
                            label = { Text("Location") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            colors = luxuryFieldColors()
                        )
                        OutlinedTextField(
                            value = sqftStr,
                            onValueChange = { sqftStr = it },
                            label = { Text("Area (Sq Ft)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            colors = luxuryFieldColors()
                        )
                    }
                }

                item {
                    OutlinedTextField(
                        value = desc,
                        onValueChange = { desc = it },
                        label = { Text("Architectural Design Notes & Highlights") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3,
                        colors = luxuryFieldColors()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val sqft = sqftStr.toIntOrNull() ?: 2500
                    if (title.isNotBlank()) {
                        onConfirm(title, category, client, location, year, sqft, desc)
                    }
                },
                enabled = title.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = ImperialGold, contentColor = ObsidianBlack),
                modifier = Modifier.testTag("btn_save_portfolio")
            ) {
                Text("Publish to Gallery", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondaryDark)
            }
        }
    )
}
