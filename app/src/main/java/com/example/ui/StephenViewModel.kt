package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.R
import com.example.data.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class Screen(val title: String) {
    data object Dashboard : Screen("Executive Dashboard")
    data object Customers : Screen("Client Management")
    data object Projects : Screen("Project Portfolio")
    data object Services : Screen("Design Services")
    data object MaterialsVendors : Screen("Materials & Vendor Library")
    data object Visualizer3D : Screen("3D Room Spatial Studio")
    data object Portfolio : Screen("Luxury Showcase")
    data object QuotationBilling : Screen("Quotations & Billing")
    data object CustomerReviews : Screen("Client Testimonials")
    data object Settings : Screen("Business Settings")
}

data class DashboardMetrics(
    val totalCustomers: Int = 0,
    val totalProjects: Int = 0,
    val activeProjects: Int = 0,
    val completedProjects: Int = 0,
    val totalRevenue: Double = 0.0,
    val pendingRevenue: Double = 0.0
)

class StephenViewModel(application: Application) : AndroidViewModel(application) {
    private val database = StephenDatabase.getDatabase(application, viewModelScope)
    val repository = StephenRepository(database.dao())

    private val _currentScreen = MutableStateFlow<Screen>(Screen.Dashboard)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val _userRole = MutableStateFlow("Principal Architect / Executive")
    val userRole: StateFlow<String> = _userRole.asStateFlow()

    val customers: StateFlow<List<CustomerEntity>> = repository.customers.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val projects: StateFlow<List<ProjectEntity>> = repository.projects.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val quotations: StateFlow<List<QuotationEntity>> = repository.quotations.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val portfolioItems: StateFlow<List<PortfolioEntity>> = repository.portfolioItems.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val reviews: StateFlow<List<ReviewEntity>> = repository.reviews.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val teamMembers: StateFlow<List<TeamMemberEntity>> = repository.teamMembers.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val vendors: StateFlow<List<VendorEntity>> = repository.vendors.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val materials: StateFlow<List<MaterialEntity>> = repository.materials.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val projectMaterials: StateFlow<List<ProjectMaterialEntity>> = repository.projectMaterials.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // UI Feedback state
    private val _snackBarMessage = MutableStateFlow<String?>(null)
    val snackBarMessage: StateFlow<String?> = _snackBarMessage.asStateFlow()

    fun navigateTo(screen: Screen) {
        _currentScreen.value = screen
    }

    fun clearSnackBar() {
        _snackBarMessage.value = null
    }

    fun showMessage(msg: String) {
        _snackBarMessage.value = msg
    }

    // Customer operations
    fun createCustomer(name: String, email: String, phone: String, location: String, req: String, budget: String, notes: String) {
        viewModelScope.launch {
            val customer = CustomerEntity(
                name = name,
                email = email,
                phone = phone,
                location = location,
                projectRequirement = req,
                budgetRange = budget,
                notes = notes,
                status = "Active"
            )
            repository.addCustomer(customer)
            _snackBarMessage.value = "New client profile created for $name"
        }
    }

    fun deleteCustomer(customer: CustomerEntity) {
        viewModelScope.launch {
            repository.deleteCustomer(customer)
            _snackBarMessage.value = "Client ${customer.name} removed"
        }
    }

    // Project operations
    fun createProject(
        title: String,
        customerId: Long,
        customerName: String,
        category: String,
        totalBudget: Double,
        startDate: String,
        deadlineDate: String,
        assignedLead: String,
        siteLocation: String,
        scopeSummary: String
    ) {
        viewModelScope.launch {
            val project = ProjectEntity(
                title = title,
                customerId = customerId,
                customerName = customerName,
                category = category,
                status = "Planning",
                progressPercentage = 10,
                totalBudget = totalBudget,
                paidAmount = 0.0,
                startDate = startDate,
                deadlineDate = deadlineDate,
                assignedLead = assignedLead,
                siteLocation = siteLocation,
                scopeSummary = scopeSummary,
                coverImageRes = when (category) {
                    "Modular Kitchen" -> R.drawable.img_modular_kitchen
                    "Bedroom" -> R.drawable.img_master_bedroom
                    "Commercial", "Office" -> R.drawable.img_luxury_office
                    else -> R.drawable.img_luxury_living
                }
            )
            repository.addProject(project)
            _snackBarMessage.value = "Project '$title' commissioned successfully"
        }
    }

