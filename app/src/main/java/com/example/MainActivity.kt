package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.Screen
import com.example.ui.StephenViewModel
import com.example.ui.screens.*
import com.example.ui.theme.*

data class NavItem(
    val screen: Screen,
    val label: String,
    val icon: ImageVector
)

class MainActivity : ComponentActivity() {
    private val viewModel: StephenViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StephenEnterprisesTheme(darkTheme = true) {
                MainAppScaffold(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScaffold(viewModel: StephenViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val snackBarMsg by viewModel.snackBarMessage.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    val customers by viewModel.customers.collectAsStateWithLifecycle()
    val projects by viewModel.projects.collectAsStateWithLifecycle()
    val quotations by viewModel.quotations.collectAsStateWithLifecycle()
    val portfolioItems by viewModel.portfolioItems.collectAsStateWithLifecycle()
    val reviews by viewModel.reviews.collectAsStateWithLifecycle()
    val teamMembers by viewModel.teamMembers.collectAsStateWithLifecycle()
    val vendors by viewModel.vendors.collectAsStateWithLifecycle()
    val materials by viewModel.materials.collectAsStateWithLifecycle()
    val projectMaterials by viewModel.projectMaterials.collectAsStateWithLifecycle()
    val userRole by viewModel.userRole.collectAsStateWithLifecycle()

    // Handle back button to return to Dashboard if inside another screen
    BackHandler(enabled = currentScreen != Screen.Dashboard) {
        viewModel.navigateTo(Screen.Dashboard)
    }

    LaunchedEffect(snackBarMsg) {
        snackBarMsg?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSnackBar()
        }
    }

    val primaryNavItems = listOf(
        NavItem(Screen.Dashboard, "Dashboard", Icons.Default.Dashboard),
        NavItem(Screen.Customers, "Clients", Icons.Default.People),
        NavItem(Screen.Projects, "Projects", Icons.Default.Architecture),
        NavItem(Screen.MaterialsVendors, "Materials", Icons.Default.Inventory2),
        NavItem(Screen.Visualizer3D, "3D Studio", Icons.Default.ViewInAr),
        NavItem(Screen.Services, "Services", Icons.Default.DesignServices),
        NavItem(Screen.Portfolio, "Portfolio", Icons.Default.PhotoLibrary),
        NavItem(Screen.QuotationBilling, "Billing", Icons.Default.ReceiptLong),
        NavItem(Screen.CustomerReviews, "Reviews", Icons.Default.Star),
        NavItem(Screen.Settings, "Settings", Icons.Default.Settings)
    )

    var showDrawerMenu by remember { mutableStateOf(false) }

    BoxWithConstraints(modifier = Modifier.fillMaxSize().background(ObsidianBlack)) {
        val isWideScreen = maxWidth >= 840.dp

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = ObsidianBlack,
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState) { data ->
                    Snackbar(
                        snackbarData = data,
                        containerColor = DeepCharcoal,
                        contentColor = PaleGold,
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            topBar = {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .border(1.dp, ImperialGold, CircleShape)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_stephen_logo),
                                    contentDescription = "Stephen Enterprises",
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "STEPHEN ENTERPRISES",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = ChampagneGold,
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = currentScreen.title,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = SoftBeige
                                )
                            }
                        }
                    },
                    actions = {
                        // Quick switch role / user indicator
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(CharcoalSurface)
                                .border(1.dp, ImperialGold.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Domlur Atelier",
                                style = MaterialTheme.typography.labelSmall,
                                color = ImperialGold,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        IconButton(onClick = { viewModel.navigateTo(Screen.Settings) }) {
                            Icon(Icons.Default.Tune, contentDescription = "Settings", tint = SoftBeige)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = ObsidianBlack,
                        titleContentColor = TextPrimaryDark
                    )
                )
            },
            bottomBar = {
                if (!isWideScreen) {
                    NavigationBar(
                        containerColor = DeepCharcoal,
                        contentColor = TextPrimaryDark,
                        tonalElevation = 8.dp,
                        windowInsets = WindowInsets.navigationBars,
                        modifier = Modifier.border(
                            androidx.compose.foundation.BorderStroke(1.dp, Brush.verticalGradient(listOf(CharcoalBorder, Color.Transparent)))
                        )
                    ) {
                        // Display the top 5 most critical items in bottom bar, others accessible via side drawer or header
                        val bottomItems = listOf(
                            primaryNavItems[0], // Dashboard
                            primaryNavItems[1], // Clients
                            primaryNavItems[2], // Projects
                            primaryNavItems[4], // Portfolio
                            primaryNavItems[5]  // Billing
                        )
                        bottomItems.forEach { item ->
                            val isSelected = currentScreen == item.screen
                            NavigationBarItem(
                                selected = isSelected,
                                onClick = { viewModel.navigateTo(item.screen) },
                                icon = {
                                    Icon(
                                        imageVector = item.icon,
                                        contentDescription = item.label,
                                        modifier = Modifier.size(20.dp)
                                    )
                                },
                                label = {
                                    Text(
                                        text = item.label,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = ObsidianBlack,
                                    selectedTextColor = ChampagneGold,
                                    indicatorColor = ImperialGold,
                                    unselectedIconColor = TextSecondaryDark,
                                    unselectedTextColor = TextSecondaryDark
                                )
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Wide Screen Navigation Rail (Tablet / Desktop responsive)
                if (isWideScreen) {
                    NavigationRail(
                        containerColor = DeepCharcoal,
                        contentColor = TextPrimaryDark,
                        modifier = Modifier.width(90.dp)
                    ) {
                        primaryNavItems.forEach { item ->
                            val isSelected = currentScreen == item.screen
                            NavigationRailItem(
                                selected = isSelected,
                                onClick = { viewModel.navigateTo(item.screen) },
                                icon = { Icon(item.icon, contentDescription = item.label) },
                                label = { Text(item.label, style = MaterialTheme.typography.labelSmall) },
                                colors = NavigationRailItemDefaults.colors(
                                    selectedIconColor = ObsidianBlack,
                                    selectedTextColor = ChampagneGold,
                                    indicatorColor = ImperialGold,
                                    unselectedIconColor = TextSecondaryDark,
                                    unselectedTextColor = TextSecondaryDark
                                )
                            )
                        }
                    }
                }

                // Main Content Screen
                Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                    // Secondary quick navigation chips at the top of content for fast switching between all 8 sections
                    Column(modifier = Modifier.fillMaxSize()) {
                        ScrollableTabRow(
                            selectedTabIndex = primaryNavItems.indexOfFirst { it.screen == currentScreen }.coerceAtLeast(0),
                            containerColor = ObsidianBlack,
                            contentColor = ChampagneGold,
                            edgePadding = 16.dp,
                            divider = {},
                            modifier = Modifier.fillMaxWidth().height(48.dp)
                        ) {
                            primaryNavItems.forEachIndexed { index, navItem ->
                                val selected = currentScreen == navItem.screen
                                Tab(
                                    selected = selected,
                                    onClick = { viewModel.navigateTo(navItem.screen) },
                                    text = {
                                        Text(
                                            text = navItem.label,
                                            style = MaterialTheme.typography.labelMedium,
                                            color = if (selected) ChampagneGold else TextSecondaryDark,
                                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    }
                                )
                            }
                        }

                        // Screen content dispatcher
                        when (currentScreen) {
                            Screen.Dashboard -> DashboardScreen(
                                customers = customers,
                                projects = projects,
                                quotations = quotations,
                                onNavigate = { viewModel.navigateTo(it) }
                            )
                            Screen.Customers -> CustomerManagementScreen(
                                customers = customers,
                                onAddCustomer = { name, email, phone, loc, req, budget, notes ->
                                    viewModel.createCustomer(name, email, phone, loc, req, budget, notes)
                                },
                                onDeleteCustomer = { viewModel.deleteCustomer(it) }
                            )
                            Screen.Projects -> ProjectManagementScreen(
                                projects = projects,
                                customers = customers,
                                teamMembers = teamMembers,
                                projectMaterials = projectMaterials,
                                onCreateProject = { title, cId, cName, cat, budget, start, deadline, lead, loc, scope ->
                                    viewModel.createProject(title, cId, cName, cat, budget, start, deadline, lead, loc, scope)
                                },
                                onUpdateProgress = { project, progress, status ->
                                    viewModel.updateProjectProgress(project, progress, status)
                                },
                                onDeleteProject = { viewModel.deleteProject(it) },
                                onOpen3DStudio = { viewModel.navigateTo(Screen.Visualizer3D) },
                                onOpenMaterialLibrary = { viewModel.navigateTo(Screen.MaterialsVendors) },
                                onGenerateQuotationFromMaterials = { pId, pTitle, cName, cEmail ->
                                    viewModel.generateQuotationFromProjectMaterials(pId, pTitle, cName, cEmail)
                                }
                            )
                            Screen.MaterialsVendors -> MaterialVendorLibraryScreen(
                                materials = materials,
                                vendors = vendors,
                                projects = projects,
                                onAddVendor = { name, cat, contact, phone, email, addr, rating, leadTime, notes ->
                                    viewModel.addVendor(name, cat, contact, phone, email, addr, rating, leadTime, notes)
                                },
                                onDeleteVendor = { viewModel.deleteVendor(it) },
                                onAddMaterial = { name, cat, subCat, unit, cost, vId, vName, spec, rating ->
                                    viewModel.addMaterial(name, cat, subCat, unit, cost, vId, vName, spec, rating)
                                },
                                onDeleteMaterial = { viewModel.deleteMaterial(it) },
                                onAssignMaterialToProject = { pId, pTitle, mat, qty ->
                                    viewModel.assignMaterialToProject(pId, pTitle, mat, qty)
                                }
                            )
                            Screen.Visualizer3D -> RoomVisualizer3DScreen(
                                projects = projects,
                                onAttachDesignToProject = { pId, pTitle, summary ->
                                    viewModel.showMessage("3D Concept Design attached to project: $pTitle")
                                }
                            )
                            Screen.Services -> InteriorDesignServicesScreen(
                                onBookConsultation = { serviceName ->
                                    viewModel.showMessage("Consultation inquiry booked for $serviceName")
                                },
                                onNavigate = { viewModel.navigateTo(it) }
                            )
                            Screen.Portfolio -> PortfolioGalleryScreen(
                                portfolioItems = portfolioItems,
                                onAddPortfolioItem = { title, cat, client, loc, year, sqft, desc ->
                                    viewModel.addPortfolioItem(title, cat, client, loc, year, sqft, desc)
                                }
                            )
                            Screen.QuotationBilling -> QuotationBillingScreen(
                                quotations = quotations,
                                projects = projects,
                                onCreateQuotation = { projId, projTitle, cName, cEmail, subtotal, itemsJson ->
                                    viewModel.createQuotation(projId, projTitle, cName, cEmail, subtotal, itemsJson)
                                },
                                onRecordPayment = { quotation, amount ->
                                    viewModel.recordPayment(quotation, amount)
                                }
                            )
                            Screen.CustomerReviews -> CustomerReviewsScreen(
                                reviews = reviews,
                                onAddReview = { name, proj, loc, rating, comment ->
                                    viewModel.addReview(name, proj, loc, rating, comment)
                                }
                            )
                            Screen.Settings -> BusinessSettingsScreen(
                                currentRole = userRole,
                                teamMembers = teamMembers,
                                onSwitchRole = { viewModel.switchRole(it) },
                                onAddTeamMember = { name, role, email, phone ->
                                    viewModel.addTeamMember(name, role, email, phone)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
