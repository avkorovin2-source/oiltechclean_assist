import SwiftData
import SwiftUI

/// Stack SwiftData — нативная замена Room для iOS.
/// Реализует те же сущности, что и Android Room:
/// - JournalRecord (journal_records)
/// - AcceptanceCheck (acceptance_checks)
final class SwiftDataProvider {
    static let shared = SwiftDataProvider()

    let container: ModelContainer

    private init() {
        do {
            container = try ModelContainer(
                for: JournalRecord.self, AcceptanceCheck.self,
                configurations: ModelConfiguration(for: JournalRecord.self, AcceptanceCheck.self)
            )
        } catch {
            fatalError("Не удалось создать SwiftData контейнер: \(error)")
        }
    }

    var context: ModelContext {
        container.mainContext
    }
}

/// Запись полевого журнала (аналог JournalRecordEntity на Android)
@Model
final class JournalRecord {
    @Attribute var id: Int
    @Attribute var substationName: String
    @Attribute var transformerName: String
    @Attribute var unitModel: String
    @Attribute var recordDate: String
    @Attribute var moistureBefore: Double
    @Attribute var moistureAfter: Double
    @Attribute var breakdownBefore: Double
    @Attribute var breakdownAfter: Double
    @Attribute var acidNumberBefore: Double?
    @Attribute var acidNumberAfter: Double?
    @Attribute var tanDeltaBefore: Double?
    @Attribute var tanDeltaAfter: Double?
    @Attribute var remarks: String?
    @Attribute var voltageClass: String?
    @Attribute var oilTemperatureC: Double?
    @Attribute var gasContentAfter: Double?
    @Attribute var flashPointAfter: Double?
    @Attribute var conclusion: String?

    init(id: Int = 0,
         substationName: String,
         transformerName: String,
         unitModel: String,
         recordDate: String,
         moistureBefore: Double,
         moistureAfter: Double,
         breakdownBefore: Double,
         breakdownAfter: Double,
         acidNumberBefore: Double? = nil,
         acidNumberAfter: Double? = nil,
         tanDeltaBefore: Double? = nil,
         tanDeltaAfter: Double? = nil,
         remarks: String? = nil,
         voltageClass: String? = nil,
         oilTemperatureC: Double? = nil,
         gasContentAfter: Double? = nil,
         flashPointAfter: Double? = nil,
         conclusion: String? = nil) {
        self.id = id
        self.substationName = substationName
        self.transformerName = transformerName
        self.unitModel = unitModel
        self.recordDate = recordDate
        self.moistureBefore = moistureBefore
        self.moistureAfter = moistureAfter
        self.breakdownBefore = breakdownBefore
        self.breakdownAfter = breakdownAfter
        self.acidNumberBefore = acidNumberBefore
        self.acidNumberAfter = acidNumberAfter
        self.tanDeltaBefore = tanDeltaBefore
        self.tanDeltaAfter = tanDeltaAfter
        self.remarks = remarks
        self.voltageClass = voltageClass
        self.oilTemperatureC = oilTemperatureC
        self.gasContentAfter = gasContentAfter
        self.flashPointAfter = flashPointAfter
        self.conclusion = conclusion
    }
}

/// Результат проверки чек-листа приемки масла (аналог AcceptanceCheckEntity)
@Model
final class AcceptanceCheck {
    @Attribute var id: Int
    @Attribute var objectName: String
    @Attribute var checkedAt: Double
    @Attribute var checkedItemIds: String
    @Attribute var approved: Bool

    init(id: Int = 0,
         objectName: String,
         checkedAt: Double,
         checkedItemIds: String,
         approved: Bool) {
        self.id = id
        self.objectName = objectName
        self.checkedAt = checkedAt
        self.checkedItemIds = checkedItemIds
        self.approved = approved
    }
}