package uk.ac.bbsrc.tgac.miso.core.data.impl;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.IlluminaRun;
import uk.ac.bbsrc.tgac.miso.core.data.Run;

@Entity
@Table(name = "RunSolid")
public class SolidRun extends Run {
  private static final long serialVersionUID = 1L;
  private static final Logger log = LoggerFactory.getLogger(IlluminaRun.class);

  public SolidRun() {
    super();
  }

  public SolidRun(User user) {
    super(user);
  }

  @Column(nullable = false)
  private boolean pairedEnd = true;

  @Override
  public Boolean getPairedEnd() {
    return pairedEnd;
  }

  @Override
  public void setPairedEnd(boolean pairedEnd) {
    this.pairedEnd = pairedEnd;
  }

}
