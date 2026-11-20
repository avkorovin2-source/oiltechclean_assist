package ru.oiltechclean.assistant.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [JournalRecordEntity::class, AcceptanceCheckEntity::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun journalDao(): JournalDao
    abstract fun acceptanceDao(): AcceptanceDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /** Добавление полей полного протокола замера. */
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE journal_records ADD COLUMN voltageClass TEXT")
                db.execSQL("ALTER TABLE journal_records ADD COLUMN oilTemperatureC REAL")
                db.execSQL("ALTER TABLE journal_records ADD COLUMN gasContentAfter REAL")
                db.execSQL("ALTER TABLE journal_records ADD COLUMN flashPointAfter REAL")
                db.execSQL("ALTER TABLE journal_records ADD COLUMN conclusion TEXT")
            }
        }

        /** Таблица истории проверок приёмки масла. */
        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS acceptance_checks (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        objectName TEXT NOT NULL,
                        checkedAt INTEGER NOT NULL,
                        checkedItemIds TEXT NOT NULL,
                        approved INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "oiltech_assistant_db"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
