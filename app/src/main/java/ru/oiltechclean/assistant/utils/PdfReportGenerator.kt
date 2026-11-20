package ru.oiltechclean.assistant.utils

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import ru.oiltechclean.assistant.data.JournalRecordEntity
import ru.oiltechclean.assistant.data.OfflineCatalogRepository
import ru.oiltechclean.assistant.domain.GostComplianceEvaluator
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Генерация протокола обработки трансформаторного масла в PDF.
 * Формат A4 (595 × 842 pt), поля 40 pt.
 */
object PdfReportGenerator {

    private const val FILENAME_PREFIX = "oiltech_report_"
    private const val PAGE_WIDTH = 595
    private const val PAGE_HEIGHT = 842
    private const val PADDING = 40f
    private const val CONTENT_WIDTH = PAGE_WIDTH - PADDING * 2

    private val COLOR_INK = Color.parseColor("#0F172A")
    private val COLOR_BRAND = Color.parseColor("#667EEA")
    private val COLOR_SLATE = Color.parseColor("#5C6570")
    private val COLOR_LINE = Color.parseColor("#E3E8F5")
    private val COLOR_SUCCESS = Color.parseColor("#28A745")
    private val COLOR_DANGER = Color.parseColor("#E5484D")

    fun generateReport(context: Context, record: JournalRecordEntity): File {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val outputDir = File(context.cacheDir, "reports").apply { if (!exists()) mkdirs() }
        val outputFile = File(outputDir, "$FILENAME_PREFIX$timestamp.pdf")

        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, 1).create()
        val page = document.startPage(pageInfo)
        val canvas = page.canvas
        canvas.drawColor(Color.WHITE)

        val titlePaint = textPaint(18f, COLOR_INK, bold = true)
        val brandPaint = textPaint(11f, COLOR_BRAND, bold = true)
        val sectionPaint = textPaint(12f, COLOR_INK, bold = true)
        val bodyPaint = textPaint(10f, COLOR_INK)
        val mutedPaint = textPaint(9f, COLOR_SLATE)
        val cellPaint = textPaint(10f, COLOR_INK)
        val headerCellPaint = textPaint(10f, Color.WHITE, bold = true)

        var y = PADDING + 10f

        // Шапка
        canvas.drawText("ОИЛТЕХКЛИН", PADDING, y, brandPaint)
        y += 22f
        canvas.drawText("ПРОТОКОЛ ОБРАБОТКИ ТРАНСФОРМАТОРНОГО МАСЛА", PADDING, y, titlePaint)
        y += 8f
        canvas.drawRect(PADDING, y, PADDING + CONTENT_WIDTH, y + 2f, fillPaint(COLOR_BRAND))
        y += 26f

        // Реквизиты
        y = drawKeyValue(canvas, "Дата и время замера", record.recordDate, PADDING, y, mutedPaint, bodyPaint)
        y = drawKeyValue(canvas, "Объект / подстанция", record.substationName, PADDING, y, mutedPaint, bodyPaint)
        y = drawKeyValue(canvas, "Трансформатор", record.transformerName, PADDING, y, mutedPaint, bodyPaint)
        y = drawKeyValue(canvas, "Установка ОИЛТЕХКЛИН", record.unitModel, PADDING, y, mutedPaint, bodyPaint)
        record.voltageClass?.let {
            y = drawKeyValue(canvas, "Класс напряжения", it, PADDING, y, mutedPaint, bodyPaint)
        }
        record.oilTemperatureC?.let {
            y = drawKeyValue(canvas, "Температура масла", "$it °C", PADDING, y, mutedPaint, bodyPaint)
        }
        y += 14f

        // Таблица параметров
        canvas.drawText("РЕЗУЛЬТАТЫ ИЗМЕРЕНИЙ", PADDING, y, sectionPaint)
        y += 14f

        val rows = buildRows(record)
        y = drawTable(canvas, rows, y, headerCellPaint, cellPaint, bodyPaint)
        y += 20f

