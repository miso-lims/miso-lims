package uk.ac.bbsrc.tgac.miso.core.data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;

@Entity
@Table(name = "RunSolid")
public class SolidRun extends Run {
  private static final long serialVersionUID = 1L;

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

  @Override
  public PlatformType getPlatformType() {
    return PlatformType.SOLID;
  }

}
