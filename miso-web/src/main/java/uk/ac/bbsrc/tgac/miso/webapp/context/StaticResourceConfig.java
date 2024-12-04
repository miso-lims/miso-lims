package uk.ac.bbsrc.tgac.miso.webapp.context;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class StaticResourceConfig implements WebMvcConfigurer {

  @Value("${miso.fileStorageDirectory}")
  private String fileStorageDirectory;

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/freezermaps/**")
        .addResourceLocations("file:" + fileStorageDirectory + "freezermaps/");
  }

}
