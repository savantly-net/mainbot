package net.savantly.mainbot.dom.auditing;

import java.time.Instant;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AuditedEntity {

    @CreatedDate
    @Column(name = "created_date")
    private Instant createdDate;

    @CreatedBy
    @Column(name = "created_by", length = 50)
    private String createdBy;

    @LastModifiedDate
    @Column(name = "last_modifed_date")
    private Instant lastModifiedDate;

    @LastModifiedBy
    @Column(name = "last_modified_by", length = 50)
    private String updatedBy;

    @Column(name = "deleted_date")
    private Instant deletedDate;

    @Column(name = "is_deleted", length = 50)
    private Boolean isDeleted = false;

    @PreUpdate
    @PrePersist
    public void beforeAnyUpdate() {
        if (isDeleted != null && isDeleted) {

            if (getDeletedDate() == null) {
                deletedDate = Instant.now();
            }
        }
    }

}