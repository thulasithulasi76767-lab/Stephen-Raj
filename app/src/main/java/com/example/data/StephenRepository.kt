package com.example.data

import kotlinx.coroutines.flow.Flow

class StephenRepository(private val dao: StephenDao) {
    // Customers
    val customers: Flow<List<CustomerEntity>> = dao.getAllCustomers()
    suspend fun addCustomer(customer: CustomerEntity): Long = dao.insertCustomer(customer)
    suspend fun updateCustomer(customer: CustomerEntity) = dao.updateCustomer(customer)
    suspend fun deleteCustomer(customer: CustomerEntity) = dao.deleteCustomer(customer)

    // Projects
    val projects: Flow<List<ProjectEntity>> = dao.getAllProjects()
    suspend fun getProjectById(id: Long): ProjectEntity? = dao.getProjectById(id)
    suspend fun addProject(project: ProjectEntity): Long = dao.insertProject(project)
    suspend fun updateProject(project: ProjectEntity) = dao.updateProject(project)
    suspend fun deleteProject(project: ProjectEntity) = dao.deleteProject(project)

    // Quotations / Billing
    val quotations: Flow<List<QuotationEntity>> = dao.getAllQuotations()
    suspend fun addQuotation(quotation: QuotationEntity): Long = dao.insertQuotation(quotation)
    suspend fun updateQuotation(quotation: QuotationEntity) = dao.updateQuotation(quotation)

    // Portfolio
    val portfolioItems: Flow<List<PortfolioEntity>> = dao.getAllPortfolioItems()
    suspend fun addPortfolioItem(item: PortfolioEntity): Long = dao.insertPortfolioItem(item)

    // Reviews
    val reviews: Flow<List<ReviewEntity>> = dao.getAllReviews()
    suspend fun addReview(review: ReviewEntity): Long = dao.insertReview(review)

    // Team Members
    val teamMembers: Flow<List<TeamMemberEntity>> = dao.getAllTeamMembers()
    suspend fun addTeamMember(member: TeamMemberEntity): Long = dao.insertTeamMember(member)

    // Vendors
    val vendors: Flow<List<VendorEntity>> = dao.getAllVendors()
    suspend fun addVendor(vendor: VendorEntity): Long = dao.insertVendor(vendor)
    suspend fun updateVendor(vendor: VendorEntity) = dao.updateVendor(vendor)
    suspend fun deleteVendor(vendor: VendorEntity) = dao.deleteVendor(vendor)

    // Materials
    val materials: Flow<List<MaterialEntity>> = dao.getAllMaterials()
    fun getMaterialsByCategory(category: String): Flow<List<MaterialEntity>> = dao.getMaterialsByCategory(category)
    suspend fun addMaterial(material: MaterialEntity): Long = dao.insertMaterial(material)
    suspend fun updateMaterial(material: MaterialEntity) = dao.updateMaterial(material)
    suspend fun deleteMaterial(material: MaterialEntity) = dao.deleteMaterial(material)

    // Project Materials
    val projectMaterials: Flow<List<ProjectMaterialEntity>> = dao.getAllProjectMaterials()
    fun getMaterialsForProject(projectId: Long): Flow<List<ProjectMaterialEntity>> = dao.getMaterialsForProject(projectId)
    suspend fun addProjectMaterial(item: ProjectMaterialEntity): Long = dao.insertProjectMaterial(item)
    suspend fun deleteProjectMaterial(item: ProjectMaterialEntity) = dao.deleteProjectMaterial(item)
}

