package com.sonarcube.eighty;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.ZoneId;
import java.util.Set;
import java.util.TimeZone;

@SpringBootApplication
public class CrudSonarCubeApplication {

    public static void main(String[] args) {
        SpringApplication.run(CrudSonarCubeApplication.class, args);
//        Set<String> allZoneIds = ZoneId.getAvailableZoneIds();
//        allZoneIds.forEach(System.out::println);
    }

//    @PostConstruct
//    public void init(){
//        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Jakarta"));
//    }

}
