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
            String raw;

            // 1) Try classpath (works inside the JAR on Railway)
            try (java.io.InputStream is =
                         new org.springframework.core.io.ClassPathResource(filePath).getInputStream()) {
                raw = new String(is.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
                System.out.println("[JsonFileHandler] Loaded from CLASSPATH: " + filePath);
            } catch (Exception miss) {
                // 2) Fallback to external file (works locally if you run from project root)
                java.io.File file = new java.io.File(filePath);
                System.out.println("[JsonFileHandler] Classpath miss, trying FILE: " + file.getAbsolutePath());
                if (!file.exists() || file.length() == 0) {
                    System.out.println("[JsonFileHandler] File missing or empty: " + file.getAbsolutePath());
                    return new java.util.ArrayList<>();
                }
                raw = java.nio.file.Files.readString(file.toPath());
            }

            // DEBUG: show what we actually read
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

            // 3) Parse into objects and return
            // NOTE: parse from the same 'raw' we inspected so classpath works
            java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(raw.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            List<T> data = objectMapper.readValue(bin, typeReference);
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