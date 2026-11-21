# Обратная связь: как подключить приёмник

В приложении (Android и iOS) появилась вкладка **«Связь»**: пользователь пишет
сообщение и при желании оставляет контакт. Отправка — обычный HTTPS `POST`
с JSON, без почтовых клиентов и без токенов внутри приложения.

## Схема

```
Приложение ──POST JSON──▶ ваш приёмник (Formspree / Getform / Worker / Apps Script)
                                   │
                                   ├─▶ личный кабинет сервиса
                                   └─▶ уведомление в Telegram / на почту / в таблицу
```

Тело запроса (одинаковое на Android и iOS):

```json
{
  "type": "feedback",
  "message": "Текст пожелания",
  "contact": "Телефон, e-mail или «—»",
  "app": "OilTech Assistant",
  "platform": "Android",
  "appVersion": "1.1",
  "sentAt": "2026-11-21T08:00:00Z"
}
```

## Что нужно сделать (5–10 минут, один раз)

1. Зарегистрировать форму на бесплатном сервисе-приёмнике:
   - [Formspree](https://formspree.io) — форма создаётся в кабинете, есть
     антиспам, просмотр заявок и пересылка уведомлений;
   - [Getform](https://getform.io) — аналогично.
2. Скопировать адрес формы вида `https://formspree.io/f/abcdwxyz`.
3. Вставить адрес в двух местах:
   - Android: `app/src/main/java/ru/oiltechclean/assistant/data/FeedbackConfig.kt`
     → константа `ENDPOINT`;
   - iOS: `iosApp/iosApp/Services/FeedbackService.swift`
     → `FeedbackConfig.endpoint`.

Пока адрес не задан (в коде стоит `REPLACE_WITH_FORM_ID`), кнопка
«Отправить» заблокирована и в форме видна подсказка — запросы наружу не уходят.

## Альтернативы без SaaS

Любой приёмник, принимающий `POST` c JSON, подойдёт без изменения кода:

| Вариант | Плюсы | Минусы |
|---|---|---|
| Cloudflare Worker → Telegram Bot | бесплатно, мгновенные уведомления | нужен Worker, токен бота живёт на сервере |
| Google Apps Script → Google Sheets | бесплатно, заявки в таблице | настройка через скрипт-редактор |
| Свой backend (`/api/feedback`) | полный контроль | нужен сервер и хостинг |

Важно: не встраивайте в приложение токен Telegram-бота или приватный ключ —
он извлекается из APK/IPA. Токен должен оставаться на сервере (Worker/Apps Script),
а приложение знает только публичный URL приёмника.

## Проверка

```bash
curl -X POST https://formspree.io/f/ВАШ_ID \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{"type":"feedback","message":"Проверка формы","contact":"—","app":"OilTech Assistant","platform":"test","appVersion":"1.1","sentAt":"2026-11-21T08:00:00Z"}'
```

Ответ `200 OK` — приёмник настроен верно; заявка появится в кабинете сервиса.
