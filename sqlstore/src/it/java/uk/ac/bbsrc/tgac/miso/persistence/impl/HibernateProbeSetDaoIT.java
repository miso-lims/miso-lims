package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.junit.Test;

import uk.ac.bbsrc.tgac.miso.AbstractHibernateSaveDaoTest;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Probe;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ProbeSet;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ProbeSetProbe;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleProbe;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Probe.Read;

public class HibernateProbeSetDaoIT extends AbstractHibernateSaveDaoTest<ProbeSet, HibernateProbeSetDao> {

  public HibernateProbeSetDaoIT() {
    super(ProbeSet.class, 1L, 3);
  }

  @Override
  public HibernateProbeSetDao constructTestSubject() {
    HibernateProbeSetDao sut = new HibernateProbeSetDao();
    sut.setEntityManager(getEntityManager());
    return sut;
  }

  @Override
  public ProbeSet getCreateItem() {
    ProbeSet set = new ProbeSet();
    set.setName("Test Set");

    ProbeSetProbe probe = new ProbeSetProbe();
    probe.setIdentifier("A1");
    probe.setName("Probe 1");
    probe.setRead(Read.R2);
    probe.setPattern("5PNNNNNNNNNN(BC)");
    probe.setSequence("ACGTACGTACGT");
    probe.setFeatureType(Probe.ProbeFeatureType.ANTIBODY_CAPTURE);
    set.setProbes(Collections.singleton(probe));

    return set;
  }

  @SuppressWarnings("unchecked")
  @Override
  public UpdateParameters<ProbeSet, String> getUpdateParams() {
    return new UpdateParameters<>(1L, ProbeSet::getName, ProbeSet::setName, "changed");
  }

  @Test
  public void testGetByName() throws Exception {
    testGetBy(HibernateProbeSetDao::getByName, "Probe set one", ProbeSet::getName);
  }

  @Test
  public void testSearchByName() throws Exception {
    List<ProbeSet> results = getTestSubject().searchByName("Probe set");
    assertEquals(2, results.size());
    assertTrue(results.stream().anyMatch(probeSet -> Objects.equals(probeSet.getName(), "Probe set one")));
    assertTrue(results.stream().anyMatch(probeSet -> Objects.equals(probeSet.getName(), "Probe set two")));
  }

  @Test
  public void testListByIdList() throws Exception {
    testListByIdList(HibernateProbeSetDao::listByIdList, Arrays.asList(1L, 2L));
  }
}
