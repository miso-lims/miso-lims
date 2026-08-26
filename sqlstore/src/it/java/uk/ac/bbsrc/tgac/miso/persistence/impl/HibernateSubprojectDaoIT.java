package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.AbstractHibernateSaveDaoTest;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.ReferenceGenome;
import uk.ac.bbsrc.tgac.miso.core.data.Subproject;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ReferenceGenomeImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SubprojectImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;

public class HibernateSubprojectDaoIT extends AbstractHibernateSaveDaoTest<Subproject, HibernateSubprojectDao> {

  public HibernateSubprojectDaoIT() {
    super(SubprojectImpl.class, 1L, 3);
  }

  @Override
  public HibernateSubprojectDao constructTestSubject() {
    HibernateSubprojectDao sut = new HibernateSubprojectDao();
    sut.setEntityManager(getEntityManager());
    return sut;
  }

  @Override
  public Subproject getCreateItem() {
    Subproject subproject = new SubprojectImpl();
    subproject.setAlias("Exciting");
    subproject.setPriority(true);
    Project project = (Project) currentSession().find(ProjectImpl.class, 2L);
    subproject.setParentProject(project);
    ReferenceGenome reference = (ReferenceGenome) currentSession().find(ReferenceGenomeImpl.class, 1L);
    subproject.setReferenceGenome(reference);
    User user = (User) currentSession().find(UserImpl.class, 1L);
    subproject.setChangeDetails(user);
    return subproject;
  }

  @SuppressWarnings("unchecked")
  @Override
  public UpdateParameters<Subproject, String> getUpdateParams() {
    return new UpdateParameters<>(1L, Subproject::getAlias, Subproject::setAlias, "Changed");
  }

  @Test
  public void testGetUsage() throws Exception {
    Subproject subproject1 = (Subproject) currentSession().find(SubprojectImpl.class, 1L);
    assertEquals(1, getTestSubject().getUsage(subproject1));
    Subproject subproject3 = (Subproject) currentSession().find(SubprojectImpl.class, 3L);
    assertEquals(0, getTestSubject().getUsage(subproject3));
  }

  @Test
  public void testListByProjectId() throws Exception {
    long projectId = 2L;
    List<Subproject> results = getTestSubject().listByProjectId(2L);
    assertNotNull(results);
    assertEquals(3, results.size());
    for (Subproject subproject : results) {
      assertEquals(projectId, subproject.getParentProject().getId());
    }
  }

  @Test
  public void testGetByProjectAndAlias() throws Exception {
    Project project = (Project) currentSession().find(ProjectImpl.class, 2L);
    String existingAlias = "Meh";
    Subproject existing = getTestSubject().getByProjectAndAlias(project, existingAlias);
    assertNotNull(existing);
    assertEquals(existingAlias, existing.getAlias());

    assertNull(getTestSubject().getByProjectAndAlias(project, "doesnotexist"));
  }

}
