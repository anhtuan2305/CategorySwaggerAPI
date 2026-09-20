package vn.tuan.categoryswaggerapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import vn.tuan.categoryswaggerapi.config.StorageProperties;

@SpringBootApplication
@EnableConfigurationProperties(
        StorageProperties.class
)
public class CategorySwaggerApiApplication {

    public static void main(String[] args) {

        SpringApplication.run(
                CategorySwaggerApiApplication.class,
                args
        );
    }
}