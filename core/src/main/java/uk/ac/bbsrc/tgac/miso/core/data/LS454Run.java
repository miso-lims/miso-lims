package uk.ac.bbsrc.tgac.miso.core.data;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.eaglegenomics.simlims.core.User;

@Entity
@Table(name = "RunLS454")
public class LS454Run extends Run {
  private static final long serialVersionUID = 1L;
  private int cycles;

  public LS454Run() {
    super();
  }

  public LS454Run(User user) {
    super(user);
  }

  public int getCycles() {
    return cycles;
  }

  public void setCycles(int cycles) {
    this.cycles = cycles;
  }
}
