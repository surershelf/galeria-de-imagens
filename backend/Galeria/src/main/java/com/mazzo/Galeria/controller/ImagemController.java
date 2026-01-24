package com.mazzo.Galeria.controller;

import com.mazzo.Galeria.model.Imagem;
import com.mazzo.Galeria.repository.ImagemRepository;
import jakarta.websocket.server.PathParam;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

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
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse("Título ou imagem não enviada!"));
            }
            if (!Objects.requireNonNull(file.getContentType()).startsWith("image")) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse("Arquivo não é uma imagem!"));
            }
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

            Path uploadDir = Paths.get("/app/uploads");
            Files.createDirectories(uploadDir);

            Path filePath = uploadDir.resolve(fileName);
            Files.write(filePath, file.getBytes());


            Imagem imagem = new Imagem();
            imagem.setTitle(title);
            imagem.setDescription(description);
            imagem.setPath(filePath.toString());
            imagem.setType(file.getContentType());
            imagem.setSize(file.getSize());
            Imagem savedImage = imagemRepository.save(imagem);

            return  ResponseEntity.status(HttpStatus.CREATED)
                    .body(new AddImagemResponse(savedImage.getId(),savedImage.getTitle(), savedImage.getDescription()));
        } catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(e.getMessage()));
        }


    }

    @GetMapping(path = "see")
    public ResponseEntity<?> findAll(){
        try{
            List<Imagem> images = imagemRepository.findAll();
            return  ResponseEntity.status(HttpStatus.OK).body(images);
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Erro interno do servidor"));
    }

    }

    @GetMapping(path = "see/actives")
    public ResponseEntity<?> findAllActives(){
        try{
            List<Imagem> images = imagemRepository.findAllActive();
            return  ResponseEntity.status(HttpStatus.OK).body(images);
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Erro interno do servidor"));
        }
    }

    @DeleteMapping(path = "del/{id}")
    public ResponseEntity<?> deleteImagem(@PathVariable("id") UUID id){
        try{
            Optional<Imagem> OptImagem = imagemRepository.findById(id);

            if (OptImagem.isEmpty()){
                return ResponseEntity.badRequest().body(new ErrorResponse("Imagem não encontrada!"));
            }
            Imagem imagem = OptImagem.get();
            imagem.setDeleted(true);
            imagemRepository.save(imagem);

            return ResponseEntity.noContent().build();

        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Erro interno do servidor"));
        }
    }
}

