package ru.oiltechclean.assistant.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Forum
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import ru.oiltechclean.assistant.data.FeedbackConfig
import ru.oiltechclean.assistant.data.FeedbackSender
import ru.oiltechclean.assistant.ui.components.HairLine
import ru.oiltechclean.assistant.ui.components.LabeledTextField
import ru.oiltechclean.assistant.ui.components.MultilineTextField
import ru.oiltechclean.assistant.ui.components.SectionCard
import ru.oiltechclean.assistant.ui.components.SectionHeader
import ru.oiltechclean.assistant.ui.theme.BrandIndigo
import ru.oiltechclean.assistant.ui.theme.Danger
import ru.oiltechclean.assistant.ui.theme.DangerSoft
import ru.oiltechclean.assistant.ui.theme.Slate
import ru.oiltechclean.assistant.ui.theme.Success
import ru.oiltechclean.assistant.ui.theme.SuccessSoft
import ru.oiltechclean.assistant.ui.theme.Warning

private const val MIN_MESSAGE_LENGTH = 10

private enum class FeedbackSendState { Idle, Sending, Success, Error }

/** Обратная связь: пожелания и предложения уходят POST-запросом на приёмник. */
@Composable
fun FeedbackScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var message by remember { mutableStateOf("") }
    var contact by remember { mutableStateOf("") }
    var sendState by remember { mutableStateOf(FeedbackSendState.Idle) }
    var errorText by remember { mutableStateOf<String?>(null) }

    val appVersion = remember(context) {
        runCatching {
            @Suppress("DEPRECATION")
            context.packageManager.getPackageInfo(context.packageName, 0).versionName
        }.getOrNull().orEmpty()
    }

    val canSend = FeedbackConfig.isConfigured &&
        message.trim().length >= MIN_MESSAGE_LENGTH &&
        sendState != FeedbackSendState.Sending

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        SectionCard {
            SectionHeader(
                title = "Обратная связь",
                icon = Icons.Outlined.Forum,
                subtitle = "Пожелания и предложения"
            )

            Text(
                text = "Сообщение уйдёт разработчику приложения. Ответим, если оставите контакт.",
                style = MaterialTheme.typography.bodySmall,
                color = Slate
            )

            MultilineTextField(
                label = "Сообщение",
                value = message,
                onValueChange = {
                    message = it
                    if (sendState == FeedbackSendState.Error) {
                        sendState = FeedbackSendState.Idle
                        errorText = null
                    }
                }
            )

            LabeledTextField(
                label = "Контакт для ответа (необязательно)",
                value = contact,
                onValueChange = { contact = it }
            )

            Text(
                text = if (FeedbackConfig.isConfigured) {
                    "Минимум $MIN_MESSAGE_LENGTH символов."
                } else {
                    "Форма пока не подключена к приёмнику: задайте адрес в FeedbackConfig.kt."
                },
                style = MaterialTheme.typography.labelSmall,
                color = if (FeedbackConfig.isConfigured) Slate else Warning
            )

            HairLine()

            Button(
                onClick = {
                    val text = message.trim()
                    val replyTo = contact.trim()
                    scope.launch {
                        sendState = FeedbackSendState.Sending
                        errorText = null
                        FeedbackSender.send(
                            message = text,
                            contact = replyTo,
                            appVersion = appVersion
                        )
                            .onSuccess {
                                message = ""
                                contact = ""
                                sendState = FeedbackSendState.Success
                            }
                            .onFailure {
                                errorText = it.message
                                sendState = FeedbackSendState.Error
                            }
                    }
                },
                enabled = canSend,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BrandIndigo,
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
            ) {
                if (sendState == FeedbackSendState.Sending) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.Send,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Text(
                    text = if (sendState == FeedbackSendState.Sending) "Отправка…" else "Отправить",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }

        when (sendState) {
            FeedbackSendState.Success -> FeedbackStatusCard(
                icon = Icons.Outlined.CheckCircle,
                title = "Спасибо!",
                text = "Сообщение отправлено. Мы обязательно его прочитаем.",
                color = Success,
                soft = SuccessSoft
            )

            FeedbackSendState.Error -> FeedbackStatusCard(
                icon = Icons.Outlined.ErrorOutline,
                title = "Не удалось отправить",
                text = errorText ?: "Повторите попытку позже.",
                color = Danger,
                soft = DangerSoft
            )

            else -> Unit
        }
    }
}

@Composable
private fun FeedbackStatusCard(
    icon: ImageVector,
    title: String,
    text: String,
    color: Color,
    soft: Color
) {
    SectionCard {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(28.dp)
            )
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodySmall,
                    color = Slate
                )
            }
        }
    }
}