        // Оценка соответствия нормам
        val standard = OfflineCatalogRepository.gostStandards.firstOrNull {
            it.voltageClass == record.voltageClass
        }
        if (standard != null) {
            val report = GostComplianceEvaluator.evaluate(
                standard = standard,
                breakdownVoltageKv = record.breakdownAfter,
                moisturePpm = record.moistureAfter,
                gasContentPercent = record.gasContentAfter,
                acidNumber = record.acidNumberAfter,
                tanDelta90C = record.tanDeltaAfter
            )
            canvas.drawText("ОЦЕНКА СООТВЕТСТВИЯ НОРМАМ РД 34.45-51.300-97", PADDING, y, sectionPaint)
            y += 16f
            canvas.drawText("Класс напряжения: ${standard.voltageClass}", PADDING, y, bodyPaint)
            y += 15f

            report.checks.filter { it.verdict != GostComplianceEvaluator.Verdict.UNKNOWN }.forEach { check ->
                val mark = if (check.isFail) "НЕ СООТВ." else "соответствует"
                val color = if (check.isFail) COLOR_DANGER else COLOR_SUCCESS
                val limitText = if (check.isUpperBound) "не более" else "не менее"
                val actual = check.actual ?: 0.0
                canvas.drawText(
                    "${check.parameter}: ${formatNumber(actual)} ${check.unit} ($limitText ${formatNumber(check.limit)})",
                    PADDING + 8f, y, bodyPaint
                )
                canvas.drawText(mark, PADDING + CONTENT_WIDTH - 90f, y, textPaint(10f, color, bold = true))
                y += 14f
            }
            y += 8f
        }

        // Заключение
        canvas.drawText("ЗАКЛЮЧЕНИЕ", PADDING, y, sectionPaint)
        y += 16f
        val conclusion = record.conclusion?.takeIf { it.isNotBlank() }
            ?: standard?.let {
                GostComplianceEvaluator.buildConclusion(
                    GostComplianceEvaluator.evaluate(
                        standard = it,
                        breakdownVoltageKv = record.breakdownAfter,
                        moisturePpm = record.moistureAfter,
                        gasContentPercent = record.gasContentAfter,
                        acidNumber = record.acidNumberAfter,
                        tanDelta90C = record.tanDeltaAfter
                    )
                )
            }
            ?: "Оценка соответствия нормам не выполнялась: класс напряжения не указан."
        y = drawWrappedText(canvas, conclusion, PADDING, y, CONTENT_WIDTH, bodyPaint)
        y += 10f

        record.remarks?.takeIf { it.isNotBlank() }?.let { remarks ->
            canvas.drawText("Примечания", PADDING, y, mutedPaint)
            y += 13f
            y = drawWrappedText(canvas, remarks, PADDING, y, CONTENT_WIDTH, bodyPaint)
            y += 10f
        }

        // Подпись
        val signatureY = (PAGE_HEIGHT - PADDING - 40f).coerceAtLeast(y + 30f)
        canvas.drawLine(PADDING, signatureY, PADDING + 200f, signatureY, strokePaint(COLOR_LINE))
        canvas.drawText("Инженер-электротехник", PADDING, signatureY + 14f, mutedPaint)
        canvas.drawLine(
            PADDING + CONTENT_WIDTH - 160f, signatureY,
            PADDING + CONTENT_WIDTH, signatureY, strokePaint(COLOR_LINE)
        )
        canvas.drawText("Подпись заказчика", PADDING + CONTENT_WIDTH - 160f, signatureY + 14f, mutedPaint)

        document.finishPage(page)

        try {
            FileOutputStream(outputFile).use { stream ->
                document.writeTo(stream)
                stream.flush()
            }
        } finally {
            document.close()
        }

