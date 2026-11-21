import SwiftUI
import UIKit

// MARK: - Палитра

/// Фирменная палитра OilTechClean — синхронизирована с Android (ui/theme/Color.kt).
enum BrandColor {
    static let indigo = Color(hex: 0x667EEA)
    static let violet = Color(hex: 0x764BA2)
    static let sky = Color(hex: 0x4EA5FF)
    static let purple = Color(hex: 0x7F5AF0)
    static let deep = Color(hex: 0x4C5BD5)

    static let ink = Color(hex: 0x0F172A)
    static let inkSoft = Color(hex: 0x1F2937)
    static let slate = Color(hex: 0x5C6570)
    static let slateLight = Color(hex: 0x8A94A6)
    static let mist = Color(hex: 0xF5F7FF)
    static let mistDeep = Color(hex: 0xEEF1FF)
    static let line = Color(hex: 0xE3E8F5)
    static let surface = Color.white

    static let success = Color(hex: 0x28A745)
    static let successSoft = Color(hex: 0xE7F7EC)
    static let warning = Color(hex: 0xF5A524)
    static let warningSoft = Color(hex: 0xFFF4E0)
    static let danger = Color(hex: 0xE5484D)
    static let dangerSoft = Color(hex: 0xFDECEC)

    /// Основной фирменный градиент: #667EEA → #764BA2.
    static let brandGradient = LinearGradient(
        colors: [indigo, violet],
        startPoint: .topLeading,
        endPoint: .bottomTrailing
    )

    /// Акцентный градиент: #7F5AF0 → #4EA5FF.
    static let accentGradient = LinearGradient(
        colors: [purple, sky],
        startPoint: .topLeading,
        endPoint: .bottomTrailing
    )
}

extension Color {
    init(hex: UInt32) {
        self.init(
            .sRGB,
            red: Double((hex >> 16) & 0xFF) / 255.0,
            green: Double((hex >> 8) & 0xFF) / 255.0,
            blue: Double(hex & 0xFF) / 255.0,
            opacity: 1.0
        )
    }
}

// MARK: - Оформление системных панелей

enum BrandAppearance {
    /// Белые панели с фирменным акцентом вместо системного чёрного вида.
    @MainActor
    static func configure() {
        let tab = UITabBarAppearance()
        tab.configureWithOpaqueBackground()
        tab.backgroundColor = .white
        tab.shadowColor = UIColor(BrandColor.line)
        let tabItems = [
            tab.stackedLayoutAppearance,
            tab.inlineLayoutAppearance,
            tab.compactInlineLayoutAppearance
        ]
        for item in tabItems {
            item.normal.iconColor = UIColor(BrandColor.slate)
            item.normal.titleTextAttributes = [.foregroundColor: UIColor(BrandColor.slate)]
            item.selected.iconColor = UIColor(BrandColor.indigo)
            item.selected.titleTextAttributes = [
                .foregroundColor: UIColor(BrandColor.indigo),
                .font: UIFont.systemFont(ofSize: 10, weight: .semibold)
            ]
        }
        UITabBar.appearance().standardAppearance = tab
        UITabBar.appearance().scrollEdgeAppearance = tab

        let nav = UINavigationBarAppearance()
        nav.configureWithOpaqueBackground()
        nav.backgroundColor = .white
        nav.shadowColor = UIColor(BrandColor.line)
        nav.titleTextAttributes = [.foregroundColor: UIColor(BrandColor.ink)]
        nav.largeTitleTextAttributes = [
            .foregroundColor: UIColor(BrandColor.ink),
            .font: UIFont.systemFont(ofSize: 30, weight: .heavy)
        ]
        UINavigationBar.appearance().standardAppearance = nav
        UINavigationBar.appearance().compactAppearance = nav
        UINavigationBar.appearance().scrollEdgeAppearance = nav
        UINavigationBar.appearance().tintColor = UIColor(BrandColor.indigo)
    }
}

// MARK: - Модификаторы экрана

