package com.example.data.repository

import com.example.data.dao.AppointmentDao
import com.example.data.dao.GownDao
import com.example.data.dao.NotificationDao
import com.example.data.dao.UserDao
import com.example.data.models.AppointmentEntity
import com.example.data.models.GownEntity
import com.example.data.models.NotificationEntity
import com.example.data.models.UserEntity
import kotlinx.coroutines.flow.Flow

class GownScoutRepository(
    private val userDao: UserDao,
    private val gownDao: GownDao,
    private val appointmentDao: AppointmentDao,
    private val notificationDao: NotificationDao
) {
    fun getUser(userId: String): Flow<UserEntity?> = userDao.getUserById(userId)
    suspend fun saveUser(user: UserEntity) = userDao.insertUser(user)

    val allGowns: Flow<List<GownEntity>> = gownDao.getAllGowns()
    val featuredGowns: Flow<List<GownEntity>> = gownDao.getFeaturedGowns()
    fun getGownById(id: Int): Flow<GownEntity?> = gownDao.getGownById(id)
    suspend fun insertGown(gown: GownEntity) = gownDao.insertGown(gown)
    suspend fun updateGown(gown: GownEntity) = gownDao.updateGown(gown)
    suspend fun deleteGown(id: Int) = gownDao.deleteGown(id)

    val allAppointments: Flow<List<AppointmentEntity>> = appointmentDao.getAllAppointments()
    fun getUserAppointments(userId: String): Flow<List<AppointmentEntity>> = appointmentDao.getAppointmentsForUser(userId)
    fun getAppointmentsForDate(date: String): Flow<List<AppointmentEntity>> = appointmentDao.getAppointmentsByDate(date)

    suspend fun bookAppointment(appointment: AppointmentEntity): Long {
        val newId = appointmentDao.insertAppointment(appointment)
        
        // Auto generate confirmation notification
        notificationDao.insertNotification(
            NotificationEntity(
                userId = appointment.userId,
                title = "Booking Confirmed & Calendar Synced",
                message = "Confirmed for ${appointment.gownTitle} on ${appointment.appointmentDate} at ${appointment.timeSlot}. Confirmation sent to ${appointment.userEmail}.",
                type = "BOOKING_CONFIRMATION"
            )
        )
        return newId
    }

    suspend fun updateAppointmentStatus(id: Long, status: String) {
        appointmentDao.updateStatus(id, status)
    }

    suspend fun cancelAppointment(id: Long) {
        appointmentDao.updateStatus(id, "CANCELLED")
    }

    fun getUserNotifications(userId: String): Flow<List<NotificationEntity>> = notificationDao.getNotificationsForUser(userId)
    suspend fun markNotificationsRead(userId: String) = notificationDao.markAllAsRead(userId)
}
