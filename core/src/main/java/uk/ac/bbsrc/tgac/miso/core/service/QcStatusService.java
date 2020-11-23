package uk.ac.bbsrc.tgac.miso.core.service;

import java.io.IOException;
import java.util.Collection;

import uk.ac.bbsrc.tgac.miso.core.data.impl.view.qc.QcStatusUpdate;

public interface QcStatusService {

  public void update(QcStatusUpdate update) throws IOException;

  public void update(Collection<QcStatusUpdate> updates) throws IOException;

}
