package com.orb.gateway.common.entity.mysql;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import jakarta.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QRole is a Querydsl query type for Role
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QRole extends EntityPathBase<Role> {

    private static final long serialVersionUID = 1887496688L;

    public static final QRole role = new QRole("role");

    public final QBaseTime _super = new QBaseTime(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createDate = _super.createDate;

    public final EnumPath<Role.MemberType> roleName = createEnum("roleName", Role.MemberType.class);

    public final NumberPath<Long> roleNo = createNumber("roleNo", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updateDate = _super.updateDate;

    public QRole(String variable) {
        super(Role.class, forVariable(variable));
    }

    public QRole(Path<? extends Role> path) {
        super(path.getType(), path.getMetadata());
    }

    public QRole(PathMetadata metadata) {
        super(Role.class, metadata);
    }

}

