package com.rawik.bucketlist.demo.service;

import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.nio.file.Path;
import java.util.stream.Stream;

public interface StorageService {

    void init();
    String store(MultipartFile file, String username);
    Stream<Path> loadAll();
    Path load(String filename, String username);
    Resource loadAsResource(String filename);
    void deleteAll();

}
