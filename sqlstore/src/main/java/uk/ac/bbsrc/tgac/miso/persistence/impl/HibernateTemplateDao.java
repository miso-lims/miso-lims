package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryTemplate;
import uk.ac.bbsrc.tgac.miso.core.store.TemplateStore;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateTemplateDao implements TemplateStore {

  @Autowired
  private SessionFactory sessionFactory;

  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  private Session currentSession() {
    return sessionFactory.getCurrentSession();
  }

  @Override
  public List<LibraryTemplate> listLibraryTemplatesForProject(long projectId) {
    @SuppressWarnings("unchecked")
    List<LibraryTemplate> list = currentSession().createCriteria(LibraryTemplate.class)
        .createAlias("projects", "project")
        .add(Restrictions.eq("project.id", projectId))
        .list();
    return list;
  }

}