private struct BrandScreenModifier: ViewModifier {
    func body(content: Content) -> some View {
        content
            .background(BrandColor.mist.ignoresSafeArea())
            .tint(BrandColor.indigo)
    }
}

extension View {
    /// Светлый фирменный фон (#F5F7FF) и акцентный tint.
    func brandScreen() -> some View {
        modifier(BrandScreenModifier())
    }
}

// MARK: - Hero-заголовок

/// Градиентная шапка приложения: брендинг + заголовок текущего раздела.
struct BrandHeroHeader: View {
    let title: String
    let subtitle: String
    let icon: String
    var actionTitle: String? = nil
    var action: (() -> Void)? = nil

    var body: some View {
        VStack(alignment: .leading, spacing: 16) {
            HStack(spacing: 12) {
                RoundedRectangle(cornerRadius: 14, style: .continuous)
                    .fill(Color.white.opacity(0.18))
                    .frame(width: 42, height: 42)
                    .overlay(
                        Image(systemName: "bolt.fill")
                            .font(.system(size: 20, weight: .semibold))
                            .foregroundColor(.white)
                    )

                VStack(alignment: .leading, spacing: 2) {
                    Text("OilTech Assistant")
                        .font(.title3)
                        .fontWeight(.heavy)
                    Text("Сервис трансформаторного масла")
                        .font(.caption)
                        .opacity(0.75)
                }

                Spacer(minLength: 8)
            }

            HStack(spacing: 10) {
                Image(systemName: icon)
                    .font(.system(size: 17, weight: .semibold))
                    .opacity(0.9)
                Text(title)
                    .font(.title2)
                    .fontWeight(.heavy)
                Spacer(minLength: 8)
                if let actionTitle, let action {
                    Button(action: action) {
                        HStack(spacing: 5) {
                            Image(systemName: "plus")
                                .font(.system(size: 12, weight: .bold))
                            Text(actionTitle)
                                .font(.caption)
                                .fontWeight(.bold)
                        }
                        .padding(.horizontal, 12)
                        .padding(.vertical, 7)
                        .background(Capsule().fill(Color.white.opacity(0.18)))
                    }
                    .buttonStyle(.plain)
                }
            }

            Text(subtitle)
                .font(.subheadline)
                .opacity(0.8)
        }
        .foregroundColor(.white)
        .padding(20)
        .frame(maxWidth: .infinity, alignment: .leading)
        .background(BrandColor.brandGradient)
        .overlay(alignment: .topTrailing) {
            Circle()
                .fill(Color.white.opacity(0.08))
                .frame(width: 170, height: 170)
                .offset(x: 60, y: -70)
        }
        .overlay(alignment: .bottomLeading) {
            Circle()
                .fill(Color.white.opacity(0.06))
                .frame(width: 110, height: 110)
                .offset(x: -34, y: 54)
        }
        .clipShape(RoundedRectangle(cornerRadius: 28, style: .continuous))
        .shadow(color: BrandColor.indigo.opacity(0.25), radius: 14, x: 0, y: 8)
        .padding(.horizontal, 16)
    }
}

// MARK: - Карточки и заголовки

/// Базовая карточка интерфейса: белая поверхность, мягкая обводка и тень.
struct BrandCard<Content: View>: View {
    let content: Content

    init(@ViewBuilder content: () -> Content) {
        self.content = content()
    }

    var body: some View {
        VStack(alignment: .leading, spacing: 12) { content }
            .padding(18)
            .frame(maxWidth: .infinity, alignment: .leading)
            .background(
                RoundedRectangle(cornerRadius: 22, style: .continuous)
                    .fill(BrandColor.surface)
            )
            .overlay(
                RoundedRectangle(cornerRadius: 22, style: .continuous)
                    .stroke(BrandColor.line, lineWidth: 1)
            )
            .shadow(color: BrandColor.ink.opacity(0.05), radius: 10, x: 0, y: 6)
            .padding(.horizontal, 16)
    }
}

