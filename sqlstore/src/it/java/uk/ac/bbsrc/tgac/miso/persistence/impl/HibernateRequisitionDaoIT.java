package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Date;

import org.junit.Test;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.AbstractHibernateSaveDaoTest;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Assay;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Requisition;
import uk.ac.bbsrc.tgac.miso.core.data.impl.RequisitionSupplementalSample;
import uk.ac.bbsrc.tgac.miso.core.data.impl.RequisitionSupplementalSample.RequisitionSupplementalSampleId;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;

public class HibernateRequisitionDaoIT extends AbstractHibernateSaveDaoTest<Requisition, HibernateRequisitionDao> {

  public HibernateRequisitionDaoIT() {
    super(Requisition.class, 1L, 2);
  }

  @Override
  public HibernateRequisitionDao constructTestSubject() {
    HibernateRequisitionDao sut = new HibernateRequisitionDao();
    sut.setSessionFactory(getSessionFactory());
    return sut;
  }

  @Override
  public Requisition getCreateItem() {
    Requisition req = new Requisition();
    req.setAlias("New Req");
    Assay assay = (Assay) currentSession().get(Assay.class, 1L);
    req.setAssay(assay);
    User user = (User) currentSession().get(UserImpl.class, 1L);
    Date date = new Date();
    req.setCreator(user);
    req.setCreationTime(date);
    req.setLastModifier(user);
    req.setLastModified(date);
    return req;
  }

  @SuppressWarnings("unchecked")
  @Override
  public UpdateParameters<Requisition, String> getUpdateParams() {
    return new UpdateParameters<>(2L, Requisition::getAlias, Requisition::setAlias, "Changed");
  }

  @Test
  public void testGetByAlias() throws Exception {
    String alias = "Detailed Req";
    Requisition req = getTestSubject().getByAlias(alias);
    assertNotNull(req);
    assertEquals(alias, req.getAlias());
  }

  @Test
  public void testGetSupplementalSample() throws Exception {
    long reqId = 2L;
    long sampleId = 21L;
    Requisition req = (Requisition) currentSession().get(Requisition.class, reqId);
    Sample sample = (Sample) currentSession().get(SampleImpl.class, sampleId);
    RequisitionSupplementalSample result = getTestSubject().getSupplementalSample(req, sample);
    assertNotNull(result);
    assertEquals(Long.valueOf(reqId), result.getRequisitionId());
    assertNotNull(result.getSample());
    assertEquals(sampleId, result.getSample().getId());
  }

  @Test
  public void testSaveSupplementalSample() throws Exception {
    long reqId = 1L;
    long sampleId = 5L;
    Requisition req = (Requisition) currentSession().get(Requisition.class, reqId);
    Sample sample = (Sample) currentSession().get(SampleImpl.class, sampleId);
    assertNull(getTestSubject().getSupplementalSample(req, sample));

    getTestSubject().saveSupplementalSample(new RequisitionSupplementalSample(req.getId(), sample));

    assertNotNull(getTestSubject().getSupplementalSample(req, sample));
  }

  @Test
  public void testRemoveSupplementalSample() throws Exception {
    long reqId = 2L;
    long sampleId = 21L;
    Sample sample = (Sample) currentSession().get(SampleImpl.class, sampleId);
    RequisitionSupplementalSampleId supplementalSampleId = new RequisitionSupplementalSampleId();
    supplementalSampleId.setRequisitionId(reqId);
    supplementalSampleId.setSample(sample);
    RequisitionSupplementalSample supplementalSample =
        currentSession().get(RequisitionSupplementalSample.class, supplementalSampleId);
    assertNotNull(supplementalSample);

    getTestSubject().removeSupplementalSample(supplementalSample);

    assertNull(currentSession().get(RequisitionSupplementalSample.class, supplementalSampleId));
  }

  @Test
  public void testListByIdList() throws Exception {
    testListByIdList(HibernateRequisitionDao::listByIdList, Arrays.asList(1L, 2L));
  }

}
