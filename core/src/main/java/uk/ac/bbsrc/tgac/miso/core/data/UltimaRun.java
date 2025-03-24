package uk.ac.bbsrc.tgac.miso.core.data;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;

@Entity
@Table(name = "RunUltima")
public class UltimaRun extends Run {
  private static final long serialVersionUID = 1L;

  public UltimaRun() {
    super();
  }

  @Override
  public PlatformType getPlatformType() {
    return PlatformType.ULTIMA;
  }

  @Override
  public String getDeleteType() {
    return "Ultima Run";
  }

}
