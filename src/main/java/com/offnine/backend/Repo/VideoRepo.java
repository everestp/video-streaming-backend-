package com.offnine.backend.Repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.offnine.backend.entities.Video;


@Repository
public interface VideoRepo extends JpaRepository<Video, String> {
    Optional<Video> findByTitle(String title);
    
}
