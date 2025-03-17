package com.offnine.backend.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.offnine.backend.BackendApplication;
import com.offnine.backend.Repo.VideoRepo;
import com.offnine.backend.entities.Video;
import com.offnine.backend.payload.CustomMessage;
import com.offnine.backend.services.VideoService;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/v1/videos")
@CrossOrigin("*")
public class VideoController {

@Autowired
private VideoRepo videoRepo;

    
    @Autowired
    private VideoService  videoService;

  

    @PostMapping
    public ResponseEntity<?> create(
        @RequestParam("file") MultipartFile file,
        @RequestParam("title") String title,
        @RequestParam("description") String description

    ) 
    
    {
        Video video = new Video();
        video.setTitle(title);
        video.setDescription(description);
        video.setVideoId(UUID.randomUUID().toString());

         Video savedVideo = videoService.save(video,file);
      if(savedVideo !=null){
        return ResponseEntity.status(HttpStatus.OK).body(video);
      }
      else{
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(CustomMessage.builder().message("Video not Upoaded").success(false).build());
      }
   
    }

// stream video


     public ResponseE
}
