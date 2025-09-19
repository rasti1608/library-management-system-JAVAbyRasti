package com.example.librarymanagementsystem.util;

import java.util.UUID;

public class UuidGenerator {

    // Generate a random UUID string for entity IDs
    public static String generate() {
        return UUID.randomUUID().toString();
    }
}