package com.maurolnl.dropit.file.infrastructure;

import com.maurolnl.dropit.file.domain.File;
import com.maurolnl.dropit.file.domain.Files;
import com.maurolnl.dropit.file.domain.IFileRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository("InMemoryDao")
public class InMemoryFileDAO implements IFileRepository {

    private static final List<File> DB = new ArrayList<>();

    @Override
    public boolean insertFileMetadata(File metadata) {
        return DB.add(metadata);
    }

    @Override
    public Files selectAllFilesMetadata() {
        return new Files(DB);
    }

    @Override
    public boolean updateFileMetadata(String actualName, String newName) {
        DB.stream()
                .filter(file -> file.getName().equals(actualName))
                .forEach(file -> file.setName(newName));
        return true;
    }

    @Override
    public Optional<File> getFileMetadata(String fileName) {
        return DB.stream()
                .filter(file -> fileName.equals(file.getName()))
                .findFirst();
    }

    @Override
    public boolean deleteFileMetadata(String fileName) {
        Optional<File> file = getFileMetadata(fileName);
        if(file.isEmpty()){
            return false;
        }
        return DB.remove(file.get());
    }

}
