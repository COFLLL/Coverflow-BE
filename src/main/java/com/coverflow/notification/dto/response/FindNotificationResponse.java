package com.coverflow.notification.dto.response;

import com.coverflow.notification.domain.Notification;
import com.coverflow.notification.domain.NotificationType;

import java.time.LocalDate;

public record FindNotificationResponse(
        String content,
        String url,
        NotificationType type,
        boolean isRead,
        LocalDate createdAt
) {

    public static FindNotificationResponse from(Notification notification) {
        return new FindNotificationResponse(
                notification.getContent(),
                notification.getUrl(),
                notification.getType(),
                notification.isRead(),
                notification.getCreatedAt().toLocalDate()
        );
    }
}
