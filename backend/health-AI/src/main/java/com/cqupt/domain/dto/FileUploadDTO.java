package com.cqupt.domain.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Data
public class FileUploadDTO {

    private String institution;

    private LocalDate date;

    private MultipartFile file;

    private String tag;

}
