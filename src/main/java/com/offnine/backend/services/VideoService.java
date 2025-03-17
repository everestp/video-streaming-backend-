package com.offnine.backend.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.multipart.MultipartFile;

import com.offnine.backend.entities.Video;

@Service

public interface VideoService {
    // Saved video
Video save(Video video,MultipartFile file);

    //get video bt id

Video get(String videoId);


    //get video title
  Video getByTitle(String title);

List<Video> getAll();
    
}