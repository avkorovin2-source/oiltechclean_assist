package ru.oiltechclean.assistant.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AcceptanceChecklistTest {

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
    fun checkedIdSetIgnoresBlankEntries() {
        val entity = AcceptanceCheckEntity(
            objectName = "ПС 110 кВ",
            checkedAt = 0L,
            checkedItemIds = "doc_passport,,lab_acid,",
            approved = false
        )
        assertEquals(setOf("doc_passport", "lab_acid"), entity.checkedIdSet())
    }

    @Test
    fun checkedIdSetOfEmptyStringIsEmpty() {
        val entity = AcceptanceCheckEntity(
            objectName = "ПС 110 кВ",
            checkedAt = 0L,
            checkedItemIds = "",
            approved = false
        )
        assertTrue(entity.checkedIdSet().isEmpty())
    }
}
