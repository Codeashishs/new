package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.models.AppointmentEntity
import com.example.data.models.GownEntity
import com.example.data.models.NotificationEntity
import com.example.data.models.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    fun getUserById(userId: String): Flow<UserEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)
}

@Dao
interface GownDao {
    @Query("SELECT * FROM gowns ORDER BY id DESC")
    fun getAllGowns(): Flow<List<GownEntity>>

    @Query("SELECT * FROM gowns WHERE id = :id LIMIT 1")
    fun getGownById(id: Int): Flow<GownEntity?>

    @Query("SELECT * FROM gowns WHERE isFeatured = 1")
    fun getFeaturedGowns(): Flow<List<GownEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGowns(gowns: List<GownEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGown(gown: GownEntity)

    @Update
    suspend fun updateGown(gown: GownEntity)

    @Query("DELETE FROM gowns WHERE id = :id")
    suspend fun deleteGown(id: Int)
}

@Dao
interface AppointmentDao {
    @Query("SELECT * FROM appointments ORDER BY id DESC")
    fun getAllAppointments(): Flow<List<AppointmentEntity>>

    @Query("SELECT * FROM appointments WHERE userId = :userId ORDER BY id DESC")
    fun getAppointmentsForUser(userId: String): Flow<List<AppointmentEntity>>

    @Query("SELECT * FROM appointments WHERE appointmentDate = :date ORDER BY timeSlot ASC")
    fun getAppointmentsByDate(date: String): Flow<List<AppointmentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppointment(appointment: AppointmentEntity): Long

    @Update
    suspend fun updateAppointment(appointment: AppointmentEntity)

    @Query("UPDATE appointments SET bookingStatus = :status WHERE id = :id")
    suspend fun updateStatus(id: Long, status: String)

    @Query("DELETE FROM appointments WHERE id = :id")
    suspend fun deleteAppointment(id: Long)
}

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notifications WHERE userId = :userId ORDER BY timestamp DESC")
    fun getNotificationsForUser(userId: String): Flow<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity)

    @Query("UPDATE notifications SET isRead = 1 WHERE userId = :userId")
    suspend fun markAllAsRead(userId: String)
}
