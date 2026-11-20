package ru.oiltechclean.assistant.ui.screens

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.oiltechclean.assistant.data.AcceptanceCheckEntity
import ru.oiltechclean.assistant.data.AppDatabase
import ru.oiltechclean.assistant.data.OilAcceptanceChecklist

class AcceptanceChecklistViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getInstance(application).acceptanceDao()

    private val _checkedIds = MutableStateFlow<Set<String>>(emptySet())
    val checkedIds: StateFlow<Set<String>> = _checkedIds.asStateFlow()

    private val _objectName = MutableStateFlow("")
    val objectName: StateFlow<String> = _objectName.asStateFlow()

    val history: StateFlow<List<AcceptanceCheckEntity>> = dao.getAllChecks()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun setObjectName(name: String) {
        _objectName.value = name
    }

    fun toggleItem(itemId: String) {
        _checkedIds.update { current ->
            if (itemId in current) current - itemId else current + itemId
        }
    }

    fun resetChecklist() {
        _checkedIds.value = emptySet()
    }

    /** Сохраняет снимок отметок в историю. Пустой объект или пустой набор отметок не сохраняются. */
    fun saveCheck() {
        val name = _objectName.value.trim()
        val ids = _checkedIds.value
        if (name.isEmpty() || ids.isEmpty()) return

        viewModelScope.launch {
            dao.insertCheck(
                AcceptanceCheckEntity(
                    objectName = name,
                    checkedAt = System.currentTimeMillis(),
                    checkedItemIds = ids.sorted().joinToString(","),
                    approved = OilAcceptanceChecklist.isApproved(ids)
                )
            )
        }
    }

    fun deleteCheck(check: AcceptanceCheckEntity) {
        viewModelScope.launch { dao.deleteCheck(check) }
    }
}
