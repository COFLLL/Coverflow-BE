package com.coverflow.question.dto.response;

import com.coverflow.question.domain.Question;

import java.util.UUID;

public record QuestionResponse(
        long questionId,
        String title,
        String content,
        long viewCount,
        int answerCount,
        long companyId,
        UUID memberId
) {

    public static QuestionResponse from(final Question question) {
        return new QuestionResponse(
                question.getId(),
                question.getTitle(),
                question.getContent(),
                question.getViewCount(),
                question.getAnswerCount(),
                question.getCompany().getId(),
                question.getMember().getId()
        );
    }
}
