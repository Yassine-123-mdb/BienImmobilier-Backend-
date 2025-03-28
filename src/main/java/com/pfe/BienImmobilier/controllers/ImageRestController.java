package com.pfe.BienImmobilier.controllers;

import com.pfe.BienImmobilier.entities.Image;
import com.pfe.BienImmobilier.services.inter.IimagesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

@CrossOrigin(origins = "http://localhost:4300", allowedHeaders = {"Content-Type"})
@RestController
@RequestMapping("/api/images")
public class ImageRestController {

    @Autowired
    private IimagesService imageService;

    @PostMapping("/upload/{bienId}")
    public Image uploadImageForBien(@RequestParam("image") MultipartFile file, @PathVariable("bienId") Long bienId) throws IOException {
        return imageService.uploadImageForBien(file, bienId);
    }

    @GetMapping("/getImagesForBien/{bienId}")
    public ResponseEntity<List<Image>> getImagesForBien(@PathVariable Long bienId) {
        List<Image> images = imageService.getImagesForBien(bienId);
        return ResponseEntity.ok(images);
    }

    @GetMapping("/load/{id}")
    public ResponseEntity<byte[]> getImage(@PathVariable("id") Long id) throws IOException {
        return imageService.getImage(id);
    }
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public Image uploadImage(@RequestParam("image") MultipartFile file) throws IOException {
        return imageService.uploadImage(file);
    }


    @DeleteMapping("/delete/{id}")
    public void deleteImage(@PathVariable("id") Long id) {
        imageService.deleteImage(id);
    }
}