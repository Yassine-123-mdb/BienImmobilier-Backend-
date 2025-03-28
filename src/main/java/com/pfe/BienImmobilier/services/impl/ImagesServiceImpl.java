package com.pfe.BienImmobilier.services.impl;

import com.pfe.BienImmobilier.entities.BienImmobilier;
import com.pfe.BienImmobilier.entities.Image;
import com.pfe.BienImmobilier.repository.BienImmobilierRepository;
import com.pfe.BienImmobilier.repository.ImagesRepository;
import com.pfe.BienImmobilier.services.inter.IimagesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.transaction.Transactional;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ImagesServiceImpl implements IimagesService {

    @Autowired
    ImagesRepository imageRepository;

    @Autowired
    BienImmobilierRepository bienImmobilierRepository;

    @Override
    public Image uploadImage(MultipartFile file) throws IOException {
        return imageRepository.save(
                Image.builder()
                        .name(file.getOriginalFilename())
                        .type(file.getContentType())
                        .image(file.getBytes())
                        .build()
        );
    }

    @Override
    public Image uploadImageForBien(MultipartFile file, Long bienId) throws IOException {
        BienImmobilier bienImmobilier = new BienImmobilier();
        bienImmobilier.setId(bienId);

        return imageRepository.save(
                Image.builder()
                        .name(file.getOriginalFilename())
                        .type(file.getContentType())
                        .image(file.getBytes())
                        .bienImmobilier(bienImmobilier)
                        .build()
        );
    }

    @Override
    public List<Image> getImagesForBien(Long bienId) {
        BienImmobilier bienImmobilier = bienImmobilierRepository.findById(bienId).get();
        return bienImmobilier.getImages();
    }

    @Override
    public ResponseEntity<byte[]> getImage(Long id) throws IOException {
        Optional<Image> dbImage = imageRepository.findById(id);
        return ResponseEntity.ok().contentType(MediaType.valueOf(dbImage.get().getType())).body(dbImage.get().getImage());
    }

    @Override
    public void deleteImage(Long id) {
        imageRepository.deleteById(id);
    }

}
