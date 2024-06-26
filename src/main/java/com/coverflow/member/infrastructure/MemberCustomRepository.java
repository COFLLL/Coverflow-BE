package com.coverflow.member.infrastructure;

import com.coverflow.member.domain.Member;
import com.coverflow.member.dto.request.FindMemberAdminRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface MemberCustomRepository {

    Optional<Page<Member>> findWithFilters(final Pageable pageable, final FindMemberAdminRequest request);
}
