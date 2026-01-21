package com.mazzo.Galeria.repository;

import com.mazzo.Galeria.model.Imagem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ImagemRepository extends JpaRepository<Imagem, UUID> {


    Optional<Imagem> findByPath(String path);
}
