package uk.ac.bbsrc.tgac.miso.webapp.context;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.support.SessionAttributeStore;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import uk.ac.bbsrc.tgac.miso.core.data.Barcodable.EntityType;
import uk.ac.bbsrc.tgac.miso.core.data.workflow.ProgressStep;
import uk.ac.bbsrc.tgac.miso.core.manager.ProgressStepFactory;
import uk.ac.bbsrc.tgac.miso.core.service.BarcodableService;
import uk.ac.bbsrc.tgac.miso.webapp.util.SessionConversationAttributeStore;

/**
 * uk.ac.bbsrc.tgac.miso.webapp.context
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @date 07/02/13
 * @since 0.1.9
 */
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
    return progressStepFactories.stream().collect(Collectors.toMap(ProgressStepFactory::getFactoryType, Function.identity()));
  }

  @Bean(name = "objectMapper")
  public ObjectMapper objectMapper() {
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JsonStringValidator());
    mapper.getFactory().setCharacterEscapes(new JsonCharacterEscapes());
    return mapper;
  }
}
