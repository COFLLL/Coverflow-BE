package com.coverflow.notice.domain;

import com.coverflow.global.entity.BaseTimeEntity;
import com.coverflow.member.domain.Member;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tbl_notice")
public class Notice extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 공지 고유 번호
    @Column
    private String title; // 공지 제목
    @Column
    private String content; // 공지 내용
    @Column
    private boolean noticeStatus; // 상태(T: 등록/F: 삭제)

    @ManyToOne
    @JoinColumn(name = "member_id")
    @JsonBackReference
    private Member member; // 작성자 정보
}
