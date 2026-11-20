package ru.oiltechclean.assistant.ui.components

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.click
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import ru.oiltechclean.assistant.ui.theme.OilTechTheme

@RunWith(AndroidJUnit4::class)
class DropdownFieldTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun tapOnFieldOpensMenuAndSelectionIsApplied() {
        composeRule.setContent {
            var selected by remember { mutableStateOf("") }
            OilTechTheme {
                DropdownField(
                    label = "Тип трансформатора",
                    value = selected.ifBlank { "Выбрать из справочника" },
                    options = listOf("ТМ-100", "ТМ-250"),
                    onSelect = { selected = it }
                )
            }
        }

        // До открытия списка пунктов в дереве нет.
        composeRule.onNodeWithText("ТМ-250").assertDoesNotExist()

        // Тап по полю должен развернуть список.
        composeRule.onNodeWithText("Выбрать из справочника").performTouchInput { click() }
        composeRule.onNodeWithText("ТМ-250").assertIsDisplayed()

        // Выбор пункта применяется и закрывает меню.
        composeRule.onNodeWithText("ТМ-250").performClick()
        composeRule.onNodeWithText("ТМ-250").assertIsDisplayed()
        composeRule.onNodeWithText("Выбрать из справочника").assertDoesNotExist()
    }
}
