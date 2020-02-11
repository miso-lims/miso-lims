package uk.ac.bbsrc.tgac.miso.core.service.naming;

import org.springframework.stereotype.Service;

@Service
public class NamingSchemeHolder {

  private NamingScheme primary;
  private NamingScheme secondary;

  public NamingScheme getPrimary() {
    return primary;
  }

  public void setPrimary(NamingScheme primary) {
    this.primary = primary;
  }

  public NamingScheme getSecondary() {
    return secondary;
  }

  public void setSecondary(NamingScheme secondary) {
    this.secondary = secondary;
  }

  public NamingScheme get(boolean secondary) {
    return secondary ? getSecondary() : getPrimary();
  }

}
