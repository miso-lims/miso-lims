package uk.ac.bbsrc.tgac.miso.core.data.impl.changelog;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import uk.ac.bbsrc.tgac.miso.core.data.AbstractChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.Identifiable;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.box.LibraryAliquotBoxableView;

@Entity
public class LibraryAliquotChangeLog extends AbstractChangeLog {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long aliquotChangeLogId;

  @ManyToOne(fetch = FetchType.LAZY, targetEntity = LibraryAliquot.class)
  @JoinColumn(name = "aliquotId", nullable = false, updatable = false)
  private Identifiable libraryAliquot;

  @Override
  public Long getId() {
    return aliquotChangeLogId;
  }

  @Override
  public void setId(Long id) {
    this.aliquotChangeLogId = id;
  }

  public Identifiable getLibraryAliquot() {
    return libraryAliquot;
  }

  public void setLibraryAliquot(LibraryAliquot aliquot) {
    this.libraryAliquot = aliquot;
  }

  public void setLibraryAliquot(LibraryAliquotBoxableView aliquot) {
    this.libraryAliquot = aliquot;
  }

}
