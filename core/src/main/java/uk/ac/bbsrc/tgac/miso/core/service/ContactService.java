package uk.ac.bbsrc.tgac.miso.core.service;

import java.io.IOException;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.impl.Contact;

public interface ContactService extends DeleterService<Contact>, BulkSaveService<Contact>, ListService<Contact> {

  public List<Contact> listBySearch(String search) throws IOException;

}
