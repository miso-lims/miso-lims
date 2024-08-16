package uk.ac.bbsrc.tgac.miso.core.data;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;

@Entity
@Table(name = "RunPacBio")
public class PacBioRun extends Run {
  private static final long serialVersionUID = 1L;

  public PacBioRun() {
    super();
  }

  @Override
  public PlatformType getPlatformType() {
    return PlatformType.PACBIO;
  }

  @Override
  public String getDeleteType() {
    return "PacBio Run";
  }

}
