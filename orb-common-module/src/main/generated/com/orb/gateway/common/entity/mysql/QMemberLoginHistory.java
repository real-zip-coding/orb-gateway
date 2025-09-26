package com.orb.gateway.common.entity.mysql;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import jakarta.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMemberLoginHistory is a Querydsl query type for MemberLoginHistory
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMemberLoginHistory extends EntityPathBase<MemberLoginHistory> {

    private static final long serialVersionUID = -637261665L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMemberLoginHistory memberLoginHistory = new QMemberLoginHistory("memberLoginHistory");

    public final QBaseTime _super = new QBaseTime(this);

    public final StringPath appUid = createString("appUid");

    public final StringPath buildVersion = createString("buildVersion");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createDate = _super.createDate;

    public final StringPath loginType = createString("loginType");

    public final QMember member;

    public final NumberPath<Long> memberLoginHistoryNo = createNumber("memberLoginHistoryNo", Long.class);

    public final EnumPath<com.orb.gateway.common.constraint.OsType> osType = createEnum("osType", com.orb.gateway.common.constraint.OsType.class);

    public final StringPath osVersion = createString("osVersion");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updateDate = _super.updateDate;

    public QMemberLoginHistory(String variable) {
        this(MemberLoginHistory.class, forVariable(variable), INITS);
    }

    public QMemberLoginHistory(Path<? extends MemberLoginHistory> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMemberLoginHistory(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMemberLoginHistory(PathMetadata metadata, PathInits inits) {
        this(MemberLoginHistory.class, metadata, inits);
    }

    public QMemberLoginHistory(Class<? extends MemberLoginHistory> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new QMember(forProperty("member")) : null;
    }

}

