package com.orb.gateway.auth.v1.repository.dsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import com.orb.gateway.auth.entity.mysql.Member;
import com.orb.gateway.auth.entity.mysql.MemberAuthToken;
import org.springframework.stereotype.Repository;
import com.orb.gateway.auth.v1.model.dto.AuthInfoDTO;
import com.orb.gateway.auth.v1.model.dto.PermissionDTO;

import java.util.List;
import java.util.Optional;

import static com.querydsl.core.types.Projections.fields;
import static com.orb.gateway.auth.entity.mysql.QMemberAuthToken.memberAuthToken;
import static com.orb.gateway.auth.entity.mysql.QMember.member;
import static com.orb.gateway.auth.entity.mysql.QRole.role;
import static com.orb.gateway.auth.entity.mysql.QMemberDevice.memberDevice;
import static com.orb.gateway.auth.entity.mysql.QMemberRoleBridge.memberRoleBridge;

@Repository
@RequiredArgsConstructor
public class AuthProcDSLRepository {
	private final JPAQueryFactory queryFactory;

    public Optional<AuthInfoDTO> findMemberByEmailForAuthProc(String email) {
		return Optional.ofNullable(
				queryFactory
						.select(fields(
								AuthInfoDTO.class,
								member.memberNo,
								member.email,
								role.roleName,
								memberDevice.appUid,
								memberAuthToken.modifyCount.coalesce(0).as("modifyCount")
						))
						.from(member)
						.join(memberRoleBridge).on(member.eq(memberRoleBridge.member))
						.join(role).on(memberRoleBridge.role.eq(role))
                        .leftJoin(memberDevice).on(member.eq(memberDevice.member))
						.leftJoin(memberAuthToken).on(member.eq(memberAuthToken.member))
						.where(member.email.eq(email)
								.and(member.memberStatus.eq(Member.MemberStatus.ACTIVE.getCode())))
						.orderBy(memberRoleBridge.memberRoleBridgeNo.desc())
						.limit(1)
						.fetchOne()
		);
	}

	public List<PermissionDTO> findRoleByUserId(long userId) {
		return queryFactory
				.select(fields(
						PermissionDTO.class,
						role.roleNo.as("permissionNo"),
						role.roleName.as("permissionName"),
						memberDevice.appUid
				))
				.from(memberRoleBridge)
				.join(role).on(memberRoleBridge.role.eq(role))
				.join(member).on(memberRoleBridge.member.eq(member))
				.leftJoin(memberDevice).on(member.eq(memberDevice.member))
				.where(memberRoleBridge.member.memberNo.eq(userId))
				.fetch();
	}

	public Optional<MemberAuthToken> findRefreshTokenByEmail(String email) {
		return Optional.ofNullable(
				queryFactory
                .selectFrom(memberAuthToken)
                .join(member).on(member.memberNo.eq(memberAuthToken.member.memberNo))
                .where(member.email.eq(email)
						.and(member.memberStatus.eq(Member.MemberStatus.ACTIVE.getCode()))
						.and(memberAuthToken.authType.eq(MemberAuthToken.AuthType.JWT_REFRESH)))
				.orderBy(memberAuthToken.authTokenNo.desc())
				.limit(1)
                .fetchOne()
		);
	}

    public Optional<MemberAuthToken> findAppPushTokenByMemberNo(Long memberNo) {
        return Optional.ofNullable(
                queryFactory
                        .selectFrom(memberAuthToken)
                        .join(member).on(member.memberNo.eq(memberAuthToken.member.memberNo))
                        .where(member.memberNo.eq(memberNo)
                                .and(memberAuthToken.authType.eq(MemberAuthToken.AuthType.APP_PUSH)))
                        .orderBy(memberAuthToken.authTokenNo.desc())
                        .limit(1)
                        .fetchOne()
        );
    }

	public long deleteRefreshToken(String email) {
		MemberAuthToken authToken = this.findRefreshTokenByEmail(email).orElseThrow(
				() -> new IllegalArgumentException("not found user info by email: " + email)
		);

		return queryFactory.delete(memberAuthToken)
				.where(memberAuthToken.eq(authToken))
				.execute();
	}
}