    fun updateProjectProgress(project: ProjectEntity, newProgress: Int, newStatus: String) {
        viewModelScope.launch {
            repository.updateProject(
                project.copy(
                    progressPercentage = newProgress,
                    status = newStatus
                )
            )
            _snackBarMessage.value = "Updated progress for ${project.title} to $newProgress%"
        }
    }

    fun deleteProject(project: ProjectEntity) {
        viewModelScope.launch {
            repository.deleteProject(project)
            _snackBarMessage.value = "Project removed"
        }
    }

    // Quotation operations
    fun createQuotation(
        projectId: Long,
        projectTitle: String,
        customerName: String,
        customerEmail: String,
        subtotal: Double,
        itemsJson: String
    ) {
        viewModelScope.launch {
            val gst = subtotal * 0.18
            val total = subtotal + gst
            val quoNumber = "SE-QUO-2026-${(100..999).random()}"
            val quotation = QuotationEntity(
                quotationNumber = quoNumber,
                projectId = projectId,
                projectTitle = projectTitle,
                customerName = customerName,
                customerEmail = customerEmail,
                dateIssued = "Today",
                validUntil = "30 Days",
                subtotal = subtotal,
                gstRatePercent = 18.0,
                grandTotal = total,
                status = "Sent",
                paymentStatus = "Pending",
                itemsJson = itemsJson
            )
            repository.addQuotation(quotation)
            _snackBarMessage.value = "Quotation $quoNumber generated and sent to $customerName"
        }
    }

    fun recordPayment(quotation: QuotationEntity, amount: Double) {
        viewModelScope.launch {
            val newStatus = if (amount >= quotation.grandTotal) "Fully Paid" else "Partially Paid"
            repository.updateQuotation(
                quotation.copy(
                    paymentStatus = newStatus,
                    status = if (newStatus == "Fully Paid") "Paid" else "Approved"
                )
            )
            _snackBarMessage.value = "Payment recorded for ${quotation.quotationNumber}"
        }
    }

    // Reviews
    fun addReview(name: String, project: String, location: String, rating: Int, comment: String) {
        viewModelScope.launch {
            val review = ReviewEntity(
                clientName = name,
                projectTitle = project,
                location = location,
                rating = rating,
                comment = comment,
                date = "Recent Client"
            )
            repository.addReview(review)
            _snackBarMessage.value = "Testimonial published for $name"
        }
    }

    // Portfolio
    fun addPortfolioItem(title: String, category: String, client: String, loc: String, year: String, sqft: Int, desc: String) {
        viewModelScope.launch {
            val item = PortfolioEntity(
                title = title,
                category = category,
                clientName = client,
                location = loc,
                completionYear = year,
                areaSqFt = sqft,
                description = desc,
                coverImageRes = when (category) {
                    "Modular Kitchen" -> R.drawable.img_modular_kitchen
                    "Bedroom" -> R.drawable.img_master_bedroom
                    "Commercial" -> R.drawable.img_luxury_office
                    else -> R.drawable.img_luxury_living
                },
                isFeatured = true
            )
            repository.addPortfolioItem(item)
            _snackBarMessage.value = "Showcase piece '$title' added to portfolio"
        }
    }

    fun addTeamMember(name: String, role: String, email: String, phone: String) {
        viewModelScope.launch {
            val member = TeamMemberEntity(
                name = name,
                role = role,
                email = email,
                phone = phone,
                activeProjectsCount = 1
            )
            repository.addTeamMember(member)
            _snackBarMessage.value = "Team member $name added to Stephen Enterprises staff"
        }
    }

