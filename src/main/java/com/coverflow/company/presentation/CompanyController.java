package com.coverflow.company.presentation;

import com.coverflow.company.application.CompanyService;
import com.coverflow.company.dto.request.FindCompanyAdminRequest;
import com.coverflow.company.dto.request.FindCompanyQuestionRequest;
import com.coverflow.company.dto.request.SaveCompanyRequest;
import com.coverflow.company.dto.request.UpdateCompanyRequest;
import com.coverflow.company.dto.response.*;
import com.coverflow.global.annotation.AdminAuthorize;
import com.coverflow.global.handler.ResponseHandler;
import com.coverflow.global.util.BadWordUtil;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/company")
@RequiredArgsConstructor
@RestController
public class CompanyController {

    private final CompanyService companyService;

    @GetMapping
    public ResponseEntity<ResponseHandler<SearchCompanyResponse>> search(
            @RequestParam @PositiveOrZero final int pageNo,
            @RequestParam final String name
    ) {
        return ResponseEntity.ok()
                .body(ResponseHandler.<SearchCompanyResponse>builder()
                        .statusCode(HttpStatus.OK)
                        .data(companyService.search(pageNo, name))
                        .build()
                );
    }

    @GetMapping("/count")
    public ResponseEntity<ResponseHandler<SearchCompanyCountResponse>> search(@RequestParam final String name) {
        return ResponseEntity.ok()
                .body(ResponseHandler.<SearchCompanyCountResponse>builder()
                        .statusCode(HttpStatus.OK)
                        .data(companyService.search(name))
                        .build()
                );
    }

    @GetMapping("/{companyId}")
    public ResponseEntity<ResponseHandler<FindCompanyResponse>> findByCompanyId(
            @PathVariable @Positive final long companyId,
            @RequestParam @PositiveOrZero final int pageNo,
            @RequestParam(defaultValue = "createdAt") final String criterion,
            @ModelAttribute final FindCompanyQuestionRequest request
    ) {
        return ResponseEntity.ok()
                .body(ResponseHandler.<FindCompanyResponse>builder()
                        .statusCode(HttpStatus.OK)
                        .data(companyService.findByCompanyId(pageNo, criterion, companyId, request))
                        .build()
                );
    }

    @GetMapping("/admin")
    @AdminAuthorize
    public ResponseEntity<ResponseHandler<FindCompanyAdminResponse>> find(
            @RequestParam @PositiveOrZero final int pageNo,
            @RequestParam(defaultValue = "createdAt") final String criterion,
            @ModelAttribute final FindCompanyAdminRequest request
    ) {
        return ResponseEntity.ok()
                .body(ResponseHandler.<FindCompanyAdminResponse>builder()
                        .statusCode(HttpStatus.OK)
                        .data(companyService.find(pageNo, criterion, request))
                        .build()
                );
    }

    @GetMapping("/admin/count")
    @AdminAuthorize
    public ResponseEntity<ResponseHandler<FindCompanyAdminCountResponse>> find(
            @ModelAttribute final FindCompanyAdminRequest request
    ) {
        return ResponseEntity.ok()
                .body(ResponseHandler.<FindCompanyAdminCountResponse>builder()
                        .statusCode(HttpStatus.OK)
                        .data(companyService.find(request))
                        .build()
                );
    }

    @PostMapping
    public ResponseEntity<ResponseHandler<Void>> save(
            @RequestBody @Valid final SaveCompanyRequest request
    ) {
        BadWordUtil.check(request.name());
        companyService.save(request);
        return ResponseEntity.ok()
                .body(ResponseHandler.<Void>builder()
                        .statusCode(HttpStatus.CREATED)
                        .build());
    }

    @PatchMapping("/admin/{companyId}")
    @AdminAuthorize
    public ResponseEntity<ResponseHandler<Void>> update(
            @PathVariable @Positive final long companyId,
            @RequestBody @Valid final UpdateCompanyRequest request
    ) {
        companyService.update(companyId, request);
        return ResponseEntity.ok()
                .body(ResponseHandler.<Void>builder()
                        .statusCode(HttpStatus.NO_CONTENT)
                        .build());
    }

    @DeleteMapping("/admin/{companyId}")
    @AdminAuthorize
    public ResponseEntity<ResponseHandler<Void>> deleteData(
            @PathVariable @Positive final long companyId
    ) {
        companyService.delete(companyId);
        return ResponseEntity.ok()
                .body(ResponseHandler.<Void>builder()
                        .statusCode(HttpStatus.NO_CONTENT)
                        .build());
    }
}
