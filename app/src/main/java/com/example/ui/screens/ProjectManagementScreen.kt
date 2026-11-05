package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import com.example.data.CustomerEntity
import com.example.data.ProjectEntity
import com.example.data.TeamMemberEntity
import com.example.ui.components.LuxuryCard
import com.example.ui.components.StatusBadge
import com.example.ui.components.formatInr
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectManagementScreen(
    projects: List<ProjectEntity>,
    customers: List<CustomerEntity>,
    teamMembers: List<TeamMemberEntity>,
    projectMaterials: List<com.example.data.ProjectMaterialEntity> = emptyList(),
    onCreateProject: (title: String, customerId: Long, customerName: String, category: String, totalBudget: Double, startDate: String, deadlineDate: String, assignedLead: String, siteLocation: String, scopeSummary: String) -> Unit,
    onUpdateProgress: (ProjectEntity, Int, String) -> Unit,
    onDeleteProject: (ProjectEntity) -> Unit,
    onOpen3DStudio: () -> Unit,
    onOpenMaterialLibrary: () -> Unit,
    onGenerateQuotationFromMaterials: (projectId: Long, projectTitle: String, customerName: String, customerEmail: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showCreateDialog by remember { mutableStateOf(false) }
    var selectedCategoryFilter by remember { mutableStateOf("All") }
    var projectToEditProgress by remember { mutableStateOf<ProjectEntity?>(null) }

    val categories = listOf("All", "Full Home", "Modular Kitchen", "Living Room", "Bedroom", "Commercial")

    val filteredProjects = remember(projects, selectedCategoryFilter) {
        if (selectedCategoryFilter == "All") projects
        else projects.filter { it.category.equals(selectedCategoryFilter, ignoreCase = true) }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateDialog = true },
                containerColor = ImperialGold,
                contentColor = ObsidianBlack,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.testTag("fab_create_project")
            ) {
                Icon(Icons.Default.AddHomeWork, contentDescription = "New Project")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            // Category Filter Pills
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(vertical = 12.dp)
            ) {
                items(categories) { category ->
                    val isSelected = category == selectedCategoryFilter
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedCategoryFilter = category },
                        label = {
                            Text(
                                text = category,
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

            if (filteredProjects.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 60.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Architecture,
                            contentDescription = null,
                            tint = ImperialGold.copy(alpha = 0.5f),
                            modifier = Modifier.size(54.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("No projects in this category", color = TextSecondaryDark, style = MaterialTheme.typography.titleMedium)
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(filteredProjects, key = { it.id }) { project ->
                        val attachedMaterials = projectMaterials.filter { it.projectId == project.id }
                        ProjectItemCard(
                            project = project,
                            attachedMaterials = attachedMaterials,
                            onUpdateProgress = { projectToEditProgress = project },
                            onOpen3DStudio = onOpen3DStudio,
                            onOpenMaterialLibrary = onOpenMaterialLibrary,
                            onGenerateQuotation = {
                                val cEmail = customers.find { it.id == project.customerId }?.email ?: "client@domain.com"
                                onGenerateQuotationFromMaterials(project.id, project.title, project.customerName, cEmail)
                            },
                            onDelete = { onDeleteProject(project) }
                        )
                    }
                }
            }
        }
    }

    if (showCreateDialog) {
        CreateProjectDialog(
            customers = customers,
            teamMembers = teamMembers,
            onDismiss = { showCreateDialog = false },
            onConfirm = { title, cId, cName, cat, budget, start, deadline, lead, loc, scope ->
                onCreateProject(title, cId, cName, cat, budget, start, deadline, lead, loc, scope)
                showCreateDialog = false
            }
        )
    }

    projectToEditProgress?.let { project ->
        UpdateProgressDialog(
            project = project,
            onDismiss = { projectToEditProgress = null },
            onConfirm = { newProgress, newStatus ->
                onUpdateProgress(project, newProgress, newStatus)
                projectToEditProgress = null
            }
        )
    }
}

@Composable
fun ProjectItemCard(
    project: ProjectEntity,
    attachedMaterials: List<com.example.data.ProjectMaterialEntity> = emptyList(),
    onUpdateProgress: () -> Unit,
    onOpen3DStudio: () -> Unit,
    onOpenMaterialLibrary: () -> Unit,
    onGenerateQuotation: () -> Unit,
    onDelete: () -> Unit
) {
    LuxuryCard(modifier = Modifier.fillMaxWidth()) {
        if (project.coverImageRes != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(RoundedCornerShape(12.dp))
            ) {
                Image(
                    painter = painterResource(id = project.coverImageRes),
                    contentDescription = project.title,
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
                            .background(ObsidianBlack.copy(alpha = 0.75f))
                            .border(1.dp, ImperialGold, RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = project.category.uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            color = ChampagneGold,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = project.siteLocation,
                        style = MaterialTheme.typography.labelSmall,
                        color = SoftBeige
                    )
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = project.title,
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimaryDark,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Client: ${project.customerName}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondaryDark
                )
            }
            StatusBadge(status = project.status)
        }

        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = project.scopeSummary,
            style = MaterialTheme.typography.bodyMedium,
            color = SoftBeige,
            maxLines = 3
        )

        // Attached Material Specifications Section
        Spacer(modifier = Modifier.height(10.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(CharcoalSurface)
                .padding(10.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Inventory2, contentDescription = null, tint = ImperialGold, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "ATTACHED MATERIALS (${attachedMaterials.size})",
                            style = MaterialTheme.typography.labelSmall,
                            color = ChampagneGold,
                            letterSpacing = 1.sp
                        )
                    }
                    Text(
                        text = "+ Specify More",
                        style = MaterialTheme.typography.labelSmall,
                        color = PaleGold,
                        modifier = Modifier.clickable { onOpenMaterialLibrary() }
                    )
                }

                if (attachedMaterials.isEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("No materials attached yet. Select from Material Library.", style = MaterialTheme.typography.labelSmall, color = TextSecondaryDark)
                } else {
                    Spacer(modifier = Modifier.height(6.dp))
                    attachedMaterials.forEach { mat ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("• ${mat.materialName} (${mat.quantity.toInt()} ${mat.unit})", style = MaterialTheme.typography.bodyMedium, color = TextPrimaryDark, modifier = Modifier.weight(1f))
                            Text(formatInr(mat.totalCost), style = MaterialTheme.typography.labelSmall, color = ImperialGold, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = onGenerateQuotation,
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Icon(Icons.Default.Receipt, contentDescription = null, tint = PaleGold, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Generate Quotation from Materials →", color = PaleGold, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Overall Progress", style = MaterialTheme.typography.labelSmall, color = TextSecondaryDark)
            Text(text = "${project.progressPercentage}% Completed", style = MaterialTheme.typography.labelSmall, color = ChampagneGold, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(6.dp))
        LinearProgressIndicator(
            progress = { project.progressPercentage / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = ImperialGold,
            trackColor = CharcoalBorder
        )

        Spacer(modifier = Modifier.height(14.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(CharcoalSurface)
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = "TOTAL BUDGET", style = MaterialTheme.typography.labelSmall, color = TextSecondaryDark)
                Text(text = formatInr(project.totalBudget), style = MaterialTheme.typography.titleMedium, color = ImperialGold, fontWeight = FontWeight.Bold)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(text = "DEADLINE", style = MaterialTheme.typography.labelSmall, color = TextSecondaryDark)
                Text(text = project.deadlineDate, style = MaterialTheme.typography.titleMedium, color = TextPrimaryDark, fontWeight = FontWeight.SemiBold)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Lead: ${project.assignedLead}",
                style = MaterialTheme.typography.labelSmall,
                color = SoftBeige
            )
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                // 3D Visualizer Launch button
                IconButton(
                    onClick = onOpen3DStudio,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(CharcoalSurface)
                        .border(1.dp, ImperialGold, CircleShape)
                ) {
                    Icon(Icons.Default.ViewInAr, contentDescription = "3D Room Studio", tint = ImperialGold, modifier = Modifier.size(18.dp))
                }

                OutlinedButton(
                    onClick = onUpdateProgress,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = PaleGold),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = Brush.linearGradient(listOf(ImperialGold, CharcoalBorder))
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Milestone", style = MaterialTheme.typography.labelSmall)
                }

                IconButton(onClick = onDelete, modifier = Modifier.size(34.dp)) {
                    Icon(Icons.Default.DeleteOutline, contentDescription = "Delete Project", tint = Color(0xFFEF5350), modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@Composable
fun UpdateProgressDialog(
    project: ProjectEntity,
    onDismiss: () -> Unit,
    onConfirm: (Int, String) -> Unit
) {
    var progressSlider by remember { mutableFloatStateOf(project.progressPercentage.toFloat()) }
    var selectedStatus by remember { mutableStateOf(project.status) }
    val statuses = listOf("Planning", "3D Rendering", "Material Procurement", "In Progress", "Handover Ready", "Completed")

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DeepCharcoal,
        title = {
            Text("Update Milestone Progress", color = ChampagneGold, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text(text = project.title, style = MaterialTheme.typography.titleMedium, color = TextPrimaryDark)

                Text(
                    text = "Progress: ${progressSlider.toInt()}%",
                    color = ImperialGold,
                    fontWeight = FontWeight.Bold
                )
                Slider(
                    value = progressSlider,
                    onValueChange = { progressSlider = it },
                    valueRange = 0f..100f,
                    colors = SliderDefaults.colors(
                        thumbColor = ImperialGold,
                        activeTrackColor = ImperialGold,
                        inactiveTrackColor = CharcoalBorder
                    )
                )

                Text(text = "Current Status / Phase:", style = MaterialTheme.typography.labelSmall, color = TextSecondaryDark)
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    statuses.chunked(2).forEach { rowStatuses ->
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            rowStatuses.forEach { st ->
                                FilterChip(
                                    selected = selectedStatus == st,
                                    onClick = {
                                        selectedStatus = st
                                        if (st == "Completed") progressSlider = 100f
                                    },
                                    label = { Text(st, style = MaterialTheme.typography.labelSmall) },
                                    modifier = Modifier.weight(1f),
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
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(progressSlider.toInt(), selectedStatus) },
                colors = ButtonDefaults.buttonColors(containerColor = ImperialGold, contentColor = ObsidianBlack)
            ) {
                Text("Save Updates")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondaryDark)
            }
        }
    )
}

@Composable
fun CreateProjectDialog(
    customers: List<CustomerEntity>,
    teamMembers: List<TeamMemberEntity>,
    onDismiss: () -> Unit,
    onConfirm: (title: String, customerId: Long, customerName: String, category: String, totalBudget: Double, startDate: String, deadlineDate: String, assignedLead: String, siteLocation: String, scopeSummary: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var selectedCustomer by remember { mutableStateOf(customers.firstOrNull()) }
    var category by remember { mutableStateOf("Full Home") }
    var budgetStr by remember { mutableStateOf("4500000") }
    var startDate by remember { mutableStateOf("15 Nov 2026") }
    var deadlineDate by remember { mutableStateOf("30 Mar 2027") }
    var assignedLead by remember { mutableStateOf(teamMembers.firstOrNull()?.name ?: "Ar. Stephen Raj") }
    var location by remember { mutableStateOf("Domlur, Bengaluru") }
    var scopeSummary by remember { mutableStateOf("") }

    val categories = listOf("Full Home", "Modular Kitchen", "Living Room", "Bedroom", "Office", "Commercial")

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DeepCharcoal,
        title = {
            Text("Commission New Project", color = ChampagneGold, fontWeight = FontWeight.Bold)
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
                        label = { Text("Project Title (e.g., Indiranagar Penthouse)") },
                        modifier = Modifier.fillMaxWidth().testTag("input_project_title"),
                        singleLine = true,
                        colors = luxuryFieldColors()
                    )
                }

                item {
                    Text(text = "Assign Client:", style = MaterialTheme.typography.labelSmall, color = TextSecondaryDark)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(customers) { c ->
                            FilterChip(
                                selected = selectedCustomer?.id == c.id,
                                onClick = {
                                    selectedCustomer = c
                                    location = c.location
                                },
                                label = { Text(c.name, style = MaterialTheme.typography.labelSmall) },
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
                        value = budgetStr,
                        onValueChange = { budgetStr = it },
                        label = { Text("Total Budget (INR) *") },
                        modifier = Modifier.fillMaxWidth().testTag("input_project_budget"),
                        singleLine = true,
                        colors = luxuryFieldColors()
                    )
                }

                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = startDate,
                            onValueChange = { startDate = it },
                            label = { Text("Start Date") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            colors = luxuryFieldColors()
                        )
                        OutlinedTextField(
                            value = deadlineDate,
                            onValueChange = { deadlineDate = it },
                            label = { Text("Deadline") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            colors = luxuryFieldColors()
                        )
                    }
                }

                item {
                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = it },
                        label = { Text("Site Address / Bengaluru Locality") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = luxuryFieldColors()
                    )
                }

                item {
                    OutlinedTextField(
                        value = scopeSummary,
                        onValueChange = { scopeSummary = it },
                        label = { Text("Scope Summary & Civil Alterations") },
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
                    val budget = budgetStr.toDoubleOrNull() ?: 3500000.0
                    val clientName = selectedCustomer?.name ?: "Valued Client"
                    val clientId = selectedCustomer?.id ?: 1L
                    if (title.isNotBlank()) {
                        onConfirm(title, clientId, clientName, category, budget, startDate, deadlineDate, assignedLead, location, scopeSummary)
                    }
                },
                enabled = title.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = ImperialGold, contentColor = ObsidianBlack),
                modifier = Modifier.testTag("btn_save_project")
            ) {
                Text("Commission Project", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondaryDark)
            }
        }
    )
}
