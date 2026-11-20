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
}
