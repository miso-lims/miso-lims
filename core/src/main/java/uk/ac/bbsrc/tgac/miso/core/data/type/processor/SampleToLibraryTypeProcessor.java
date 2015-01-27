package uk.ac.bbsrc.tgac.miso.core.data.type.processor;

import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.TypeProcessor;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;
import uk.ac.bbsrc.tgac.miso.core.exception.TypeProcessingException;
import uk.ac.bbsrc.tgac.miso.core.service.naming.MisoNamingScheme;
import uk.ac.bbsrc.tgac.miso.core.service.naming.NamingSchemeAware;
import uk.ac.bbsrc.tgac.miso.core.service.naming.RequestManagerAwareNamingScheme;

/**
 * Process an input Sample and output a new Library
 *
 * @author Rob Davey
 * @date 17/11/14
 * @since 0.2.1
 */
public class SampleToLibraryTypeProcessor implements TypeProcessor<Sample, Library>, NamingSchemeAware<Library> {
  protected static final Logger log = LoggerFactory.getLogger(SampleToLibraryTypeProcessor.class);

  @Autowired
  private RequestManagerAwareNamingScheme<Library> libraryNamingScheme;

  @Override
  public Library process(Sample input) throws TypeProcessingException {
    Library output = new LibraryImpl();
    output.setSample(input);

    if (input.getSecurityProfile() != null) {
      output.setSecurityProfile(input.getSecurityProfile());
    }

    try {
      output.setName(libraryNamingScheme.generateNameFor("name", output));
      output.setAlias(libraryNamingScheme.generateNameFor("alias", output));
    }
    catch (MisoNamingException e) {
      e.printStackTrace();
      log.error("Cannot process input sample into output library", e);
      throw new TypeProcessingException("Cannot process input sample into output library", e);
    }

    return output;
  }

  @Override
  public JSONObject buildUI() {
    return new JSONObject();
  }

  @Override
  public RequestManagerAwareNamingScheme<Library> getNamingScheme() {
    return libraryNamingScheme;
  }

  @Override
  public void setNamingScheme(MisoNamingScheme<Library> namingScheme) {
    this.libraryNamingScheme = (RequestManagerAwareNamingScheme<Library>)namingScheme;
  }
}
