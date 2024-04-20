package com.coverflow.question.presentation;

import com.coverflow.global.annotation.AdminAuthorize;
import com.coverflow.global.annotation.MemberAuthorize;
import com.coverflow.global.handler.ResponseHandler;
import com.coverflow.global.util.BadwordUtil;
import com.coverflow.question.application.AnswerService;
import com.coverflow.question.dto.request.FindAnswerAdminRequest;
import com.coverflow.question.dto.request.SaveAnswerRequest;
import com.coverflow.question.dto.request.UpdateAnswerRequest;
import com.coverflow.question.dto.request.UpdateSelectionRequest;
import com.coverflow.question.dto.response.FindAnswerResponse;
import com.coverflow.question.dto.response.FindMyAnswersResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@RequestMapping("/api/answer")
@RestController
public class AnswerController {

    private final AnswerService answerService;

    @GetMapping("/me")
    @MemberAuthorize
    public ResponseEntity<ResponseHandler<FindMyAnswersResponse>> findMyAnswers(
            @RequestParam @PositiveOrZero final int pageNo,
            @RequestParam(defaultValue = "createdAt") @NotBlank final String criterion,
            @AuthenticationPrincipal final UserDetails userDetails
    ) {
        return ResponseEntity.ok()
                .body(ResponseHandler.<FindMyAnswersResponse>builder()
                        .statusCode(HttpStatus.OK)
                        .data(answerService.findByMemberId(pageNo, criterion, UUID.fromString(userDetails.getUsername())))
                        .build()
                );
    }

    @GetMapping("/admin")
    @AdminAuthorize
    public ResponseEntity<ResponseHandler<FindAnswerResponse>> find(
            @RequestParam @PositiveOrZero final int pageNo,
            @RequestParam(defaultValue = "createdAt") @NotBlank final String criterion,
            @ModelAttribute final FindAnswerAdminRequest request
    ) {
        return ResponseEntity.ok()
                .body(ResponseHandler.<FindAnswerResponse>builder()
                        .statusCode(HttpStatus.OK)
                        .data(answerService.find(pageNo, criterion, request))
                        .build()
                );
    }

    @PostMapping
    @MemberAuthorize
    public ResponseEntity<ResponseHandler<Void>> save(
            @RequestBody @Valid final SaveAnswerRequest request,
            @AuthenticationPrincipal final UserDetails userDetails
    ) {
        BadwordUtil.check(request.content());
        answerService.save(request, userDetails.getUsername());
        return ResponseEntity.ok()
                .body(ResponseHandler.<Void>builder()
                        .statusCode(HttpStatus.CREATED)
                        .build());
    }

    @PatchMapping("/selection/{answerId}")
    @MemberAuthorize
    public ResponseEntity<ResponseHandler<Void>> choose(
            @PathVariable @Positive final long answerId,
            @RequestBody @Valid final UpdateSelectionRequest request,
            @AuthenticationPrincipal final UserDetails userDetails
    ) {
        answerService.choose(answerId, request, userDetails.getUsername());
        return ResponseEntity.ok()
                .body(ResponseHandler.<Void>builder()
                        .statusCode(HttpStatus.NO_CONTENT)
                        .build());
    }

    @PatchMapping("/admin/{answerId}")
    @AdminAuthorize
    public ResponseEntity<ResponseHandler<Void>> update(
            @PathVariable @Positive final long answerId,
            @RequestBody @Valid final UpdateAnswerRequest request
    ) {
        BadwordUtil.check(request.content());
        answerService.update(answerId, request);
        return ResponseEntity.ok()
                .body(ResponseHandler.<Void>builder()
                        .statusCode(HttpStatus.NO_CONTENT)
                        .build());
    }

    @DeleteMapping("/admin/{answerId}")
    @AdminAuthorize
    public ResponseEntity<ResponseHandler<Void>> delete(
            @PathVariable @Positive final long answerId
    ) {
        answerService.delete(answerId);
        return ResponseEntity.ok()
                .body(ResponseHandler.<Void>builder()
                        .statusCode(HttpStatus.NO_CONTENT)
                        .build());
    }
}
