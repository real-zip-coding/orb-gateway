package com.orb.gateway.common.entity.mysql;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import jakarta.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMemberDevice is a Querydsl query type for MemberDevice
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMemberDevice extends EntityPathBase<MemberDevice> {

    private static final long serialVersionUID = 2113014026L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMemberDevice memberDevice = new QMemberDevice("memberDevice");

    public final QBaseTime _super = new QBaseTime(this);

    public final StringPath appUid = createString("appUid");

    public final StringPath buildVersion = createString("buildVersion");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createDate = _super.createDate;

    public final QMember member;

    public final NumberPath<Long> memberDeviceNo = createNumber("memberDeviceNo", Long.class);

    public final NumberPath<Long> memberNo = createNumber("memberNo", Long.class);

    public final EnumPath<com.orb.gateway.common.constraint.OsType> osType = createEnum("osType", com.orb.gateway.common.constraint.OsType.class);

    public final StringPath osVersion = createString("osVersion");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updateDate = _super.updateDate;

    public QMemberDevice(String variable) {
        this(MemberDevice.class, forVariable(variable), INITS);
    }

    public QMemberDevice(Path<? extends MemberDevice> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMemberDevice(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMemberDevice(PathMetadata metadata, PathInits inits) {
        this(MemberDevice.class, metadata, inits);
    }

    public QMemberDevice(Class<? extends MemberDevice> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new QMember(forProperty("member")) : null;
    }

}

