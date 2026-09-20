package vn.tuan.categoryswaggerapi.service;

import java.nio.file.Path;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface IStorageService {

    void init();

    void delete(String storedFilename);

    Path load(String filename);

    Resource loadAsResource(String filename);

    void store(
            MultipartFile file,
            String storedFilename
    );

    String getStorageFilename(
            MultipartFile file,
            String id
    );
}