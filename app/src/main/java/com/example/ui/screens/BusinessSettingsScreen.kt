package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.TeamMemberEntity
import com.example.ui.components.LuxuryCard
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusinessSettingsScreen(
    currentRole: String,
    teamMembers: List<TeamMemberEntity>,
    onSwitchRole: (String) -> Unit,
    onAddTeamMember: (name: String, role: String, email: String, phone: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showAddStaffDialog by remember { mutableStateOf(false) }
    var notificationToggle by remember { mutableStateOf(true) }
    var invoiceAlertsToggle by remember { mutableStateOf(true) }
    var highValueClientAlerts by remember { mutableStateOf(true) }

    val roles = listOf("Principal Architect / Executive", "Site Project Manager", "3D Interior Visualizer", "Accounts & Billing")

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 40.dp)
    ) {
        // Atelier Brand Card
        item {
            LuxuryCard(
                modifier = Modifier.fillMaxWidth(),
                borderBrush = Brush.linearGradient(
                    listOf(ImperialGold, CharcoalBorder)
                )
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .border(2.dp, ImperialGold, CircleShape)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_stephen_logo),
                            contentDescription = "Stephen Enterprises Monogram",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "STEPHEN ENTERPRISES",
                            style = MaterialTheme.typography.titleLarge,
                            color = ChampagneGold,
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Luxury Interior Architecture & Turnkey Solutions",
                            style = MaterialTheme.typography.bodyMedium,
                            color = SoftBeige
                        )
                        Text(
                            text = "GSTIN: 29AAECS1234F1Z8 • Domlur, Bengaluru",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondaryDark
                        )
                    }
                }
            }
        }

        // Active Role & Permissions
        item {
            LuxuryCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "ACCESS CONTROL & ROLE SIMULATION",
                    style = MaterialTheme.typography.labelSmall,
                    color = ImperialGold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Current Mode: $currentRole",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimaryDark,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(10.dp))
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    roles.forEach { role ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (role == currentRole) ImperialGold.copy(alpha = 0.15f) else CharcoalSurface)
                                .border(1.dp, if (role == currentRole) ImperialGold else CharcoalBorder, RoundedCornerShape(8.dp))
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = role,
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (role == currentRole) ChampagneGold else TextPrimaryDark,
                                fontWeight = if (role == currentRole) FontWeight.Bold else FontWeight.Normal
                            )
                            if (role == currentRole) {
                                Icon(Icons.Default.Check, contentDescription = "Active", tint = ImperialGold, modifier = Modifier.size(18.dp))
                            } else {
                                TextButton(onClick = { onSwitchRole(role) }) {
                                    Text("Switch", color = PaleGold, style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Atelier Contact & Studio Info
        item {
            LuxuryCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "DOMUR STUDIO & HEADQUARTERS",
                    style = MaterialTheme.typography.labelSmall,
                    color = ImperialGold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                StudioDetailRow(
                    icon = Icons.Default.LocationOn,
                    label = "Studio Address",
                    value = "#42, 100 Feet Road, Domlur Layout, Bengaluru, Karnataka 560071, India"
                )
                StudioDetailRow(
                    icon = Icons.Default.Phone,
                    label = "Executive Studio Line",
                    value = "+91 98450 12345 / 080 4123 9988"
                )
                StudioDetailRow(
                    icon = Icons.Default.Email,
                    label = "Corporate Inquiries",
                    value = "concierge@stephenenterprises.in"
                )
                StudioDetailRow(
                    icon = Icons.Default.Public,
                    label = "Official Atelier Website",
                    value = "https://stephenenterprises.in"
                )

                Spacer(modifier = Modifier.height(10.dp))
                OutlinedButton(
                    onClick = {
                        val gmmIntentUri = Uri.parse("geo:12.9609,77.6387?q=Domlur,+Bengaluru")
                        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                        context.startActivity(mapIntent)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = PaleGold),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = Brush.linearGradient(listOf(ImperialGold, CharcoalBorder))
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Map, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Locate Atelier on Google Maps")
                }
            }
        }

        // Staff & Team Management
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ATELIER STAFF & ARCHITECTS (${teamMembers.size})",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondaryDark,
                    letterSpacing = 1.5.sp
                )
                TextButton(onClick = { showAddStaffDialog = true }) {
                    Icon(Icons.Default.PersonAdd, contentDescription = null, tint = ImperialGold, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Staff", color = ImperialGold, style = MaterialTheme.typography.labelSmall)
                }
            }
        }

        items(teamMembers) { member ->
            LuxuryCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = member.name,
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimaryDark,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = member.role,
                            style = MaterialTheme.typography.labelSmall,
                            color = ImperialGold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${member.phone} • ${member.email}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondaryDark
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(CharcoalSurface)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${member.activeProjectsCount} Sites",
                            style = MaterialTheme.typography.labelSmall,
                            color = ChampagneGold,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Notification Settings
        item {
            LuxuryCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "ATELIER NOTIFICATION PREFERENCES",
                    style = MaterialTheme.typography.labelSmall,
                    color = ImperialGold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Milestone Approvals & Push Alerts", color = TextPrimaryDark, style = MaterialTheme.typography.bodyLarge)
                        Text("Instant notifications when project milestones complete", color = TextSecondaryDark, style = MaterialTheme.typography.labelSmall)
                    }
                    Switch(
                        checked = notificationToggle,
                        onCheckedChange = { notificationToggle = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = ObsidianBlack, checkedTrackColor = ImperialGold)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Invoice Payment Notifications", color = TextPrimaryDark, style = MaterialTheme.typography.bodyLarge)
                        Text("Real-time SMS/Email alert when client transfers advance or milestone billing", color = TextSecondaryDark, style = MaterialTheme.typography.labelSmall)
                    }
                    Switch(
                        checked = invoiceAlertsToggle,
                        onCheckedChange = { invoiceAlertsToggle = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = ObsidianBlack, checkedTrackColor = ImperialGold)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("High-Value Villa Client Alerts (> ₹50L)", color = TextPrimaryDark, style = MaterialTheme.typography.bodyLarge)
                        Text("Priority notification directly to Principal Architect", color = TextSecondaryDark, style = MaterialTheme.typography.labelSmall)
                    }
                    Switch(
                        checked = highValueClientAlerts,
                        onCheckedChange = { highValueClientAlerts = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = ObsidianBlack, checkedTrackColor = ImperialGold)
                    )
                }
            }
        }
    }

    if (showAddStaffDialog) {
        AddStaffDialog(
            onDismiss = { showAddStaffDialog = false },
            onConfirm = { name, role, email, phone ->
                onAddTeamMember(name, role, email, phone)
                showAddStaffDialog = false
            }
        )
    }
}

@Composable
fun StudioDetailRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = ChampagneGold, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = TextSecondaryDark)
            Text(text = value, style = MaterialTheme.typography.bodyMedium, color = TextPrimaryDark)
        }
    }
}

@Composable
fun AddStaffDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, role: String, email: String, phone: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("Interior Architect") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("+91 ") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DeepCharcoal,
        title = {
            Text("Add Staff / Architect", color = ChampagneGold, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Staff Full Name *") }, modifier = Modifier.fillMaxWidth(), colors = luxuryFieldColors())
                OutlinedTextField(value = role, onValueChange = { role = it }, label = { Text("Role (e.g. Lead 3D Visualizer)") }, modifier = Modifier.fillMaxWidth(), colors = luxuryFieldColors())
                OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Company Email *") }, modifier = Modifier.fillMaxWidth(), colors = luxuryFieldColors())
                OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Mobile Phone *") }, modifier = Modifier.fillMaxWidth(), colors = luxuryFieldColors())
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onConfirm(name, role, email, phone)
                    }
                },
                enabled = name.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = ImperialGold, contentColor = ObsidianBlack),
                modifier = Modifier.testTag("btn_save_staff")
            ) {
                Text("Add Team Member", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondaryDark)
            }
        }
    )
}
