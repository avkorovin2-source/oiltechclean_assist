package ru.oiltechclean.assistant.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val LightColors = lightColorScheme(
    primary = BrandIndigo,
    onPrimary = Color.White,
    primaryContainer = MistDeep,
    onPrimaryContainer = BrandDeep,
    secondary = BrandViolet,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFF1ECFF),
    onSecondaryContainer = BrandViolet,
    tertiary = BrandSky,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFE8F3FF),
    onTertiaryContainer = Color(0xFF0B5FA5),
    background = Mist,
    onBackground = Ink,
    surface = Surface,
    onSurface = Ink,
    surfaceVariant = MistDeep,
    onSurfaceVariant = Slate,
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = Color(0xFFFAFBFF),
    surfaceContainer = Color(0xFFF7F9FF),
    surfaceContainerHigh = Color(0xFFF1F4FE),
    surfaceContainerHighest = Color(0xFFEAEFFB),
    outline = Line,
    outlineVariant = Color(0xFFEDF1FA),
    error = Danger,
    onError = Color.White,
    errorContainer = DangerSoft,
    onErrorContainer = Color(0xFF8C1D20)
)

val OilTechShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(30.dp)
)

/** Основной фирменный градиент сайта: #667eea → #764ba2 */
val BrandGradient = Brush.linearGradient(listOf(BrandIndigo, BrandViolet))

/** Дополнительный градиент для акцентов: #7f5af0 → #4ea5ff */
val AccentGradient = Brush.linearGradient(listOf(BrandPurple, BrandSky))

/** Мягкая подложка под карточками и хедерами */
val SoftGradient = Brush.linearGradient(listOf(Color(0xFFFDFEFF), Color(0xFFEEF3FF)))

@Composable
fun OilTechTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = OilTechTypography,
        shapes = OilTechShapes,
        content = content
    )
}
