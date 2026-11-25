package ru.oiltechclean.assistant.data

/**
 * Настройки приёмника обратной связи.
 *
 * Приложение не использует почтовые клиенты и не хранит токенов: форма
 * отправляет HTTPS POST с JSON на адрес `feedback_api.php` сайта
 * ОИЛТЕХКЛИН. Отзывы складываются в `data/feedback.json` и видны
 * в админ-панели сайта на вкладке «Отзывы из приложения».
 *
 * Приёмник — обычный HTTPS POST с JSON, поэтому при необходимости адрес
 * можно заменить на Formspree, Getform, Cloudflare Worker и т. п.
 */
object FeedbackConfig {
    const val ENDPOINT = "https://oiltechclean.ru/feedback_api.php"

    /** `true`, когда адрес приёмника задан (а не оставлен шаблоном). */
    val isConfigured: Boolean
        get() = ENDPOINT.startsWith("https://") && !ENDPOINT.contains("REPLACE_WITH_FORM_ID")
}
