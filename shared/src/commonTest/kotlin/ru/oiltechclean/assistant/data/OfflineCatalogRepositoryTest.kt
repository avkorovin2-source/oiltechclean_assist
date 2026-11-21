package ru.oiltechclean.assistant.data

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class OfflineCatalogRepositoryTest {

    @Test
    fun troubleshootingRowsMatchRawPairs() {
        for (unit in OfflineCatalogRepository.equipmentList) {
            val rows = OfflineCatalogRepository.troubleshootingRows(unit)
            assertEquals(unit.troubleshooting.size, rows.size)

            unit.troubleshooting.forEachIndexed { index, pair ->
                assertEquals(pair.first, rows[index].problem)
                assertEquals(pair.second, rows[index].solution)
            }
        }
    }

    @Test
    fun unitSummaryIsNotEmptyForEveryUnit() {
        assertTrue(OfflineCatalogRepository.equipmentList.isNotEmpty())
        for (unit in OfflineCatalogRepository.equipmentList) {
            assertTrue(OfflineCatalogRepository.unitSummary(unit).isNotBlank())
        }
    }
}
