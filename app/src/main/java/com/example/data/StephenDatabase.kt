package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        CustomerEntity::class,
        ProjectEntity::class,
        QuotationEntity::class,
        PortfolioEntity::class,
        ReviewEntity::class,
        TeamMemberEntity::class,
        VendorEntity::class,
        MaterialEntity::class,
        ProjectMaterialEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class StephenDatabase : RoomDatabase() {
    abstract fun dao(): StephenDao

    companion object {
        @Volatile
        private var INSTANCE: StephenDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): StephenDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    StephenDatabase::class.java,
                    "stephen_enterprises.db"
                ).addCallback(StephenDatabaseCallback(scope))
                 .fallbackToDestructiveMigration()
                 .build()
                INSTANCE = instance
                instance
            }
        }

        private class StephenDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialLuxuryData(database.dao())
                    }
                }
            }
        }

        suspend fun populateInitialLuxuryData(dao: StephenDao) {
            // Seed Team Members
            dao.insertTeamMember(
                TeamMemberEntity(
                    name = "Ar. Stephen Raj",
                    role = "Founder & Principal Architect",
                    email = "stephen@stephenenterprises.in",
                    phone = "+91 98450 12345",
                    activeProjectsCount = 4
                )
            )
            dao.insertTeamMember(
                TeamMemberEntity(
                    name = "Priya Nambiar",
                    role = "Lead Interior Stylist & Decor",
                    email = "priya@stephenenterprises.in",
                    phone = "+91 98450 67890",
                    activeProjectsCount = 3
                )
            )
            dao.insertTeamMember(
                TeamMemberEntity(
                    name = "Vikram Sengupta",
                    role = "Modular Architecture Specialist",
                    email = "vikram@stephenenterprises.in",
                    phone = "+91 98450 33445",
                    activeProjectsCount = 2
                )
            )

            // Seed Customers
            val c1Id = dao.insertCustomer(
                CustomerEntity(
                    name = "Dr. Rohan & Ananya Hegde",
                    email = "rohan.hegde@apollo.org",
                    phone = "+91 99801 44521",
                    location = "Defence Colony, Indiranagar, Bengaluru",
                    projectRequirement = "Complete 4BHK Luxury Villa Interior with Italian Modular Kitchen and home automation",
                    budgetRange = "₹55,00,000 - ₹75,00,000",
                    status = "Active",
                    notes = "Client requested high-end Italian quartz countertops and bespoke fluted wood wall panelling."
                )
            )

            val c2Id = dao.insertCustomer(
                CustomerEntity(
                    name = "Karthik Subramanian (CEO, FinNext)",
                    email = "karthik@finnext.io",
                    phone = "+91 98802 99112",
                    location = "Embassy GolfLinks, Domlur, Bengaluru",
                    projectRequirement = "5,000 sq.ft Executive Corporate Suite & Boardroom",
                    budgetRange = "₹85,00,000 - ₹1,10,00,000",
                    status = "Active",
                    notes = "Acoustic baffles, concealed gold warm LED lighting, custom walnut conference tables."
                )
            )

            val c3Id = dao.insertCustomer(
                CustomerEntity(
                    name = "Meera Krishnan",
                    email = "meera.krishnan@gmail.com",
                    phone = "+91 97412 88301",
                    location = "Koramangala 4th Block, Bengaluru",
                    projectRequirement = "3BHK Penthouse Master Suite & Modular Kitchen Overhaul",
                    budgetRange = "₹32,00,000 - ₹40,00,000",
                    status = "Completed",
                    notes = "Finished 2 weeks ahead of scheduled Diwali handover."
                )
            )

            val c4Id = dao.insertCustomer(
                CustomerEntity(
                    name = "Arjun & Shalini Varma",
                    email = "arjun.varma@techcorp.com",
                    phone = "+91 96118 77209",
                    location = "Diamond District, Old Airport Road, Domlur",
                    projectRequirement = "Contemporary Minimalist Living Room & Dining space makeover",
                    budgetRange = "₹18,00,000 - ₹24,00,000",
                    status = "New Enquiry",
                    notes = "Requested catalog preview of beige and brass finishes."
                )
            )

            // Seed Projects
            val p1Id = dao.insertProject(
                ProjectEntity(
                    title = "The Hegde Residence - Indiranagar Villa",
                    customerId = c1Id,
                    customerName = "Dr. Rohan & Ananya Hegde",
                    category = "Full Home",
                    status = "In Progress",
                    progressPercentage = 68,
                    totalBudget = 6200000.0,
                    paidAmount = 4500000.0,
                    startDate = "15 Jan 2026",
                    deadlineDate = "30 May 2026",
                    assignedLead = "Ar. Stephen Raj",
                    siteLocation = "Defence Colony, Indiranagar, Bengaluru",
                    scopeSummary = "Complete civil alterations, Italian marble flooring, bespoke fluted wall paneling, smart lighting fixtures, modular kitchen & 4 designer bedrooms.",
                    coverImageRes = R.drawable.img_luxury_living
                )
            )

            val p2Id = dao.insertProject(
                ProjectEntity(
                    title = "FinNext Corporate Headquarters",
                    customerId = c2Id,
                    customerName = "Karthik Subramanian",
                    category = "Commercial",
                    status = "Material Procurement",
                    progressPercentage = 42,
                    totalBudget = 9500000.0,
                    paidAmount = 3800000.0,
                    startDate = "01 Feb 2026",
                    deadlineDate = "15 Jul 2026",
                    assignedLead = "Priya Nambiar",
                    siteLocation = "Embassy GolfLinks, Domlur, Bengaluru",
                    scopeSummary = "Executive boardrooms with acoustic panelling, break-out ergonomic zones, modern minimalist reception desk and biophilic glass installations.",
                    coverImageRes = R.drawable.img_luxury_office
                )
            )

            val p3Id = dao.insertProject(
                ProjectEntity(
                    title = "Koramangala Penthouse Master Haven",
                    customerId = c3Id,
                    customerName = "Meera Krishnan",
                    category = "Bedroom",
                    status = "Completed",
                    progressPercentage = 100,
                    totalBudget = 3600000.0,
                    paidAmount = 3600000.0,
                    startDate = "10 Oct 2025",
                    deadlineDate = "20 Dec 2025",
                    assignedLead = "Vikram Sengupta",
                    siteLocation = "Koramangala 4th Block, Bengaluru",
                    scopeSummary = "Walk-in wardrobe with bronze glass doors, upholstered velvet headboard, custom dimmable cove lighting and private ensuite bath.",
                    coverImageRes = R.drawable.img_master_bedroom,
                    beforeImageRes = R.drawable.img_luxury_living,
                    afterImageRes = R.drawable.img_master_bedroom
                )
            )

            val p4Id = dao.insertProject(
                ProjectEntity(
                    title = "Bespoke Chef's Modular Kitchen",
                    customerId = c1Id,
                    customerName = "Dr. Rohan & Ananya Hegde",
                    category = "Modular Kitchen",
                    status = "In Progress",
                    progressPercentage = 75,
                    totalBudget = 2100000.0,
                    paidAmount = 1800000.0,
                    startDate = "20 Feb 2026",
                    deadlineDate = "25 Apr 2026",
                    assignedLead = "Vikram Sengupta",
                    siteLocation = "Defence Colony, Indiranagar",
                    scopeSummary = "Matte anthracite handle-less cabinets, warm LED under-cabinet illumination, quartz waterfall island, Blum German servo-drive hardware.",
                    coverImageRes = R.drawable.img_modular_kitchen,
                    beforeImageRes = R.drawable.img_luxury_living,
                    afterImageRes = R.drawable.img_modular_kitchen
                )
            )

            // Seed Quotations & Invoices
            dao.insertQuotation(
                QuotationEntity(
                    quotationNumber = "SE-QUO-2026-101",
                    projectId = p1Id,
                    projectTitle = "The Hegde Residence - Indiranagar Villa",
                    customerName = "Dr. Rohan & Ananya Hegde",
                    customerEmail = "rohan.hegde@apollo.org",
                    dateIssued = "10 Jan 2026",
                    validUntil = "10 Feb 2026",
                    subtotal = 5254237.28,
                    gstRatePercent = 18.0,
                    grandTotal = 6200000.0,
                    status = "Approved",
                    paymentStatus = "Advance Paid (40%)",
                    itemsJson = """[{"description":"Architectural Design & 3D Spatial Planning","amount":450000.0},{"description":"Italian Botticino Marble Flooring & Installation","amount":1250000.0},{"description":"Bespoke Fluted Oak Wall Paneling & False Ceilings","amount":980000.0},{"description":"Full Modular Kitchen with Blum Soft-Close Hardware","amount":1500000.0},{"description":"Smart Home Cove Lighting & Automation Conduiting","amount":1074237.28}]"""
                )
            )

            dao.insertQuotation(
                QuotationEntity(
                    quotationNumber = "SE-INV-2025-089",
                    projectId = p3Id,
                    projectTitle = "Koramangala Penthouse Master Haven",
                    customerName = "Meera Krishnan",
                    customerEmail = "meera.krishnan@gmail.com",
                    dateIssued = "05 Oct 2025",
                    validUntil = "05 Nov 2025",
                    subtotal = 3050847.45,
                    gstRatePercent = 18.0,
                    grandTotal = 3600000.0,
                    status = "Paid",
                    paymentStatus = "Fully Paid",
                    itemsJson = """[{"description":"Master Suite Spatial Layout & Lighting Design","amount":250000.0},{"description":"Floor-to-Ceiling Bronze Glass Tinted Wardrobes","amount":1200000.0},{"description":"Upholstered King Headboard & Bed Base with Storage","amount":600000.0},{"description":"Ensuite Bathroom Italian Tiles & Concealed Plumbing","amount":1000847.45}]"""
                )
            )

            dao.insertQuotation(
                QuotationEntity(
                    quotationNumber = "SE-QUO-2026-104",
                    projectId = p2Id,
                    projectTitle = "FinNext Corporate Headquarters",
                    customerName = "Karthik Subramanian",
                    customerEmail = "karthik@finnext.io",
                    dateIssued = "28 Jan 2026",
                    validUntil = "28 Feb 2026",
                    subtotal = 8050847.45,
                    gstRatePercent = 18.0,
                    grandTotal = 9500000.0,
                    status = "Sent",
                    paymentStatus = "Advance Paid (40%)",
                    itemsJson = """[{"description":"Corporate Spatial Architecture & Fire Safety Clearances","amount":900000.0},{"description":"Acoustic Wood Paneling for 24-Seater Boardroom","amount":2200000.0},{"description":"Modular Workstations with Cable Management (60 Pods)","amount":3100000.0},{"description":"Executive Cabin Custom Walnut Desks & Brass Credenzas","amount":1850847.45}]"""
                )
            )

            // Seed Portfolio Showcase
            dao.insertPortfolioItem(
                PortfolioEntity(
                    title = "Prestige Kingfisher Penthouse",
                    category = "Living Room",
                    clientName = "Naveen Jindal Estate",
                    location = "Lavelle Road, Bengaluru",
                    completionYear = "2025",
                    areaSqFt = 4200,
                    description = "An epitome of understated luxury featuring double-height travertine marble walls, warm recessed champagne gold cove accents, and custom Italian leather furniture curated exclusively for Stephen Enterprises.",
                    coverImageRes = R.drawable.img_luxury_living,
                    beforeImageRes = R.drawable.img_luxury_living,
                    afterImageRes = R.drawable.img_luxury_living,
                    isFeatured = true
                )
            )

            dao.insertPortfolioItem(
                PortfolioEntity(
                    title = "Elysian Contemporary Villa",
                    category = "Modular Kitchen",
                    clientName = "Aditi & Rahul Singhania",
                    location = "Domlur Layout, Bengaluru",
                    completionYear = "2025",
                    areaSqFt = 950,
                    description = "Showcase German Blum fittings, dark fluted veneer cabinets, Calacatta gold quartz waterfall counters, and integrated concealed wine coolers crafted for gourmet culinary dining.",
                    coverImageRes = R.drawable.img_modular_kitchen,
                    beforeImageRes = R.drawable.img_luxury_living,
                    afterImageRes = R.drawable.img_modular_kitchen,
                    isFeatured = true
                )
            )

            dao.insertPortfolioItem(
                PortfolioEntity(
                    title = "Serene Velvet Master Sanctuary",
                    category = "Bedroom",
                    clientName = "Meera Krishnan",
                    location = "Koramangala, Bengaluru",
                    completionYear = "2025",
                    areaSqFt = 1200,
                    description = "A peaceful sanctuary enveloped in beige velvet upholstered paneling, warm 2700K ambient circadian lighting, custom walk-in dressing room with fluted bronze glass.",
                    coverImageRes = R.drawable.img_master_bedroom,
                    beforeImageRes = R.drawable.img_luxury_living,
                    afterImageRes = R.drawable.img_master_bedroom,
                    isFeatured = true
                )
            )

            dao.insertPortfolioItem(
                PortfolioEntity(
                    title = "Apex Global Executive Suite",
                    category = "Commercial",
                    clientName = "FinNext Technologies",
                    location = "Embassy GolfLinks, Domlur",
                    completionYear = "2026",
                    areaSqFt = 5800,
                    description = "International grade corporate architecture balancing private focus hubs with collaborative acoustic glass meeting pavilions and a striking brass-accented welcome lounge.",
                    coverImageRes = R.drawable.img_luxury_office,
                    beforeImageRes = R.drawable.img_luxury_living,
                    afterImageRes = R.drawable.img_luxury_office,
                    isFeatured = true
                )
            )

            // Seed Reviews
            dao.insertReview(
                ReviewEntity(
                    clientName = "Dr. Rohan Hegde",
                    projectTitle = "4BHK Indiranagar Villa",
                    location = "Indiranagar, Bengaluru",
                    rating = 5,
                    comment = "Stephen Enterprises transformed our villa beyond expectations. The attention to detail in the gold finishes, fluted paneling, and German kitchen hardware is simply unmatched in Bangalore. Highly recommended!",
                    date = "January 2026"
                )
            )

            dao.insertReview(
                ReviewEntity(
                    clientName = "Karthik Subramanian (CEO)",
                    projectTitle = "FinNext Corporate Headquarters",
                    location = "Domlur, Bengaluru",
                    rating = 5,
                    comment = "Ar. Stephen and his team delivered our 5,000 sq ft office in Embassy GolfLinks on time with world-class finish. Every visiting international client compliments our boardroom aesthetics.",
                    date = "February 2026"
                )
            )

            dao.insertReview(
                ReviewEntity(
                    clientName = "Meera Krishnan",
                    projectTitle = "Koramangala Penthouse Bedroom",
                    location = "Koramangala 4th Block",
                    rating = 5,
                    comment = "The master suite feels like a 5-star presidential hotel suite every single day. The warm lighting design and bronze tinted wardrobe craftsmanship is exemplary.",
                    date = "December 2025"
                )
            )

            // Seed Vendors (Bangalore Luxury Interior Suppliers)
            val v1Id = dao.insertVendor(
                VendorEntity(
                    businessName = "Marmi Orobici Italian Stone Atelier",
                    category = "Flooring & Marble",
                    contactPerson = "Gianni Rossi & Suresh Babu",
                    phone = "+91 98451 77889",
                    email = "bangalore@marmiorobici.it",
                    address = "Survey 48, Bannerghatta Road / Domlur Hub, Bengaluru",
                    rating = 4.9,
                    leadTimeDays = 14,
                    isPreferredPartner = true,
                    notes = "Direct importer of Botticino Classico, Calacatta Gold & Statuario slabs."
                )
            )

            val v2Id = dao.insertVendor(
                VendorEntity(
                    businessName = "Blum Austria Hardware Gallery",
                    category = "Modular Hardware",
                    contactPerson = "Naveen Chawla",
                    phone = "+91 98801 22334",
                    email = "sales.blr@blum.com",
                    address = "100 Feet Road, Indiranagar, Bengaluru",
                    rating = 5.0,
                    leadTimeDays = 5,
                    isPreferredPartner = true,
                    notes = "Official source for Aventos lift systems, Legrabox drawers and Servo-Drive motorized units."
                )
            )

            val v3Id = dao.insertVendor(
                VendorEntity(
                    businessName = "Lutron & Flos Architectural Lighting",
                    category = "Electrical & Smart Lighting",
                    contactPerson = "Aditya Kulkarni",
                    phone = "+91 97400 99881",
                    email = "aditya@floslux.in",
                    address = "Lavelle Road, Bengaluru",
                    rating = 4.8,
                    leadTimeDays = 10,
                    isPreferredPartner = true,
                    notes = "Smart Dali/KNX architectural recessed dimmable downlights and 2700K warm circadian LED strips."
                )
            )

            val v4Id = dao.insertVendor(
                VendorEntity(
                    businessName = "Gessi & Kohler Exclusive Bath Suites",
                    category = "Fixtures & Sanitaryware",
                    contactPerson = "Deepak Nair",
                    phone = "+91 99002 66778",
                    email = "deepak@luxurysanitary.in",
                    address = "Koramangala 80 Feet Road, Bengaluru",
                    rating = 4.9,
                    leadTimeDays = 7,
                    isPreferredPartner = true,
                    notes = "Brushed warm brass thermostatic mixers, sensor-flush cisterns and freestanding bathtubs."
                )
            )

            val v5Id = dao.insertVendor(
                VendorEntity(
                    businessName = "Asian Paints Royale & Oikos Italian Stucco",
                    category = "Paint & Finishes",
                    contactPerson = "Rajesh G.",
                    phone = "+91 96110 33441",
                    email = "rajesh@oikosdecor.in",
                    address = "Domlur Layout, Bengaluru",
                    rating = 4.7,
                    leadTimeDays = 3,
                    isPreferredPartner = true,
                    notes = "Travertino lime wash, Venetian Marmorino plaster, and metallic champagne velvet textures."
                )
            )

            // Seed Materials
            val m1Id = dao.insertMaterial(
                MaterialEntity(
                    name = "Italian Botticino Classico Bookmatched Slab",
                    category = "Flooring",
                    subCategory = "Natural Stone",
                    unit = "sq.ft",
                    unitCost = 550.0,
                    vendorId = v1Id,
                    vendorName = "Marmi Orobici Italian Stone Atelier",
                    specification = "18mm calibrated high-density Italian beige marble with crystalline beige veins, mirror diamond polish.",
                    rating = 4.9,
                    inStock = true,
                    imageRes = R.drawable.img_luxury_living
                )
            )

            val m2Id = dao.insertMaterial(
                MaterialEntity(
                    name = "Calacatta Gold Engineered Quartz Waterfall Countertop",
                    category = "Flooring",
                    subCategory = "Engineered Stone",
                    unit = "sq.ft",
                    unitCost = 480.0,
                    vendorId = v1Id,
                    vendorName = "Marmi Orobici Italian Stone Atelier",
                    specification = "20mm seamless quartz slab with gold-bronze veining, zero porosity and heat-resistant finish.",
                    rating = 5.0,
                    inStock = true,
                    imageRes = R.drawable.img_modular_kitchen
                )
            )

            val m3Id = dao.insertMaterial(
                MaterialEntity(
                    name = "Blum Aventos HF Servo-Drive Bi-fold Lift System",
                    category = "Hardware",
                    subCategory = "Motorized Lift",
                    unit = "set",
                    unitCost = 28500.0,
                    vendorId = v2Id,
                    vendorName = "Blum Austria Hardware Gallery",
                    specification = "Electric touch-to-open motorized lift mechanism with integrated Blumotion soft-close, 10-year warranty.",
                    rating = 5.0,
                    inStock = true,
                    imageRes = R.drawable.img_modular_kitchen
                )
            )

            val m4Id = dao.insertMaterial(
                MaterialEntity(
                    name = "Architectural Flos 2700K Warm Dimmable COB Downlight",
                    category = "Lighting",
                    subCategory = "Architectural Spot",
                    unit = "piece",
                    unitCost = 3800.0,
                    vendorId = v3Id,
                    vendorName = "Lutron & Flos Architectural Lighting",
                    specification = "CRI 97, anti-glare deep recessed trimless design with 24-degree beam angle.",
                    rating = 4.8,
                    inStock = true,
                    imageRes = R.drawable.img_master_bedroom
                )
            )

            val m5Id = dao.insertMaterial(
                MaterialEntity(
                    name = "Gessi Brushed Warm Gold Thermostatic Concealed Shower Suite",
                    category = "Fixtures",
                    subCategory = "Thermostatic Mixer",
                    unit = "set",
                    unitCost = 85000.0,
                    vendorId = v4Id,
                    vendorName = "Gessi & Kohler Exclusive Bath Suites",
                    specification = "PVD electroplated brushed brass, 3-outlet diverter with ceiling rainfall shower & body jets.",
                    rating = 4.9,
                    inStock = true,
                    imageRes = R.drawable.img_master_bedroom
                )
            )

            val m6Id = dao.insertMaterial(
                MaterialEntity(
                    name = "Oikos Italian Lime Travertino Texture Stucco",
                    category = "Paint & Texture",
                    subCategory = "Venetian Stucco",
                    unit = "sq.ft",
                    unitCost = 145.0,
                    vendorId = v5Id,
                    vendorName = "Asian Paints Royale & Oikos Italian Stucco",
                    specification = "Natural slaked lime and marble powder formulation providing authentic porous travertine stone texture.",
                    rating = 4.7,
                    inStock = true,
                    imageRes = R.drawable.img_luxury_living
                )
            )

            // Seed Project Materials Selection
            dao.insertProjectMaterial(
                ProjectMaterialEntity(
                    projectId = p1Id,
                    projectTitle = "The Hegde Residence - Indiranagar Villa",
                    materialId = m1Id,
                    materialName = "Italian Botticino Classico Bookmatched Slab",
                    category = "Flooring",
                    unit = "sq.ft",
                    unitCost = 550.0,
                    quantity = 2200.0,
                    totalCost = 1210000.0,
                    status = "Approved"
                )
            )

            dao.insertProjectMaterial(
                ProjectMaterialEntity(
                    projectId = p1Id,
                    projectTitle = "The Hegde Residence - Indiranagar Villa",
                    materialId = m3Id,
                    materialName = "Blum Aventos HF Servo-Drive Bi-fold Lift System",
                    category = "Hardware",
                    unit = "set",
                    unitCost = 28500.0,
                    quantity = 6.0,
                    totalCost = 171000.0,
                    status = "Ordered"
                )
            )

            dao.insertProjectMaterial(
                ProjectMaterialEntity(
                    projectId = p4Id,
                    projectTitle = "Bespoke Chef's Modular Kitchen",
                    materialId = m2Id,
                    materialName = "Calacatta Gold Engineered Quartz Waterfall Countertop",
                    category = "Flooring",
                    unit = "sq.ft",
                    unitCost = 480.0,
                    quantity = 350.0,
                    totalCost = 168000.0,
                    status = "Approved"
                )
            )
        }
    }
}
