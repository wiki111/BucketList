package com.rawik.bucketlist.demo.service;

import com.rawik.bucketlist.demo.exceptions.StorageException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.stream.Stream;

@Service
public class StorageServiceImpl implements StorageService{

    private final Path rootLocation;
    private final Path defaultLocation;
    private final String defImg = "default_image.png";
    private final String defAva = "sample_avatar.png";

    public StorageServiceImpl() {
        this.rootLocation = Paths.get("upload-dir");
        this.defaultLocation = Paths.get("defaults");
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
        String filename = prepareFilename(StringUtils.cleanPath(file.getOriginalFilename()));
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

    private String prepareFilename(String originalFilename){
        String extension = "";
        int i = originalFilename.lastIndexOf('.');
        if(i > 0) extension = originalFilename.substring(i+1);
        return new Date().getTime() + "." + extension;
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
                throw new StorageException("Cannot create user directory. Please try again.");
            }
        }
    }

    @Override
    public Path load(String filename, String username) {
        //todo catch and resolve situation when there is no file at specified location, or no filename at all
        Path location = getDirectoryForUser(username);
        return location.resolve(filename);
    }

    @Override
    public Path loadDefault() {
        Path location = defaultLocation;
        return location.resolve(defImg);
    }

    @Override
    public Path loadDefaultAvatar() {
        return defaultLocation.resolve(defAva);
    }


    @Override
    public boolean deleteFile(String ownerUsername, String filename) {
        Path path = getDirectoryForUser(ownerUsername);
        try{
            Files.deleteIfExists(path.resolve(filename));
            return true;
        }catch (Exception e){
            throw new StorageException("Cannot delete file. Please try again.");
        }
    }
}
