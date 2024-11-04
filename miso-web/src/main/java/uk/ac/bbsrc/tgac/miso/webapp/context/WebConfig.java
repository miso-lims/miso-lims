package uk.ac.bbsrc.tgac.miso.webapp.context;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.bind.support.SessionAttributeStore;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import com.fasterxml.jackson.core.json.JsonWriteFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import uk.ac.bbsrc.tgac.miso.core.data.Barcodable.EntityType;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.ProgressStep;
import uk.ac.bbsrc.tgac.miso.core.manager.ProgressStepFactory;
import uk.ac.bbsrc.tgac.miso.core.service.BarcodableService;
import uk.ac.bbsrc.tgac.miso.webapp.util.SessionConversationAttributeStore;

@Configuration
public class WebConfig extends WebMvcConfigurationSupport {

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

  @Bean(name = "objectMapper")
  public ObjectMapper objectMapper() {
    ObjectMapper mapper = Jackson2ObjectMapperBuilder.json()
        .modulesToInstall(new JsonStringValidator())
        .build();
    mapper.getFactory().setCharacterEscapes(new JsonCharacterEscapes())
        .configure(JsonWriteFeature.ESCAPE_NON_ASCII.mappedFeature(), true);
    return mapper;
  }
}
