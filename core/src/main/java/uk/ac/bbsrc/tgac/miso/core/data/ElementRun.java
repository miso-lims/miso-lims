package uk.ac.bbsrc.tgac.miso.core.data;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;

@Entity
@Table(name = "RunElement")
public class ElementRun extends Run {
  private static final long serialVersionUID = 1L;

  public ElementRun() {
    super();
  }

  @Override
  public PlatformType getPlatformType() {
    return PlatformType.ELEMENT;
  }

  @Override
  public String getDeleteType() {
    return "Element Run";
  }

}
