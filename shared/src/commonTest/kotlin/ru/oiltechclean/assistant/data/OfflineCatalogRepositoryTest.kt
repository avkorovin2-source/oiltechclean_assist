package ru.oiltechclean.assistant.data

import ru.oiltechclean.assistant.model.GOST_CATEGORY_FRESH_OIL
import ru.oiltechclean.assistant.model.GOST_CATEGORY_OPERATION
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

    @Test
    fun gostStandardsSplitByOilCategory() {
        val operation110 = OfflineCatalogRepository.gostStandardsInOperation.first {
            it.voltageClass == "110 – 150 кВ"
        }
        assertEquals(GOST_CATEGORY_OPERATION, operation110.category)
        assertEquals(15.0, operation110.moisturePpmMax)
        assertEquals(10.0, operation110.tanDelta90CMax)

        val fresh110 = OfflineCatalogRepository.gostStandardsForFreshOil.first {
            it.voltageClass == "Свежее масло, 110 кВ и выше"
        }
        assertEquals(GOST_CATEGORY_FRESH_OIL, fresh110.category)
        assertEquals(10.0, fresh110.moisturePpmMax)
        assertEquals(2.5, fresh110.tanDelta90CMax)

        assertEquals(5, OfflineCatalogRepository.gostStandardsInOperation.size)
        assertEquals(2, OfflineCatalogRepository.gostStandardsForFreshOil.size)
        assertEquals(7, OfflineCatalogRepository.gostStandards.size)
    }
}