/// Заголовок секции с иконкой в фирменном градиентном квадрате.
struct BrandSectionHeader: View {
    let title: String
    var subtitle: String? = nil
    var icon: String = "bolt.fill"

    var body: some View {
        HStack(alignment: .center, spacing: 12) {
            RoundedRectangle(cornerRadius: 12, style: .continuous)
                .fill(BrandColor.brandGradient)
                .frame(width: 38, height: 38)
                .overlay(
                    Image(systemName: icon)
                        .font(.system(size: 18, weight: .semibold))
                        .foregroundColor(.white)
                )

            VStack(alignment: .leading, spacing: 2) {
                Text(title)
                    .font(.headline)
                    .fontWeight(.bold)
                    .foregroundColor(BrandColor.ink)
                if let subtitle {
                    Text(subtitle)
                        .font(.caption)
                        .foregroundColor(BrandColor.slate)
                }
            }
            Spacer(minLength: 0)
        }
    }
}

/// Крупная плитка результата на акцентном градиенте.
struct BrandResultTile: View {
    let label: String
    let value: String
    var unit: String? = nil
    var gradient: LinearGradient = BrandColor.accentGradient

    var body: some View {
        VStack(alignment: .leading, spacing: 3) {
            Text(label.uppercased())
                .font(.caption2)
                .fontWeight(.semibold)
                .foregroundColor(.white.opacity(0.8))
            HStack(alignment: .bottom, spacing: 3) {
                Text(value)
                    .font(.title3)
                    .fontWeight(.heavy)
                    .foregroundColor(.white)
                if let unit {
                    Text(unit)
                        .font(.caption)
                        .foregroundColor(.white.opacity(0.85))
                }
            }
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .padding(.horizontal, 14)
        .padding(.vertical, 12)
        .background(gradient)
        .clipShape(RoundedRectangle(cornerRadius: 18, style: .continuous))
    }
}

/// Строка «параметр — значение».
struct BrandSpecRow: View {
    let label: String
    let value: String
    var valueColor: Color? = nil

    var body: some View {
        HStack(alignment: .center, spacing: 12) {
            Text(label)
                .font(.subheadline)
                .foregroundColor(BrandColor.slate)
            Spacer(minLength: 8)
            Text(value)
                .font(.subheadline)
                .fontWeight(.semibold)
                .foregroundColor(valueColor ?? BrandColor.ink)
        }
    }
}

/// Цветной бейдж (статусы, классы напряжения).
struct BrandStatusBadge: View {
    let text: String
    let color: Color

    var body: some View {
        Text(text)
            .font(.caption)
            .fontWeight(.bold)
            .foregroundColor(color)
            .padding(.horizontal, 12)
            .padding(.vertical, 5)
            .background(Capsule().fill(color.opacity(0.12)))
    }
}

/// Тонкий разделитель в цветах бренда.
struct BrandHairLine: View {
    var body: some View {
        Rectangle()
            .fill(BrandColor.line)
            .frame(height: 1)
    }
}

// MARK: - Поля ввода

/// Числовое поле в фирменном оформлении.
struct BrandNumberField: View {
    let label: String
    @Binding var value: String
    var suffix: String? = nil

    init(_ label: String, value: Binding<String>, suffix: String? = nil) {
        self.label = label
        self._value = value
        self.suffix = suffix
    }

    var body: some View {
        VStack(alignment: .leading, spacing: 6) {
            Text(label.uppercased())
                .font(.caption2)
                .fontWeight(.semibold)
                .foregroundColor(BrandColor.slateLight)
            HStack(spacing: 8) {
                TextField("", text: $value)
                    .keyboardType(.decimalPad)
                    .foregroundColor(BrandColor.ink)
                if let suffix {
                    Text(suffix)
                        .font(.caption)
                        .foregroundColor(BrandColor.slate)
                }
            }
            .padding(.horizontal, 12)
            .padding(.vertical, 10)
            .background(
                RoundedRectangle(cornerRadius: 12, style: .continuous)
                    .fill(Color.white)
            )
            .overlay(
                RoundedRectangle(cornerRadius: 12, style: .continuous)
                    .stroke(BrandColor.line, lineWidth: 1)
            )
        }
    }
}

/// Текстовое поле с подписью.
struct BrandLabeledTextField: View {
    let label: String
    @Binding var text: String

