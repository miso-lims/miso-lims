package uk.ac.bbsrc.tgac.miso.webapp.context;

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.isStringBlankOrNull;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.util.IndexChecker;
import uk.ac.bbsrc.tgac.miso.webapp.util.MisoPropertyExporter;

@Configuration
public class IndexCheckerConfigurer {

  @Value("${miso.pools.error.index.mismatches:1}")
  private int errorMismatches;
  @Value("${miso.pools.error.index.mismatches.message:DUPLICATE INDICES}")
  private String errorMismatchesMessage;
  @Value("${miso.pools.warning.index.mismatches:2}")
  private int warningMismatches;
  @Value("${miso.pools.warning.index.mismatches.message:Near-Duplicate Indices}")
  private String warningMismatchesMessage;

  @Bean
  public IndexChecker indexChecker(@Autowired MisoPropertyExporter propertyExporter) {
    IndexChecker indexChecker = new IndexChecker();
    indexChecker.setErrorMismatchesMessage(errorMismatchesMessage);
    indexChecker.setWarningMismatchesMessage(warningMismatchesMessage);
    indexChecker.setDefaultErrorMismatches(errorMismatches);
    indexChecker.setDefaultWarningMismatches(warningMismatches);

    Map<String, String> misoProperties = propertyExporter.getResolvedProperties();
    for (PlatformType platform : PlatformType.values()) {
      String errorsString = misoProperties.get("miso.pools.error.index.mismatches." + platform.name().toLowerCase());
      if (!isStringBlankOrNull(errorsString)) {
        indexChecker.setErrorMismatches(platform, Integer.parseInt(errorsString));
      }
      String warningsString =
          misoProperties.get("miso.pools.warning.index.mismatches." + platform.name().toLowerCase());
      if (!isStringBlankOrNull(warningsString)) {
        indexChecker.setWarningMismatches(platform, Integer.parseInt(warningsString));
      }
    }
    return indexChecker;
  }
}
