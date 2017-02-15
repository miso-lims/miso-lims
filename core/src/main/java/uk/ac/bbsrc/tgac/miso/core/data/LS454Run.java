package uk.ac.bbsrc.tgac.miso.core.data;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "RunLS454")
public class LS454Run extends Run {
  private int cycles;

  public int getCycles() {
    return cycles;
  }

  public void setCycles(int cycles) {
    this.cycles = cycles;
  }
}
