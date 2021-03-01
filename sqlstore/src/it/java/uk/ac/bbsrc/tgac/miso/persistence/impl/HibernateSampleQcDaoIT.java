/*
 * Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
 * MISO project contacts: Robert Davey @ TGAC
 * *********************************************************************
 *
 * This file is part of MISO.
 *
 * MISO is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MISO is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MISO. If not, see <http://www.gnu.org/licenses/>.
 *
 * *********************************************************************
 */

package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.math.BigDecimal;

import uk.ac.bbsrc.tgac.miso.AbstractHibernateQcDaoTest;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;
import uk.ac.bbsrc.tgac.miso.core.data.qc.QcControlRun;
import uk.ac.bbsrc.tgac.miso.core.data.qc.QcTarget;
import uk.ac.bbsrc.tgac.miso.core.data.qc.SampleQC;
import uk.ac.bbsrc.tgac.miso.core.data.qc.SampleQcControlRun;

public class HibernateSampleQcDaoIT extends AbstractHibernateQcDaoTest<SampleQC, HibernateSampleQcDao, Sample, SampleQcControlRun> {

  public HibernateSampleQcDaoIT() {
    super(SampleQC.class, SampleImpl.class, SampleQcControlRun.class, QcTarget.Sample, 13L, 8L, 8L, 4L, 17, 1L);
  }

  @Override
  public HibernateSampleQcDao constructTestSubject() {
    return new HibernateSampleQcDao();
  }

  @Override
  protected SampleQC makeQc(Sample entity) {
    SampleQC qc = new SampleQC();
    qc.setSample(entity);
    return qc;
  }

  @Override
  protected QcControlRun makeControlRun(SampleQC qc) {
    SampleQcControlRun controlRun = new SampleQcControlRun();
    controlRun.setQc(qc);
    return controlRun;
  }

  @Override
  protected BigDecimal getConcentration(Sample entity) {
    return entity.getConcentration();
  }

  @Override
  protected void setConcentration(Sample entity, BigDecimal concentration) {
    entity.setConcentration(concentration);
  }

}
