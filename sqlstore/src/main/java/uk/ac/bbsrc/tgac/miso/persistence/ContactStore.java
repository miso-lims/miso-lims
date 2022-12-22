package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.impl.Contact;

public interface ContactStore extends BulkSaveDao<Contact> {

  public List<Contact> listBySearch(String search) throws IOException;

  public Contact getByEmail(String email) throws IOException;

}
