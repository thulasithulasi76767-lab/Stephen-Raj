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
import androidx.compose.runtime.Composable
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
import com.example.data.QuotationEntity
import com.example.ui.Screen
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun DashboardScreen(
    customers: List<CustomerEntity>,
    projects: List<ProjectEntity>,
    quotations: List<QuotationEntity>,
    onNavigate: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    val totalRevenue = projects.sumOf { it.paidAmount }
    val totalBudget = projects.sumOf { it.totalBudget }
    val pendingRevenue = totalBudget - totalRevenue
    val activeCount = projects.count { it.status != "Completed" }
    val completedCount = projects.count { it.status == "Completed" }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 32.dp)
    ) {
        // Hero Brand Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .border(
                        1.dp,
                        Brush.horizontalGradient(listOf(ImperialGold.copy(alpha = 0.5f), Color.Transparent)),
                        RoundedCornerShape(20.dp)
                    )
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_luxury_living),
                    contentDescription = "Stephen Enterprises Luxury Living",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                // Dark subtle luxury gradient overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color.Black.copy(alpha = 0.35f),
                                    Color.Black.copy(alpha = 0.88f)
                                )
                            )
                        )
                )
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(ImperialGold.copy(alpha = 0.2f))
                                .border(1.dp, ImperialGold, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "Location",
                                tint = ImperialGold,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "DOMLUR, BENGALURU",
                            style = MaterialTheme.typography.labelSmall,
                            color = ChampagneGold,
                            letterSpacing = 1.5.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Stephen Enterprises",
                        style = MaterialTheme.typography.headlineLarge,
                        color = Color.White,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "World-Class Architectural & Luxury Interior Atelier",
                        style = MaterialTheme.typography.bodyMedium,
                        color = SoftBeige
                    )
                }
            }
        }

        // Executive Quick Actions
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { onNavigate(Screen.Projects) },
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp)
                        .testTag("action_new_project"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ImperialGold,
                        contentColor = ObsidianBlack
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("New Project", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
                }

                OutlinedButton(
                    onClick = { onNavigate(Screen.QuotationBilling) },
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp)
                        .testTag("action_new_quote"),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = PaleGold
                    ),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = Brush.linearGradient(listOf(ImperialGold, CharcoalBorder))
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.ReceiptLong, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Quotes & Invoices", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.labelLarge)
                }
            }
        }

        // Section: Real-time Business Metrics
        item {
            Text(
                text = "EXECUTIVE METRICS",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondaryDark,
                letterSpacing = 1.5.sp
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricStatCard(
                    modifier = Modifier.weight(1f),
                    title = "Revenue Collected",
                    value = formatInr(totalRevenue),
                    subtitle = "Realized from billing",
                    icon = Icons.Default.AccountBalanceWallet,
                    accentColor = ImperialGold
                )
                MetricStatCard(
                    modifier = Modifier.weight(1f),
                    title = "Pending Dues",
                    value = formatInr(pendingRevenue),
                    subtitle = "Under execution milestones",
                    icon = Icons.Default.HourglassTop,
                    accentColor = StatusPending
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricStatCard(
                    modifier = Modifier.weight(1f),
                    title = "Active Projects",
                    value = "$activeCount",
                    subtitle = "$completedCount completed handovers",
                    icon = Icons.Default.Architecture,
                    accentColor = ChampagneGold
                )
                MetricStatCard(
                    modifier = Modifier.weight(1f),
                    title = "Total Clients",
                    value = "${customers.size}",
                    subtitle = "High-net-worth Bangalore",
                    icon = Icons.Default.People,
                    accentColor = Color(0xFF64B5F6)
                )
            }
        }

        // Section: Active High-Priority Projects
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ACTIVE SITES & PROGRESS",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondaryDark,
                    letterSpacing = 1.5.sp
                )
                Text(
                    text = "View All (${projects.size})",
                    style = MaterialTheme.typography.labelSmall,
                    color = ImperialGold,
                    modifier = Modifier.clickable { onNavigate(Screen.Projects) }
                )
            }
        }

        items(projects.take(3)) { project ->
            LuxuryCard(
                onClick = { onNavigate(Screen.Projects) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = project.category.uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            color = ImperialGold
                        )
                        Text(
                            text = project.title,
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimaryDark,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Client: ${project.customerName} • ${project.siteLocation}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondaryDark
                        )
                    }
                    StatusBadge(status = project.status)
                }

                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Site Progress",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondaryDark
                    )
                    Text(
                        text = "${project.progressPercentage}%",
                        style = MaterialTheme.typography.labelSmall,
                        color = ChampagneGold,
                        fontWeight = FontWeight.Bold
                    )
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

                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Budget: ${formatInr(project.totalBudget)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextPrimaryDark
                    )
                    Text(
                        text = "Lead: ${project.assignedLead}",
                        style = MaterialTheme.typography.labelSmall,
                        color = SoftBeige
                    )
                }
            }
        }

        // Section: Recent Client Enquiries
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "RECENT CLIENT ENQUIRIES",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondaryDark,
                    letterSpacing = 1.5.sp
                )
                Text(
                    text = "Manage Clients",
                    style = MaterialTheme.typography.labelSmall,
                    color = ImperialGold,
                    modifier = Modifier.clickable { onNavigate(Screen.Customers) }
                )
            }
        }

        items(customers.take(3)) { customer ->
            LuxuryCard(
                onClick = { onNavigate(Screen.Customers) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = customer.name,
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimaryDark,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${customer.phone} • ${customer.location}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondaryDark
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = customer.projectRequirement,
                            style = MaterialTheme.typography.bodyMedium,
                            color = SoftBeige
                        )
                    }
                    StatusBadge(status = customer.status)
                }
            }
        }
    }
}
