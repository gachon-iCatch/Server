package org.example.icatch.Admin.Ai;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class AiWebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 동적 폴더 대응
        registry.addResourceHandler("/api/results/**")
                .addResourceLocations("file:/home/t25104/v0.1src/ai/runs/detect/");
    }
}