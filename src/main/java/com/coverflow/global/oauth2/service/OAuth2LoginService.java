package com.coverflow.global.oauth2.service;

import com.coverflow.global.jwt.service.JwtService;
import com.coverflow.global.oauth2.dto.TokenRequest;
import com.coverflow.global.util.AesUtil;
import com.coverflow.member.domain.Member;
import com.coverflow.member.domain.MemberStatus;
import com.coverflow.member.domain.RefreshTokenStatus;
import com.coverflow.member.domain.Role;
import com.coverflow.member.exception.MemberException;
import com.coverflow.member.infrastructure.MemberRepository;
import com.coverflow.notification.application.NotificationService;
import com.coverflow.notification.domain.Notification;
import com.coverflow.visitor.application.VisitorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class OAuth2LoginService {

    private final JwtService jwtService;
    private final VisitorService visitorService;
    private final NotificationService notificationService;
    private final MemberRepository memberRepository;

    @Transactional
    public String getToken(
            final TokenRequest request
    ) {
        // 인가 코드 디코딩
        String decodingCode;
        try {
            decodingCode = AesUtil.decrypt(request.code());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // 회원 정보 가져오기
        Member findMember = memberRepository.findById(UUID.fromString(decodingCode))
                .orElseThrow(MemberException.MemberNotFoundException::new);

        // 신규 회원이라면 회원 상태 (대기 -> 등록), 회원 권한 (GUEST -> MEMBER)로 수정
        // =====> 회원가입
        if (MemberStatus.WAIT.equals(findMember.getMemberStatus())) {
            findMember.updateMemberStatus(MemberStatus.REGISTRATION);
            findMember.updateAuthorization(Role.MEMBER);
            if (StringUtils.hasText(request.agreeMarket())) {
                findMember.updateAgreeMarketing(Boolean.parseBoolean(request.agreeMarket()));
            } else {
                findMember.updateAgreeMarketing(false);
            }
            if (StringUtils.hasText(request.agreeCollection())) {
                findMember.updateAgreeCollection(Boolean.parseBoolean(request.agreeCollection()));
            } else {
                findMember.updateAgreeMarketing(false);
            }
            log.info("회원가입 성공!");
        }

        // 유예 상태 회원이면 로그인 및 가입 예외 발생
        if (MemberStatus.LEAVE.equals(findMember.getMemberStatus())) {
            throw new MemberException.SuspendedMembershipException(findMember.getId());
        }

        // 액세스 토큰 + 리프레쉬 토큰 발급
        // 리프레쉬 토큰은 1회용(보안 강화)
        String accessToken = jwtService.createAccessToken(String.valueOf(findMember.getId()), findMember.getRole());
        String refreshToken = jwtService.createRefreshToken();

        // 리프레쉬 토큰 DB에 저장
        // 리프레쉬 토큰 상태 로그인으로 변경
        findMember.updateRefreshToken(refreshToken);
        findMember.updateTokenStatus(RefreshTokenStatus.LOGIN);

        // 출석 체크 & 접속 시간 업데이트
        dailyCheck(findMember);

        // 일일 방문자 수 증가
        visitorService.update();

        return accessToken + "/" + refreshToken;
    }

    /**
     * [출석 체크 메서드]
     * 당일 첫 로그인 시 화폐 5 증가
     */
    private void dailyCheck(final Member member) {
        // 오늘 첫 로그인 시 = 출석
        if (null == member.getConnectedAt() || !LocalDateTime.now().toLocalDate().isEqual(LocalDate.from(member.getConnectedAt()))) {
            // 출석 체크 시 붕어빵 지급
            member.updateFishShapedBun(member.getCurrency() + 5);
            notificationService.save(new Notification(member));
        }
        member.updateConnectedAt();
    }
}