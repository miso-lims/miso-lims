package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.impl.ContactRole;

public interface ContactRoleDao extends SaveDao<ContactRole> {
  ContactRole getByName(String name) throws IOException;

  long getProjectUsage(ContactRole contactRole) throws IOException;

  long getRequisitionUsage(ContactRole contactRole) throws IOException;

  List<ContactRole> listByIdList(Collection<Long> id) throws IOException;

}
