package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.impl.ContactRole;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectContact;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectContact_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.RequisitionContact;
import uk.ac.bbsrc.tgac.miso.core.data.impl.RequisitionContact_;
import uk.ac.bbsrc.tgac.miso.persistence.ContactRoleDao;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateContactRoleDao extends HibernateSaveDao<ContactRole> implements ContactRoleDao {

  public HibernateContactRoleDao() {
    super(ContactRole.class);
  }

  @Override
  public long getProjectUsage(ContactRole contactRole) throws IOException {
    return getUsageBy(ProjectContact.class, ProjectContact_.contactRole, contactRole);
  }

  @Override
  public long getRequisitionUsage(ContactRole contactRole) throws IOException {
    return getUsageBy(RequisitionContact.class, RequisitionContact_.contactRole, contactRole);
  }

  @Override
  public List<ContactRole> listByIdList(Collection<Long> ids) throws IOException {
    return listByIdList("contactRoleId", ids);
  }

  @Override
  public ContactRole getByName(String name) throws IOException {
    return getBy("name", name);
  }
}
