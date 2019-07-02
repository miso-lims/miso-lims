package uk.ac.bbsrc.tgac.miso.core.data.impl.changelog;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import uk.ac.bbsrc.tgac.miso.core.data.AbstractChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryAliquot;

@Entity
public class LibraryAliquotChangeLog extends AbstractChangeLog {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long aliquotChangeLogId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "aliquotId", nullable = false, updatable = false)
  private LibraryAliquot libraryAliquot;

  @Override
  public Long getId() {
    return aliquotChangeLogId;
  }

  @Override
  public void setId(Long id) {
    this.aliquotChangeLogId = id;
  }

  public LibraryAliquot getLibraryAliquot() {
    return libraryAliquot;
  }

  public void setLibraryAliquot(LibraryAliquot aliquot) {
    this.libraryAliquot = aliquot;
  }

}
