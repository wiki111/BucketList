package com.rawik.bucketlist.demo.service;

import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.nio.file.Path;
import java.util.stream.Stream;

public interface StorageService {

    void init();
    String store(MultipartFile file, String username);
    Path load(String filename, String username);
    boolean deleteFile(String ownerUsername, String filename);

}
