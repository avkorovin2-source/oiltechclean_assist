import Foundation

/// Настройки приёмника обратной связи.
///
/// Приложение ничего не знает о почте и не содержит токенов: форма просто
/// отправляет HTTPS POST с JSON на один адрес. Подойдёт любой приёмник,
/// принимающий JSON: Formspree, Getform, Cloudflare Worker, Google Apps Script.
///
/// Как подключить: зарегистрировать форму на https://formspree.io, скопировать
/// адрес вида `https://formspree.io/f/abcdwxyz` и вставить его ниже.
enum FeedbackConfig {
    static let endpoint = "https://formspree.io/f/REPLACE_WITH_FORM_ID"

    /// `true`, когда адрес приёмника задан (а не оставлен шаблоном).
    static var isConfigured: Bool {
        endpoint.hasPrefix("https://") && !endpoint.contains("REPLACE_WITH_FORM_ID")
    }
}

enum FeedbackError: LocalizedError {
    case notConfigured
    case badStatus(Int)
    case transport

    var errorDescription: String? {
        switch self {
        case .notConfigured:
            return "Форма обратной связи ещё не подключена к приёмнику."
        case .badStatus(let code):
            return "Сервер отклонил сообщение (код \(code)). Попробуйте позже."
        case .transport:
            return "Не удалось отправить сообщение. Проверьте подключение к интернету."
        }
    }
}

/// Отправка пожеланий на настроенный HTTPS-приёмник.
enum FeedbackService {
    static func send(message: String, contact: String) async throws {
        guard FeedbackConfig.isConfigured,
              let url = URL(string: FeedbackConfig.endpoint) else {
            throw FeedbackError.notConfigured
        }

        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        request.timeoutInterval = 20
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        request.setValue("application/json", forHTTPHeaderField: "Accept")

        let payload: [String: Any] = [
            "type": "feedback",
            "message": message,
            "contact": contact.isEmpty ? "—" : contact,
            "app": "OilTech Assistant",
            "platform": "iOS",
            "appVersion": Bundle.main.infoDictionary?["CFBundleShortVersionString"] as? String ?? "unknown",
            "sentAt": ISO8601DateFormatter().string(from: Date())
        ]
        request.httpBody = try JSONSerialization.data(withJSONObject: payload)

        let result: (Data, URLResponse)
        do {
            result = try await URLSession.shared.data(for: request)
        } catch {
            throw FeedbackError.transport
        }

        guard let http = result.1 as? HTTPURLResponse else {
            throw FeedbackError.transport
        }
        guard (200..<300).contains(http.statusCode) else {
            throw FeedbackError.badStatus(http.statusCode)
        }
    }
}
