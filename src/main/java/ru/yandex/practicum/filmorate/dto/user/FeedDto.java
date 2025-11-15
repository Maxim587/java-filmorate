package ru.yandex.practicum.filmorate.dto.user;

import lombok.Data;

@Data
public class FeedDto {
    private Integer eventId;
    private Long timestamp;
    private Integer userId;
    private Integer entityId;
    private String eventType;
    private String operation;
}
