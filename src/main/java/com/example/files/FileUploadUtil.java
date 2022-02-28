package com.example.files;

import java.io.*;
import java.nio.file.*;

import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.web.multipart.MultipartFile;

public class FileUploadUtil {

    public static void saveFile(String uploadDir, String fileName, MultipartFile multipartFile) throws IOException {
        Path uploadPath = Paths.get(uploadDir); // создаём объект пути к файлу
        if (!Files.exists(uploadPath)) { // при отсутствии директории - создаём её
            Files.createDirectories(uploadPath);
        }
        FileUtils.cleanDirectory(uploadPath.toFile());
        try (InputStream inputStream = multipartFile.getInputStream()) {
            Path filePath = uploadPath.resolve(fileName); // это уже путь к непосредственно файлу
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ioe) {
            throw new IOException("Could not save image file: " + fileName, ioe);
        }
    }


}
