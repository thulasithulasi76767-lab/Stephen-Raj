package com.example.ui.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.clickable
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.MaterialEntity
import com.example.data.ProjectEntity
import com.example.data.VendorEntity
import com.example.ui.components.LuxuryCard
import com.example.ui.components.StarRatingBar
import com.example.ui.components.StatusBadge
import com.example.ui.components.formatInr
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaterialVendorLibraryScreen(
    materials: List<MaterialEntity>,
    vendors: List<VendorEntity>,
    projects: List<ProjectEntity>,
    onAddVendor: (name: String, cat: String, contact: String, phone: String, email: String, addr: String, rating: Double, leadTime: Int, notes: String) -> Unit,
    onDeleteVendor: (VendorEntity) -> Unit,
    onAddMaterial: (name: String, cat: String, subCat: String, unit: String, cost: Double, vendorId: Long, vendorName: String, spec: String, rating: Double) -> Unit,
    onDeleteMaterial: (MaterialEntity) -> Unit,
    onAssignMaterialToProject: (projectId: Long, projectTitle: String, material: MaterialEntity, quantity: Double) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Materials, 1: Vendors
    var selectedCategoryFilter by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }

    var showAddMaterialDialog by remember { mutableStateOf(false) }
    var showAddVendorDialog by remember { mutableStateOf(false) }
    var materialToAssign by remember { mutableStateOf<MaterialEntity?>(null) }

    val materialCategories = listOf("All", "Flooring", "Paint & Texture", "Hardware", "Lighting", "Fixtures", "Wood & Veneer")
    val vendorCategories = listOf("All", "Flooring & Marble", "Paint & Finishes", "Modular Hardware", "Electrical & Smart Lighting", "Fixtures & Sanitaryware")

    val filteredMaterials = remember(materials, selectedCategoryFilter, searchQuery) {
        materials.filter { mat ->
            (selectedCategoryFilter == "All" || mat.category.equals(selectedCategoryFilter, ignoreCase = true)) &&
            (searchQuery.isBlank() || mat.name.contains(searchQuery, ignoreCase = true) || mat.vendorName.contains(searchQuery, ignoreCase = true) || mat.specification.contains(searchQuery, ignoreCase = true))
        }
    }

    val filteredVendors = remember(vendors, selectedCategoryFilter, searchQuery) {
        vendors.filter { ven ->
            (selectedCategoryFilter == "All" || ven.category.equals(selectedCategoryFilter, ignoreCase = true)) &&
            (searchQuery.isBlank() || ven.businessName.contains(searchQuery, ignoreCase = true) || ven.contactPerson.contains(searchQuery, ignoreCase = true) || ven.address.contains(searchQuery, ignoreCase = true))
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (selectedTab == 0) showAddMaterialDialog = true
                    else showAddVendorDialog = true
                },
                containerColor = ImperialGold,
                contentColor = ObsidianBlack,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.testTag("fab_add_item")
            ) {
                Icon(
                    imageVector = if (selectedTab == 0) Icons.Default.AddShoppingCart else Icons.Default.AddBusiness,
                    contentDescription = "Add Entry"
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            // Main Library Switcher: Materials vs Vendors
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = DeepCharcoal,
                contentColor = ImperialGold,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = ImperialGold
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .padding(vertical = 8.dp)
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = {
                        selectedTab = 0
                        selectedCategoryFilter = "All"
                    },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Layers, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Material Specifications (${materials.size})", fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal)
                        }
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = {
                        selectedTab = 1
                        selectedCategoryFilter = "All"
                    },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Storefront, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Vendor Partners (${vendors.size})", fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal)
                        }
                    }
                )
            }

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .testTag("input_search_library"),
                placeholder = {
                    Text(
                        if (selectedTab == 0) "Search Italian marble, Blum lift, Flos lights..."
                        else "Search suppliers, stone ateliers, sanitary showrooms...",
                        color = TextSecondaryDark,
                        fontSize = 13.sp
                    )
                },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = ImperialGold) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ImperialGold,
                    unfocusedBorderColor = CharcoalBorder,
                    focusedTextColor = TextPrimaryDark,
                    unfocusedTextColor = TextPrimaryDark,
                    focusedContainerColor = CharcoalSurface,
                    unfocusedContainerColor = CharcoalSurface
                ),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            // Category Filter Row
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(vertical = 6.dp)
            ) {
                val filterList = if (selectedTab == 0) materialCategories else vendorCategories
                items(filterList) { cat ->
                    val isSelected = cat == selectedCategoryFilter
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedCategoryFilter = cat },
                        label = { Text(cat, style = MaterialTheme.typography.labelSmall) },
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
                        shape = RoundedCornerShape(16.dp)
                    )
                }
            }

            // Body List
            if (selectedTab == 0) {
                // Materials List
                if (filteredMaterials.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No materials matching filter", color = TextSecondaryDark)
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        contentPadding = PaddingValues(top = 8.dp, bottom = 80.dp)
                    ) {
                        items(filteredMaterials, key = { it.id }) { material ->
                            MaterialItemCard(
                                material = material,
                                onAttachToProject = { materialToAssign = material },
                                onDelete = { onDeleteMaterial(material) }
                            )
                        }
                    }
                }
            } else {
                // Vendors List
                if (filteredVendors.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No vendor partners matching filter", color = TextSecondaryDark)
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        contentPadding = PaddingValues(top = 8.dp, bottom = 80.dp)
                    ) {
                        items(filteredVendors, key = { it.id }) { vendor ->
                            VendorItemCard(
                                vendor = vendor,
                                onCall = {
                                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${vendor.phone}"))
                                    context.startActivity(intent)
                                },
                                onEmail = {
                                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                                        data = Uri.parse("mailto:${vendor.email}")
                                        putExtra(Intent.EXTRA_SUBJECT, "Stephen Enterprises - Material Inquiry")
                                    }
                                    context.startActivity(intent)
                                },
                                onDelete = { onDeleteVendor(vendor) }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showAddMaterialDialog) {
        AddMaterialDialog(
            vendors = vendors,
            onDismiss = { showAddMaterialDialog = false },
            onConfirm = { name, cat, subCat, unit, cost, vId, vName, spec, rating ->
                onAddMaterial(name, cat, subCat, unit, cost, vId, vName, spec, rating)
                showAddMaterialDialog = false
            }
        )
    }

    if (showAddVendorDialog) {
        AddVendorDialog(
            onDismiss = { showAddVendorDialog = false },
            onConfirm = { name, cat, contact, phone, email, addr, rating, leadTime, notes ->
                onAddVendor(name, cat, contact, phone, email, addr, rating, leadTime, notes)
                showAddVendorDialog = false
            }
        )
    }

    materialToAssign?.let { material ->
        AssignMaterialToProjectDialog(
            material = material,
            projects = projects,
            onDismiss = { materialToAssign = null },
            onConfirm = { projId, projTitle, qty ->
                onAssignMaterialToProject(projId, projTitle, material, qty)
                materialToAssign = null
            }
        )
    }
}

@Composable
fun MaterialItemCard(
    material: MaterialEntity,
    onAttachToProject: () -> Unit,
    onDelete: () -> Unit
) {
    LuxuryCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(ImperialGold)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = material.category.uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            color = ObsidianBlack,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = material.subCategory,
                        style = MaterialTheme.typography.labelSmall,
                        color = ChampagneGold
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = material.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimaryDark,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif
                )
                Text(
                    text = "Supplier: ${material.vendorName}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = SoftBeige
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${formatInr(material.unitCost)} / ${material.unit}",
                    style = MaterialTheme.typography.titleMedium,
                    color = ImperialGold,
                    fontWeight = FontWeight.Bold
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = ImperialGold, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(text = "${material.rating}", style = MaterialTheme.typography.labelSmall, color = TextPrimaryDark)
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = material.specification,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondaryDark
        )

        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (material.inStock) Icons.Default.CheckCircle else Icons.Default.Warning,
                    contentDescription = null,
                    tint = if (material.inStock) Color(0xFF81C784) else Color(0xFFFFB74D),
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (material.inStock) "Verified Stock in Bengaluru" else "Lead Time 15-20 Days",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (material.inStock) Color(0xFF81C784) else Color(0xFFFFB74D)
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onAttachToProject,
                    colors = ButtonDefaults.buttonColors(containerColor = ImperialGold, contentColor = ObsidianBlack),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.AddLink, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Attach to Project", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)
                }

                IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Default.DeleteOutline, contentDescription = "Delete Material", tint = Color(0xFFEF5350), modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@Composable
