package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "customers")
data class CustomerEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val email: String,
    val phone: String,
    val location: String, // e.g. "Indiranagar, Bengaluru"
    val projectRequirement: String, // e.g. "4BHK Luxury Villa Interior + Modular Kitchen"
    val budgetRange: String, // e.g. "₹45L - ₹60L"
    val enquiryDate: Long = System.currentTimeMillis(),
    val status: String = "Active", // "New Enquiry", "Discussion", "Active", "Completed"
    val notes: String = ""
)

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String, // e.g. "Prestige Golfshire Luxury Penthouse"
    val customerId: Long,
    val customerName: String,
    val category: String, // "Full Home", "Modular Kitchen", "Living Room", "Bedroom", "Office", "Commercial"
    val status: String, // "Planning", "3D Rendering", "Material Procurement", "In Progress", "Handover Ready", "Completed"
    val progressPercentage: Int, // 0 - 100
    val totalBudget: Double, // in INR
    val paidAmount: Double, // in INR
    val startDate: String,
    val deadlineDate: String,
    val assignedLead: String, // Team member e.g. "Ar. Stephen Raj"
    val siteLocation: String, // "Domlur, Bengaluru"
    val scopeSummary: String,
    val coverImageRes: Int? = null,
    val beforeImageRes: Int? = null,
    val afterImageRes: Int? = null
)

@Entity(tableName = "quotations")
data class QuotationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val quotationNumber: String, // "SE-QUO-2026-101"
    val projectId: Long,
    val projectTitle: String,
    val customerName: String,
    val customerEmail: String,
    val dateIssued: String,
    val validUntil: String,
    val subtotal: Double,
    val gstRatePercent: Double = 18.0,
    val grandTotal: Double,
    val status: String = "Sent", // "Draft", "Sent", "Approved", "Paid", "Partially Paid"
    val paymentStatus: String = "Pending", // "Unpaid", "Advance Paid (40%)", "Fully Paid"
    val itemsJson: String // Serialized items list
)

@Entity(tableName = "portfolio_items")
data class PortfolioEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val category: String, // "Full Home", "Living Room", "Bedroom", "Modular Kitchen", "Commercial"
    val clientName: String,
    val location: String, // e.g. "Domlur Layout, Bengaluru"
    val completionYear: String,
    val areaSqFt: Int,
    val description: String,
    val coverImageRes: Int,
    val beforeImageRes: Int? = null,
    val afterImageRes: Int? = null,
    val isFeatured: Boolean = true
)

@Entity(tableName = "reviews")
data class ReviewEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val clientName: String,
    val projectTitle: String,
    val location: String,
    val rating: Int, // 1 to 5
    val comment: String,
    val date: String,
    val isVerified: Boolean = true
)

@Entity(tableName = "team_members")
data class TeamMemberEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val role: String, // "Principal Architect", "Senior 3D Visualizer", "Modular Kitchen Specialist", "Project Manager"
    val email: String,
    val phone: String,
    val activeProjectsCount: Int
)

@Entity(tableName = "vendors")
data class VendorEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val businessName: String, // e.g. "Royal Italian Marble Traders"
    val category: String, // "Flooring & Marble", "Paint & Finishes", "Fixtures & Sanitaryware", "Electrical & Smart Lighting", "Modular Hardware", "Luxury Furniture"
    val contactPerson: String,
    val phone: String,
    val email: String,
    val address: String, // "Kalasipalyam / Domlur, Bengaluru"
    val rating: Double = 4.8, // 1.0 to 5.0
    val leadTimeDays: Int = 7,
    val isPreferredPartner: Boolean = true,
    val notes: String = ""
)

@Entity(tableName = "materials")
data class MaterialEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String, // "Italian Botticino Classico Marble"
    val category: String, // "Flooring", "Paint & Texture", "Fixtures", "Lighting", "Hardware", "Wood & Veneer", "Fabrics"
    val subCategory: String, // "Natural Stone", "Acrylic Emulsion", "Brass Concealed", etc.
    val unit: String, // "sq.ft", "litre", "piece", "set", "running ft"
    val unitCost: Double, // in INR
    val vendorId: Long,
    val vendorName: String,
    val specification: String,
    val rating: Double = 4.9,
    val inStock: Boolean = true,
    val imageRes: Int? = null
)

@Entity(tableName = "project_materials")
data class ProjectMaterialEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val projectId: Long,
    val projectTitle: String,
    val materialId: Long,
    val materialName: String,
    val category: String,
    val unit: String,
    val unitCost: Double,
    val quantity: Double,
    val totalCost: Double,
    val status: String = "Approved" // "Draft Spec", "Approved", "Ordered", "Installed"
)

