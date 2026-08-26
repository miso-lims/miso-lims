package uk.ac.bbsrc.tgac.miso.webapp.context;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.support.SessionAttributeStore;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import tools.jackson.core.json.JsonFactory;
import tools.jackson.core.json.JsonWriteFeature;
import tools.jackson.databind.json.JsonMapper;
import uk.ac.bbsrc.tgac.miso.core.data.Barcodable.EntityType;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.ProgressStep;
import uk.ac.bbsrc.tgac.miso.core.manager.ProgressStepFactory;
import uk.ac.bbsrc.tgac.miso.core.service.BarcodableService;
import uk.ac.bbsrc.tgac.miso.webapp.util.SessionConversationAttributeStore;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/scripts/**").addResourceLocations("/scripts/");
    registry.addResourceHandler("/styles/**").addResourceLocations("/styles/");
    registry.addResourceHandler("/favicon.ico").addResourceLocations("/");
    registry.addResourceHandler("/index.html").addResourceLocations("/");
  }

  @Bean
  public SessionAttributeStore sessionAttributeStore() {
    SessionConversationAttributeStore sessionAttributeStore = new SessionConversationAttributeStore();
    sessionAttributeStore.setNumConversationsToKeep(1000);
    return sessionAttributeStore;
  }

  @Value("${miso.project.report.links:}")
  private String projectReportLinksConfigLine;

  @Value("${miso.run.report.links:}")
  private String runReportLinksConfigLine;

  @Bean
  public ExternalUriBuilder externalUriBuilder() {
    ExternalUriBuilder externalUriBuilder = new ExternalUriBuilder();
    externalUriBuilder.setProjectReportLinksConfig(projectReportLinksConfigLine);
    externalUriBuilder.setRunReportLinksConfig(runReportLinksConfigLine);
    return externalUriBuilder;
  }

  @Autowired
  private List<BarcodableService<?>> barcodableServices;

  @Bean
  public Map<EntityType, BarcodableService<?>> barcodableServicesMap() {
    return barcodableServices.stream().collect(Collectors.toMap(BarcodableService::getEntityType, Function.identity()));
  }

  @Autowired
  private List<ProgressStepFactory> progressStepFactories;

  @Bean
  public Map<ProgressStep.FactoryType, ProgressStepFactory> progressStepFactoryMap() {
    return progressStepFactories.stream()
        .collect(Collectors.toMap(ProgressStepFactory::getFactoryType, Function.identity()));
  }

  @Bean(name = "jsonMapper")
  public JsonMapper jsonMapper() {
    JsonFactory factory = JsonFactory.builder()
        .characterEscapes(new JsonCharacterEscapes())
        .configure(JsonWriteFeature.ESCAPE_NON_ASCII, true)
        .build();

    return JsonMapper.builder(factory)
        .addModule(new JsonStringValidator())
        .build();
  }
}
