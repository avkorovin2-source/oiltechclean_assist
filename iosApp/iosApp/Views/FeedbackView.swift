import SwiftUI

/// Обратная связь: пожелания и предложения уходят POST-запросом на приёмник.
struct FeedbackView: View {
    @StateObject private var vm = FeedbackViewModel()

    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(spacing: 14) {
                    BrandHeroHeader(
                        title: "Связь",
                        subtitle: "Обратная связь и пожелания",
                        icon: "bubble.left.and.bubble.right.fill"
                    )

                    BrandCard {
                        BrandSectionHeader(
                            title: "Написать нам",
                            subtitle: "Пожелания, замечания, идеи",
                            icon: "envelope.fill"
                        )

                        Text("Сообщение уйдёт разработчику приложения. Ответим, если оставите контакт.")
                            .font(.caption)
                            .foregroundColor(BrandColor.slate)

                        BrandMultilineTextField("Сообщение", text: $vm.message)
                        BrandLabeledTextField("Контакт для ответа (необязательно)", text: $vm.contact)

                        Text(vm.hint)
                            .font(.caption2)
                            .foregroundColor(FeedbackConfig.isConfigured ? BrandColor.slateLight : BrandColor.warning)

                        BrandHairLine()

                        Button {
                            Task { await vm.send() }
                        } label: {
                            HStack(spacing: 8) {
                                if vm.isSending {
                                    ProgressView()
                                        .tint(.white)
                                } else {
                                    Image(systemName: "paperplane.fill")
                                }
                                Text(vm.isSending ? "Отправка…" : "Отправить")
                            }
                        }
                        .buttonStyle(BrandPrimaryButtonStyle())
                        .disabled(!vm.canSend || vm.isSending)
                        .frame(maxWidth: .infinity)
                    }

                    switch vm.status {
                    case .success:
                        StatusCard(
                            icon: "checkmark.circle.fill",
                            title: "Спасибо!",
                            text: "Сообщение отправлено. Мы обязательно его прочитаем.",
                            color: BrandColor.success,
                            soft: BrandColor.successSoft
                        )
                    case .failure(let reason):
                        StatusCard(
                            icon: "exclamationmark.triangle.fill",
                            title: "Не удалось отправить",
                            text: reason,
                            color: BrandColor.danger,
                            soft: BrandColor.dangerSoft
                        )
                    case .idle, .sending:
                        EmptyView()
                    }
                }
                .padding(.vertical, 8)
            }
            .brandScreen()
            .toolbar(.hidden, for: .navigationBar)
        }
    }

    private struct StatusCard: View {
        let icon: String
        let title: String
        let text: String
        let color: Color
        let soft: Color

        var body: some View {
            BrandCard {
                HStack(alignment: .top, spacing: 12) {
                    Image(systemName: icon)
                        .font(.system(size: 22))
                        .foregroundColor(color)
                        .frame(width: 42, height: 42)
                        .background(soft)
                        .cornerRadius(14)

                    VStack(alignment: .leading, spacing: 4) {
                        Text(title)
                            .font(.headline)
                            .fontWeight(.bold)
                            .foregroundColor(color)
                        Text(text)
                            .font(.caption)
                            .foregroundColor(BrandColor.slate)
                    }
                    Spacer(minLength: 0)
                }
            }
        }
    }
}
