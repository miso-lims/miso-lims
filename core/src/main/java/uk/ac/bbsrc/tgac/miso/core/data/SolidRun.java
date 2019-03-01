package uk.ac.bbsrc.tgac.miso.core.data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;

@Entity
@Table(name = "RunSolid")
public class SolidRun extends Run {
  private static final long serialVersionUID = 1L;

  public SolidRun() {
    super();
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

  @Override
  public PlatformType getPlatformType() {
    return PlatformType.SOLID;
  }

}
