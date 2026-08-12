package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.AppointmentDao
import com.example.data.dao.GownDao
import com.example.data.dao.NotificationDao
import com.example.data.dao.UserDao
import com.example.data.models.AppointmentEntity
import com.example.data.models.GownEntity
import com.example.data.models.NotificationEntity
import com.example.data.models.UserEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [UserEntity::class, GownEntity::class, AppointmentEntity::class, NotificationEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun gownDao(): GownDao
    abstract fun appointmentDao(): AppointmentDao
    abstract fun notificationDao(): NotificationDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "gown_scout_database"
                )
                .addCallback(DatabaseCallback(context))
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(private val context: Context) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        seedDatabase(database)
                    }
                }
            }
        }

        private suspend fun seedDatabase(db: AppDatabase) {
            // Seed default users
            db.userDao().insertUser(
                UserEntity(
                    id = "user_client_1",
                    email = "sophia.bride@example.com",
                    name = "Sophia Montgomery",
                    role = "CLIENT",
                    phone = "+1 (555) 234-8901"
                )
            )
            db.userDao().insertUser(
                UserEntity(
                    id = "user_boutique_1",
                    email = "elena.couture@gownscout.com",
                    name = "Elena Vance (Boutique Director)",
                    role = "BOUTIQUE_OWNER",
                    phone = "+1 (555) 987-6543"
                )
            )

            // Seed initial gowns catalog
            val initialGowns = listOf(
                GownEntity(
                    id = 1,
                    title = "Aurelia Royal Silhouette",
                    designer = "Vera Wang Haute",
                    silhouette = "Ballgown",
                    fabric = "Mikado Silk & Italian Tulle",
                    price = 4850.0,
                    rating = 4.9f,
                    reviewCount = 38,
                    imageDrawableName = "img_gown_ballgown_1786563003304",
                    description = "A dramatic cathedral-length ballgown with off-the-shoulder draped silk straps, sculpted corset bodice, and delicate horsehair hemline for graceful volume.",
                    neckline = "Sweetheart Off-Shoulder",
                    sampleSizes = "4, 6, 8, 10, 12",
                    isFeatured = true
                ),
                GownEntity(
                    id = 2,
                    title = "Celeste Mermaid Couture",
                    designer = "Galia Lahav",
                    silhouette = "Mermaid",
                    fabric = "French Chantilly Lace & Glass Beads",
                    price = 5200.0,
                    rating = 5.0f,
                    reviewCount = 42,
                    imageDrawableName = "img_gown_mermaid_1786562991705",
                    description = "A breathtaking figure-sculpting gown featuring hand-embroidered 3D floral lace appliqués, low illusion back, and cascading sheer train.",
                    neckline = "Deep V-Plunge",
                    sampleSizes = "2, 4, 6, 8, 10",
                    isFeatured = true
                ),
                GownEntity(
                    id = 3,
                    title = "Seraphina Bohemian Grace",
                    designer = "Rue de Seine",
                    silhouette = "Boho Tulle",
                    fabric = "Botanical Guipure & Silk Chiffon",
                    price = 3600.0,
                    rating = 4.8f,
                    reviewCount = 29,
                    imageDrawableName = "img_gown_boho_1786563038495",
                    description = "An ethereal gown crafted with detachable sheer bishop sleeves, romantic embroidered vines, and lightweight layered tulle skirt for effortless movement.",
                    neckline = "Illusion High Neck",
                    sampleSizes = "4, 6, 8, 12, 14",
                    isFeatured = true
                ),
                GownEntity(
                    id = 4,
                    title = "Genevieve Silk Sheath",
                    designer = "Monique Lhuillier",
                    silhouette = "Sheath",
                    fabric = "Pure Silk Crepe de Chine",
                    price = 3950.0,
                    rating = 4.7f,
                    reviewCount = 19,
                    imageDrawableName = "img_hero_gown_1786562980554",
                    description = "Minimalist luxury at its finest. Sleek liquid silk crepe sheath gown with cowl neckline, pearl button detail down the back, and subtle side slit.",
                    neckline = "Cowl Neckline",
                    sampleSizes = "4, 6, 8, 10",
                    isFeatured = false
                ),
                GownEntity(
                    id = 5,
                    title = "Valentina Regal A-Line",
                    designer = "Elie Saab Bridal",
                    silhouette = "A-Line",
                    fabric = "Embroidered Organza & Sparkle Net",
                    price = 6100.0,
                    rating = 4.9f,
                    reviewCount = 51,
                    imageDrawableName = "img_gown_ballgown_1786563003304",
                    description = "Opulent A-Line gown adorned with subtle crystal hand-beading, scoop back, and royal chapel train that glows under bridal suite chandelier lighting.",
                    neckline = "Square Neck",
                    sampleSizes = "6, 8, 10, 12, 16",
                    isFeatured = true
                )
            )
            db.gownDao().insertGowns(initialGowns)

            // Seed initial appointments for demonstration & analytics
            val sampleAppointments = listOf(
                AppointmentEntity(
                    id = 101L,
                    userId = "user_client_1",
                    userName = "Sophia Montgomery",
                    userEmail = "sophia.bride@example.com",
                    gownId = 2,
                    gownTitle = "Celeste Mermaid Couture",
                    boutiqueLocation = "Gown Scout Fifth Ave - NYC",
                    appointmentType = "VIP Fitting Suite",
                    appointmentDate = "2026-08-18",
                    timeSlot = "10:00 AM - 11:30 AM",
                    guestsCount = 3,
                    specialRequests = "Chilled Rosé Champagne requested for mother and bridesmaids.",
                    totalDepositPaid = 150.0,
                    paymentStatus = "PAID",
                    bookingStatus = "CONFIRMED",
                    isCalendarSynced = true,
                    emailNotificationSent = true
                ),
                AppointmentEntity(
                    id = 102L,
                    userId = "user_client_1",
                    userName = "Sophia Montgomery",
                    userEmail = "sophia.bride@example.com",
                    gownId = 1,
                    gownTitle = "Aurelia Royal Silhouette",
                    boutiqueLocation = "Gown Scout Beverly Hills",
                    appointmentType = "Second Fitting & Alterations",
                    appointmentDate = "2026-08-25",
                    timeSlot = "02:00 PM - 03:30 PM",
                    guestsCount = 2,
                    specialRequests = "Hemming measurement check with heirloom bridal heels.",
                    totalDepositPaid = 150.0,
                    paymentStatus = "PAID",
                    bookingStatus = "CONFIRMED",
                    isCalendarSynced = true,
                    emailNotificationSent = true
                )
            )
            for (app in sampleAppointments) {
                db.appointmentDao().insertAppointment(app)
            }

            // Seed notifications
            db.notificationDao().insertNotification(
                NotificationEntity(
                    userId = "user_client_1",
                    title = "Fitting Confirmed & Calendar Synced",
                    message = "Your VIP Fitting Suite appointment at Fifth Ave NYC for Celeste Mermaid Couture on Aug 18 at 10:00 AM is confirmed. Calendar event and confirmation email sent.",
                    type = "BOOKING_CONFIRMATION"
                )
            )
        }
    }
}
