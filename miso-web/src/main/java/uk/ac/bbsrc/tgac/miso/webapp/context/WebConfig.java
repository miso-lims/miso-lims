package uk.ac.bbsrc.tgac.miso.webapp.context;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.support.ConfigurableWebBindingInitializer;
import org.springframework.web.bind.support.SessionAttributeStore;
import org.springframework.web.bind.support.WebBindingInitializer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import uk.ac.bbsrc.tgac.miso.spring.LimsBindingInitializer;
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
  public WebBindingInitializer bindingInitializer() {
    ConfigurableWebBindingInitializer initializer = new LimsBindingInitializer();
    initializer.setConversionService(mvcConversionService());
    initializer.setValidator(mvcValidator());
    return initializer;
  }

  @Bean
  public SessionAttributeStore sessionAttributeStore() {
    SessionConversationAttributeStore sessionAttributeStore = new SessionConversationAttributeStore();
    sessionAttributeStore.setNumConversationsToKeep(1000);
    return sessionAttributeStore;
  }

  @Override
  @Bean
  public RequestMappingHandlerAdapter requestMappingHandlerAdapter() {
    RequestMappingHandlerAdapter adapter = super.requestMappingHandlerAdapter();
    adapter.setWebBindingInitializer(bindingInitializer());
    adapter.setSessionAttributeStore(sessionAttributeStore());
    return adapter;
  }
}
