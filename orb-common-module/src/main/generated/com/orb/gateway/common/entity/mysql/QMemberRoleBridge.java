package com.orb.gateway.common.entity.mysql;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import jakarta.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMemberRoleBridge is a Querydsl query type for MemberRoleBridge
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMemberRoleBridge extends EntityPathBase<MemberRoleBridge> {

    private static final long serialVersionUID = -1053372333L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMemberRoleBridge memberRoleBridge = new QMemberRoleBridge("memberRoleBridge");

    public final QBaseTime _super = new QBaseTime(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createDate = _super.createDate;

    public final QMember member;

    public final NumberPath<Long> memberRoleBridgeNo = createNumber("memberRoleBridgeNo", Long.class);

    public final QRole role;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updateDate = _super.updateDate;

    public QMemberRoleBridge(String variable) {
        this(MemberRoleBridge.class, forVariable(variable), INITS);
    }

    public QMemberRoleBridge(Path<? extends MemberRoleBridge> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMemberRoleBridge(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMemberRoleBridge(PathMetadata metadata, PathInits inits) {
        this(MemberRoleBridge.class, metadata, inits);
    }

    public QMemberRoleBridge(Class<? extends MemberRoleBridge> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new QMember(forProperty("member")) : null;
        this.role = inits.isInitialized("role") ? new QRole(forProperty("role")) : null;
    }

}

