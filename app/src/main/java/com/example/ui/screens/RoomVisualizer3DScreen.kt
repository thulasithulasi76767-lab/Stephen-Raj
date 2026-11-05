package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.ProjectEntity
import com.example.ui.components.LuxuryCard
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*

data class RoomTemplate(
    val id: String,
    val name: String,
    val dimensions: String,
    val description: String,
    val renderRes: Int,
    val defaultItems: List<FurnishingItem>
)

data class FurnishingItem(
    val id: String,
    val name: String,
    val category: String,
    val finish: String,
    val priceInr: Double,
    val iconName: String,
    var positionX: Float,
    var positionY: Float,
    val colorHex: Long
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomVisualizer3DScreen(
    projects: List<ProjectEntity>,
    onAttachDesignToProject: (projectId: Long, projectTitle: String, designSummary: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedProject by remember { mutableStateOf(projects.firstOrNull()) }
    var selectedTemplateIndex by remember { mutableIntStateOf(0) }
    var viewMode by remember { mutableIntStateOf(0) } // 0: 3D Render View, 1: 2D Spatial Floor Plan Editor
    var isDayLighting by remember { mutableStateOf(true) }
    var activeWallFinish by remember { mutableStateOf("Travertino Roman Marble") }
    var showAttachDialog by remember { mutableStateOf(false) }

    val templates = listOf(
        RoomTemplate(
            id = "living_penthouse",
            name = "Indiranagar Penthouse Grand Living",
            dimensions = "28 ft x 18 ft • 504 Sq.Ft",
            description = "Double-height living space featuring floor-to-ceiling panoramic glass, floating travertine television console, and champagne gold recessed linear cove LEDs.",
            renderRes = R.drawable.img_luxury_living,
            defaultItems = listOf(
                FurnishingItem("f1", "Bespoke Beige Curved Sectional", "Furniture", "Italian Boucle Velvet", 320000.0, "Sofa", 0.35f, 0.45f, 0xFFE5C158),
                FurnishingItem("f2", "Calacatta Gold Low Cocktail Table", "Furniture", "Honed Quartz & Brass", 115000.0, "Table", 0.50f, 0.50f, 0xFFD4AF37),
                FurnishingItem("f3", "Architectural Brass Fluted Console", "Millwork", "Smoked Oak & Brass", 195000.0, "Cabinet", 0.80f, 0.25f, 0xFF997A15),
                FurnishingItem("f4", "Flos Dimmable Cove Lighting Array", "Lighting", "2700K Warm Circadian", 85000.0, "Light", 0.50f, 0.15f, 0xFFFAF7F2)
            )
        ),
        RoomTemplate(
            id = "kitchen_italian",
            name = "Milanese Minimalist Island Kitchen",
            dimensions = "20 ft x 14 ft • 280 Sq.Ft",
            description = "Gourmet open-concept modular kitchen with 10-ft monolithic quartz island, Blum servo-drive motorized lift cabinetry, and concealed wine cooler column.",
            renderRes = R.drawable.img_modular_kitchen,
            defaultItems = listOf(
                FurnishingItem("k1", "Monolithic Island with Waterfall Edge", "Counter", "Calacatta Gold Quartz", 450000.0, "Island", 0.50f, 0.50f, 0xFFD4AF37),
                FurnishingItem("k2", "Blum Aventos Overhead Fluted Cabinets", "Cabinetry", "Anthracite Matte & Fluted Oak", 380000.0, "Cabinet", 0.50f, 0.20f, 0xFF8E8880),
                FurnishingItem("k3", "Architectural Linear Pendant Bar", "Lighting", "Brushed Brass Warm LED", 95000.0, "Light", 0.50f, 0.45f, 0xFFE5C158),
                FurnishingItem("k4", "Bar Stools with Leather Upholstery (Set of 3)", "Furniture", "Tan Italian Nappa", 120000.0, "Chair", 0.50f, 0.65f, 0xFFF7E7A9)
            )
        ),
        RoomTemplate(
            id = "bedroom_sanctuary",
            name = "Presidential Master Suite & Dressing",
            dimensions = "24 ft x 16 ft • 384 Sq.Ft",
            description = "Acoustic fluted headboard paneling, bronze-tinted walk-in dressing wardrobes with integrated sensor LEDs, and intimate lounge seating nook.",
            renderRes = R.drawable.img_master_bedroom,
            defaultItems = listOf(
                FurnishingItem("b1", "King Size Floating Platform Bed", "Bed", "Taupe Velvet & Integrated Tables", 290000.0, "Bed", 0.50f, 0.40f, 0xFFE5C158),
                FurnishingItem("b2", "Tinted Bronze Glass Wardrobe Wall", "Wardrobe", "Anodized Champagne Frame", 420000.0, "Wardrobe", 0.85f, 0.50f, 0xFF997A15),
                FurnishingItem("b3", "Lounge Reading Armchair & Ottoman", "Furniture", "Sand Boucle", 98000.0, "Chair", 0.20f, 0.70f, 0xFFF4EDE4),
                FurnishingItem("b4", "Concealed Perimeter Cove Lighting", "Lighting", "Tunable White Smart LED", 75000.0, "Light", 0.50f, 0.10f, 0xFFFAF7F2)
            )
        ),
        RoomTemplate(
            id = "office_suite",
            name = "Domlur Executive Boardroom & Suite",
            dimensions = "32 ft x 22 ft • 704 Sq.Ft",
            description = "Corporate executive suite with acoustic ribbed glass partitions, 14-seater walnut conference table, and motorized biophilic greenery paneling.",
            renderRes = R.drawable.img_luxury_office,
            defaultItems = listOf(
                FurnishingItem("o1", "Bespoke Walnut Boardroom Table", "Conference", "Solid American Walnut", 580000.0, "Table", 0.50f, 0.50f, 0xFF997A15),
                FurnishingItem("o2", "Ergonomic Executive High-Back Chairs (10)", "Seating", "Black Top-Grain Leather", 450000.0, "Chair", 0.50f, 0.50f, 0xFF222226),
                FurnishingItem("o3", "Acoustic Glass Demountable Partition", "Partition", "Double Glazed Acoustic Black", 320000.0, "Wall", 0.50f, 0.15f, 0xFF8E8880),
                FurnishingItem("o4", "Biophilic Live Green Wall System", "Decor", "Automated Drip Irrigation", 160000.0, "Plant", 0.15f, 0.50f, 0xFF2E7D32)
            )
        )
    )

    val currentTemplate = templates[selectedTemplateIndex]
    var floorPlanItems by remember(selectedTemplateIndex) { mutableStateOf(currentTemplate.defaultItems) }

    val totalDecorCost = floorPlanItems.sumOf { it.priceInr }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 40.dp)
    ) {
        // Room Template Selector Header
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "3D SPATIAL ARCHITECTURAL STUDIO",
                            style = MaterialTheme.typography.labelSmall,
                            color = ChampagneGold,
                            letterSpacing = 1.5.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Interactive Room Visualization",
                            style = MaterialTheme.typography.titleLarge,
                            color = TextPrimaryDark,
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Mode Switcher (3D Photoreal Render vs Interactive 2D Plan)
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(CharcoalSurface)
                            .border(1.dp, CharcoalBorder, RoundedCornerShape(10.dp))
                            .padding(4.dp)
                    ) {
                        IconButton(
                            onClick = { viewMode = 0 },
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (viewMode == 0) ImperialGold else Color.Transparent)
                        ) {
                            Icon(Icons.Default.ViewInAr, contentDescription = "3D Render", tint = if (viewMode == 0) ObsidianBlack else TextSecondaryDark, modifier = Modifier.size(18.dp))
                        }
                        IconButton(
                            onClick = { viewMode = 1 },
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (viewMode == 1) ImperialGold else Color.Transparent)
                        ) {
                            Icon(Icons.Default.Architecture, contentDescription = "Floor Plan", tint = if (viewMode == 1) ObsidianBlack else TextSecondaryDark, modifier = Modifier.size(18.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Template Chips
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(templates.indices.toList()) { index ->
                        val isSelected = index == selectedTemplateIndex
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedTemplateIndex = index },
                            label = { Text(templates[index].name, style = MaterialTheme.typography.labelSmall) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ImperialGold,
                                selectedLabelColor = ObsidianBlack,
                                containerColor = CharcoalSurface,
                                labelColor = TextPrimaryDark
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                borderColor = CharcoalBorder,
                                selectedBorderColor = ChampagneGold,
                                enabled = true,
                                selected = isSelected
                            ),
                            shape = RoundedCornerShape(16.dp)
                        )
                    }
                }
            }
        }

        // Main Visualizer Canvas
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .border(1.dp, ImperialGold.copy(alpha = 0.45f), RoundedCornerShape(18.dp))
            ) {
                if (viewMode == 0) {
                    // 3D Realistic Render View
                    Image(
                        painter = painterResource(id = currentTemplate.renderRes),
                        contentDescription = currentTemplate.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    // Ambient Light Simulation Overlay
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                if (isDayLighting) Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.65f)))
                                else Brush.verticalGradient(listOf(Color(0xFF0F0B05).copy(alpha = 0.4f), Color(0xFF0F0F11).copy(alpha = 0.85f)))
                            )
                    )

                    // Overlay HUD Controls
                    Row(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { isDayLighting = !isDayLighting },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ObsidianBlack.copy(alpha = 0.8f),
                                contentColor = ChampagneGold
                            ),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = if (isDayLighting) Icons.Default.WbSunny else Icons.Default.NightsStay,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (isDayLighting) "Daylight (5000K)" else "Night Cove (2700K)", style = MaterialTheme.typography.labelSmall)
                        }
                    }

                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(14.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(ImperialGold)
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "3D PHOTOREALISTIC RAYTRACING",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = ObsidianBlack,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = currentTemplate.dimensions,
                                style = MaterialTheme.typography.labelSmall,
                                color = SoftBeige
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = currentTemplate.name,
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif
                        )
                    }
                } else {
                    // Interactive 2D Spatial Floor Plan Layout Editor
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(DeepCharcoal)
                    ) {
                        // Architectural Grid Canvas
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val gridSpacing = 40f
                            // Grid lines
                            for (x in 0 until (size.width / gridSpacing).toInt()) {
                                drawLine(
                                    color = CharcoalBorder.copy(alpha = 0.5f),
                                    start = Offset(x * gridSpacing, 0f),
                                    end = Offset(x * gridSpacing, size.height),
                                    strokeWidth = 1f
                                )
                            }
                            for (y in 0 until (size.height / gridSpacing).toInt()) {
                                drawLine(
                                    color = CharcoalBorder.copy(alpha = 0.5f),
                                    start = Offset(0f, y * gridSpacing),
                                    end = Offset(size.width, y * gridSpacing),
                                    strokeWidth = 1f
                                )
                            }
                            // Outer Wall Boundary
                            drawRect(
                                color = ImperialGold,
                                topLeft = Offset(20f, 20f),
                                size = Size(size.width - 40f, size.height - 40f),
                                style = Stroke(width = 4f)
                            )
                        }

                        // Furnishing Nodes Placed on Plan
                        floorPlanItems.forEachIndexed { idx, item ->
                            Box(
                                modifier = Modifier
                                    .offset(
                                        x = (item.positionX * 240).dp,
                                        y = (item.positionY * 200).dp
                                    )
                                    .size(72.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(item.colorHex).copy(alpha = 0.25f))
                                    .border(1.5.dp, Color(item.colorHex), RoundedCornerShape(8.dp))
                                    .padding(4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Default.Chair,
                                        contentDescription = item.name,
                                        tint = Color(item.colorHex),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        text = item.name.take(12),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextPrimaryDark,
                                        fontSize = 9.sp,
                                        maxLines = 1
                                    )
                                }
                            }
                        }

                        // Plan Legend Overlay
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(10.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(ObsidianBlack.copy(alpha = 0.85f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "2D ARCHITECTURAL SCHEMATIC • ${floorPlanItems.size} SPECIFIED ELEMENTS",
                                style = MaterialTheme.typography.labelSmall,
                                color = ChampagneGold
                            )
                        }
                    }
                }
            }
        }

        // Spatial Specifications and Material Palette Selection
        item {
            LuxuryCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "SPECIFIED FINISHES & ACCENTS",
                    style = MaterialTheme.typography.labelSmall,
                    color = ImperialGold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = currentTemplate.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = SoftBeige
                )

                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "Wall Cladding Texture / Plaster:",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondaryDark
                )
                val finishes = listOf("Travertino Roman Marble", "Oikos Venetian Stucco", "Fluted Oak Wood Paneling", "Acoustic Fabric Baffles")
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(top = 6.dp)
                ) {
                    items(finishes) { finish ->
                        val isSelected = finish == activeWallFinish
                        FilterChip(
                            selected = isSelected,
                            onClick = { activeWallFinish = finish },
                            label = { Text(finish, style = MaterialTheme.typography.labelSmall) },
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
        }

        // Curated Furniture & Decor Bill of Materials for this 3D Room
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "CURATED FURNITURE & LIGHTING SPECIFICATIONS",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondaryDark,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Total: ₹${(totalDecorCost / 100000).toInt()}L+",
                    style = MaterialTheme.typography.labelSmall,
                    color = ImperialGold,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        items(floorPlanItems) { item ->
            LuxuryCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(item.colorHex).copy(alpha = 0.2f))
                                .border(1.dp, Color(item.colorHex), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Chair, contentDescription = null, tint = Color(item.colorHex), modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = item.name,
                                style = MaterialTheme.typography.titleMedium,
                                color = TextPrimaryDark,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${item.category} • ${item.finish}",
                                style = MaterialTheme.typography.labelSmall,
                                color = SoftBeige
                            )
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "₹${item.priceInr.toInt()}",
                            style = MaterialTheme.typography.titleMedium,
                            color = ImperialGold,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Specified",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF81C784)
                        )
                    }
                }
            }
        }

        // Attach to Project Button (Project Management Integration)
        item {
            Button(
                onClick = { showAttachDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("btn_attach_3d_to_project"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ImperialGold,
                    contentColor = ObsidianBlack
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.BookmarkAdd, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Attach 3D Concept Design to Commissioned Project",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }

    if (showAttachDialog) {
        AlertDialog(
            onDismissRequest = { showAttachDialog = false },
            containerColor = DeepCharcoal,
            title = {
                Text(
                    text = "Attach 3D Visualization to Project",
                    color = ChampagneGold,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Concept: ${currentTemplate.name}",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimaryDark
                    )
                    Text(
                        text = "Wall Texture: $activeWallFinish • ${floorPlanItems.size} Bespoke Furniture Items",
                        style = MaterialTheme.typography.bodyMedium,
                        color = SoftBeige
                    )

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Select Commissioned Project:",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondaryDark
                    )

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(projects) { p ->
                            FilterChip(
                                selected = selectedProject?.id == p.id,
                                onClick = { selectedProject = p },
                                label = { Text(p.title, style = MaterialTheme.typography.labelSmall) },
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
            },
            confirmButton = {
                Button(
                    onClick = {
                        val p = selectedProject
                        if (p != null) {
                            val summary = "Attached 3D Room Concept: ${currentTemplate.name} with $activeWallFinish and ${floorPlanItems.size} bespoke furniture pieces."
                            onAttachDesignToProject(p.id, p.title, summary)
                            showAttachDialog = false
                        }
                    },
                    enabled = selectedProject != null,
                    colors = ButtonDefaults.buttonColors(containerColor = ImperialGold, contentColor = ObsidianBlack)
                ) {
                    Text("Save to Project")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAttachDialog = false }) {
                    Text("Cancel", color = TextSecondaryDark)
                }
            }
        )
    }
}
