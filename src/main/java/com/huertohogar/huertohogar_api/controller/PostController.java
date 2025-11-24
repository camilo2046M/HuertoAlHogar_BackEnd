package com.huertohogar.huertohogar_api.controller;

import com.huertohogar.huertohogar_api.model.Post;
import com.huertohogar.huertohogar_api.repository.PostRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@Tag(name = "Blog", description = "Gestión de artículos del blog")
public class PostController {

    private final PostRepository postRepository;

    public PostController(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @GetMapping
    @Operation(summary = "Obtener todos los artículos del blog")
    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }
}