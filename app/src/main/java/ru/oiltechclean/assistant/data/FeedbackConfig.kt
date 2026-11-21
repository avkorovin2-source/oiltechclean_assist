package ru.oiltechclean.assistant.data

/**
 * Настройки приёмника обратной связи.
 *
 * Приложение не использует почтовые клиенты и не хранит токенов: форма
 * отправляет HTTPS POST с JSON на один адрес. Подойдёт любой приёмник,
 * принимающий JSON: Formspree, Getform, Cloudflare Worker, Google Apps Script.
 *
 * Как подключить: зарегистрировать форму на https://formspree.io, скопировать
 * адрес вида `https://formspree.io/f/abcdwxyz` и вставить его ниже.
 */
object FeedbackConfig {
    const val ENDPOINT = "https://formspree.io/f/REPLACE_WITH_FORM_ID"

    /** `true`, когда адрес приёмника задан (а не оставлен шаблоном). */
    val isConfigured: Boolean
        get() = ENDPOINT.startsWith("https://") && !ENDPOINT.contains("REPLACE_WITH_FORM_ID")
}
