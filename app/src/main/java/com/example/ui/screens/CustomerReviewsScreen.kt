package com.example.ui.screens

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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ReviewEntity
import com.example.ui.components.LuxuryCard
import com.example.ui.components.StarRatingBar
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerReviewsScreen(
    reviews: List<ReviewEntity>,
    onAddReview: (name: String, project: String, loc: String, rating: Int, comment: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddDialog by remember { mutableStateOf(false) }

    val avgRating = if (reviews.isEmpty()) 5.0 else reviews.map { it.rating }.average()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = ImperialGold,
                contentColor = ObsidianBlack,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.testTag("fab_add_review")
            ) {
                Icon(Icons.Default.RateReview, contentDescription = "Add Testimonial")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 80.dp)
        ) {
            // Rating Overview Banner
            item {
                LuxuryCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderBrush = Brush.linearGradient(
                        listOf(ImperialGold, CharcoalBorder)
                    )
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "CLIENT SATISFACTION INDEX",
                                style = MaterialTheme.typography.labelSmall,
                                color = ChampagneGold,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = String.format("%.1f / 5.0", avgRating),
                                style = MaterialTheme.typography.displayMedium,
                                color = TextPrimaryDark,
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            StarRatingBar(rating = avgRating.toInt(), starSize = 22)
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(CharcoalSurface)
                                .border(1.dp, ImperialGold.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "100%",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = ChampagneGold,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Verified Clients",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextSecondaryDark
                                )
                            }
                        }
                    }
                }
            }

            item {
                Text(
                    text = "VERIFIED CLIENT TESTIMONIALS (${reviews.size})",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondaryDark,
                    letterSpacing = 1.5.sp
                )
            }

            items(reviews, key = { it.id }) { review ->
                LuxuryCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(CharcoalSurface)
                                    .border(1.dp, ImperialGold, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = review.clientName.take(1).uppercase(),
                                    fontWeight = FontWeight.Bold,
                                    color = ChampagneGold,
                                    fontSize = 16.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = review.clientName,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = TextPrimaryDark,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${review.projectTitle} • ${review.location}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = SoftBeige
                                )
                            }
                        }
                        StarRatingBar(rating = review.rating)
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "\"${review.comment}\"",
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextPrimaryDark,
                        fontFamily = FontFamily.Serif
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Default.Verified, contentDescription = "Verified", tint = ImperialGold, modifier = Modifier.size(16.dp))
                            Text("Verified Client Handover", style = MaterialTheme.typography.labelSmall, color = ImperialGold)
                        }
                        Text(review.date, style = MaterialTheme.typography.labelSmall, color = TextSecondaryDark)
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddReviewDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { name, proj, loc, rating, comment ->
                onAddReview(name, proj, loc, rating, comment)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun AddReviewDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, project: String, loc: String, rating: Int, comment: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var project by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("Bengaluru") }
    var rating by remember { mutableIntStateOf(5) }
    var comment by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DeepCharcoal,
        title = {
            Text("Publish Client Review", color = ChampagneGold, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Client Name *") }, modifier = Modifier.fillMaxWidth(), colors = luxuryFieldColors())
                OutlinedTextField(value = project, onValueChange = { project = it }, label = { Text("Project Undertaken (e.g., Indiranagar Villa)") }, modifier = Modifier.fillMaxWidth(), colors = luxuryFieldColors())
                OutlinedTextField(value = location, onValueChange = { location = it }, label = { Text("Locality") }, modifier = Modifier.fillMaxWidth(), colors = luxuryFieldColors())

                Text("Rating: $rating Stars", color = ImperialGold, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    for (i in 1..5) {
                        IconButton(onClick = { rating = i }) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "$i stars",
                                tint = if (i <= rating) ImperialGold else CharcoalBorder
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    label = { Text("Review & Testimonial *") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3,
                    colors = luxuryFieldColors()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && comment.isNotBlank()) {
                        onConfirm(name, project, location, rating, comment)
                    }
                },
                enabled = name.isNotBlank() && comment.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = ImperialGold, contentColor = ObsidianBlack),
                modifier = Modifier.testTag("btn_save_review")
            ) {
                Text("Publish Review", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondaryDark)
            }
        }
    )
}
