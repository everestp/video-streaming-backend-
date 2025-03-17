package com.offnine.backend.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.offnine.backend.AppConstants;
import com.offnine.backend.BackendApplication;
import com.offnine.backend.Repo.VideoRepo;
import com.offnine.backend.entities.Video;
import com.offnine.backend.payload.CustomMessage;
import com.offnine.backend.services.Impl.VideoServiceImpl;
import com.offnine.backend.services.VideoService;

import java.io.IOException;
import java.io.InputStream;
import org.springframework.http.HttpHeaders;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;


@RestController
@RequestMapping("/api/v1/videos")
@CrossOrigin("*")
public class VideoController {

    private final BackendApplication backendApplication;





@Autowired
private VideoRepo videoRepo;

    
    @Autowired
    private VideoService  videoService;


    VideoController(BackendApplication backendApplication) {
        this.backendApplication = backendApplication;
    }




  

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

@GetMapping("/stream/{videoId}")
public ResponseEntity<Resource> stream(
  @PathVariable String videoId
){

  Video video =  videoService.get(videoId);
   String contentType = video.getContentType();
   String filePath = video.getFilePath();
   String title = video.getTitle();
   if(contentType ==null){
    contentType ="appication/octet-stream";
   }
Resource resource = new FileSystemResource(filePath);

   return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType)).body(resource);
}

@GetMapping
public List<Video> getAll(){
  return videoService.getAll();
}

// Stream video  in chunks

@GetMapping("/stream/range/{videoId}")
public ResponseEntity<Resource> streamVideoInRange(
  @PathVariable String videoId,
  @RequestHeader(value = "Range",required = false) String range 
){

  System.out.println(range);

Video video = videoService.get(videoId);
Path path = Paths.get(video.getFilePath());
Resource resource = new FileSystemResource(path);

String contentType = video.getContentType();
if(contentType==null){
  contentType ="application/octet-stream";

  }

// file length
long fileLength = path.toFile().length();

if(range==null){
  return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType)).body(resource);

}
 long rangeStart;
 long rangeEnd;

  String[] ranges = range.replace("bytes=", "").split("-");
 rangeStart = Long.parseLong(ranges[0]);



 // cacuate end range
rangeEnd = rangeStart + AppConstants.CHUNK_SIZE -1 ;
if(rangeEnd >= fileLength){
  rangeEnd = fileLength -1;
}


// if(ranges.length  > 1){
//   rangeEnd =Long.parseLong(range);
// }
// else{
//   rangeEnd = fileLength -1;

// }
// if(rangeEnd > fileLength -1){
//   rangeEnd = fileLength -1;
// }

InputStream inputStream;
try {
  inputStream = Files.newInputStream(path);
inputStream.skip(rangeStart);

long  contentLength = rangeEnd-rangeStart +1;


byte[] data = new byte[(int) contentLength];
int read = inputStream.read(data  ,0,data.length);
System.out.println("Read number of fie"+ read);


HttpHeaders headers = new HttpHeaders();
headers.add("Content-Range", "bytes " + rangeStart + "-" + rangeEnd + "/" + fileLength);
headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
headers.add("Pragma", "no-cache");
headers.add("Expires", "0");
headers.add("X-Content-Type-Options", "nosniff");

headers.setContentLength(contentLength);
  return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT).headers(headers).contentType(MediaType.parseMediaType(contentType)).body(new ByteArrayResource(data));





  
} 

catch (IOException e) {
return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
}


}


}
