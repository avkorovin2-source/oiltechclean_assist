package ru.oiltechclean.assistant.data

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class OilAcceptanceChecklistTest {

    @Test
    fun checklistHasExpectedSize() {
        assertEquals(15, OilAcceptanceChecklist.totalItems)
        assertEquals(9, OilAcceptanceChecklist.criticalItems)
    }

    @Test
    fun emptySelectionIsNotApproved() {
        assertFalse(OilAcceptanceChecklist.isApproved(emptySet()))
    }

    @Test
    fun missingOneCriticalItemBlocksApproval() {
        val allButOne = OilAcceptanceChecklist.criticalIds - "lab_acid"
        assertFalse(OilAcceptanceChecklist.isApproved(allButOne))
    }

    @Test
    fun allCriticalItemsApproveWithoutOptionalOnes() {
        assertTrue(OilAcceptanceChecklist.isApproved(OilAcceptanceChecklist.criticalIds))
    }

    @Test
    fun checklistHasThreeGroupsWithUniqueIds() {
        val allIds = OilAcceptanceChecklist.groups.flatMap { group -> group.items.map { it.id } }
        assertEquals(allIds.size, allIds.toSet().size, "Идентификаторы пунктов должны быть уникальны")
        assertTrue(OilAcceptanceChecklist.groups.all { it.items.isNotEmpty() })
    }

    // MARK: Swift-совместимый API (используется из iOS-приложения)

    @Test
    fun listBasedApprovalMatchesSetBasedApproval() {
        val allCritical = OilAcceptanceChecklist.criticalIds.toList()
        assertTrue(OilAcceptanceChecklist.isApprovedList(allCritical))
        assertFalse(OilAcceptanceChecklist.isApprovedList(allCritical - "lab_acid"))
    }

    @Test
    fun missingCriticalCountReportsUncheckedItems() {
        assertEquals(OilAcceptanceChecklist.criticalItems, OilAcceptanceChecklist.missingCriticalCount(emptyList()))

        val allCritical = OilAcceptanceChecklist.criticalIds.toList()
        assertEquals(0, OilAcceptanceChecklist.missingCriticalCount(allCritical))

        val withoutOne = allCritical - "lab_acid"
        assertEquals(1, OilAcceptanceChecklist.missingCriticalCount(withoutOne))
    }
}
