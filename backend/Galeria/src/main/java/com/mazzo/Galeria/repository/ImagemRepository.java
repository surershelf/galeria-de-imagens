package com.mazzo.Galeria.repository;

import com.mazzo.Galeria.model.Imagem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ImagemRepository extends JpaRepository<Imagem, UUID> {


    Optional<Imagem> findByPath(String path);

    @Query("select i from Imagem i where i.deleted = false")
    List<Imagem> findAllActive();

}
