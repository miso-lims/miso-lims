package uk.ac.bbsrc.tgac.miso.core.service;

import java.io.IOException;

import uk.ac.bbsrc.tgac.miso.core.data.impl.view.BarcodableReference;

public interface BarcodableReferenceService {

  public BarcodableReference checkForExisting(String identificationBarcode) throws IOException;

}
