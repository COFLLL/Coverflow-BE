package com.coverflow.notice.dto;

import com.coverflow.notice.domain.Notice;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NoticeDTO {
    private Long id;
    private String title;
    private String content;
    private long views;
    private boolean noticeStatus;

    public static NoticeDTO from(final Notice notice) {
        return new NoticeDTO(
                notice.getId(),
                notice.getTitle(),
                notice.getContent(),
                notice.getViews(),
                notice.isNoticeStatus()
        );
    }
}
