package net.mohamed.springmultitenant;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;

@SpringBootApplication
public class SpringMultitenantApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringMultitenantApplication.class, args);
    }

}
