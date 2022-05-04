package org.enkrip.lungo.core.data;

import java.time.Instant;
import java.util.Optional;

import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.domain.Auditable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@MappedSuperclass
@EntityListeners({
    AuditingEntityListener.class
})
public abstract class AuditableEntity implements Auditable<String, Long, Instant> {
    @Id
    @GeneratedValue(generator = "flake")
    @GenericGenerator(name = "flake", strategy = "org.enkrip.lungo.core.data.FlakeIdentifierGenerator")
    private Long id;
    private String createdBy;
    private Instant createdDate;
    private String lastModifiedBy;
    private Instant lastModifiedDate;

    @Override
    public Long getId() {
        return id;
    }

    public AuditableEntity setId(Long id) {
        this.id = id;
        return this;
    }

    @Override
    public Optional<String> getCreatedBy() {
        return Optional.ofNullable(createdBy);
    }

    @Override
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public Optional<Instant> getCreatedDate() {
        return Optional.ofNullable(createdDate);
    }

    @Override
    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    public Optional<String> getLastModifiedBy() {
        return Optional.ofNullable(lastModifiedBy);
    }

    @Override
    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    @Override
    public Optional<Instant> getLastModifiedDate() {
        return Optional.ofNullable(lastModifiedDate);
    }

    @Override
    public void setLastModifiedDate(Instant lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    @Transient
    @Override
    public boolean isNew() {
        return Optional.ofNullable(id).isPresent();
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
