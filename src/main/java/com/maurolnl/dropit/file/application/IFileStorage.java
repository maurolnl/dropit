package com.maurolnl.dropit.file.application;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface IFileStorage {
    boolean store(MultipartFile file);
    Resource loadFile(String filename);
    boolean deleteFile(String filename);
    boolean updateFile(String actualName, String newName);
    //public Stream<Path> loadFiles();
}
