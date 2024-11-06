package ug.edu.pl.server.domain.common.persistance;

public interface InMemoryRepository<T extends BaseEntity> {

    default void updateTimestampsAndVersion(T entity) {
        if (entity.getCreatedAt() == null) {
            entity.onCreate();
            entity.setVersion(1L);
        } else {
            entity.onUpdate();
            entity.setVersion(entity.getVersion() + 1);
        }
    }
}
