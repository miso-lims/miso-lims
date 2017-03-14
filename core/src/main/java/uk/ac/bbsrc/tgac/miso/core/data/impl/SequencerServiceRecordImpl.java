package uk.ac.bbsrc.tgac.miso.core.data.impl;

import javax.persistence.Entity;
import javax.persistence.Table;

import uk.ac.bbsrc.tgac.miso.core.data.AbstractSequencerServiceRecord;

@Entity
@Table(name = "SequencerServiceRecord")
public class SequencerServiceRecordImpl extends AbstractSequencerServiceRecord {

  private static final long serialVersionUID = 1L;

  public SequencerServiceRecordImpl() {
    
  }
  
}
