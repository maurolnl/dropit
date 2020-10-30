package com.maurolnl.dropit.file.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maurolnl.dropit.file.application.FileStorageService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UploadFileController.class)
class UploadFileControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FileStorageService fileService;

    private final String uploadFileUrl = "/api/v1/upload";

    @Test
    void whenValidInput_UpdateSuccessfully() throws Exception {
        MockMultipartFile file
                = new MockMultipartFile(
                "file",
                "hello.txt",
                MediaType.MULTIPART_FORM_DATA_VALUE,
                "Hello, World!".getBytes()
        );

        Mockito.when(fileService.store(file)).thenReturn(true);

        mockMvc
                .perform(multipart(this.uploadFileUrl).file(file))
                .andExpect(status().isCreated());

        ArgumentCaptor<MultipartFile> fileCaptor = ArgumentCaptor.forClass(MultipartFile.class);
        verify(fileService, times(1)).store(fileCaptor.capture());
        assertThat(fileCaptor.getValue()).isEqualToComparingFieldByField(file);
    }
}