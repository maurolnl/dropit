package com.maurolnl.dropit.file.domain;

import java.util.Optional;

public interface IFileRepository {
    boolean insertFileMetadata(File metadata);
    Files selectAllFilesMetadata();
    boolean updateFileMetadata(String actualName, String newName);
    Optional<File> getFileMetadata(String fileName);
    boolean deleteFileMetadata(String fileName);
}
