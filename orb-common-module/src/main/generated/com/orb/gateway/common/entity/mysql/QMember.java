package com.orb.gateway.common.entity.mysql;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import jakarta.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QMember is a Querydsl query type for Member
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMember extends EntityPathBase<Member> {

    private static final long serialVersionUID = 1255767444L;

    public static final QMember member = new QMember("member1");

    public final QBaseTime _super = new QBaseTime(this);

    public final EnumPath<Member.AuthType> authType = createEnum("authType", Member.AuthType.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createDate = _super.createDate;

    public final StringPath email = createString("email");

    public final StringPath imgUri = createString("imgUri");

    public final NumberPath<Long> memberNo = createNumber("memberNo", Long.class);

    public final NumberPath<Integer> memberStatus = createNumber("memberStatus", Integer.class);

    public final StringPath name = createString("name");

    public final StringPath nickname = createString("nickname");

    public final DateTimePath<java.time.LocalDateTime> notificationCheck = createDateTime("notificationCheck", java.time.LocalDateTime.class);

    public final StringPath password = createString("password");

    public final StringPath phoneNumber = createString("phoneNumber");

    public final StringPath ssn = createString("ssn");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updateDate = _super.updateDate;

    public QMember(String variable) {
        super(Member.class, forVariable(variable));
    }

    public QMember(Path<? extends Member> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMember(PathMetadata metadata) {
        super(Member.class, metadata);
    }

}

