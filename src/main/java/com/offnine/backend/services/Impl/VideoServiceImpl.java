package com.offnine.backend.services.Impl;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.hibernate.exception.spi.ViolatedConstraintNameExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.multipart.MultipartFile;
import com.offnine.backend.BackendApplication;
import com.offnine.backend.Repo.VideoRepo;
import com.offnine.backend.entities.Video;
import com.offnine.backend.services.VideoService;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.criteria.Path;


@Service

public class VideoServiceImpl implements VideoService{

    private final BackendApplication backendApplication;

    @Value("${file.video}")
    String  DIR;

    @Autowired
    private VideoRepo videoRepo;

    VideoServiceImpl(BackendApplication backendApplication) {
        this.backendApplication = backendApplication;
    }

    @PostConstruct
    public void init(){
        File file = new File(DIR);
        if (!file.exists()) {
            file.mkdirs();
            System.out.println("Folder Created :");
        }
        else{
            System.out.println("Folder already Created");
        }

    }

    @Override
    public Video save(Video video, MultipartFile file) {


        try{

        //original file name
String filename = file.getOriginalFilename();
String contentType = file.getContentType();
InputStream inputStream = file.getInputStream();


// file path
 String cleanFileName = StringUtils.cleanPath(filename);
 // folderPath with file name
 String cleanFolder = StringUtils.cleanPath(DIR);

// folder path with fileName
java.nio.file.Path path = Paths.get(cleanFolder,cleanFileName);
System.out.println("This is th eopath "+path);


//copy file to folder
Files.copy(inputStream, path,StandardCopyOption.REPLACE_EXISTING);

// video metadata
video.setContentType(contentType);
video.setFilePath(path.toString());




//meta saved
 return videoRepo.save(video);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;

        }


    }

    @Override
    public Video get(String videoId) {
     Video video =  videoRepo.findById(videoId).orElseThrow(()-> new RuntimeException("Video not FOund"));
       return video;
    }

    @Override
    public Video getByTitle(String title) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getByTitle'");
    }

    @Override
    public List<Video> getAll() {
        return videoRepo.findAll();
    }
    
}
