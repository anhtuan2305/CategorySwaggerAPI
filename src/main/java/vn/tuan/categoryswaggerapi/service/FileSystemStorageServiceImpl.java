package vn.tuan.categoryswaggerapi.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.apache.commons.io.FilenameUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import vn.tuan.categoryswaggerapi.config.StorageProperties;
import vn.tuan.categoryswaggerapi.exception.StorageException;
import vn.tuan.categoryswaggerapi.exception.StorageFileNotFoundException;

@Service
public class FileSystemStorageServiceImpl
        implements IStorageService {

    private final Path rootLocation;

    public FileSystemStorageServiceImpl(
            StorageProperties properties) {

        if (properties.getLocation() == null
                || properties.getLocation().isBlank()) {

            throw new StorageException(
                    "Vị trí lưu file không được để trống."
            );
        }

        this.rootLocation =
                Paths.get(properties.getLocation())
                        .toAbsolutePath()
                        .normalize();
    }

    @PostConstruct
    @Override
    public void init() {

        try {

            Files.createDirectories(rootLocation);

        } catch (IOException exception) {

            throw new StorageException(
                    "Không thể tạo thư mục lưu file.",
                    exception
            );
        }
    }

    @Override
    public void store(
            MultipartFile file,
            String storedFilename) {

        if (file == null || file.isEmpty()) {

            throw new StorageException(
                    "Không thể lưu file rỗng."
            );
        }

        if (storedFilename == null
                || storedFilename.isBlank()) {

            throw new StorageException(
                    "Tên file lưu trữ không hợp lệ."
            );
        }

        Path destinationFile =
                rootLocation
                        .resolve(storedFilename)
                        .normalize();

        if (!destinationFile.startsWith(rootLocation)) {

            throw new StorageException(
                    "Không thể lưu file bên ngoài thư mục cho phép."
            );
        }

        try (InputStream inputStream =
                     file.getInputStream()) {

            Files.copy(
                    inputStream,
                    destinationFile,
                    StandardCopyOption.REPLACE_EXISTING
            );

        } catch (IOException exception) {

            throw new StorageException(
                    "Không thể lưu file "
                            + storedFilename,
                    exception
            );
        }
    }

    @Override
    public Path load(String filename) {

        if (filename == null
                || filename.isBlank()) {

            throw new StorageFileNotFoundException(
                    "Tên file không hợp lệ."
            );
        }

        Path file =
                rootLocation
                        .resolve(filename)
                        .normalize();

        if (!file.startsWith(rootLocation)) {

            throw new StorageFileNotFoundException(
                    "Đường dẫn file không hợp lệ."
            );
        }

        return file;
    }

    @Override
    public Resource loadAsResource(
            String filename) {

        try {

            Path file = load(filename);

            Resource resource =
                    new UrlResource(
                            file.toUri()
                    );

            if (resource.exists()
                    && resource.isReadable()) {

                return resource;
            }

            throw new StorageFileNotFoundException(
                    "Không thể đọc file: "
                            + filename
            );

        } catch (MalformedURLException exception) {

            throw new StorageFileNotFoundException(
                    "Không thể đọc file: "
                            + filename,
                    exception
            );
        }
    }

    @Override
    public void delete(String storedFilename) {

        if (storedFilename == null
                || storedFilename.isBlank()) {

            return;
        }

        try {

            Path file = load(storedFilename);

            Files.deleteIfExists(file);

        } catch (IOException exception) {

            throw new StorageException(
                    "Không thể xóa file: "
                            + storedFilename,
                    exception
            );
        }
    }

    @Override
    public String getStorageFilename(
            MultipartFile file,
            String id) {

        if (file == null || file.isEmpty()) {

            throw new StorageException(
                    "File tải lên không hợp lệ."
            );
        }

        String originalFilename =
                file.getOriginalFilename();

        String extension =
                FilenameUtils.getExtension(
                        originalFilename
                );

        String safeId =
                id == null
                        ? String.valueOf(
                                System.currentTimeMillis()
                        )
                        : id.replaceAll(
                                "[^a-zA-Z0-9-]",
                                ""
                        );

        if (extension == null
                || extension.isBlank()) {

            return safeId;
        }

        return safeId
                + "."
                + extension.toLowerCase();
    }
}