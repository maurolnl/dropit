package com.maurolnl.dropit.file.infrastructure;

import com.maurolnl.dropit.file.application.FileStorageService;
import com.maurolnl.dropit.file.domain.Files;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RequestMapping("/api/v1")
@RestController
public class FileMetadataController {

    @Autowired
    FileStorageService fileService;


    @DeleteMapping("/delete/{filename}")
    public ResponseEntity<String> deleteFile(@PathVariable String filename) {
        try{
            boolean queryResult = fileService.deleteFile(filename);
            if(!queryResult) return new ResponseEntity<>("Could not found the file", HttpStatus.NOT_FOUND);
            return new ResponseEntity<>("Deleted file: " + filename, HttpStatus.OK);
        }catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @PostMapping("/update/{filename}")
    public ResponseEntity<String> updateFile(@PathVariable String filename, @RequestBody String newname) {
        try{
            fileService.updateFile(filename, newname);
            return new ResponseEntity<>("Deleted file: " + filename, HttpStatus.OK);
        }catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @GetMapping("/getAllFilesMetadata")
    public ResponseEntity<Files> getAllFilesMetadata(){
        try {
            return new ResponseEntity<>(
                    fileService.selectAllFilesMetadata(),
                    HttpStatus.OK);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
