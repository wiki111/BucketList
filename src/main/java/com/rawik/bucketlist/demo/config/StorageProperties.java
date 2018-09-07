package com.rawik.bucketlist.demo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@ConfigurationProperties("storage")
@EnableConfigurationProperties
public class StorageProperties {

    private String location = "bucketlist-upload-dir";
    public String getLocation(){return location;}
    public void setLocation(String location){this.location = location;}

}
