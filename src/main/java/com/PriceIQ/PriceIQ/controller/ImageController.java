package com.PriceIQ.PriceIQ.controller;

import com.PriceIQ.PriceIQ.dto.response.ImageUploadResponse;
import com.PriceIQ.PriceIQ.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;

    @PostMapping("/upload")
    public ResponseEntity<ImageUploadResponse> uploadImage(
            @RequestParam("file") MultipartFile file
    ) {

        String imageUrl = imageService.uploadImage(file);

        return ResponseEntity.ok(new ImageUploadResponse(imageUrl));
    }
}