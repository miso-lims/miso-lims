package uk.ac.bbsrc.tgac.miso.webapp.context;

import java.util.Map;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import uk.ac.bbsrc.tgac.miso.core.manager.IssueTrackerManager;
import uk.ac.bbsrc.tgac.miso.webapp.service.integration.jira.JiraIssueManager;
import uk.ac.bbsrc.tgac.miso.webapp.util.MisoPropertyExporter;

@Configuration
public class IssueTrackerConfigurer {

  @Autowired
  private MisoPropertyExporter propertyExporter;

  @Bean
  public IssueTrackerManager getIssueTrackerManager() {
    Map<String, String> misoProperties = propertyExporter.getResolvedProperties();
    if (misoProperties.containsKey("miso.issuetracker.tracker")) {
      String trackerType = misoProperties.get("miso.issuetracker.tracker");
      if ("jira".equals(trackerType)) {
        IssueTrackerManager issueTracker = new JiraIssueManager();
        Properties properties = new Properties();
        properties.putAll(misoProperties);
        issueTracker.setConfiguration(properties);
        return issueTracker;
      } else {
        throw new IllegalArgumentException("Invalid tracker type specified at miso.issuetracker.tracker");
      }
    }
    return null;
  }

}
