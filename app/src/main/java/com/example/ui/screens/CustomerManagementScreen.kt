package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CustomerEntity
import com.example.ui.components.LuxuryCard
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerManagementScreen(
    customers: List<CustomerEntity>,
    onAddCustomer: (name: String, email: String, phone: String, location: String, req: String, budget: String, notes: String) -> Unit,
    onDeleteCustomer: (CustomerEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showAddDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    val filteredCustomers = remember(customers, searchQuery) {
        if (searchQuery.isBlank()) customers
        else customers.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
            it.location.contains(searchQuery, ignoreCase = true) ||
            it.projectRequirement.contains(searchQuery, ignoreCase = true)
        }
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
                modifier = Modifier.testTag("fab_add_customer")
            ) {
                Icon(Icons.Default.PersonAdd, contentDescription = "Add Customer")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            // Search Input
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
                    .testTag("input_search_customers"),
                placeholder = { Text("Search clients by name, Bangalore locality or requirements...", color = TextSecondaryDark) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = ImperialGold) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ImperialGold,
                    unfocusedBorderColor = CharcoalBorder,
                    focusedTextColor = TextPrimaryDark,
                    unfocusedTextColor = TextPrimaryDark,
                    focusedContainerColor = CharcoalSurface,
                    unfocusedContainerColor = CharcoalSurface
                ),
                shape = RoundedCornerShape(14.dp),
                singleLine = true
            )

            if (filteredCustomers.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 60.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.SearchOff,
                            contentDescription = null,
                            tint = ImperialGold.copy(alpha = 0.5f),
                            modifier = Modifier.size(54.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("No client profiles found", color = TextSecondaryDark, style = MaterialTheme.typography.titleMedium)
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(filteredCustomers, key = { it.id }) { customer ->
                        CustomerItemCard(
                            customer = customer,
                            onCall = {
                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${customer.phone}"))
                                context.startActivity(intent)
                            },
                            onEmail = {
                                val intent = Intent(Intent.ACTION_SENDTO).apply {
                                    data = Uri.parse("mailto:${customer.email}")
                                    putExtra(Intent.EXTRA_SUBJECT, "Stephen Enterprises - Interior Design Consultation")
                                }
                                context.startActivity(intent)
                            },
                            onDelete = { onDeleteCustomer(customer) }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddCustomerDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { name, email, phone, loc, req, budget, notes ->
                onAddCustomer(name, email, phone, loc, req, budget, notes)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun CustomerItemCard(
    customer: CustomerEntity,
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
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(ImperialGold, CharcoalSurface)
                            )
                        )
                        .border(1.dp, ChampagneGold, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = customer.name.take(1).uppercase(),
                        fontWeight = FontWeight.Bold,
                        color = ObsidianBlack,
                        fontSize = 18.sp
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = customer.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimaryDark,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = customer.location,
                        style = MaterialTheme.typography.labelSmall,
                        color = ImperialGold
                    )
                }
            }
            StatusBadge(status = customer.status)
        }

        Spacer(modifier = Modifier.height(12.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(CharcoalSurface)
                .padding(10.dp)
        ) {
            Column {
                Text(
                    text = "REQUIREMENT & SCOPE",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondaryDark,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = customer.projectRequirement,
                    style = MaterialTheme.typography.bodyMedium,
                    color = SoftBeige
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Budget Range: ${customer.budgetRange}",
                    style = MaterialTheme.typography.labelSmall,
                    color = ChampagneGold,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        if (customer.notes.isNotBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Architectural Notes: ${customer.notes}",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondaryDark
            )
        }

        Spacer(modifier = Modifier.height(14.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(
                    onClick = onCall,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(CharcoalSurface)
                        .border(1.dp, CharcoalBorder, CircleShape)
                ) {
                    Icon(Icons.Default.Phone, contentDescription = "Call Client", tint = PaleGold, modifier = Modifier.size(18.dp))
                }
                IconButton(
                    onClick = onEmail,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(CharcoalSurface)
                        .border(1.dp, CharcoalBorder, CircleShape)
                ) {
                    Icon(Icons.Default.Email, contentDescription = "Email Client", tint = PaleGold, modifier = Modifier.size(18.dp))
                }
            }
            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(38.dp)
            ) {
                Icon(Icons.Default.DeleteOutline, contentDescription = "Delete Client", tint = Color(0xFFEF5350), modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
fun AddCustomerDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, email: String, phone: String, location: String, req: String, budget: String, notes: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("Bengaluru") }
    var requirement by remember { mutableStateOf("") }
    var budget by remember { mutableStateOf("₹30L - ₹50L") }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DeepCharcoal,
        title = {
            Text(
                text = "New Client Onboarding",
                style = MaterialTheme.typography.titleLarge,
                color = ChampagneGold,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Client Full Name *") },
                    modifier = Modifier.fillMaxWidth().testTag("input_customer_name"),
                    singleLine = true,
                    colors = luxuryFieldColors()
                )
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Phone Number *") },
                        modifier = Modifier.weight(1f).testTag("input_customer_phone"),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true,
                        colors = luxuryFieldColors()
                    )
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email *") },
                        modifier = Modifier.weight(1f).testTag("input_customer_email"),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true,
                        colors = luxuryFieldColors()
                    )
                }
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Site / Residence Locality (Bengaluru)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = luxuryFieldColors()
                )
                OutlinedTextField(
                    value = requirement,
                    onValueChange = { requirement = it },
                    label = { Text("Project Requirement (e.g., 3BHK Penthouse, Modular Kitchen)") },
                    modifier = Modifier.fillMaxWidth().testTag("input_customer_requirement"),
                    maxLines = 2,
                    colors = luxuryFieldColors()
                )
                OutlinedTextField(
                    value = budget,
                    onValueChange = { budget = it },
                    label = { Text("Expected Budget Bracket") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = luxuryFieldColors()
                )
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Design Preferences / Notes") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2,
                    colors = luxuryFieldColors()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && phone.isNotBlank()) {
                        onConfirm(name, email, phone, location, requirement, budget, notes)
                    }
                },
                enabled = name.isNotBlank() && phone.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ImperialGold,
                    contentColor = ObsidianBlack
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("btn_save_customer")
            ) {
                Text("Add Client Profile", fontWeight = FontWeight.Bold)
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
fun luxuryFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = ImperialGold,
    unfocusedBorderColor = CharcoalBorder,
    focusedLabelColor = ImperialGold,
    unfocusedLabelColor = TextSecondaryDark,
    focusedTextColor = TextPrimaryDark,
    unfocusedTextColor = TextPrimaryDark,
    focusedContainerColor = CharcoalSurface,
    unfocusedContainerColor = CharcoalSurface
)
