package com.example.librarymanagementsystem.model.dto;

import java.util.List;

public class ImportSummary {
    private int added;        // Number of books successfully imported
    private int skipped;      // Number of books skipped (duplicates)
    private List<String> errors;  // Error messages for failed imports

    // Default constructor
    public ImportSummary() {}

    // Constructor
    public ImportSummary(int added, int skipped, List<String> errors) {
        this.added = added;
        this.skipped = skipped;
        this.errors = errors;
    }

    // Getters and setters
    public int getAdded() { return added; }
    public void setAdded(int added) { this.added = added; }

    public int getSkipped() { return skipped; }
    public void setSkipped(int skipped) { this.skipped = skipped; }

    public List<String> getErrors() { return errors; }
    public void setErrors(List<String> errors) { this.errors = errors; }
}