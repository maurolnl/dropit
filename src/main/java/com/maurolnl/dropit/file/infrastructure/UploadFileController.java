package com.maurolnl.dropit.file.infrastructure;

import com.maurolnl.dropit.file.application.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping("/api/v1")
@RestController
public class UploadFileController {

    @Autowired
    FileStorageService fileService;

    @PostMapping(
            path = "/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> uploadFile(
            @RequestParam("file") MultipartFile file
    ) {
        if(file == null) throw new RuntimeException("You must select a file for uploading");
        try{
            boolean serviceResult = fileService.store(file);
            if(!serviceResult) return new ResponseEntity<>("An Error Occurred", HttpStatus.CONFLICT);
            return new ResponseEntity<>(file.getOriginalFilename(), HttpStatus.CREATED);
        }catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

    }


}
