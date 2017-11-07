package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleNumberPerProject;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleNumberPerProjectImpl;
import uk.ac.bbsrc.tgac.miso.persistence.SampleNumberPerProjectDao;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateSampleNumberPerProjectDao implements SampleNumberPerProjectDao {

  protected static final Logger log = LoggerFactory.getLogger(HibernateSampleNumberPerProjectDao.class);
  private static final int DEFAULT_PADDING = 4;

  @Autowired
  private SessionFactory sessionFactory;

  private Session currentSession() {
    return getSessionFactory().getCurrentSession();
  }

  @Override
  public List<SampleNumberPerProject> getSampleNumberPerProject() {
    Query query = currentSession().createQuery("from SampleNumberPerProjectImpl");
    @SuppressWarnings("unchecked")
    List<SampleNumberPerProject> records = query.list();
    return records;
  }

  @Override
  public SampleNumberPerProject getSampleNumberPerProject(Long id) {
    return (SampleNumberPerProject) currentSession().get(SampleNumberPerProjectImpl.class, id);
  }

  @Override
  public Long addSampleNumberPerProject(SampleNumberPerProject sampleNumberPerProject) {
    Date now = new Date();
    sampleNumberPerProject.setCreationDate(now);
    sampleNumberPerProject.setLastUpdated(now);
    return (Long) currentSession().save(sampleNumberPerProject);
  }

  @Override
  public void deleteSampleNumberPerProject(SampleNumberPerProject sampleNumberPerProject) {
    currentSession().delete(sampleNumberPerProject);

  }

  @Override
  public void update(SampleNumberPerProject sampleNumberPerProject) {
    Date now = new Date();
    sampleNumberPerProject.setLastUpdated(now);
    currentSession().update(sampleNumberPerProject);
  }

  @SuppressWarnings("unchecked")
  @Override
  public synchronized String nextNumber(Project project, User user, String partialAlias) {
    SampleNumberPerProject sampleNumberPerProject = getByProject(project);
    if (sampleNumberPerProject == null) {
      sampleNumberPerProject = createSampleNumberPerProject(project, user);
    }
    Integer highestSampleNumber = sampleNumberPerProject.getHighestSampleNumber();
    
    String num = null;
    List<Sample> existing = null;
    do {
      highestSampleNumber++;
      num = padInteger(sampleNumberPerProject.getPadding(), highestSampleNumber);
      Criteria criteria = currentSession().createCriteria(SampleImpl.class);
      criteria.add(Restrictions.eq("alias", partialAlias + num));
      existing = criteria.list();
    } while (existing != null && !existing.isEmpty());
    
    sampleNumberPerProject.setHighestSampleNumber(highestSampleNumber);
    sampleNumberPerProject.setUpdatedBy(user);
    update(sampleNumberPerProject);
    
    return num;
  }

  private SampleNumberPerProject createSampleNumberPerProject(Project project, User user) {
    SampleNumberPerProject sampleNumberPerProject;
    sampleNumberPerProject = new SampleNumberPerProjectImpl();
    sampleNumberPerProject.setProject(project);
    sampleNumberPerProject.setHighestSampleNumber(0);
    sampleNumberPerProject.setPadding(DEFAULT_PADDING);
    sampleNumberPerProject.setCreatedBy(user);
    sampleNumberPerProject.setUpdatedBy(user);
    addSampleNumberPerProject(sampleNumberPerProject);
    return sampleNumberPerProject;
  }

  private String padInteger(Integer padLength, Integer highestSampleNumber) {
    StringBuilder stringBuffer = new StringBuilder();
    int toPad = padLength - Integer.toString(highestSampleNumber).length();
    for (int i = 0; i < toPad; i++) {
      stringBuffer.append("0");
    }
    return stringBuffer.toString() + highestSampleNumber;
  }

  public SessionFactory getSessionFactory() {
    return sessionFactory;
  }

  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public SampleNumberPerProject getByProject(Project project) {
    Query query = currentSession().createQuery("from SampleNumberPerProjectImpl sn where sn.project = :project");
    query.setParameter("project", project);
    return (SampleNumberPerProject) query.uniqueResult();
  }

}
