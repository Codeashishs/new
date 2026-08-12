package com.example.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val email: String,
    val name: String,
    val role: String, // "CLIENT" or "BOUTIQUE_OWNER"
    val phone: String = "+1 (555) 019-2834",
    val avatarUrl: String = ""
)

@Entity(tableName = "gowns")
data class GownEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val designer: String,
    val silhouette: String, // "Mermaid", "Ballgown", "A-Line", "Boho Tulle", "Sheath"
    val fabric: String,
    val price: Double,
    val rating: Float,
    val reviewCount: Int,
    val imageDrawableName: String,
    val description: String,
    val neckline: String,
    val sampleSizes: String, // "4, 6, 8, 10, 12, 14"
    val isFeatured: Boolean = false,
    val inStock: Boolean = true
)

@Entity(tableName = "appointments")
data class AppointmentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String,
    val userName: String,
    val userEmail: String,
    val gownId: Int,
    val gownTitle: String,
    val boutiqueLocation: String, // "Gown Scout Fifth Ave - NYC", "Gown Scout Beverly Hills", "Gown Scout Mayfair - London"
    val appointmentType: String, // "VIP Fitting Suite", "Bridal Consultation", "Second Fitting & Alterations", "Bridesmaid Party Suite"
    val appointmentDate: String, // e.g. "2026-08-20"
    val timeSlot: String, // e.g. "11:00 AM - 12:30 PM"
    val guestsCount: Int,
    val specialRequests: String,
    val totalDepositPaid: Double,
    val paymentStatus: String, // "PAID", "REFUNDED"
    val bookingStatus: String, // "CONFIRMED", "IN_FITTING", "COMPLETED", "CANCELLED"
    val isCalendarSynced: Boolean = true,
    val emailNotificationSent: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String,
    val title: String,
    val message: String,
    val type: String, // "BOOKING_CONFIRMATION", "CALENDAR_SYNC", "PAYMENT_RECEIPT", "FITTING_REMINDER", "ANALYTICS_ALERT"
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)