fun VendorItemCard(
    vendor: VendorEntity,
    onCall: () -> Unit,
    onEmail: () -> Unit,
    onDelete: () -> Unit
) {
    LuxuryCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(CharcoalSurface)
                            .border(1.dp, ImperialGold, RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = vendor.category.uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            color = ChampagneGold,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    if (vendor.isPreferredPartner) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(ImperialGold)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "PREFERRED ATELIER PARTNER",
                                style = MaterialTheme.typography.labelSmall,
                                color = ObsidianBlack,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = vendor.businessName,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimaryDark,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif
                )
                Text(
                    text = "Contact: ${vendor.contactPerson} • ${vendor.address}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondaryDark
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = ImperialGold, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(text = "${vendor.rating}", style = MaterialTheme.typography.titleMedium, color = ChampagneGold, fontWeight = FontWeight.Bold)
                }
                Text(text = "${vendor.leadTimeDays} Days Lead Time", style = MaterialTheme.typography.labelSmall, color = SoftBeige)
            }
        }

        if (vendor.notes.isNotBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Procurement Terms: ${vendor.notes}",
                style = MaterialTheme.typography.bodyMedium,
                color = SoftBeige
            )
        }

        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(
                    onClick = onCall,
                    modifier = Modifier.size(36.dp).clip(CircleShape).background(CharcoalSurface).border(1.dp, CharcoalBorder, CircleShape)
                ) {
                    Icon(Icons.Default.Phone, contentDescription = "Call Vendor", tint = PaleGold, modifier = Modifier.size(16.dp))
                }
                IconButton(
                    onClick = onEmail,
                    modifier = Modifier.size(36.dp).clip(CircleShape).background(CharcoalSurface).border(1.dp, CharcoalBorder, CircleShape)
                ) {
                    Icon(Icons.Default.Email, contentDescription = "Email Vendor", tint = PaleGold, modifier = Modifier.size(16.dp))
                }
            }

            IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                Icon(Icons.Default.DeleteOutline, contentDescription = "Delete Vendor", tint = Color(0xFFEF5350), modifier = Modifier.size(18.dp))
            }
        }
    }
}

