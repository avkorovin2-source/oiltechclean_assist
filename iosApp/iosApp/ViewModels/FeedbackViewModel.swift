import SwiftUI

@MainActor
final class FeedbackViewModel: ObservableObject {
    enum Status: Equatable {
        case idle
        case sending
        case success
        case failure(String)
    }

    @Published var message = ""
    @Published var contact = ""
    @Published private(set) var status: Status = .idle

    private let minMessageLength = 10

    var canSend: Bool {
        trimmedMessage.count >= minMessageLength && FeedbackConfig.isConfigured
    }

    var isSending: Bool {
        status == .sending
    }

    var hint: String {
        if !FeedbackConfig.isConfigured {
            return "Форма пока не подключена к приёмнику: задайте адрес в FeedbackConfig.swift."
        }
        return "Минимум \(minMessageLength) символов."
    }

    func send() async {
        guard canSend, status != .sending else { return }

        status = .sending
        do {
            try await FeedbackService.send(
                message: trimmedMessage,
                contact: contact.trimmingCharacters(in: .whitespacesAndNewlines)
            )
            message = ""
            contact = ""
            status = .success
        } catch {
            status = .failure(error.localizedDescription)
        }
    }

    func resetStatus() {
        status = .idle
    }

    private var trimmedMessage: String {
        message.trimmingCharacters(in: .whitespacesAndNewlines)
    }
}
