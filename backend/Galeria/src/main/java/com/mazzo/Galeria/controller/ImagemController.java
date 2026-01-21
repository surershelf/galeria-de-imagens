package com.mazzo.Galeria.controller;

import com.mazzo.Galeria.model.Imagem;
import com.mazzo.Galeria.repository.ImagemRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@RestController()
@RequestMapping(path = "images")
public class ImagemController {

    private final ImagemRepository imagemRepository;
    public ImagemController(ImagemRepository imagemRepository) {
        this.imagemRepository = imagemRepository;
    }

    record ErrorResponse(String error){}

    record AddImagemResponse(UUID id, String title, String description){}

    @PostMapping(path = "add")
    public ResponseEntity<?> addImagem(@RequestParam("file") MultipartFile file,
                                       @RequestParam("title") String title,
                                       @RequestParam("description") String description)
    {
        try {
            if (file.isEmpty() || title.isEmpty()) {
                return ResponseEntity
                        .badRequest().body(new ErrorResponse("Título ou imagem não enviada!"));
            }
            if (!file.getContentType().startsWith("image")) {
                return ResponseEntity
                        .badRequest().body(new ErrorResponse("Arquivo não é uma imagem!"));
            }
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

            Path uploadDir = Paths.get("backend/uploads");
            Files.createDirectories(uploadDir.getParent());

            Path filePath = uploadDir.resolve(fileName);
            Files.write(filePath, file.getBytes());


            Imagem imagem = new Imagem();
            imagem.setTitle(fileName);
            imagem.setDescription(description);
            imagem.setPath(filePath.toString());
            imagem.setType(file.getContentType());
            imagem.setSize(file.getSize());
            Imagem savedImage = imagemRepository.save(imagem);

            return  ResponseEntity.status(HttpStatus.CREATED)
                    .body(new AddImagemResponse(savedImage.getId(),savedImage.getTitle(), savedImage.getDescription()));
        } catch (Exception e){
            return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("Erro interno do servidor"));
        }
    }
}
