package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.models.AppointmentEntity
import com.example.data.models.GownEntity
import com.example.data.models.NotificationEntity
import com.example.data.models.UserEntity
import com.example.data.repository.GownScoutRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class Screen(val route: String, val title: String) {
    object Catalog : Screen("catalog", "Discover Gowns")
    object Detail : Screen("detail", "Gown Details")
    object Booking : Screen("booking", "Book Fitting")
    object Appointments : Screen("appointments", "My Fittings")
    object Dashboard : Screen("dashboard", "Boutique Suite")
    object Analytics : Screen("analytics", "Growth Suite")
    object Notifications : Screen("notifications", "Updates")
    object Auth : Screen("auth", "Sign In")
    object Profile : Screen("profile", "Account")
}

class GownScoutViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: GownScoutRepository

    init {
        val db = AppDatabase.getDatabase(application)
        repository = GownScoutRepository(
            db.userDao(),
            db.gownDao(),
            db.appointmentDao(),
            db.notificationDao()
        )
    }

    // Auth & User State
    val activeUserId = MutableStateFlow("user_client_1")
    val currentUser: StateFlow<UserEntity?> = activeUserId.flatMapLatest { id ->
        repository.getUser(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Navigation state
    val currentScreen = MutableStateFlow<Screen>(Screen.Catalog)
    val selectedGown = MutableStateFlow<GownEntity?>(null)

    // Search and Filter State
    val searchQuery = MutableStateFlow("")
    val selectedSilhouette = MutableStateFlow<String?>(null)
    val selectedDesigner = MutableStateFlow<String?>(null)

    val allGowns: StateFlow<List<GownEntity>> = repository.allGowns
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val featuredGowns: StateFlow<List<GownEntity>> = repository.featuredGowns
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered gowns
    val filteredGowns: StateFlow<List<GownEntity>> = combine(
        allGowns,
        searchQuery,
        selectedSilhouette,
        selectedDesigner
    ) { gownsList, query, silhouette, designer ->
        gownsList.filter { gown ->
            val matchesQuery = query.isEmpty() ||
                    gown.title.contains(query, ignoreCase = true) ||
                    gown.designer.contains(query, ignoreCase = true) ||
                    gown.fabric.contains(query, ignoreCase = true)

            val matchesSilhouette = silhouette == null || gown.silhouette.equals(silhouette, ignoreCase = true)
            val matchesDesigner = designer == null || gown.designer.equals(designer, ignoreCase = true)

            matchesQuery && matchesSilhouette && matchesDesigner
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Appointments Flow
    val userAppointments: StateFlow<List<AppointmentEntity>> = activeUserId.flatMapLatest { id ->
        repository.getUserAppointments(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allAppointments: StateFlow<List<AppointmentEntity>> = repository.allAppointments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Notifications
    val notifications: StateFlow<List<NotificationEntity>> = activeUserId.flatMapLatest { id ->
        repository.getUserNotifications(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // UI Toast or Snack notification state
    val lastActionMessage = MutableStateFlow<String?>(null)

    fun navigateTo(screen: Screen) {
        currentScreen.value = screen
    }

    fun selectGown(gown: GownEntity) {
        selectedGown.value = gown
        currentScreen.value = Screen.Detail
    }

    fun switchUserRole(userId: String) {
        activeUserId.value = userId
        val newRole = if (userId == "user_boutique_1") "Boutique Owner Suite" else "Bride-to-be Client"
        lastActionMessage.value = "Switched persona to: $newRole"
    }

    fun bookAppointment(
        gown: GownEntity,
        boutiqueLocation: String,
        appointmentType: String,
        dateStr: String,
        timeSlot: String,
        guestsCount: Int,
        specialRequests: String,
        depositAmount: Double
    ) {
        viewModelScope.launch {
            val user = currentUser.value
            val userId = user?.id ?: "user_client_1"
            val userName = user?.name ?: "Sophia Montgomery"
            val userEmail = user?.email ?: "sophia.bride@example.com"

            val appointment = AppointmentEntity(
                userId = userId,
                userName = userName,
                userEmail = userEmail,
                gownId = gown.id,
                gownTitle = gown.title,
                boutiqueLocation = boutiqueLocation,
                appointmentType = appointmentType,
                appointmentDate = dateStr,
                timeSlot = timeSlot,
                guestsCount = guestsCount,
                specialRequests = specialRequests,
                totalDepositPaid = depositAmount,
                paymentStatus = "PAID",
                bookingStatus = "CONFIRMED",
                isCalendarSynced = true,
                emailNotificationSent = true
            )

            repository.bookAppointment(appointment)
            lastActionMessage.value = "Booking confirmed! Calendar event synced and confirmation email sent to $userEmail."
            currentScreen.value = Screen.Appointments
        }
    }

    fun updateAppointmentStatus(appointmentId: Long, status: String) {
        viewModelScope.launch {
            repository.updateAppointmentStatus(appointmentId, status)
            lastActionMessage.value = "Appointment status updated to: $status"
        }
    }

    fun cancelAppointment(appointmentId: Long) {
        viewModelScope.launch {
            repository.cancelAppointment(appointmentId)
            lastActionMessage.value = "Appointment cancelled."
        }
    }

    fun addNewGown(
        title: String,
        designer: String,
        silhouette: String,
        fabric: String,
        price: Double,
        neckline: String,
        description: String
    ) {
        viewModelScope.launch {
            val newGown = GownEntity(
                title = title,
                designer = designer,
                silhouette = silhouette,
                fabric = fabric,
                price = price,
                rating = 5.0f,
                reviewCount = 1,
                imageDrawableName = "img_gown_ballgown_1786563003304",
                description = description,
                neckline = neckline,
                sampleSizes = "2, 4, 6, 8, 10, 12, 14",
                isFeatured = true
            )
            repository.insertGown(newGown)
            lastActionMessage.value = "Added $title to boutique inventory!"
        }
    }

    fun markNotificationsRead() {
        viewModelScope.launch {
            repository.markNotificationsRead(activeUserId.value)
        }
    }

    fun clearLastActionMessage() {
        lastActionMessage.value = null
    }

    fun logout() {
        activeUserId.value = "user_client_1"
        currentScreen.value = Screen.Auth
        lastActionMessage.value = "Signed out successfully."
    }
}
