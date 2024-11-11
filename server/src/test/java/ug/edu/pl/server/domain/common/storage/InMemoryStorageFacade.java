package ug.edu.pl.server.domain.common.storage;

import io.hypersistence.tsid.TSID;
import org.springframework.web.multipart.MultipartFile;
import ug.edu.pl.server.domain.common.storage.exception.DeletingImageException;
import ug.edu.pl.server.domain.common.storage.exception.UploadingImageException;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryStorageFacade implements StorageFacade {

    private final Map<String, byte[]> storage = new ConcurrentHashMap<>();

    @Override
    public String upload(MultipartFile file) {
        String uniqueKey = generateUniqueKey(file.getOriginalFilename());
        try {
            storage.put(uniqueKey, file.getBytes());
            return uniqueKey;
        } catch (IOException e) {
            throw new UploadingImageException();
        }
    }

    @Override
    public void delete(String key) {
        var element = storage.remove(key);
        if (element == null) {
            throw new DeletingImageException();
        }
    }

    private String generateUniqueKey(String fileName) {
        return TSID.Factory.getTsid() + "_" + fileName;
    }
}