    fun switchRole(role: String) {
        _userRole.value = role
        _snackBarMessage.value = "Switched active view role to: $role"
    }

    // Vendor Operations
    fun addVendor(name: String, cat: String, contact: String, phone: String, email: String, addr: String, rating: Double, leadTime: Int, notes: String) {
        viewModelScope.launch {
            val vendor = VendorEntity(
                businessName = name,
                category = cat,
                contactPerson = contact,
                phone = phone,
                email = email,
                address = addr,
                rating = rating,
                leadTimeDays = leadTime,
                isPreferredPartner = true,
                notes = notes
            )
            repository.addVendor(vendor)
            _snackBarMessage.value = "Vendor partner '$name' added to atelier network"
        }
    }

    fun deleteVendor(vendor: VendorEntity) {
        viewModelScope.launch {
            repository.deleteVendor(vendor)
            _snackBarMessage.value = "Vendor ${vendor.businessName} removed"
        }
    }

    // Material Operations
    fun addMaterial(name: String, cat: String, subCat: String, unit: String, cost: Double, vendorId: Long, vendorName: String, spec: String, rating: Double) {
        viewModelScope.launch {
            val material = MaterialEntity(
                name = name,
                category = cat,
                subCategory = subCat,
                unit = unit,
                unitCost = cost,
                vendorId = vendorId,
                vendorName = vendorName,
                specification = spec,
                rating = rating,
                inStock = true,
                imageRes = when (cat) {
                    "Modular Hardware", "Hardware" -> R.drawable.img_modular_kitchen
                    "Fixtures", "Lighting" -> R.drawable.img_master_bedroom
                    else -> R.drawable.img_luxury_living
                }
            )
            repository.addMaterial(material)
            _snackBarMessage.value = "Material specification '$name' added"
        }
    }

    fun deleteMaterial(material: MaterialEntity) {
        viewModelScope.launch {
            repository.deleteMaterial(material)
            _snackBarMessage.value = "Material ${material.name} removed"
        }
    }

    // Attach Material to Project (Project Management Integration)
    fun assignMaterialToProject(projectId: Long, projectTitle: String, material: MaterialEntity, quantity: Double) {
        viewModelScope.launch {
            val totalCost = material.unitCost * quantity
            val item = ProjectMaterialEntity(
                projectId = projectId,
                projectTitle = projectTitle,
                materialId = material.id,
                materialName = material.name,
                category = material.category,
                unit = material.unit,
                unitCost = material.unitCost,
                quantity = quantity,
                totalCost = totalCost,
                status = "Approved"
            )
            repository.addProjectMaterial(item)
            _snackBarMessage.value = "Attached ${quantity.toInt()} ${material.unit} of ${material.name} to $projectTitle"
        }
    }

    fun removeProjectMaterial(item: ProjectMaterialEntity) {
        viewModelScope.launch {
            repository.deleteProjectMaterial(item)
            _snackBarMessage.value = "Material removed from project specification"
        }
    }

    // Convert Selected Project Materials directly to an Official Quotation (Quotation & Billing Integration)
    fun generateQuotationFromProjectMaterials(projectId: Long, projectTitle: String, customerName: String, customerEmail: String) {
        viewModelScope.launch {
            val projectMats = projectMaterials.value.filter { it.projectId == projectId }
            if (projectMats.isEmpty()) {
                _snackBarMessage.value = "No materials specified for this project yet. Please attach materials first."
                return@launch
            }
            val subtotal = projectMats.sumOf { it.totalCost }
            val itemsJson = projectMats.joinToString(prefix = "[", postfix = "]", separator = ",") {
                """{"description":"${it.materialName} (${it.quantity.toInt()} ${it.unit})","amount":${it.totalCost}}"""
            }
            createQuotation(
                projectId = projectId,
                projectTitle = projectTitle,
                customerName = customerName,
                customerEmail = customerEmail,
                subtotal = subtotal,
                itemsJson = itemsJson
            )
            _snackBarMessage.value = "Generated official quotation for $projectTitle with ${projectMats.size} material line items!"
        }
    }
}
