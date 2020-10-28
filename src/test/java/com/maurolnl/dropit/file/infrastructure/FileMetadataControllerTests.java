package com.maurolnl.dropit.file.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maurolnl.dropit.file.application.FileStorageService;
import com.maurolnl.dropit.file.domain.File;
import com.maurolnl.dropit.file.domain.Files;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.containsString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.List;

@WebMvcTest(FileMetadataController.class)
class FileMetadataControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FileStorageService fileService;

    private final String getAllFilesMetadataUrl = "/api/v1/getAllFilesMetadata";
    private final String deleteFilesMetadataUrl = "/api/v1/delete/{filename}";

    @Test
    void whenMetadataFilesExists_retrieved200Ok() throws Exception {
        List<File> listFiles = new ArrayList<>();
        listFiles.add(new File("Archivo 1", "text", 1));
        listFiles.add(new File("Archivo 2", "text", 1));
        listFiles.add(new File("Archivo 3", "text", 1));
        Files files = new Files(listFiles);

        Mockito.when(fileService.selectAllFilesMetadata()).thenReturn(files);

        MvcResult mvcResult = mockMvc
                .perform(get(this.getAllFilesMetadataUrl))
                .andExpect(status().isOk())
                .andReturn();

        String actualJsonResponse = mvcResult.getResponse().getContentAsString();
        String expectedResponse = objectMapper.writeValueAsString(files);

        assertThat(actualJsonResponse).isEqualToIgnoringWhitespace(expectedResponse);
    }

    @Test
    void whenMetadataFilesAreNotProvided_retrieved200OkWithEmptyList() throws Exception {
        List<File> listFiles = new ArrayList<>();
        Files files = new Files(listFiles);

        Mockito.when(fileService.selectAllFilesMetadata()).thenReturn(files);

        MvcResult mvcResult = mockMvc
                .perform(get(this.getAllFilesMetadataUrl))
                .andExpect(status().isOk())
                .andReturn();

        String actualJsonResponse = mvcResult.getResponse().getContentAsString();
        String expectedResponse = objectMapper.writeValueAsString(files);

        assertThat(actualJsonResponse).isEqualToIgnoringWhitespace(expectedResponse);
    }

    @Test
    void whenFileNameExists_retrieved200Ok() throws Exception {
        String filename = "filename";

        Mockito.when(fileService.deleteFile(filename)).thenReturn(true);

        mockMvc
                .perform(delete(this.deleteFilesMetadataUrl, filename))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Deleted file")));
    }

    @Test
    void whenFileNameNotExists_retrieved4xx() throws Exception {
        String filename = "filename";

        Mockito.when(fileService.deleteFile(filename)).thenReturn(false);
        mockMvc
                .perform(delete(this.deleteFilesMetadataUrl, filename))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenFileNameIsNotProvided_retrieved_retrieved404NotFound() throws Exception {
        mockMvc
                .perform(delete(this.deleteFilesMetadataUrl, ""))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenValidInput_thenMapsToBusinessModel() throws Exception {
        String filename = "filename";

        Mockito.when(fileService.deleteFile(filename)).thenReturn(true);

        mockMvc
                .perform(delete(this.deleteFilesMetadataUrl, filename))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Deleted file")));

        ArgumentCaptor<String> filenameCaptor = ArgumentCaptor.forClass(String.class);
        verify(fileService, times(1)).deleteFile(filenameCaptor.capture());
        assertThat(filenameCaptor.getValue()).isEqualTo("filename");
    }
}