package uk.ac.bbsrc.tgac.miso.core.data.impl;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import uk.ac.bbsrc.tgac.miso.core.data.SampleAliquotRna;

@Entity
@DiscriminatorValue("AliquotRna")
public class SampleAliquotRnaImpl extends SampleAliquotImpl implements SampleAliquotRna {

  private static final long serialVersionUID = 1L;

}
