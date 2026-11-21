import SwiftData
import PDFKit
import UIKit
import Shared

final class PDFReportGenerator {

    private static let pageWidth: CGFloat = 595
    private static let pageHeight: CGFloat = 842
    private static let padding: CGFloat = 40
    private static let contentWidth = pageWidth - padding * 2

    static func generateReport(record: JournalRecord) throws -> URL {
        // A4 в points: размер страницы задаётся один раз при создании рендерера,
        // поэтому beginPage() вызывается без аргументов.
        let pageRect = CGRect(x: 0, y: 0, width: pageWidth, height: pageHeight)
        let format = UIGraphicsPDFRendererFormat()
        let renderer = UIGraphicsPDFRenderer(bounds: pageRect, format: format)
        let data = try renderer.pdfData { context in
            context.beginPage()
            guard let ctx = context.cgContext else { return }
            drawReport(ctx: ctx, record: record)
        }
        let fileName = "oiltech_report_\(record.id).pdf"
        let url = FileManager.default.urls(for: .cachesDirectory, in: .userDomainMask)[0]
            .appendingPathComponent(fileName)
        try data.write(to: url, options: Data.WritingOptions.atomic)
        return url
    }

    // MARK: - Отрисовка

    private static func drawReport(ctx: CGContext, record: JournalRecord) {
        ctx.setFillColor(UIColor.white.cgColor)
        ctx.fill(CGRect(x: 0, y: 0, width: pageWidth, height: pageHeight))

        var y: CGFloat = padding + 10

        // Шапка
        y = drawText(ctx: ctx, text: "ОИЛТЕХКЛИН", x: padding, y: y, font: .boldSystemFont(ofSize: 11),
                     color: UIColor(red: 0.4, green: 0.49, blue: 0.92, alpha: 1)) + 22
        y = drawText(ctx: ctx, text: "ПРОТОКОЛ ОБРАБОТКИ ТРАНСФОРМАТОРНОГО МАСЛА", x: padding, y: y,
                     font: .boldSystemFont(ofSize: 18), color: .black) + 8
        ctx.setFillColor(UIColor(red: 0.4, green: 0.49, blue: 0.92, alpha: 1).cgColor)
        ctx.fill(CGRect(x: padding, y: y, width: contentWidth, height: 2))
        y += 26

        // Реквизиты
        y = drawKeyValue(ctx: ctx, key: "Дата и время замера", value: record.recordDate, y: y)
        y = drawKeyValue(ctx: ctx, key: "Объект / подстанция", value: record.substationName, y: y)
        y = drawKeyValue(ctx: ctx, key: "Трансформатор", value: record.transformerName, y: y)
        y = drawKeyValue(ctx: ctx, key: "Установка ОИЛТЕХКЛИН", value: record.unitModel, y: y)
        if let voltageClass = record.voltageClass, !voltageClass.isEmpty {
            y = drawKeyValue(ctx: ctx, key: "Класс напряжения", value: voltageClass, y: y)
        }
        if let temp = record.oilTemperatureC {
            y = drawKeyValue(ctx: ctx, key: "Температура масла", value: "\(formatNumber(temp)) °C", y: y)
        }
        y += 14

        // Таблица результатов
        y = drawText(ctx: ctx, text: "РЕЗУЛЬТАТЫ ИЗМЕРЕНИЙ", x: padding, y: y,
                     font: .boldSystemFont(ofSize: 12), color: .black) + 14

        let colX: [CGFloat] = [padding, padding + 250, padding + 350, padding + 450]
        let headers = ["Параметр", "ДО", "ПОСЛЕ", "Ед."]
        drawTableHeader(ctx: ctx, x: colX, y: y, headers: headers)
        y += 22

        let rows: [(parameter: String, before: String, after: String, unit: String)] = [
            ("Влагосодержание", formatNumber(record.moistureBefore), formatNumber(record.moistureAfter), "ppm"),
            ("Пробивное напряжение", formatNumber(record.breakdownBefore), formatNumber(record.breakdownAfter), "кВ"),
            ("Кислотное число", formatOptional(record.acidNumberBefore), formatOptional(record.acidNumberAfter), "мг КОН/г"),
            ("tg δ при 90 °C", formatOptional(record.tanDeltaBefore), formatOptional(record.tanDeltaAfter), "%"),
        ]

        for (index, row) in rows.enumerated() {
            if index % 2 == 1 {
                ctx.setFillColor(UIColor(red: 0.97, green: 0.98, blue: 1.0, alpha: 1).cgColor)
                ctx.fill(CGRect(x: padding, y: y, width: contentWidth, height: 22))
            }
            drawText(ctx: ctx, text: row.parameter, x: colX[0] + 8, y: y + 15, font: .systemFont(ofSize: 10), color: .black)
            drawText(ctx: ctx, text: row.before, x: colX[1], y: y + 15, font: .systemFont(ofSize: 10), color: .black)
            drawText(ctx: ctx, text: row.after, x: colX[2], y: y + 15, font: .systemFont(ofSize: 10), color: .black)
            drawText(ctx: ctx, text: row.unit, x: colX[3], y: y + 15, font: .systemFont(ofSize: 10), color: .black)
            y += 22
        }

        ctx.setStrokeColor(UIColor(red: 0.89, green: 0.91, blue: 0.96, alpha: 1).cgColor)
        ctx.setLineWidth(1)
        ctx.move(to: CGPoint(x: padding, y: y))
        ctx.addLine(to: CGPoint(x: padding + contentWidth, y: y))
        ctx.strokePath()
        y += 14

        // Заключение
        if let conclusion = record.conclusion, !conclusion.isEmpty {
            y = drawText(ctx: ctx, text: "ЗАКЛЮЧЕНИЕ", x: padding, y: y,
                         font: .boldSystemFont(ofSize: 12), color: .black) + 8
            y = drawWrappedText(ctx: ctx, text: conclusion, x: padding, y: y, maxWidth: contentWidth,
                                font: .systemFont(ofSize: 10))
            y += 14
        }

        // Примечания
        if let remarks = record.remarks, !remarks.isEmpty {
            y = drawText(ctx: ctx, text: "ПРИМЕЧАНИЯ", x: padding, y: y,
                         font: .boldSystemFont(ofSize: 12), color: .black) + 8
            y = drawWrappedText(ctx: ctx, text: remarks, x: padding, y: y, maxWidth: contentWidth,
                                font: .systemFont(ofSize: 10))
        }
    }

