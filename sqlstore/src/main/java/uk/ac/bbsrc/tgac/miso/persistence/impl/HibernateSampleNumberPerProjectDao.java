package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleNumberPerProject;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleNumberPerProjectImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleNumberPerProjectImpl_;
import uk.ac.bbsrc.tgac.miso.persistence.SampleNumberPerProjectDao;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateSampleNumberPerProjectDao extends HibernateProviderDao<SampleNumberPerProject>
    implements SampleNumberPerProjectDao {

  private static final int DEFAULT_PADDING = 4;

  public HibernateSampleNumberPerProjectDao() {
    super(SampleNumberPerProject.class, SampleNumberPerProjectImpl.class);
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
    currentSession().persist(sampleNumberPerProject);
    return sampleNumberPerProject.getId();
  }

  @Override
  public void update(SampleNumberPerProject sampleNumberPerProject) {
    Date now = new Date();
    sampleNumberPerProject.setLastUpdated(now);
    currentSession().merge(sampleNumberPerProject);
  }

  @Override
  public void delete(SampleNumberPerProject sampleNumberPerProject) {
    currentSession().remove(sampleNumberPerProject);
  }

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
      QueryBuilder<Sample, SampleImpl> builder = new QueryBuilder<>(currentSession(), SampleImpl.class, Sample.class);
      builder.addPredicate(
          builder.getCriteriaBuilder().equal(builder.getRoot().get(SampleImpl_.alias), partialAlias + num));
      existing = builder.getResultList();
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

  @Override
  public SampleNumberPerProject getByProject(Project project) {
    return getBy(SampleNumberPerProjectImpl_.PROJECT, project);
  }

}