    init(_ label: String, text: Binding<String>) {
        self.label = label
        self._text = text
    }

    var body: some View {
        VStack(alignment: .leading, spacing: 6) {
            Text(label.uppercased())
                .font(.caption2)
                .fontWeight(.semibold)
                .foregroundColor(BrandColor.slateLight)
            TextField("", text: $text)
                .foregroundColor(BrandColor.ink)
                .padding(.horizontal, 12)
                .padding(.vertical, 11)
                .background(
                    RoundedRectangle(cornerRadius: 12, style: .continuous)
                        .fill(Color.white)
                )
                .overlay(
                    RoundedRectangle(cornerRadius: 12, style: .continuous)
                        .stroke(BrandColor.line, lineWidth: 1)
                )
        }
    }
}

/// Многострочное текстовое поле (сообщения, отзывы).
struct BrandMultilineTextField: View {
    let label: String
    @Binding var text: String
    var placeholder: String
    var minHeight: CGFloat = 140

    init(
        _ label: String,
        text: Binding<String>,
        placeholder: String = "Опишите пожелание или проблему…",
        minHeight: CGFloat = 140
    ) {
        self.label = label
        self._text = text
        self.placeholder = placeholder
        self.minHeight = minHeight
    }

    var body: some View {
        VStack(alignment: .leading, spacing: 6) {
            Text(label.uppercased())
                .font(.caption2)
                .fontWeight(.semibold)
                .foregroundColor(BrandColor.slateLight)

            ZStack(alignment: .topLeading) {
                if text.isEmpty {
                    Text(placeholder)
                        .font(.subheadline)
                        .foregroundColor(BrandColor.slateLight)
                        .padding(.horizontal, 16)
                        .padding(.vertical, 14)
                }

                TextEditor(text: $text)
                    .font(.subheadline)
                    .foregroundColor(BrandColor.ink)
                    .scrollContentBackground(.hidden)
                    .background(Color.clear)
                    .frame(minHeight: minHeight)
                    .padding(.horizontal, 12)
                    .padding(.vertical, 8)
            }
            .background(
                RoundedRectangle(cornerRadius: 12, style: .continuous)
                    .fill(Color.white)
            )
            .overlay(
                RoundedRectangle(cornerRadius: 12, style: .continuous)
                    .stroke(BrandColor.line, lineWidth: 1)
            )
        }
    }
}

// MARK: - Кнопки

/// Основная кнопка: фирменный градиент.
struct BrandPrimaryButtonStyle: ButtonStyle {
    func makeBody(configuration: Configuration) -> some View {
        configuration.label
            .font(.headline)
            .foregroundColor(.white)
            .frame(maxWidth: .infinity)
            .padding(.vertical, 13)
            .background(BrandColor.brandGradient)
            .clipShape(RoundedRectangle(cornerRadius: 16, style: .continuous))
            .opacity(configuration.isPressed ? 0.85 : 1)
    }
}

/// Второстепенная кнопка: белая с фирменной обводкой.
struct BrandSecondaryButtonStyle: ButtonStyle {
    func makeBody(configuration: Configuration) -> some View {
        configuration.label
            .font(.subheadline)
            .fontWeight(.semibold)
            .foregroundColor(BrandColor.deep)
            .frame(maxWidth: .infinity)
            .padding(.vertical, 11)
            .background(
                RoundedRectangle(cornerRadius: 16, style: .continuous)
                    .fill(Color.white)
            )
            .overlay(
                RoundedRectangle(cornerRadius: 16, style: .continuous)
                    .stroke(BrandColor.line, lineWidth: 1)
            )
            .opacity(configuration.isPressed ? 0.7 : 1)
    }
}
