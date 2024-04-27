package com.coverflow.member.infrastructure;

import com.coverflow.member.domain.Member;
import com.coverflow.member.domain.MemberStatus;
import com.coverflow.member.domain.SocialType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MemberRepository extends JpaRepository<Member, UUID>, MemberCustomRepository {

    Optional<Member> findByEmail(final String email);

    Optional<Member> findByNickname(final String nickname);

    Optional<Member> findByRefreshToken(final String refreshToken);

    @Query("""
            SELECT m
            FROM Member m
            WHERE m.id = :id
            AND m.memberStatus= :memberStatus
            ORDER BY m.createdAt ASC
            """)
    Optional<Member> findByIdAndMemberStatus(
            @Param("id") final UUID id,
            @Param("memberStatus") final MemberStatus memberStatus
    );

    @Query("""
            SELECT m
            FROM Member m
            WHERE m.socialType = :socialType
            AND m.socialId = :socialId
            """)
    Optional<Member> findBySocialTypeAndSocialId(
            @Param("socialType") final SocialType socialType,
            @Param("socialId") final String socialId
    );

    @Query("""
            SELECT m
            FROM Member m
            WHERE m.memberStatus= 'LEAVE'
            AND m.updatedAt <= :date
            """)
    Optional<List<Member>> findByStatus(final LocalDateTime date);

    @Modifying
    @Query("""
            DELETE FROM Member m
            WHERE  m.updatedAt< :date
            AND m.memberStatus = 'LEAVE'
            """)
    void deleteMembersWithStatus(@Param("date") final LocalDateTime date);
}