    // MARK: - Вспомогательное рисование

    @discardableResult
    private static func drawText(ctx: CGContext, text: String, x: CGFloat, y: CGFloat,
                                 font: UIFont, color: UIColor) -> CGFloat {
        let attributed = NSAttributedString(string: text, attributes: [
            .font: font,
            .foregroundColor: color
        ])
        attributed.draw(at: CGPoint(x: x, y: y))
        return y + font.lineHeight
    }

    @discardableResult
    private static func drawKeyValue(ctx: CGContext, key: String, value: String, y: CGFloat) -> CGFloat {
        drawText(ctx: ctx, text: key, x: padding, y: y, font: .systemFont(ofSize: 9), color: .gray)
        drawText(ctx: ctx, text: value, x: padding + 170, y: y, font: .systemFont(ofSize: 10), color: .black)
        return y + 16
    }

    private static func drawTableHeader(ctx: CGContext, x: [CGFloat], y: CGFloat, headers: [String]) {
        ctx.setFillColor(UIColor(red: 0.4, green: 0.49, blue: 0.92, alpha: 1).cgColor)
        ctx.fill(CGRect(x: padding, y: y, width: contentWidth, height: 22))
        for (index, header) in headers.enumerated() {
            drawText(ctx: ctx, text: header, x: x[index] + 8, y: y + 15,
                     font: .boldSystemFont(ofSize: 10), color: .white)
        }
    }

    @discardableResult
    private static func drawWrappedText(ctx: CGContext, text: String, x: CGFloat, y: CGFloat,
                                        maxWidth: CGFloat, font: UIFont) -> CGFloat {
        var currentY = y
        let words = text.split(separator: " ")
        var line = ""
        for word in words {
            let candidate = line.isEmpty ? String(word) : "\(line) \(word)"
            let width = candidate.width(withConstrainedHeight: .greatestFiniteMagnitude, font: font)
            if width > maxWidth && !line.isEmpty {
                drawText(ctx: ctx, text: line, x: x, y: currentY, font: font, color: .black)
                currentY += 14
                line = String(word)
            } else {
                line = candidate
            }
        }
        if !line.isEmpty {
            drawText(ctx: ctx, text: line, x: x, y: currentY, font: font, color: .black)
            currentY += 14
        }
        return currentY
    }

    // MARK: - Форматирование

    private static func formatNumber(_ value: Double) -> String {
        if value == Double(Int(value)) {
            return String(Int(value))
        }
        return String(format: "%.2f", value)
    }

    private static func formatOptional(_ value: Double?) -> String {
        guard let value = value else { return "—" }
        return formatNumber(value)
    }
}

// MARK: - Утилита измерения текста

private extension String {
    func width(withConstrainedHeight height: CGFloat, font: UIFont) -> CGFloat {
        let constraintRect = CGSize(width: .greatestFiniteMagnitude, height: height)
        let boundingBox = self.boundingRect(with: constraintRect, options: .usesLineFragmentOrigin,
                                            attributes: [.font: font], context: nil)
        return ceil(boundingBox.width)
    }
}
