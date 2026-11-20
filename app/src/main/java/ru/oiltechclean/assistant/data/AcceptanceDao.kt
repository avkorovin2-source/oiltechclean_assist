package ru.oiltechclean.assistant.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AcceptanceDao {

    @Insert
    suspend fun insertCheck(check: AcceptanceCheckEntity)

    @Query("SELECT * FROM acceptance_checks ORDER BY checkedAt DESC")
    fun getAllChecks(): Flow<List<AcceptanceCheckEntity>>

    @Delete
    suspend fun deleteCheck(check: AcceptanceCheckEntity)
}
