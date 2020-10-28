package com.maurolnl.dropit.file.application;

import com.maurolnl.dropit.file.domain.IFileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;

@Service
public class FileStorageService implements IFileStorage {

    private final IFileRepository iFileRepository;

    @Autowired
    public FileStorageService(@Qualifier("InMemoryDao") IFileRepository iFileRepository) {
        this.iFileRepository = iFileRepository;
    }

    private final Path rootLocation = Paths.get("filestorage");

    public boolean store(
            @RequestParam("file")MultipartFile file
    ){
        String fileName = file.getName();
        String fileContentType = file.getContentType();
        long fileSize = file.getSize();
        com.maurolnl.dropit.file.domain.File metadata = new com.maurolnl.dropit.file.domain.File(
                fileName,
                fileContentType,
                fileSize
        );
        boolean fileNameAlreadyExists = iFileRepository.getFileMetadata(fileName).isPresent();
        long bytesRead;

        try{
            if(file.getOriginalFilename() == null) throw new RuntimeException("Can't upload an empty file");
            if(fileNameAlreadyExists) {
                metadata.setName(fileName + "(1)");
                bytesRead = Files.copy(
                        file.getInputStream(),
                        this.rootLocation.resolve(file.getOriginalFilename().concat("(1)"))
                );
            }
            else {
                bytesRead = Files.copy(file.getInputStream(), this.rootLocation.resolve(file.getOriginalFilename()));
            }
            return iFileRepository.insertFileMetadata(metadata) && bytesRead > 0;
        }catch (Exception e){
            throw new RuntimeException("FAIL! -> message = " + e.getMessage());
        }
    }

    public Resource loadFile(String fileName){
        try{
            Path file = rootLocation.resolve(fileName);
            Resource resource = new UrlResource(file.toUri());

            if(resource.exists() || resource.isReadable()) return resource;

            throw new RuntimeException("Fail, either file is not readible or does not exists");

        }catch (MalformedURLException e){
            throw new RuntimeException("Error! -> message = " + e.getMessage());
        }
    }

    public boolean deleteFile(String filename) {
        boolean result = false;
        try {
            Path file = rootLocation.resolve(filename);
            Files.deleteIfExists(file);
            result = iFileRepository.deleteFileMetadata(filename);
            if(!result) {
                throw new RuntimeException("Could not remove file's metadata, but physical file was successfully removed");
            }
        }
        catch(NoSuchFileException e)
        {
            System.out.println("No such file/directory exists" + e.getMessage());
        }
        catch(DirectoryNotEmptyException e)
        {
            System.out.println("Directory is not empty." + e.getMessage());
        }
        catch(IOException e)
        {
            System.out.println("Invalid permissions." + e.getMessage());
        }

        return result;
    }

    public boolean updateFile(String actualName, String newName) {
        return iFileRepository.updateFileMetadata(actualName, newName);
    }

    public com.maurolnl.dropit.file.domain.Files selectAllFilesMetadata(){
        return iFileRepository.selectAllFilesMetadata();
    }
}
