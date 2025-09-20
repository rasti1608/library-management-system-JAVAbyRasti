package com.example.librarymanagementsystem.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class JsonFileHandler<T> {
    private final ObjectMapper objectMapper;
    private final String filePath;
    private final TypeReference<List<T>> typeReference;
    private final CacheHelper cacheHelper;
    private final String cacheKey;

    // Update constructor to include caching
    public JsonFileHandler(String filePath, TypeReference<List<T>> typeReference, CacheHelper cacheHelper) {
        this.filePath = filePath;
        this.typeReference = typeReference;
        this.cacheHelper = cacheHelper;
        this.cacheKey = "file_" + filePath.replace("/", "_");
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        ensureFileExists();
    }

    // Read all records from JSON file
    // Optimized read with caching
    public List<T> readFromFile() {
        try {
            // 1) Open file
            java.io.File file = new java.io.File(filePath);
            if (!file.exists() || file.length() == 0) {
                System.out.println("[JsonFileHandler] File missing or empty: " + filePath);
                return new java.util.ArrayList<>();
            }

            // 2) DEBUG: show what Railway actually sees
            String raw = java.nio.file.Files.readString(file.toPath());
            System.out.println("[JsonFileHandler] Raw JSON (first 300 chars): "
                    + raw.substring(0, Math.min(300, raw.length())));

            com.fasterxml.jackson.databind.JsonNode root = objectMapper.readTree(raw);
            if (root.isArray() && root.size() > 0) {
                com.fasterxml.jackson.databind.JsonNode first = root.get(0);
                java.util.Iterator<String> it = first.fieldNames();
                StringBuilder keys = new StringBuilder();
                while (it.hasNext()) {
                    if (keys.length() > 0) keys.append(", ");
                    keys.append(it.next());
                }
                System.out.println("[JsonFileHandler] First user keys: [" + keys + "]");
                System.out.println("[JsonFileHandler] Sample values -> id=" + first.get("id")
                        + ", userId=" + first.get("userId")
                        + ", email=" + first.get("email")
                        + ", userEmail=" + first.get("userEmail")
                        + ", username=" + first.get("username")
                        + ", role=" + first.get("role"));
            }

            // 3) Parse into objects and RETURN
            List<T> data = objectMapper.readValue(file, typeReference);
            return data;

        } catch (Exception e) {
            System.out.println("[JsonFileHandler] Key-dump/read failed: " + e.getMessage());
            e.printStackTrace();
            return new java.util.ArrayList<>();
        }
    }


    // Write all records to JSON file with atomic operation
    // Optimized write with cache invalidation
    public void writeToFile(List<T> data) {
        try {
            String tempFilePath = filePath + ".tmp";
            File tempFile = new File(tempFilePath);
            File targetFile = new File(filePath);

            // Write to temp file
            objectMapper.writeValue(tempFile, data);

            // Check if target file is writable
            if (targetFile.exists() && !targetFile.canWrite()) {
                throw new RuntimeException("Target file is locked: " + filePath);
            }

            // Atomic move with Windows file handle handling
            Path tempPath = tempFile.toPath();
            Path targetPath = targetFile.toPath();

            try {
                Files.move(tempPath, targetPath, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                // Retry after brief delay for Windows file handle timing
                Thread.sleep(50);
                Files.move(tempPath, targetPath, StandardCopyOption.REPLACE_EXISTING);
            }

            // Invalidate cache after successful write
            cacheHelper.evict(cacheKey);

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Failed to write to file: " + filePath, e);
        }
    }

    // Ensure file and directory exist
    private void ensureFileExists() {
        try {
            Path path = Paths.get(filePath);
            Files.createDirectories(path.getParent());

            if (!Files.exists(path)) {
                Files.createFile(path);
                writeToFile(new ArrayList<>()); // Initialize with empty array
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to create file: " + filePath, e);
        }
    }


}