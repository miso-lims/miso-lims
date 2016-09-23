package uk.ac.bbsrc.tgac.miso.core.data.impl;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import uk.ac.bbsrc.tgac.miso.core.data.SampleTissueProcessing;

@Entity
@Table(name = "SampleTissueProcessing")
@Inheritance(strategy = InheritanceType.JOINED)
public class SampleTissueProcessingImpl extends DetailedSampleImpl implements SampleTissueProcessing {

  private static final long serialVersionUID = 1L;

}
