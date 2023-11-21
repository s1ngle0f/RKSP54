package com.example.rksp54;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;

@Controller
public class UploadController {

    @Value("${upload.dir}")
    private String uploadDir;

    @GetMapping
    public String index(Model model) {
        return "index";
    }

    @GetMapping("/upload")
    public String uploadPage(Model model) {
        File[] files = new File(uploadDir).listFiles();
        if(files != null)
            model.addAttribute("files", Arrays.stream(files).toList());
        return "upload";
    }

    @GetMapping("/download")
    public ResponseEntity<InputStreamResource> downloadFile(@RequestParam("filepath") String filepath) {
        System.out.println(filepath);
        File file = new File(filepath);
        if (file.exists()) {
            try {
                InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
                HttpHeaders headers = new HttpHeaders();
                headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + file.getName());
                headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);

                return ResponseEntity.ok()
                        .headers(headers)
                        .contentLength(file.length())
                        .body(resource);
            } catch (IOException e) {
                e.printStackTrace();
                return ResponseEntity.status(500).build(); // Handle the exception appropriately
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file) {
        if (!file.isEmpty()) {
            try {
                byte[] bytes = file.getBytes();
                Files.write(Path.of(uploadDir, file.getOriginalFilename()), bytes,
                        StandardOpenOption.CREATE, StandardOpenOption.WRITE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "redirect:/upload";
    }
}