@Composable
fun AssignMaterialToProjectDialog(
    material: MaterialEntity,
    projects: List<ProjectEntity>,
    onDismiss: () -> Unit,
    onConfirm: (projectId: Long, projectTitle: String, quantity: Double) -> Unit
) {
    var selectedProject by remember { mutableStateOf(projects.firstOrNull()) }
    var quantityStr by remember { mutableStateOf("100") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DeepCharcoal,
        title = {
            Text("Attach Material to Site Specification", color = ChampagneGold, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(text = material.name, style = MaterialTheme.typography.titleMedium, color = TextPrimaryDark)
                Text(text = "Unit Cost: ${formatInr(material.unitCost)} per ${material.unit}", color = ImperialGold)

                Text(text = "Select Commissioned Project:", style = MaterialTheme.typography.labelSmall, color = TextSecondaryDark)
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

                OutlinedTextField(
                    value = quantityStr,
                    onValueChange = { quantityStr = it },
                    label = { Text("Required Quantity (${material.unit}) *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = luxuryFieldColors()
                )

                val qty = quantityStr.toDoubleOrNull() ?: 0.0
                val estCost = qty * material.unitCost
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(CharcoalSurface)
                        .padding(10.dp)
                ) {
                    Text("Calculated Procurement Line Cost: ${formatInr(estCost)}", color = ChampagneGold, fontWeight = FontWeight.Bold)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val p = selectedProject
                    val qty = quantityStr.toDoubleOrNull() ?: 1.0
                    if (p != null) {
                        onConfirm(p.id, p.title, qty)
                    }
                },
                enabled = selectedProject != null,
                colors = ButtonDefaults.buttonColors(containerColor = ImperialGold, contentColor = ObsidianBlack)
            ) {
                Text("Confirm Specification")
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
fun AddMaterialDialog(
    vendors: List<VendorEntity>,
    onDismiss: () -> Unit,
    onConfirm: (name: String, cat: String, subCat: String, unit: String, cost: Double, vendorId: Long, vendorName: String, spec: String, rating: Double) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Flooring") }
    var subCategory by remember { mutableStateOf("Natural Stone") }
    var unit by remember { mutableStateOf("sq.ft") }
    var unitCostStr by remember { mutableStateOf("450") }
    var selectedVendor by remember { mutableStateOf(vendors.firstOrNull()) }
    var specification by remember { mutableStateOf("") }
    var rating by remember { mutableDoubleStateOf(4.9) }

    val categories = listOf("Flooring", "Paint & Texture", "Hardware", "Lighting", "Fixtures", "Wood & Veneer")

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DeepCharcoal,
        title = {
            Text("Add Material Specification", color = ChampagneGold, fontWeight = FontWeight.Bold)
        },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                item {
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Material Name *") }, modifier = Modifier.fillMaxWidth(), colors = luxuryFieldColors())
                }
                item {
                    Text("Category:", style = MaterialTheme.typography.labelSmall, color = TextSecondaryDark)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(categories) { cat ->
                            FilterChip(
                                selected = category == cat,
                                onClick = { category = cat },
                                label = { Text(cat, style = MaterialTheme.typography.labelSmall) },
                                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = ImperialGold, selectedLabelColor = ObsidianBlack, containerColor = CharcoalSurface, labelColor = TextPrimaryDark)
                            )
                        }
                    }
                }
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = subCategory, onValueChange = { subCategory = it }, label = { Text("Sub-Type") }, modifier = Modifier.weight(1f), colors = luxuryFieldColors())
                        OutlinedTextField(value = unit, onValueChange = { unit = it }, label = { Text("Unit (e.g. sq.ft)") }, modifier = Modifier.weight(1f), colors = luxuryFieldColors())
                    }
                }
                item {
                    OutlinedTextField(value = unitCostStr, onValueChange = { unitCostStr = it }, label = { Text("Unit Cost (INR) *") }, modifier = Modifier.fillMaxWidth(), colors = luxuryFieldColors())
                }
                item {
                    Text("Supplier / Vendor:", style = MaterialTheme.typography.labelSmall, color = TextSecondaryDark)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(vendors) { v ->
                            FilterChip(
                                selected = selectedVendor?.id == v.id,
                                onClick = { selectedVendor = v },
                                label = { Text(v.businessName, style = MaterialTheme.typography.labelSmall) },
                                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = ImperialGold, selectedLabelColor = ObsidianBlack, containerColor = CharcoalSurface, labelColor = TextPrimaryDark)
                            )
                        }
                    }
                }
                item {
                    OutlinedTextField(value = specification, onValueChange = { specification = it }, label = { Text("Detailed Specifications & Grades") }, modifier = Modifier.fillMaxWidth(), maxLines = 3, colors = luxuryFieldColors())
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val cost = unitCostStr.toDoubleOrNull() ?: 100.0
                    val v = selectedVendor
                    if (name.isNotBlank() && v != null) {
                        onConfirm(name, category, subCategory, unit, cost, v.id, v.businessName, specification, rating)
                    }
                },
                enabled = name.isNotBlank() && selectedVendor != null,
                colors = ButtonDefaults.buttonColors(containerColor = ImperialGold, contentColor = ObsidianBlack)
            ) {
                Text("Save Material")
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
fun AddVendorDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, cat: String, contact: String, phone: String, email: String, addr: String, rating: Double, leadTime: Int, notes: String) -> Unit
) {
    var businessName by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Flooring & Marble") }
    var contactPerson by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("+91 ") }
    var email by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("Bengaluru") }
    var leadTimeStr by remember { mutableStateOf("7") }
    var notes by remember { mutableStateOf("") }

    val categories = listOf("Flooring & Marble", "Paint & Finishes", "Modular Hardware", "Electrical & Smart Lighting", "Fixtures & Sanitaryware")

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DeepCharcoal,
        title = {
            Text("Onboard Vendor Partner", color = ChampagneGold, fontWeight = FontWeight.Bold)
        },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                item {
                    OutlinedTextField(value = businessName, onValueChange = { businessName = it }, label = { Text("Vendor Business Name *") }, modifier = Modifier.fillMaxWidth(), colors = luxuryFieldColors())
                }
                item {
                    Text("Category:", style = MaterialTheme.typography.labelSmall, color = TextSecondaryDark)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(categories) { cat ->
                            FilterChip(
                                selected = category == cat,
                                onClick = { category = cat },
                                label = { Text(cat, style = MaterialTheme.typography.labelSmall) },
                                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = ImperialGold, selectedLabelColor = ObsidianBlack, containerColor = CharcoalSurface, labelColor = TextPrimaryDark)
                            )
                        }
                    }
                }
                item {
                    OutlinedTextField(value = contactPerson, onValueChange = { contactPerson = it }, label = { Text("Contact Person Name *") }, modifier = Modifier.fillMaxWidth(), colors = luxuryFieldColors())
                }
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone") }, modifier = Modifier.weight(1f), colors = luxuryFieldColors())
                        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.weight(1f), colors = luxuryFieldColors())
                    }
                }
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("Showroom / Warehouse Address") }, modifier = Modifier.weight(1.5f), colors = luxuryFieldColors())
                        OutlinedTextField(value = leadTimeStr, onValueChange = { leadTimeStr = it }, label = { Text("Lead (Days)") }, modifier = Modifier.weight(1f), colors = luxuryFieldColors())
                    }
                }
                item {
                    OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Discount Terms / Special Agreements") }, modifier = Modifier.fillMaxWidth(), maxLines = 2, colors = luxuryFieldColors())
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val days = leadTimeStr.toIntOrNull() ?: 7
                    if (businessName.isNotBlank() && contactPerson.isNotBlank()) {
                        onConfirm(businessName, category, contactPerson, phone, email, address, 4.8, days, notes)
                    }
                },
                enabled = businessName.isNotBlank() && contactPerson.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = ImperialGold, contentColor = ObsidianBlack)
            ) {
                Text("Add Vendor Partner")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondaryDark)
            }
        }
    )
}
