# iOS-версия OilTechClean Assistent

Дополнение к основному репозиторию Android-приложения. В этой папке находится нативная iOS-версия на SwiftUI с общей бизнес-логикой из KMP-модуля `:shared`.

## Содержание

1. [Архитектура](#архитектура)
2. [Структура проекта](#структура-проекта)
3. [Сборка на Windows без локального Mac](#сборка-на-windows-без-локального-mac)
4. [Сборка через Codemagic](#сборка-через-codemagic)
5. [Установка на iPhone с бесплатным Apple ID](#установка-на-iphone-с-бесплатным-apple-id)
6. [Ограничения без платного Apple Developer Program](#ограничения-без-платного-apple-developer-program)
7. [Переход к TestFlight при покупке членства](#переход-к-testflight-при-покупке-членства)
8. [Переиспользование кода Android](#переиспользование-кода-android)

---

## Архитектура

| Слой | Решение (iOS) | Источник |
|------|--------------|----------|
| UI | SwiftUI, `NavigationStack`, `TabView` | Написано заново |
| Навигация | Нативная iOS (`NavigationStack`, `.tabItem`) | Написано заново |
| Бизнес-логика, модели, справочники, калькуляторы | KMP `:shared` (`commonMain`) | Переиспользуется из Android |
| Локальное хранилище | SwiftData (`SwiftDataProvider`, `JournalRecord`, `AcceptanceCheck`) | Аналог Room на Android |
| ViewModel / состояние | `@MainActor` + `@Published` (ObservableObject) | Аналог ViewModel + StateFlow |
| PDF-отчёты | PDFKit (планируется) | Аналог `PdfReportGenerator` на Android |
| Сеть / камера / локация | Сеть — только для отправки обратной связи (см. `FEEDBACK.md`), камера/локация не требуются | — |

## Структура проекта

```
iosApp/
├── project.yml               # Конфигурация XcodeGen
├── iosApp/
│   ├── App.swift             # Точка входа (@main)
│   ├── ContentView.swift     # Корень: TabView с 5 экранами + базовые UI-компоненты
│   ├── Views/
│   │   ├── CalculatorsView.swift   # Экран «Расчеты» (этап 1 — реализован)
│   │   ├── DiagnosticsView.swift   # Заглушка (этап 2)
│   │   ├── EquipmentView.swift     # Заглушка (этап 3)
│   │   ├── JournalView.swift       # Заглушка (этап 4)
│   │   └── FeedbackView.swift      # Экран «Связь» — обратная связь
│   ├── ViewModels/
│   │   └── CalculatorsViewModel.swift # Вызывает KMP-калькуляторы
│   ├── Services/
│   │   └── FeedbackService.swift     # POST JSON на приёмник обратной связи
│   └── Storage/
│       ├── SwiftDataProvider.swift   # SwiftData стек (замена Room)
│       ├── JournalRecord.swift       # Модель журнала (SwiftData)
│       └── AcceptanceCheck.swift     # Модель проверки чек-листа (SwiftData)
shared/                       # KMP-модуль (уже есть) — commonMain, androidTarget, iOS-таргеты
codemagic.yaml                # CI для Codemagic
```

---

## Сборка на Windows без локального Mac

iOS-приложение нельзя скомпилировать на Windows — Xcode доступен только на macOS. У вас есть два пути:

### Способ А: Codemagic (рекомендуется)

1. Зарегистрируйтесь на [codemagic.io](https://codemagic.io).
2. Подключите репозиторий GitHub/GitLab.
3. Добавьте workflow `ios-debug` из `codemagic.yaml`.
4. Запустите сборку — Codemagic предоставит macOS-машину с Xcode.
5. Скачайте собранный `.app` или `.ipa`.

### Способ Б: Доступ к Mac через облако

1. Арендуйте Mac-in-the-cloud (MacStadium, MacinCloud, AWS Mac EC2 и т.д.).
2. Склонируйте репозиторий на Mac.
3. Установите Xcode из App Store.
4. Установите XcodeGen: `brew install xcodegen`.
5. Выполните: `cd iosApp && xcodegen generate`.
6. Откройте сгенерированный `iosApp.xcodeproj` в Xcode.
7. Выберите устройство/симулятор и нажмите `Cmd+R` для запуска.

> **Важно:** на Windows можно только запускать юнит-тесты.shared модуля через Gradle (`./gradlew :shared:jvmTest`), но не собирать iOS-бинарник.

---

## Сборка .ipa для Sideloadly (в этой спецификации)

Этот репозиторий содержит готовый GitHub Actions workflow `.github/workflows/ios-release.yml`, который собирает **несобранный .ipa** (без подписи Apple).

### Зачем так сделано

- **Публикация не требуется** — `.ipa` будет использоваться только для установки на ваш iPhone через Sideloadly.
- **Платный Apple Developer Program не нужен** — если у вас есть бесплатный Apple ID, вы будете подписывать приложение непосредственно в Sideloadly.
- Сборка в GitHub Actions запускается при пуше тега `v*` (например, `v1.0`), чтобы каждый релиз тоже давал разный `.ipa`.

### Как собрать .ipa на GitHub Actions

1. Зайдите в репозиторий на GitHub.
2. Выберите вкладку **Actions**.
3. Выберите workflow **iOS Build for Sideloadly (unsigned .ipa)**.
4. Нажмите **Run workflow** → **Run workflow**.
5. Дождитесь завершения (обычно 10–20 минут).
6. В разделе **Artifacts** нажмите **oiltech-ipa-sideloadly** и скачайте `oiltechunsigned.ipa`.

### Как установить на iPhone через Sideloadly

1. Установите [Sideloadly](https://sideloadly.io/) на компьютер (Windows/macOS).
2. Подключите iPhone к компьютеру через USB.
3. Откройте Sideloadly.
4. Перетащите в окно `oiltechunsigned.ipa`.
5. В поле **Apple ID** введите свой **бесплатный Apple ID** (главное — чтобы на нём не было платного аккаунта, иначе Sideloadly не сможет подписать).
6. Убедитесь, что выбран ваш iPhone.
7. Нажмите **Start** — Sideloadly пройдёт подпись и установит приложение.

> Примечание: приложение, подписанное бесплатным Apple ID, работает на iPhone в течение **7 дней**. По истечении срока потребуется снова запустить Sideloadly (любой .ipa, собранный после этого, подпишет работу заново).

---

## Лимиты без платного Apple Developer Program

| Возможность | Бесплатный Apple ID | Apple Developer Program ($99/год) |
|-------------|---------------------|-----------------------------------|
| Запуск на своем iPhone через Xcode | Да (7 дней) | Да (бессрочно) |
| Установка на устройства других людей | Нет | Да (TestFlight / Ad Hoc) |
| Выгрузка в TestFlight | Нет | Да |
| Публикация в App Store | Нет | Да |
| Push-уведомления | Нет | Да |
| Интеграция с Apple Pay / Wallet | Нет | Да |
| Кол-во устройств для тестирования | Ограничено (1–2) | Не ограничено |

---

## Переход к TestFlight при покупке членства

(Оставленный раздел из предыдущей версии — не применяется, так как сейчас программа не публикуется.)

---

## Требования

- macOS Sonoma или новее (для GitHub Actions и генерации .ipa)
- XcodeGen (для генерации `.xcodeproj`)
- Sideloadly (для установки на iPhone через USB)
- Бесплатный Apple ID на iPhone

---

## Сборка через Codemagic

### 1. Подключение репозитория

- Войдите в Codemagic → Applications → Add application.
- Выберите ваш Git-репозиторий с веткой `master`.
- Укажите рабочую директорию: корень репозитория.

### 2. Переменные окружения (секреты)

Workflow `ios-debug` собирает приложение **без подписи** и не требует никаких секретов: JDK 17 и Xcode заданы прямо в `codemagic.yaml` (`environment: java: 17`).

Для подписанной сборки (`ios-ipa`) сертификаты и профили загружаются не переменными, а через **Code signing identities**:

1. Codemagic → ваше приложение → Settings → **Code signing identities**.
2. Загрузите сертификат `.p12` (с паролем) и профили `.mobileprovision`.
3. Bundle ID в workflow — `ru.oiltechclean.assistant.iosApp` (его генерирует XcodeGen из `project.yml`).

Сертификаты `.p12`, `.p8`, профили `.mobileprovision` и пароли **НЕ коммитьте в репозиторий**. Передавайте только через Codemagic.

### 3. Запуск сборки

- Workflow `ios-debug` — отладочная сборка `.app` без подписи (артефакт: `iosApp/build/Build/Products/**/*.app`).
- Workflow `ios-ipa` — подписанный `.ipa` (требуется платный Apple Developer; артефакт: `iosApp/build/ios/ipa/*.ipa`).
- Shared-тесты JVM запускаются в `ios-debug` шагом `./gradlew :shared:jvmTest` из корня репозитория.

### 4. Локальный запуск тестов KMP (Windows/macOS/Linux)

```bash
# Запуск общих юнит-тестов на JVM (не требует Mac)
./gradlew :shared:jvmTest
```

---

## Установка на iPhone с бесплатным Apple ID

Apple позволяет бесплатно устанавливать приложения на собственные устройства через Xcode без оплаты Apple Developer Program ($99/год).

### Инструкция

1. Установите Xcode на Mac.
2. Войдите в Xcode → Settings → Accounts → добавьте свой бесплатный Apple ID.
3. Подключите iPhone через USB к Mac.
4. В Xcode выберите ваше устройство в схеме запуска.
5. Нажмите `Cmd+R` — Xcode автоматически создаст бесплатный профиль подписки и установит приложение.

### Как долго работает

Приложение, подписанное бесплатным Apple ID, работает на устройстве **7 дней**. По истечении срока нужно пересобрать и переустановить через Xcode.

---

## Ограничения без платного Apple Developer Program

| Возможность | Бесплатный Apple ID | Apple Developer Program ($99/год) |
|-------------|---------------------|-----------------------------------|
| Запуск на своем iPhone через Xcode | Да (7 дней) | Да (бессрочно) |
| Установка на устройства других людей | Нет | Да (TestFlight / Ad Hoc) |
| Выгрузка в TestFlight | Нет | Да |
| Публикация в App Store | Нет | Да |
| Push-уведомления | Нет | Да |
| Интеграция с Apple Pay / Wallet | Нет | Да |
| Кол-во устройств для тестирования | Ограничено (1–2) | Не ограничено |

---

## Переход к TestFlight при покупке членства

1. Купите Apple Developer Program на [developer.apple.com](https://developer.apple.com).
2. В Xcode → Settings → Accounts добавьте новый аккаунт Developer.
3. Создайте App ID в [App Store Connect](https://appstoreconnect.apple.com).
4. В Codemagic → Settings → **Code signing identities** загрузите сертификат `.p12` и профиль `.mobileprovision` (для TestFlight — с типом App Store).
5. Запустите workflow `ios-ipa` — соберётся `.ipa` в `iosApp/build/ios/ipa/`.
6. Загрузите `.ipa` в App Store Connect → TestFlight.
7. Пригласите тестировщиков по email.

---

## Переиспользование кода Android

- **Переиспользуется (KMP `:shared`)**: модели (`DomainModels.kt`), справочники (`OfflineCatalogRepository`, `TransformerCatalog`), доменные расчёты (`EngineeringCalculators`), оценка по ГОСТ (`GostComplianceEvaluator`), ХАРГ-диагностика (`HargDiagnostics`).
- **Переписывается на SwiftUI**: все экраны, тема, навигация, компоненты ввода.
- **Заменяется на SwiftData**: Room + DAO → `SwiftDataProvider` + `@Model` классы.
- **Не требуется**: Retrofit/OkHttp/Мoshi (мёртвые зависимости на Android), Firebase, камера, карты, платежи, пуши.

---

## Требования

- macOS Sonoma или новее
- Xcode 15+
- Swift 5.9+
- iOS 17.0+ (минимальная версия)
- XcodeGen (для генерации `.xcodeproj` из `project.yml`)

---

## Безопасность

- `.gitignore` исключает `*.p12`, `*.p8`, `*.cer`, `*.mobileprovision`, `.env`, `*.ipa`, `*.xcarchive`, `DerivedData`.
- Все секреты передаются через CI-переменные Codemagic.
- `keystore.properties` (Android) и все сертификаты iOS не коммитятся.

## Сборка iOS: актуальная схема интеграции с KMP

`Shared.framework` не подключается по «магическому» пути. Всё делает `iosApp/project.yml` (XcodeGen):

1. **preBuild-фаза** запускает `./gradlew :shared:embedAndSignAppleFrameworkForXcode` — Gradle собирает фреймворк под текущие `CONFIGURATION`/`SDK_NAME` и кладёт его в `shared/build/xcode-frameworks/<Configuration>/<SDK>/Shared.framework`.
2. `FRAMEWORK_SEARCH_PATHS` указывает на этот каталог, `OTHER_LDFLAGS` содержит `-framework Shared`.
3. `isStatic = true` — фреймворк статический, поэтому он только линкуется, но не встраивается в бандл (`embed` не нужен).
4. `ENABLE_USER_SCRIPT_SANDBOXING = NO` — иначе Xcode 15+ запрещает скрипту писать в `../shared/build`.
5. `GENERATE_INFOPLIST_FILE = YES` — отдельного `Info.plist` в проекте нет.

Ручная сборка на Mac:

```bash
cd iosApp
xcodegen generate
open iosApp.xcodeproj
```

В CI сборка идёт без подписи (`CODE_SIGN_IDENTITY="" CODE_SIGNING_ALLOWED=NO`), а `.ipa` пакуется вручную (`Payload/` + `zip`) — это позволяет ставить приложение через Sideloadly.

### Swift-дружественный API модуля `shared`

Kotlin-`object` в Swift доступен только через `Xxx.shared`, а примитивы внутри коллекций боксятся (`Map<String, Double>` → `[String: KotlinDouble]`, `List<Pair<..>>` → `KotlinPair`). Чтобы iOS-код оставался простым, в `shared` добавлены тонкие адаптеры:

| Kotlin | Назначение |
|---|---|
| `OilAcceptanceChecklist.isApprovedList(checkedIds: List<String>)` | вердикт чек-листа без `Kotlin Set` |
| `OilAcceptanceChecklist.missingCriticalCount(checkedIds: List<String>)` | число неотмеченных критичных пунктов |
| `GostComplianceEvaluator.evaluateValues(...)` | оценка по ГОСТ без `KotlinDouble?` (нет данных передаётся как `Double.NaN`) |
| `ComplianceReport.summaryText` / `.failedLines` | готовые строки для UI без доступа к вложенным типам |
| `OfflineCatalogRepository.troubleshootingRows(unit)` | `List<Pair>` → `[TroubleshootingRow]` (`problem` / `solution`) |
| `OfflineCatalogRepository.unitSummary(unit)` | описание установки (имя `description` конфликтует с `NSObject.description`) |

Адаптеры покрыты тестами в `shared/src/commonTest` (`OilAcceptanceChecklistTest`, `GostComplianceTest`, `OfflineCatalogRepositoryTest`) и проверяются на любой ОС через `./gradlew :shared:jvmTest`.
