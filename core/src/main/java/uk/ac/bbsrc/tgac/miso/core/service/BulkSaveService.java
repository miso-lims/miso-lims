package uk.ac.bbsrc.tgac.miso.core.service;

import java.io.IOException;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.Identifiable;

public interface BulkSaveService<T extends Identifiable> extends SaveService<T> {

  public List<T> bulkCreate(List<T> items) throws IOException;

  public List<T> bulkUpdate(List<T> items) throws IOException;

}
