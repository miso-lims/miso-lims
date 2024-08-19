package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.Set;

import org.junit.Test;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.AbstractHibernateSaveDaoTest;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.ReferenceGenome;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Assay;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Pipeline;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ReferenceGenomeImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.StatusType;

public class HibernateProjectDaoIT extends AbstractHibernateSaveDaoTest<Project, HibernateProjectDao> {

  public HibernateProjectDaoIT() {
    super(ProjectImpl.class, 3L, 3);
  }

  @Override
  public HibernateProjectDao constructTestSubject() {
    HibernateProjectDao sut = new HibernateProjectDao();
    sut.setEntityManager(getEntityManager());
    return sut;
  }

  @Override
  public Project getCreateItem() {
    Project project = new ProjectImpl();
    Pipeline pipeline = (Pipeline) currentSession().get(Pipeline.class, 1L);
    project.setTitle("test title");
    project.setPipeline(pipeline);
    project.setStatus(StatusType.ACTIVE);
    ReferenceGenome referenceGenome = new ReferenceGenomeImpl();
    referenceGenome.setId(1L);
    referenceGenome.setAlias("hg19");
    project.setReferenceGenome(referenceGenome);
    User user = new UserImpl();
    user.setId(1L);
    project.setCreator(user);
    project.setCreationTime(new Date());
    project.setLastModifier(user);
    project.setLastModified(new Date());
    return project;
  }

  @SuppressWarnings("unchecked")
  @Override
  public UpdateParameters<Project, String> getUpdateParams() {
    return new UpdateParameters<Project, String>(2L, Project::getDescription, Project::setDescription, "test desc");
  }

  @Test
  public void testGetByTitle() throws Exception {
    testGetBy(HibernateProjectDao::getByTitle, "TEST2", Project::getTitle);
  }

  @Test
  public void testGetByCode() throws Exception {
    testGetBy(HibernateProjectDao::getByCode, "TEST1", Project::getCode);
  }

  @Test
  public void testGetUsage() throws Exception {
    testGetUsage(HibernateProjectDao::getUsage, 1L, 21L);
  }

  @Test
  public void testAssays() throws Exception {
    String code = "TEST1";
    Set<Assay> assays = getTestSubject().getByCode(code).getAssays();
    assertNotNull(assays);
    assertTrue(assays.size() == 2);
    assertTrue(assays.stream().anyMatch(a -> a.getId() == 1));
    assertTrue(assays.stream().anyMatch(a -> a.getId() == 2));
  }

}
