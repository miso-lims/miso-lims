package uk.ac.bbsrc.tgac.miso.core.data.impl.changelog;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import uk.ac.bbsrc.tgac.miso.core.data.AbstractChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution;

@Entity
public class DilutionChangeLog extends AbstractChangeLog {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long dilutionChangeLogId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "dilutionId", nullable = false, updatable = false)
  private LibraryDilution dilution;

  @Override
  public Long getId() {
    return dilutionChangeLogId;
  }

  @Override
  public void setId(Long id) {
    this.dilutionChangeLogId = id;
  }

  public LibraryDilution getDilution() {
    return dilution;
  }

  public void setDilution(LibraryDilution dilution) {
    this.dilution = dilution;
  }

}
