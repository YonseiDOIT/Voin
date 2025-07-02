package com.voin.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.UUID;

@Slf4j
@Component
public class ImageUtil {

    // Ubuntu 서버의 고정 이미지 저장 경로
    private static final String IMAGE_STORAGE_PATH = "/home/ubuntu/voin/images/profiles";
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    /**
     * Base64 이미지 데이터를 파일로 저장하고 URL을 반환
     */
    public String saveBase64Image(String base64Data, String originalFileName) throws IOException {
        // Base64 데이터 검증
        if (base64Data == null || !base64Data.startsWith("data:image/")) {
            throw new IllegalArgumentException("유효하지 않은 이미지 데이터입니다.");
        }

        // Base64 데이터에서 실제 이미지 데이터 추출
        String[] parts = base64Data.split(",");
        if (parts.length != 2) {
            throw new IllegalArgumentException("유효하지 않은 Base64 이미지 형식입니다.");
        }

        // 파일 형식 추출 (예: data:image/jpeg)
        String fileType = parts[0].split("/")[1].split(";")[0];
        
        // 이미지 데이터 디코딩
        byte[] imageData = Base64.getDecoder().decode(parts[1]);

        // 파일 크기 검증
        if (imageData.length > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("파일 크기는 5MB를 초과할 수 없습니다.");
        }

        // 업로드 디렉토리 생성
        createUploadDirectoryIfNotExists();

        // 파일명 생성 (UUID + 원본 확장자)
        String fileName = generateFileName(originalFileName, fileType);
        String filePath = IMAGE_STORAGE_PATH + File.separator + fileName;
        
        // 파일 저장
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            fos.write(imageData);
        }

        log.info("이미지 파일 저장 완료: {}", filePath);
        
        // 상대 URL 반환 (DB에 저장될 경로)
        return "/images/profiles/" + fileName;
    }

    /**
     * 업로드 디렉토리 생성
     */
    private void createUploadDirectoryIfNotExists() throws IOException {
        Path uploadPath = Paths.get(IMAGE_STORAGE_PATH);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
            log.info("업로드 디렉토리 생성: {}", uploadPath);
        }
    }

    /**
     * 고유한 파일명 생성
     */
    private String generateFileName(String originalFileName, String fileType) {
        String extension = StringUtils.getFilenameExtension(originalFileName);
        if (extension == null) {
            extension = fileType;
        }
        return UUID.randomUUID().toString() + "." + extension;
    }

    /**
     * 이미지 파일 형식 검증
     */
    public boolean isValidImageFile(String fileName) {
        String extension = StringUtils.getFilenameExtension(fileName);
        if (extension == null) {
            return false;
        }
        extension = extension.toLowerCase();
        return extension.equals("jpg") || extension.equals("jpeg") 
            || extension.equals("png") || extension.equals("gif");
    }
} 