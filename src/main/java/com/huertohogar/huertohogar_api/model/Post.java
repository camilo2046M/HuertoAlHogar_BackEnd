package com.huertohogar.huertohogar_api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "posts")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String title;

    @Column(length = 500) // Permitir textos más largos
    private String excerpt;

    private String imageUrl;
    private String category;
    private String date; // Usamos String para guardar "10 de Noviembre, 2025" directamente
}