package uk.ac.bbsrc.tgac.miso.core.data;

import net.sf.json.JSONObject;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;
import uk.ac.bbsrc.tgac.miso.core.exception.TypeProcessingException;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;
import uk.ac.bbsrc.tgac.miso.core.service.naming.MisoNamingScheme;

/**
 * Interface defining a class that can process and input type class and produce an output type
 *
 * @author Rob Davey
 * @date 17/11/14
 * @since 0.2.1
 */
public interface TypeProcessor<I, O> {
  O process(I input) throws TypeProcessingException, MisoNamingException;
  JSONObject buildUI();
}