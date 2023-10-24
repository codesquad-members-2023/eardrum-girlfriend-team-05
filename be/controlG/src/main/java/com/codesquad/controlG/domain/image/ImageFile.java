package com.codesquad.controlG.domain.image;

import com.codesquad.controlG.exception.CustomRuntimeException;
import com.codesquad.controlG.exception.errorcode.ImageException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import java.util.regex.Pattern;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

public class ImageFile {

    private static final Pattern VALIDATE_EXTENSION = Pattern.compile("^(png|jpg|jpeg|svg)$");

    private final String originalFilename;
    private final String contentType;
    private final byte[] imageBytes;
    private final long fileSize;

    private ImageFile(String originalFilename, String contentType, byte[] imageBytes, long fileSize) {
        this.originalFilename = originalFilename;
        this.contentType = contentType;
        this.imageBytes = imageBytes;
        this.fileSize = fileSize;
    }

    public static ImageFile from(MultipartFile multipartFile) {
        validateFileExtension(multipartFile);
        try {
            return new ImageFile(
                    multipartFile.getOriginalFilename(),
                    multipartFile.getContentType(),
                    multipartFile.getBytes(),
                    multipartFile.getSize()
            );
        } catch (IOException ex) {
            throw new CustomRuntimeException(ImageException.UPLOAD_FAIL);
        }
    }

    private static void validateFileExtension(MultipartFile multipartFile) {
        String extension = StringUtils.getFilenameExtension(multipartFile.getOriginalFilename());
        if (extension == null || !VALIDATE_EXTENSION.matcher(extension).matches()) {
            throw new CustomRuntimeException(ImageException.INVALID_FILE_EXTENSION);
        }
    }

    public String getSavedFileName() {
        return originalFilename +
                UUID.randomUUID() +
                "." +
                StringUtils.getFilenameExtension(originalFilename);
    }

    public String getContentType() {
        return contentType;
    }

    public InputStream getInputStream() {
        return new ByteArrayInputStream(imageBytes);
    }

    public long getFileSize() {
        return fileSize;
    }
}