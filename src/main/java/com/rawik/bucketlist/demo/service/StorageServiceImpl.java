package com.rawik.bucketlist.demo.service;

import com.rawik.bucketlist.demo.exceptions.StorageException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

@Service
public class StorageServiceImpl implements StorageService{

    private final Path rootLocation;

    public StorageServiceImpl() {
        this.rootLocation = Paths.get("upload-dir");
    }

    @Override
    public void init() {
        try{
            Files.createDirectories(rootLocation);
        }catch (IOException e){
            throw new StorageException("Could not initialize storage", e);
        }
    }

    @Override
    public String store(MultipartFile file, String username) {

        String filename = StringUtils.cleanPath(file.getOriginalFilename());

        Path usersDirectory = getDirectoryForUser(username);

        try{
            if(file.isEmpty()){
                throw new StorageException("Failed to store empty file " + filename);
            }
            if(filename.contains("..")){
                throw new StorageException("Cannot store file with relative path outside current directory " + filename);
            }
            try(InputStream inputStream = file.getInputStream()){
                Files.copy(inputStream, usersDirectory.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
            }
        }catch (IOException e){
            throw new StorageException("Failed to store file " + filename, e);
        }

        return filename;
    }

    private Path getDirectoryForUser(String username){

        String usersDirName = username.split("@")[0];
        Path usersDir = rootLocation.resolve(usersDirName);

        if(Files.exists(usersDir)){
            return usersDir;
        }else {
            try{
                Files.createDirectory(usersDir);
                return usersDir;
            }catch (IOException e){
                throw new RuntimeException();
                //todo handle error
            }
        }
    }

    @Override
    public Stream<Path> loadAll() {
        return null;
    }

    @Override
    public Path load(String filename, String username) {

        Path location = getDirectoryForUser(username);
        return location.resolve(filename);
    }

    @Override
    public Resource loadAsResource(String filename) {
        return null;
    }

    @Override
    public void deleteAll() {

    }
}
