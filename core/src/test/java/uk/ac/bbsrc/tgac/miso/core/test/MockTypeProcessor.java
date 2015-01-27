package uk.ac.bbsrc.tgac.miso.core.test;

import net.sf.json.JSONObject;
import uk.ac.bbsrc.tgac.miso.core.data.Nameable;
import uk.ac.bbsrc.tgac.miso.core.data.TypeProcessor;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;
import uk.ac.bbsrc.tgac.miso.core.exception.TypeProcessingException;

/**
 * Info
 *
 * @author Rob Davey
 * @date 28/11/14
 * @since version
 */
public class MockTypeProcessor implements TypeProcessor<Nameable, Nameable> {
  @Override
  public Nameable process(Nameable input) throws TypeProcessingException, MisoNamingException {
    return input;
  }

  @Override
  public JSONObject buildUI() {
    return null;
  }
}
