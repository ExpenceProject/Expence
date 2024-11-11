package ug.edu.pl.server.domain.common.storage;

import org.springframework.web.multipart.MultipartFile;

public interface StorageFacade {
    String upload(MultipartFile file);

    void delete(String key);
}
