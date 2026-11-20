package ru.oiltechclean.assistant.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface JournalDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: JournalRecordEntity)

    @Query("SELECT * FROM journal_records ORDER BY id DESC")
    fun getAllRecords(): Flow<List<JournalRecordEntity>>

    /**
     * Поиск по объекту, трансформатору и установке с фильтром по классу напряжения.
     * Пустая строка в [voltageClass] означает «любой класс».
     */
    @Query(
        """
        SELECT * FROM journal_records
        WHERE (:query = '' OR substationName LIKE '%' || :query || '%'
               OR transformerName LIKE '%' || :query || '%'
               OR unitModel LIKE '%' || :query || '%')
          AND (:voltageClass = '' OR voltageClass = :voltageClass)
        ORDER BY id DESC
        """
    )
    fun searchRecords(query: String, voltageClass: String): Flow<List<JournalRecordEntity>>

    @Query("SELECT DISTINCT voltageClass FROM journal_records WHERE voltageClass IS NOT NULL ORDER BY voltageClass")
    fun getUsedVoltageClasses(): Flow<List<String>>

    @Query("SELECT * FROM journal_records WHERE id = :id")
    fun getRecordById(id: Long): Flow<JournalRecordEntity?>

    @Delete
    suspend fun deleteRecord(record: JournalRecordEntity)

    @Query("DELETE FROM journal_records")
    suspend fun deleteAllRecords()
}
