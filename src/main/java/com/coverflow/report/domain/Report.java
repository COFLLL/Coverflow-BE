package com.coverflow.report.domain;

import com.coverflow.global.entity.BaseTimeEntity;
import com.coverflow.member.domain.Member;
import com.coverflow.question.domain.Answer;
import com.coverflow.question.domain.Question;
import com.coverflow.report.dto.request.SaveReportRequest;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tbl_report")
public class Report extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 신고 고유 번호
    @Column
    private String content; // 내용
    @Column
    private Boolean reportStatus; // 상태(T: 등록/F: 삭제)

    @Enumerated(EnumType.STRING)
    private ReportType type; // 신고 종류

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @JsonBackReference
    private Member member; // 작성자 정보

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    @JsonBackReference
    private Question question; // 질문 정보

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "answer_id")
    @JsonBackReference
    private Answer answer; // 답변 정보

    public Report(
            final SaveReportRequest request,
            final String memberId
    ) {
        this.content = request.content();
        this.type = ReportType.QUESTION;
        this.reportStatus = true;
        this.member = Member.builder()
                .id(UUID.fromString(memberId))
                .build();
        this.question = Question.builder()
                .id(request.id())
                .build();
    }

    public Report(
            final SaveReportRequest request,
            final Answer answer,
            final String memberId
    ) {
        this.content = request.content();
        this.type = ReportType.ANSWER;
        this.reportStatus = true;
        this.member = Member.builder()
                .id(UUID.fromString(memberId))
                .build();
        this.question = Question.builder()
                .id(answer.getId())
                .build();
        this.answer = Answer.builder()
                .id(request.id())
                .build();
    }
}
