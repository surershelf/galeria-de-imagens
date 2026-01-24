package com.mazzo.Galeria.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
public class Imagem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 75)
    private String title;

    private String description;

    private String type;

    private Long size;

    private String path;

    @Column (name = "data_criacao", nullable = false)
    private LocalDateTime createdAt;

    private Boolean deleted;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.deleted = false;
    }

}
