package uk.ac.bbsrc.tgac.miso.core.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
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

  @Override
  public String getDeleteType() {
    return "Solid Run";
  }

}
