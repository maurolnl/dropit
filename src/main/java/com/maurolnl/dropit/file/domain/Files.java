package com.maurolnl.dropit.file.domain;

import java.util.List;

public class Files {
    private List<File> allFiles;

    public Files(List<File> allFiles) {
        this.allFiles = allFiles;
    }

    public List<File> getAllFiles() {
        return allFiles;
    }

    public void setAllFiles(List<File> allFiles) {
        this.allFiles = allFiles;
    }
}
