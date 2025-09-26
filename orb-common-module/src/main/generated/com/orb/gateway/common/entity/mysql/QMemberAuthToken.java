package com.orb.gateway.common.entity.mysql;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import jakarta.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMemberAuthToken is a Querydsl query type for MemberAuthToken
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMemberAuthToken extends EntityPathBase<MemberAuthToken> {

    private static final long serialVersionUID = -1109336579L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMemberAuthToken memberAuthToken = new QMemberAuthToken("memberAuthToken");

    public final QBaseTime _super = new QBaseTime(this);

    public final StringPath authToken = createString("authToken");

    public final NumberPath<Long> authTokenNo = createNumber("authTokenNo", Long.class);

    public final EnumPath<MemberAuthToken.AuthType> authType = createEnum("authType", MemberAuthToken.AuthType.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createDate = _super.createDate;

    public final QMember member;

    public final NumberPath<Integer> modifyCount = createNumber("modifyCount", Integer.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updateDate = _super.updateDate;

    public QMemberAuthToken(String variable) {
        this(MemberAuthToken.class, forVariable(variable), INITS);
    }

    public QMemberAuthToken(Path<? extends MemberAuthToken> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMemberAuthToken(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMemberAuthToken(PathMetadata metadata, PathInits inits) {
        this(MemberAuthToken.class, metadata, inits);
    }

    public QMemberAuthToken(Class<? extends MemberAuthToken> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new QMember(forProperty("member")) : null;
    }

}

