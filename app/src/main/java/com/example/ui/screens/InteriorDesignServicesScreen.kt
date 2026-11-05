package com.example.ui.screens

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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.Screen
import com.example.ui.components.LuxuryCard
import com.example.ui.theme.*

data class ServiceOffering(
    val title: String,
    val subtitle: String,
    val description: String,
    val keyFeatures: List<String>,
    val priceStartingFrom: String,
    val turnaroundTime: String,
    val imageRes: Int,
    val icon: ImageVector
)

@Composable
fun InteriorDesignServicesScreen(
    onBookConsultation: (String) -> Unit,
    onNavigate: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    val services = listOf(
        ServiceOffering(
            title = "Full Home Interior Design",
            subtitle = "Turnkey Luxury Villas & Apartments",
            description = "End-to-end architectural planning, 3D photorealistic visualization, premium Italian finishes, civil alterations, smart lighting integration, and bespoke furniture crafted for high-end Bengaluru living.",
            keyFeatures = listOf("Comprehensive 3D VR Walkthroughs", "Italian Marble Flooring & Wall Cladding", "Complete False Ceiling with Dimmable Cove Lighting", "10-Year Comprehensive Hardware Warranty"),
            priceStartingFrom = "₹45,00,000",
            turnaroundTime = "90 - 120 Days",
            imageRes = R.drawable.img_luxury_living,
            icon = Icons.Default.Villa
        ),
        ServiceOffering(
            title = "Modular Kitchen Design",
            subtitle = "German Blum Hardware & Quartz Islands",
            description = "Custom-engineered modular kitchens configured with waterproof marine ply, Blum servo-drive motorized drawers, anti-scratch quartz countertops, and smart integrated appliances.",
            keyFeatures = listOf("Blum Servo-Drive Lift Systems", "Calacatta Quartz Island Counters", "Concealed Cutlery Organizers & Pantry Units", "Anti-Fingerprint Acrylic / Fluted Veneer"),
            priceStartingFrom = "₹12,00,000",
            turnaroundTime = "30 - 45 Days",
            imageRes = R.drawable.img_modular_kitchen,
            icon = Icons.Default.Kitchen
        ),
        ServiceOffering(
            title = "Bedroom Design",
            subtitle = "Presidential Master Sanctuary",
            description = "Bespoke upholstered king beds with integrated acoustic headboard paneling, walk-in dressing rooms with bronze tinted glass, dimmable circadian lighting and automated curtain drapery.",
            keyFeatures = listOf("Floor-to-Ceiling Bronze Glass Wardrobes", "Velvet Acoustic Wall Panelling", "Circadian Warm Ambient Lighting (2700K)", "Bespoke Vanity Dressing Island"),
            priceStartingFrom = "₹9,50,000",
            turnaroundTime = "25 - 35 Days",
            imageRes = R.drawable.img_master_bedroom,
            icon = Icons.Default.Bed
        ),
        ServiceOffering(
            title = "Living Room Design",
            subtitle = "Grand Statement Spaces",
            description = "Double-height travertine feature walls, motorized acoustic partition sliders, custom brass inlays, luxury Italian leather loungers and custom bar console tables.",
            keyFeatures = listOf("Bookmatched Travertine / Onyx Feature Walls", "Custom Brass Inlay Millwork", "Integrated Audio-Visual Concealed Consoles", "Designer Chandelier Suspension Engineering"),
            priceStartingFrom = "₹14,00,000",
            turnaroundTime = "30 - 40 Days",
            imageRes = R.drawable.img_luxury_living,
            icon = Icons.Default.Weekend
        ),
        ServiceOffering(
            title = "Office & Commercial Interior Design",
            subtitle = "Executive Corporate Suites",
            description = "Modern commercial spaces for MNCs, VC firms, and tech leaders in Domlur, Indiranagar, and Whitefield. Designed for acoustic privacy, ergonomics, and prestigious international brand identity.",
            keyFeatures = listOf("Acoustic Glass Demountable Partitions", "Bespoke Executive Walnut Boardroom Tables", "Smart Access Control & Conduiting", "Biophilic Green Wall Architecture"),
            priceStartingFrom = "₹35,00,000",
            turnaroundTime = "60 - 90 Days",
            imageRes = R.drawable.img_luxury_office,
            icon = Icons.Default.Business
        ),
        ServiceOffering(
            title = "Bathroom & Spa Sanctuary",
            subtitle = "Italian Bath Suites & Wellness",
            description = "Spa-inspired master bathrooms featuring thermostatic rain showers, anti-fog backlit smart mirrors, concealed geberit cisterns, and natural stone mosaics.",
            keyFeatures = listOf("Thermostatic Concealed Diverters", "Freestanding Acrylic Soaking Tubs", "Custom Fluted Floating Vanities", "Waterproof Mood Lighting"),
            priceStartingFrom = "₹6,50,000",
            turnaroundTime = "20 - 25 Days",
            imageRes = R.drawable.img_master_bedroom,
            icon = Icons.Default.Bathtub
        )
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 40.dp)
    ) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(CharcoalSurface, DeepCharcoal)
                        )
                    )
                    .border(1.dp, ImperialGold.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
                    .padding(18.dp)
            ) {
                Column {
                    Text(
                        text = "OUR ATELIER EXPERTISE",
                        style = MaterialTheme.typography.labelSmall,
                        color = ChampagneGold,
                        letterSpacing = 1.5.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Seven Disciplines of Luxury Interior Craftsmanship",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimaryDark,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "From penthouse residences in Domlur to commercial corporate towers across Bangalore, Stephen Enterprises provides turnkey architectural interior solutions.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondaryDark
                    )
                }
            }
        }

        items(services) { service ->
            LuxuryCard(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clip(RoundedCornerShape(12.dp))
                ) {
                    Image(
                        painter = painterResource(id = service.imageRes),
                        contentDescription = service.title,
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
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(ImperialGold)
                                .border(1.dp, PaleGold, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = service.icon,
                                contentDescription = null,
                                tint = ObsidianBlack,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = service.title,
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = service.subtitle,
                                style = MaterialTheme.typography.labelSmall,
                                color = SoftBeige
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = service.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = SoftBeige
                )

                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "SPECIFICATIONS & CRAFT:",
                    style = MaterialTheme.typography.labelSmall,
                    color = ImperialGold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                service.keyFeatures.forEach { feature ->
                    Row(
                        modifier = Modifier.padding(vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = ChampagneGold,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = feature,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextPrimaryDark
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(CharcoalSurface)
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "ESTIMATED PACKAGES FROM", style = MaterialTheme.typography.labelSmall, color = TextSecondaryDark)
                        Text(text = service.priceStartingFrom, style = MaterialTheme.typography.titleMedium, color = ImperialGold, fontWeight = FontWeight.Bold)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = "EXECUTION DURATION", style = MaterialTheme.typography.labelSmall, color = TextSecondaryDark)
                        Text(text = service.turnaroundTime, style = MaterialTheme.typography.bodyMedium, color = TextPrimaryDark, fontWeight = FontWeight.SemiBold)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                Button(
                    onClick = {
                        onBookConsultation(service.title)
                        onNavigate(Screen.Customers)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("btn_consult_${service.title.replace(" ", "_")}"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ImperialGold,
                        contentColor = ObsidianBlack
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Schedule, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Commission Design Consultation", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}