        return outputFile
    }

    private data class TableRow(val parameter: String, val before: String, val after: String, val unit: String)

    private fun buildRows(record: JournalRecordEntity): List<TableRow> = buildList {
        add(TableRow("Влагосодержание", formatNumber(record.moistureBefore), formatNumber(record.moistureAfter), "ppm"))
        add(TableRow("Пробивное напряжение", formatNumber(record.breakdownBefore), formatNumber(record.breakdownAfter), "кВ"))
        record.acidNumberBefore?.let { before ->
            add(TableRow("Кислотное число", formatNumber(before), formatNumber(record.acidNumberAfter ?: 0.0), "мг КОН/г"))
        }
        record.tanDeltaBefore?.let { before ->
            add(TableRow("tg δ при 90 °C", formatNumber(before), formatNumber(record.tanDeltaAfter ?: 0.0), "%"))
        }
        record.gasContentAfter?.let {
            add(TableRow("Газосодержание", "—", formatNumber(it), "%"))
        }
        record.flashPointAfter?.let {
            add(TableRow("Температура вспышки", "—", formatNumber(it), "°C"))
        }
    }

    private fun drawTable(
        canvas: android.graphics.Canvas,
        rows: List<TableRow>,
        startY: Float,
        headerPaint: Paint,
        cellPaint: Paint,
        bodyPaint: Paint
    ): Float {
        val colParameter = PADDING
        val colBefore = PADDING + 250f
        val colAfter = PADDING + 350f
        val colUnit = PADDING + 450f
        val rowHeight = 22f
        var y = startY

        canvas.drawRect(PADDING, y, PADDING + CONTENT_WIDTH, y + rowHeight, fillPaint(COLOR_BRAND))
        canvas.drawText("Параметр", colParameter + 8f, y + 15f, headerPaint)
        canvas.drawText("ДО", colBefore, y + 15f, headerPaint)
        canvas.drawText("ПОСЛЕ", colAfter, y + 15f, headerPaint)
        canvas.drawText("Ед.", colUnit, y + 15f, headerPaint)
        y += rowHeight

        rows.forEachIndexed { index, row ->
            if (index % 2 == 1) {
                canvas.drawRect(PADDING, y, PADDING + CONTENT_WIDTH, y + rowHeight, fillPaint(Color.parseColor("#F7F9FF")))
            }
            canvas.drawText(row.parameter, colParameter + 8f, y + 15f, bodyPaint)
            canvas.drawText(row.before, colBefore, y + 15f, cellPaint)
            canvas.drawText(row.after, colAfter, y + 15f, cellPaint)
            canvas.drawText(row.unit, colUnit, y + 15f, cellPaint)
            y += rowHeight
        }

        canvas.drawLine(PADDING, y, PADDING + CONTENT_WIDTH, y, strokePaint(COLOR_LINE))
        return y
    }

    private fun drawKeyValue(
        canvas: android.graphics.Canvas,
        key: String,
        value: String,
        x: Float,
        y: Float,
        keyPaint: Paint,
        valuePaint: Paint
    ): Float {
        canvas.drawText(key, x, y, keyPaint)
        canvas.drawText(value, x + 170f, y, valuePaint)
        return y + 16f
    }

    /** Перенос длинного текста по словам с ограничением ширины. */
    private fun drawWrappedText(
        canvas: android.graphics.Canvas,
        text: String,
        x: Float,
        startY: Float,
        maxWidth: Float,
        paint: Paint
    ): Float {
        var y = startY
        val words = text.split(' ')
        val line = StringBuilder()
        words.forEach { word ->
            val candidate = if (line.isEmpty()) word else "$line $word"
            if (paint.measureText(candidate) > maxWidth && line.isNotEmpty()) {
                canvas.drawText(line.toString(), x, y, paint)
                y += 14f
                line.clear()
                line.append(word)
            } else {
                line.clear()
                line.append(candidate)
            }
        }
        if (line.isNotEmpty()) {
            canvas.drawText(line.toString(), x, y, paint)
            y += 14f
        }
        return y
    }

    private fun formatNumber(value: Double): String =
        if (value == value.toLong().toDouble()) value.toLong().toString()
        else String.format(Locale.US, "%.2f", value)

    private fun textPaint(size: Float, color: Int, bold: Boolean = false) = Paint().apply {
        textSize = size
        this.color = color
        isAntiAlias = true
        if (bold) typeface = android.graphics.Typeface.DEFAULT_BOLD
    }

    private fun fillPaint(color: Int) = Paint().apply {
        this.color = color
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    private fun strokePaint(color: Int) = Paint().apply {
        this.color = color
        style = Paint.Style.STROKE
        strokeWidth = 1f
        isAntiAlias = true
    }
}
