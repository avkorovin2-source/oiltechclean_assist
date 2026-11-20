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
| Сеть / камера / локация | Не требуются (приложение 100 % офлайн) | — |

## Структура проекта

```
iosApp/
├── project.yml               # Конфигурация XcodeGen
├── iosApp/
│   ├── App.swift             # Точка входа (@main)
│   ├── ContentView.swift     # Корень: TabView с 4 экранами + базовые UI-компоненты
│   ├── Views/
│   │   ├── CalculatorsView.swift   # Экран «Расчеты» (этап 1 — реализован)
│   │   ├── DiagnosticsView.swift   # Заглушка (этап 2)
│   │   ├── EquipmentView.swift     # Заглушка (этап 3)
│   │   └── JournalView.swift       # Заглушка (этап 4)
│   ├── ViewModels/
│   │   └── CalculatorsViewModel.swift # Вызывает KMP-калькуляторы
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

В настройках приложения Codemagic (Environment variables) добавьте:

| Переменная | Назначение | Пример |
|------------|------------|--------|
| `APPLE_ID` | Ваш Apple ID (бесплатный) | `user@example.com` |
| `TEAM_ID` | ID команды Apple (если есть) | `ABCD123456` |
| `CODE_SIGN_IDENTITY` | Идентификатор подписи (для release) | `Apple Development` |
| `PROVISIONING_PROFILE` | UUID профиля провижининга | `uuid-of-profile` |

Сертификаты `.p12`, `.p8`, профили `.mobileprovision` и пароли **НЕ коммитьте в репозиторий**. Передавайте только через зашифрованные переменные Codemagic.

### 3. Запуск сборки

- Выберите workflow `ios-debug` для отладочной сборки.
- Выберите workflow `ios-ipa` для сборки `.ipa` (требуется платный аккаунт Apple Developer).
- Артефакты доступны после завершения билда на вкладке Artifacts.

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
4. В Codemagic добавьте секреты: `CODE_SIGN_IDENTITY`, `PROVISIONING_PROFILE`, пароль от Apple ID (App-Specific Password для 2FA).
5. Запустите workflow `ios-ipa` — соберется `.ipa`.
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