package com.huertohogar.huertohogar_api.repository;

import com.huertohogar.huertohogar_api.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}