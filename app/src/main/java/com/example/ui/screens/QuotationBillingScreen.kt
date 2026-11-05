package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.example.data.ProjectEntity
import com.example.data.QuotationEntity
import com.example.ui.components.LuxuryCard
import com.example.ui.components.StatusBadge
import com.example.ui.components.formatInr
import com.example.ui.theme.*
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuotationBillingScreen(
    quotations: List<QuotationEntity>,
    projects: List<ProjectEntity>,
    onCreateQuotation: (projectId: Long, projectTitle: String, customerName: String, customerEmail: String, subtotal: Double, itemsJson: String) -> Unit,
    onRecordPayment: (QuotationEntity, Double) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showCreateDialog by remember { mutableStateOf(false) }
    var quotationToPay by remember { mutableStateOf<QuotationEntity?>(null) }
    var quotationToInspect by remember { mutableStateOf<QuotationEntity?>(null) }

    val totalInvoiced = quotations.sumOf { it.grandTotal }
    val paidQuotations = quotations.filter { it.status == "Paid" || it.paymentStatus == "Fully Paid" }
    val pendingQuotations = quotations.filter { it.paymentStatus != "Fully Paid" }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateDialog = true },
                containerColor = ImperialGold,
                contentColor = ObsidianBlack,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.testTag("fab_create_quotation")
            ) {
                Icon(Icons.Default.PostAdd, contentDescription = "Create Quotation")
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
            // Billing Summary Banner
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(CharcoalSurface)
                            .border(1.dp, ImperialGold.copy(alpha = 0.35f), RoundedCornerShape(14.dp))
                            .padding(14.dp)
                    ) {
                        Column {
                            Text("TOTAL INVOICED", style = MaterialTheme.typography.labelSmall, color = TextSecondaryDark)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(formatInr(totalInvoiced), style = MaterialTheme.typography.titleLarge, color = ChampagneGold, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Serif)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("${quotations.size} Official Quotations", style = MaterialTheme.typography.labelSmall, color = SoftBeige)
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(CharcoalSurface)
                            .border(1.dp, StatusPending.copy(alpha = 0.35f), RoundedCornerShape(14.dp))
                            .padding(14.dp)
                    ) {
                        Column {
                            Text("PENDING CLEARANCE", style = MaterialTheme.typography.labelSmall, color = TextSecondaryDark)
                            Spacer(modifier = Modifier.height(4.dp))
                            val pendingTotal = pendingQuotations.sumOf { it.grandTotal }
                            Text(formatInr(pendingTotal), style = MaterialTheme.typography.titleLarge, color = Color(0xFFFFB74D), fontWeight = FontWeight.Bold, fontFamily = FontFamily.Serif)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("${pendingQuotations.size} Pending Actions", style = MaterialTheme.typography.labelSmall, color = SoftBeige)
                        }
                    }
                }
            }

            item {
                Text(
                    text = "OFFICIAL INVOICES & QUOTATIONS",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondaryDark,
                    letterSpacing = 1.5.sp
                )
            }

            items(quotations, key = { it.id }) { quote ->
                LuxuryCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = quote.quotationNumber,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = ChampagneGold,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                StatusBadge(status = quote.paymentStatus)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = quote.projectTitle,
                                style = MaterialTheme.typography.titleLarge,
                                color = TextPrimaryDark,
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Client: ${quote.customerName} (${quote.customerEmail})",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondaryDark
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(CharcoalSurface)
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("NET AMOUNT", style = MaterialTheme.typography.labelSmall, color = TextSecondaryDark)
                            Text(formatInr(quote.subtotal), style = MaterialTheme.typography.bodyMedium, color = TextPrimaryDark)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("GST (18%)", style = MaterialTheme.typography.labelSmall, color = TextSecondaryDark)
                            Text(formatInr(quote.subtotal * 0.18), style = MaterialTheme.typography.bodyMedium, color = SoftBeige)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("TOTAL PAYABLE", style = MaterialTheme.typography.labelSmall, color = TextSecondaryDark)
                            Text(formatInr(quote.grandTotal), style = MaterialTheme.typography.titleMedium, color = ImperialGold, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Issued: ${quote.dateIssued} • Valid: ${quote.validUntil}",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondaryDark
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Download & Share PDF Button
                            OutlinedButton(
                                onClick = {
                                    generateAndSharePdfInvoice(context, quote)
                                },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = PaleGold),
                                border = ButtonDefaults.outlinedButtonBorder.copy(
                                    brush = Brush.linearGradient(listOf(ImperialGold, CharcoalBorder))
                                ),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("PDF Invoice", style = MaterialTheme.typography.labelSmall)
                            }

                            if (quote.paymentStatus != "Fully Paid") {
                                Button(
                                    onClick = { quotationToPay = quote },
                                    colors = ButtonDefaults.buttonColors(containerColor = ImperialGold, contentColor = ObsidianBlack),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Icon(Icons.Default.Payment, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Pay", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showCreateDialog) {
        CreateQuotationDialog(
            projects = projects,
            onDismiss = { showCreateDialog = false },
            onConfirm = { projId, projTitle, cName, cEmail, subtotal, itemsJson ->
                onCreateQuotation(projId, projTitle, cName, cEmail, subtotal, itemsJson)
                showCreateDialog = false
            }
        )
    }

    quotationToPay?.let { quote ->
        RecordPaymentDialog(
            quotation = quote,
            onDismiss = { quotationToPay = null },
            onConfirm = { amount ->
                onRecordPayment(quote, amount)
                quotationToPay = null
            }
        )
    }
}

fun generateAndSharePdfInvoice(context: Context, quote: QuotationEntity) {
    try {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 standard
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        val paint = Paint()
        paint.isAntiAlias = true

        // Header Background
        paint.color = android.graphics.Color.parseColor("#121212")
        canvas.drawRect(0f, 0f, 595f, 130f, paint)

        // Brand Name
        paint.color = android.graphics.Color.parseColor("#D4AF37")
        paint.textSize = 22f
        paint.isFakeBoldText = true
        canvas.drawText("STEPHEN ENTERPRISES", 40f, 55f, paint)

        paint.color = android.graphics.Color.parseColor("#E0E0E0")
        paint.textSize = 11f
        paint.isFakeBoldText = false
        canvas.drawText("Luxury Architectural & Interior Design Atelier", 40f, 75f, paint)
        canvas.drawText("Domlur, Bengaluru, Karnataka 560071, India | +91 98450 12345", 40f, 95f, paint)

        // Invoice Meta
        paint.color = android.graphics.Color.parseColor("#121212")
        paint.textSize = 18f
        paint.isFakeBoldText = true
        canvas.drawText("TAX INVOICE / QUOTATION", 40f, 170f, paint)

        paint.textSize = 12f
        paint.isFakeBoldText = false
        paint.color = android.graphics.Color.parseColor("#555555")
        canvas.drawText("Invoice Number: ${quote.quotationNumber}", 40f, 195f, paint)
        canvas.drawText("Date: ${quote.dateIssued} | Valid: ${quote.validUntil}", 40f, 215f, paint)
        canvas.drawText("Project: ${quote.projectTitle}", 40f, 235f, paint)

        // Client Info Box
        canvas.drawText("BILLED TO:", 340f, 195f, paint)
        paint.isFakeBoldText = true
        paint.color = android.graphics.Color.parseColor("#121212")
        canvas.drawText(quote.customerName, 340f, 215f, paint)
        paint.isFakeBoldText = false
        paint.color = android.graphics.Color.parseColor("#555555")
        canvas.drawText(quote.customerEmail, 340f, 235f, paint)

        // Table Header
        paint.color = android.graphics.Color.parseColor("#EEEEEE")
        canvas.drawRect(40f, 270f, 555f, 300f, paint)
        paint.color = android.graphics.Color.parseColor("#121212")
        paint.isFakeBoldText = true
        paint.textSize = 11f
        canvas.drawText("SL", 50f, 290f, paint)
        canvas.drawText("SCOPE DESCRIPTION & SPECIFICATION", 90f, 290f, paint)
        canvas.drawText("AMOUNT (INR)", 450f, 290f, paint)

        // Table Items
        var yPos = 330f
        paint.isFakeBoldText = false
        paint.color = android.graphics.Color.parseColor("#333333")

        canvas.drawText("1", 50f, yPos, paint)
        canvas.drawText("Interior Architectural Scope & Premium Finishes", 90f, yPos, paint)
        canvas.drawText(formatInr(quote.subtotal), 440f, yPos, paint)

        yPos += 30f
        canvas.drawText("2", 50f, yPos, paint)
        canvas.drawText("Project Management, Structural Civil Alterations & Logistics", 90f, yPos, paint)
        canvas.drawText("INCLUDED", 450f, yPos, paint)

        // Totals Box
        yPos += 70f
        paint.color = android.graphics.Color.parseColor("#F9F9F9")
        canvas.drawRect(300f, yPos, 555f, yPos + 110f, paint)

        paint.color = android.graphics.Color.parseColor("#333333")
        paint.textSize = 11f
        canvas.drawText("Subtotal:", 320f, yPos + 30f, paint)
        canvas.drawText(formatInr(quote.subtotal), 440f, yPos + 30f, paint)

        canvas.drawText("GST (18%):", 320f, yPos + 55f, paint)
        canvas.drawText(formatInr(quote.subtotal * 0.18), 440f, yPos + 55f, paint)

        paint.isFakeBoldText = true
        paint.color = android.graphics.Color.parseColor("#D4AF37")
        paint.textSize = 13f
        canvas.drawText("Grand Total:", 320f, yPos + 85f, paint)
        canvas.drawText(formatInr(quote.grandTotal), 440f, yPos + 85f, paint)

        // Footer & Payment Terms
        paint.color = android.graphics.Color.parseColor("#777777")
        paint.textSize = 10f
        paint.isFakeBoldText = false
        canvas.drawText("Bank: HDFC Bank, Domlur Branch | A/C: 50200088991122 | IFSC: HDFC0001234", 40f, 760f, paint)
        canvas.drawText("Thank you for choosing Stephen Enterprises. This is a computer-generated tax invoice.", 40f, 780f, paint)

        pdfDocument.finishPage(page)

        // Save PDF to cache and share via Intent
        val file = File(context.cacheDir, "${quote.quotationNumber}.pdf")
        val outputStream = FileOutputStream(file)
        pdfDocument.writeTo(outputStream)
        pdfDocument.close()
        outputStream.close()

        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        val shareIntent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(Intent.createChooser(shareIntent, "View / Share Quotation PDF"))
    } catch (e: Exception) {
        // Fallback share as text if PDF viewer fails
        val shareText = "STEPHEN ENTERPRISES INVOICE\n${quote.quotationNumber}\nProject: ${quote.projectTitle}\nClient: ${quote.customerName}\nTotal Payable: ${formatInr(quote.grandTotal)}\nLocation: Domlur, Bengaluru"
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Invoice ${quote.quotationNumber}")
            putExtra(Intent.EXTRA_TEXT, shareText)
        }
        context.startActivity(Intent.createChooser(intent, "Share Invoice Summary"))
    }
}

@Composable
fun RecordPaymentDialog(
    quotation: QuotationEntity,
    onDismiss: () -> Unit,
    onConfirm: (Double) -> Unit
) {
    var amountStr by remember { mutableStateOf(quotation.grandTotal.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DeepCharcoal,
        title = {
            Text("Record Client Payment", color = ChampagneGold, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(text = "Invoice: ${quotation.quotationNumber}", style = MaterialTheme.typography.titleMedium, color = TextPrimaryDark)
                Text(text = "Total Bill: ${formatInr(quotation.grandTotal)}", color = ImperialGold, fontWeight = FontWeight.Bold)

                OutlinedTextField(
                    value = amountStr,
                    onValueChange = { amountStr = it },
                    label = { Text("Amount Received (INR)") },
                    modifier = Modifier.fillMaxWidth().testTag("input_payment_amount"),
                    singleLine = true,
                    colors = luxuryFieldColors()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amount = amountStr.toDoubleOrNull() ?: quotation.grandTotal
                    onConfirm(amount)
                },
                colors = ButtonDefaults.buttonColors(containerColor = ImperialGold, contentColor = ObsidianBlack)
            ) {
                Text("Confirm Payment")
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
fun CreateQuotationDialog(
    projects: List<ProjectEntity>,
    onDismiss: () -> Unit,
    onConfirm: (projectId: Long, projectTitle: String, customerName: String, customerEmail: String, subtotal: Double, itemsJson: String) -> Unit
) {
    var selectedProject by remember { mutableStateOf(projects.firstOrNull()) }
    var subtotalStr by remember { mutableStateOf("3500000") }
    var item1 by remember { mutableStateOf("Architectural Spatial Planning & 3D Walkthrough") }
    var item2 by remember { mutableStateOf("Civil Alterations & Italian Botticino Flooring") }
    var item3 by remember { mutableStateOf("Modular Kitchen with Blum Hardware") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DeepCharcoal,
        title = {
            Text("Generate Professional Quotation", color = ChampagneGold, fontWeight = FontWeight.Bold)
        },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                item {
                    Text("Select Commissioned Project:", style = MaterialTheme.typography.labelSmall, color = TextSecondaryDark)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(projects) { p ->
                            FilterChip(
                                selected = selectedProject?.id == p.id,
                                onClick = {
                                    selectedProject = p
                                    subtotalStr = (p.totalBudget / 1.18).toInt().toString()
                                },
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

                item {
                    OutlinedTextField(
                        value = subtotalStr,
                        onValueChange = { subtotalStr = it },
                        label = { Text("Base Subtotal Amount (excl. 18% GST) *") },
                        modifier = Modifier.fillMaxWidth().testTag("input_quote_subtotal"),
                        singleLine = true,
                        colors = luxuryFieldColors()
                    )
                }

                item {
                    val sub = subtotalStr.toDoubleOrNull() ?: 0.0
                    val gst = sub * 0.18
                    val total = sub + gst
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(CharcoalSurface)
                            .padding(10.dp)
                    ) {
                        Column {
                            Text("GST (18%): ${formatInr(gst)}", style = MaterialTheme.typography.labelSmall, color = TextSecondaryDark)
                            Text("Grand Total: ${formatInr(total)}", style = MaterialTheme.typography.titleMedium, color = ImperialGold, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                item {
                    Text("Primary Deliverables:", style = MaterialTheme.typography.labelSmall, color = TextSecondaryDark)
                    OutlinedTextField(value = item1, onValueChange = { item1 = it }, label = { Text("Milestone 1") }, modifier = Modifier.fillMaxWidth(), colors = luxuryFieldColors())
                }
                item {
                    OutlinedTextField(value = item2, onValueChange = { item2 = it }, label = { Text("Milestone 2") }, modifier = Modifier.fillMaxWidth(), colors = luxuryFieldColors())
                }
                item {
                    OutlinedTextField(value = item3, onValueChange = { item3 = it }, label = { Text("Milestone 3") }, modifier = Modifier.fillMaxWidth(), colors = luxuryFieldColors())
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val proj = selectedProject
                    val sub = subtotalStr.toDoubleOrNull() ?: 3000000.0
                    if (proj != null) {
                        val items = """[{"description":"$item1"},{"description":"$item2"},{"description":"$item3"}]"""
                        onConfirm(proj.id, proj.title, proj.customerName, "client@domain.com", sub, items)
                    }
                },
                enabled = selectedProject != null,
                colors = ButtonDefaults.buttonColors(containerColor = ImperialGold, contentColor = ObsidianBlack),
                modifier = Modifier.testTag("btn_save_quotation")
            ) {
                Text("Generate Quotation", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondaryDark)
            }
        }
    )
}
