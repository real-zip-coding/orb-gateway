package com.orb.gateway.common.entity.mysql;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Data
public abstract class BaseTime {
    @CreatedDate
    @Column(name = "create_date", updatable = false, nullable = false, columnDefinition = "DATETIME(3)")
    private LocalDateTime createDate;

    @LastModifiedDate
    @Column(name = "update_date", insertable = false, columnDefinition = "DATETIME(3)")
    private LocalDateTime updateDate;
}