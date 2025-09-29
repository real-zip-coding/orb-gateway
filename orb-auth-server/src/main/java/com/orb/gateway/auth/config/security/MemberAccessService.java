package com.orb.gateway.auth.config.security;

import com.orb.gateway.auth.common.exception.Exceptions;
import com.orb.gateway.auth.entity.mysql.*;
import com.orb.gateway.auth.entity.redis.TokenBlackList;
import com.orb.gateway.auth.v1.impl.UserDetailsImpl;
import com.orb.gateway.auth.v1.model.dto.AuthInfoDTO;
import com.orb.gateway.auth.v1.model.dto.AuthMemberDeviceInfo;
import com.orb.gateway.auth.v1.model.request.SignInRequest;
import com.orb.gateway.auth.v1.model.response.MemberAccountResponse;
import com.orb.gateway.auth.v1.model.response.SearchMemberResponse;
import com.orb.gateway.auth.v1.model.response.SignInResponse;
import com.orb.gateway.auth.v1.model.response.SignupRequest;
import com.orb.gateway.auth.v1.repository.dsl.AuthProcDSLRepository;
import com.orb.gateway.auth.v1.repository.jpa.*;
import com.orb.gateway.auth.entity.redis.CheckComparedVerification;
import com.orb.gateway.auth.v1.repository.redis.CheckComparedVerificationRepository;
import com.orb.gateway.auth.v1.repository.redis.TokenBlackListRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberAccessService {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;

    //RDB Repositories
    private final MemberRepository memberRepository;
    private final MemberRoleRepository memberRoleRepository;
    private final MemberTokenRepository memberTokenRepository;
    private final MemberDeviceRepository memberDeviceRepository;
    private final MemberLoginHistoryRepository memberLoginHistoryRepository;
    private final MemberPasswordHistoryRepository memberPasswordHistoryRepository;
    private final RoleRepository roleRepository;

    //DSL
    private final AuthProcDSLRepository authProcDSLRepository;

    //Redis Repositories
    private final TokenBlackListRepository tokenBlackListRepository;

    /**
     * 사용자 로그인, 이메일과 비밀번호로 사용자를 인증합니다.
     *
     * @param email    사용자의 이메일.
     * @param password 사용자의 비밀번호.
     * @return Access Token과 Refresh Token이 포함된 AuthenticationToken.
     * @throws Exceptions.BadCredentialsException 잘못된 자격 증명인 경우 예외를 발생시킵니다.
     */
    @Transactional
    public SignInResponse signIn(String email, String password, AuthMemberDeviceInfo deviceInfo) {
        try {
            AuthenticationToken token = this.authCheckProc(email, password, deviceInfo);
            SearchMemberResponse member = this.findMemberInfoByEmail(email);
            return SignInResponse.of(token, member);

        } catch (BadCredentialsException ex) {
            throw new Exceptions.BadCredentialsException("아이디 또는 비밀번호를 잘못 입력했습니다.");
        }
    }

    /**
     * Access Token과 Refresh Token을 사용하여 새 Access Token을 재발급 합니다.
     *
     * @param accessTokenInHeader  요청 헤더에서 받은 Access Token.
     * @param refreshTokenInHeader 요청에서 받은 Refresh Token.
     * @return 새로 발급된 Access Token과 Refresh Token이 포함된 AuthenticationToken.
     * @throws Exceptions.JwtTokenException 잘못된 Refresh Token인 경우 예외를 발생시킵니다.
     */
    @Transactional
    public AuthenticationToken reissue(String accessTokenInHeader, String refreshTokenInHeader, AuthMemberDeviceInfo deviceInfo) {
        String requestAccessToken = this.resolveTokenProc(accessTokenInHeader);
        Claims claims = jwtTokenProvider.getClaim(requestAccessToken);
        MemberAuthToken token = this.findMemberTokenByClaims(claims);
        AuthInfoDTO authInfoDTO = AuthInfoDTO.fromClaims(claims);

        // Refresh Token 유효성 검사 및 비교.
        if (!token.getAuthToken().equals(refreshTokenInHeader) || !jwtTokenProvider.validateToken(refreshTokenInHeader))
            throw new Exceptions.JwtTokenException("Invalid Refresh Token");

        // 새 Access Token을 생성하고 Refresh Token을 업데이트합니다.
        AuthenticationToken tokenResponse = jwtTokenProvider.createToken(authInfoDTO.getEmail(), deviceInfo);
        token.updateToken(tokenResponse.getRefreshToken());

        // accessToken 블랙리스트 등록
        tokenBlackListRepository.save(TokenBlackList.to(requestAccessToken));

        return tokenResponse;
    }

    /**
     * 사용자를 로그아웃 처리합니다. Access Token에 해당하는 Refresh Token을 삭제합니다.
     *
     * @param accessTokenInHeader 요청 헤더에서 받은 Access Token(Bearer ${access-token}).
     */
    @Transactional
    public void signOut(String accessTokenInHeader) {
        String accessToken = this.resolveTokenProc(accessTokenInHeader);
        Claims claims = jwtTokenProvider.getClaim(accessToken);
        AuthInfoDTO authInfoDTO = AuthInfoDTO.fromClaims(claims);
        long affectCnt = authProcDSLRepository.deleteRefreshToken(authInfoDTO.getEmail());
        if (affectCnt < 1)
            log.warn("failed to delete refresh-token data with email: {}", authInfoDTO.getEmail());

        // accessToken 블랙리스트 등록
        tokenBlackListRepository.save(TokenBlackList.builder().accessToken(accessToken).build());
    }

    /**
     * 사용자 인증 객체로 토큰을 조회합니다.
     *
     * @param claims 사용자 인증객체.
     * @return 해당 이메일에 대한 Token 객체.
     * @throws Exceptions.NotFoundData 이메일로 조회된 Refresh Token이 없을 경우 예외를 발생시킵니다.
     */
    public MemberAuthToken findMemberTokenByClaims(Claims claims) {
        AuthInfoDTO authInfoDTO = AuthInfoDTO.fromClaims(claims);
        return authProcDSLRepository.findRefreshTokenByEmail(authInfoDTO.getEmail()).orElseThrow(
                () -> new Exceptions.JwtTokenException("Invalid Access Token - email:" + authInfoDTO.getEmail())
        );
    }

    /**
     * 인증 객체를 통해 사용자 정보를 조회합니다.
     *
     * @return 해당 이메일에 대한 Member 객체.
     * @throws Exceptions.NotFoundAuth 권한정보가 확인되지 않을 경우 예외를 발생시킵니다.
     */
    public SearchMemberResponse findMemberInfoByAccessToken() {
        String email = this.getEmailToSession();
        SearchMemberResponse searchMemberResponse = this.findMemberInfoByEmail(email);
        if (searchMemberResponse == null)
            throw new UsernameNotFoundException("User not found with email: " + email);

        return searchMemberResponse;
    }

    /**
     * 이메일로 사용자 가입여부 조회
     */
    public SearchMemberResponse findMemberByEmail(String email) {
        Optional<Member> member = memberRepository.findMemberEntityByEmail(email);
        if (member.isPresent()) {
            Member m = member.get();
            return SearchMemberResponse.builder()
                    .signed(true)
                    .authType(m.getAuthType())
                    .status(Member.MemberStatus.of(m.getMemberStatus()))
                    .build();
        }

        return SearchMemberResponse.builder()
                .signed(false)
                .build();
    }

    /**
     * 이메일로 사용자 정보를 조회합니다.
     *
     * @param email 사용자의 이메일.
     * @return 해당 이메일에 대한 Member 객체.
     */
    public SearchMemberResponse findMemberInfoByEmail(String email) {
        Optional<Member> member = memberRepository.findMemberEntityByEmailAndMemberStatus(email, Member.MemberStatus.ACTIVE.getCode());
        if (!member.isEmpty()) {
            return SearchMemberResponse.of(member.get());
        }
        return SearchMemberResponse.builder().signed(false).build();
    }

    /**
     * 사용자 정보를 저장하고 인증 토큰을 반환합니다.
     *
     * @param signupRequest 사용자 등록 요청 객체.
     * @return Access Token과 Refresh Token이 포함된 AuthenticationToken.
     * @throws Exceptions.DuplicateUserAuthException 이미 등록된 사용자인 경우 예외를 발생시킵니다.
     * @throws Exceptions.BadCredentialsException 잘못된 자격 증명인 경우 예외를 발생시킵니다.
     */
    @Transactional
    public SignInResponse signUp(SignupRequest signupRequest) {
        Optional<Member> checkMember = memberRepository.findMemberEntityByEmailAndMemberStatus(signupRequest.email(), Member.MemberStatus.ACTIVE.getCode());
        if (checkMember.isPresent())
            throw new Exceptions.DuplicateUserAuthException("requested email address is already registered. - Email:" + signupRequest.email(), checkMember.get().getAuthType());

        // 회원 정보를 저장하기 위한 Member 객체 생성 및 저장.
        Member member = signupRequest.toMemberEntity();
        this.initializeMember(member);

        // 저장된 사용자 정보를 기반으로 인증 토큰 생성.
        try {
            AuthenticationToken token = this.authCheckProc(signupRequest.email(), signupRequest.password(), null);
            SearchMemberResponse memberRes = this.findMemberInfoByEmail(signupRequest.email());
            return SignInResponse.of(token, memberRes);
        } catch (BadCredentialsException ex) {
            throw new Exceptions.BadCredentialsException("아이디 또는 비밀번호를 잘못 입력했습니다.");
        }
    }

    /**
     * 회원가입시 멤버 정보 초기화 및 default 데이터 생성
     *
     * @param member 멤버 객체
     * @throws Exceptions.IllegalArgumentException 권한 정보가 없을 경우 예외를 발생시킵니다.
     */
    public void initializeMember(Member member) {
        memberRepository.save(member);
        Role role = roleRepository.findPermissionByRoleName(Role.MemberType.UNVERIFIED).orElseThrow(
                () -> new Exceptions.IllegalArgumentException("permission not found error: " + Role.MemberType.UNVERIFIED)
        );

        if (member.getAuthType().equals(Member.AuthType.EMAIL))
            memberPasswordHistoryRepository.save(
                    MemberPasswordHistory.builder()
                            .member(member)
                            .password(member.getPassword())
                            .build()
            );

        // 멤버 권한 정보 저장
        memberRoleRepository.save(
                MemberRoleBridge.builder()
                        .member(member)
                        .role(role)
                        .build()
        );
    }

    /**
     * 단말인증 정보 조회 및 삭제
     */
    public void resolveDeviceAuth(Member member, AuthMemberDeviceInfo deviceInfo) {
        memberLoginHistoryRepository.save(deviceInfo.toMemberLoginHistoryEntity(member));
        MemberDevice memberDeviceInfo = memberDeviceRepository.findByMemberNo(member.getMemberNo()).orElse(null);

        //요청 단말과 등록된 단말 정보가 다르다면 삭제
        if (memberDeviceInfo != null && !deviceInfo.appUid().equals(memberDeviceInfo.getAppUid())) {
            log.info("delete device auth - memberNo:{}, appUid(saved):{}, appUid(requested):{}", member.getMemberNo(), memberDeviceInfo.getAppUid(), deviceInfo.appUid());
            memberDeviceRepository.delete(memberDeviceInfo);
        }
    }

    /**
     * 사용자의 Access Token과 Refresh Token을 생성합니다.
     *
     * @param email 사용자의 이메일.
     * @return 새로 생성된 Access Token과 Refresh Token이 포함된 AuthenticationToken.
     */
    @Transactional
    public AuthenticationToken generateTokenProc(String email, AuthMemberDeviceInfo deviceInfo) {
        AuthenticationToken authenticationToken = jwtTokenProvider.createToken(email, deviceInfo);
        Optional<MemberAuthToken> authToken = authProcDSLRepository.findRefreshTokenByEmail(email);
        //AuthToken upsert process
        if (authToken.isPresent())
            authToken.get().updateToken(authenticationToken.getRefreshToken());
        else {
            Member member = memberRepository.findMemberEntityByEmailAndMemberStatus(email, Member.MemberStatus.ACTIVE.getCode()).orElseThrow(Exceptions.NotFoundAuth::new);
            memberTokenRepository.save(
                    MemberAuthToken.builder()
                            .member(member)
                            .authType(MemberAuthToken.AuthType.JWT_REFRESH)
                            .authToken(authenticationToken.getRefreshToken())
                            .build());
        }

        return authenticationToken;
    }

    /**
     * 이메일과 비밀번호를 기반으로 인증 절차를 수행하고 인증 토큰을 반환합니다.
     *
     * @param email    사용자의 이메일.
     * @param password 사용자의 비밀번호.
     * @return Access Token과 Refresh Token이 포함된 AuthenticationToken.
     * @throws Exceptions.NotFoundAuth 잘못된 자격 증명인 경우 예외를 발생시킵니다.
     */
    private AuthenticationToken authCheckProc(String email, String password, AuthMemberDeviceInfo deviceInfo) {
        log.info("Password to BCryptPasswordEncoder - email:{}, pw:{}", email, new BCryptPasswordEncoder().encode(password));
        // 1. 이메일과 비밀번호를 기반으로 Authentication 객체 생성.
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email, password);

        // 2. 실제 검증(비밀번호 체크) 진행. CustomUserDetailService.java > loadUserByUsername()
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // 3. 인증 정보를 기반으로 JWT 토큰 생성.
        if (authentication.isAuthenticated()) {
            // 4. 단말인증 조회
            if (!ObjectUtils.isEmpty(deviceInfo)) {
                UserDetailsImpl principal = (UserDetailsImpl) authentication.getPrincipal();
                this.resolveDeviceAuth(principal.getMember(), deviceInfo);
            }
            return this.generateTokenProc(email, deviceInfo);
        } else {
            log.warn("User authentication failed. - email:{}", email);
            throw new Exceptions.NotFoundAuth();
        }
    }

    /**
     * 요청 헤더에서 Access Token을 추출합니다.
     *
     * @param accessTokenInHeader 요청 헤더에서 받은 Access Token.
     * @return 추출한 Access Token 또는 null.
     */
    private String resolveTokenProc(String accessTokenInHeader) {
        if (accessTokenInHeader != null && accessTokenInHeader.startsWith("Bearer "))
            return accessTokenInHeader.substring(7);
        else
            return null;
    }

    /**
     * 6자리 난수 인증 코드를 생성합니다.
     *
     * @return 6자리 인증 코드 문자열.
     */
    private String getRandomVerificationCode() {
        Random random = new Random();
        int randomCode = 100000 + random.nextInt(900000); // 6자리 난수 생성
        return String.format("%06d", randomCode); // 6자리 LPadding
    }

    /**
     * 인증된 사용자의 이메일을 보안 컨텍스트에서 가져옵니다.
     *
     * @return 인증된 사용자의 이메일.
     * @throws Exceptions.NotFoundAuth 사용자가 인증되지 않은 경우 예외를 발생시킵니다.
     */
    private String getEmailToSession() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication(); // 인증사용자 정보반환
        if (authentication == null || !authentication.isAuthenticated())
            throw new Exceptions.NotFoundAuth();
        return authentication.getName();
    }

    /**
     * 이메일 인증 템플릿을 로드 및 포맷
     *
     * @return 본문
     */
    private String verificationEmailFormat(String title, String code) {
        try {
            // 클래스패스 기준으로 리소스 로드
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("templates/email/verification.html");
            if (inputStream == null) {
                throw new Exceptions.IllegalArgumentException("Email template not found");
            }

            String template = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            return template
                    .replace("{title}", title)
                    .replace("{code}", code);
        } catch (Exception e) {
            log.warn("Email template not found");
            return "Verification Code: " + code; // Fallback in case of error
        }
    }

}