package ug.edu.pl.server.domain.common.persistance;

import io.hypersistence.tsid.TSID;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.Objects;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
public abstract class BaseEntity {

    @Id
    @Column(updatable = false, nullable = false)
    private Long id = TSID.Factory.getTsid().toLong();

    @Setter
    @Version
    private Long version;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @PrePersist
    public void onCreate() {
        updatedAt = createdAt = Instant.now();
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = Instant.now();
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object that) {
        return this == that || that instanceof BaseEntity
                && Objects.equals(id, ((BaseEntity) that).id);
    }
}
