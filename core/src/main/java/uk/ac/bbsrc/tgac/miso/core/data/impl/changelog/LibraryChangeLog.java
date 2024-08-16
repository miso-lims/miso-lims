package uk.ac.bbsrc.tgac.miso.core.data.impl.changelog;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import uk.ac.bbsrc.tgac.miso.core.data.AbstractChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.Identifiable;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.box.LibraryBoxableView;

@Entity
@Table(name = "LibraryChangeLog", indexes = {
    @Index(name = "LibraryChangeLog_libraryId_changeTime", columnList = "libraryId, changeTime")})
public class LibraryChangeLog extends AbstractChangeLog {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long libraryChangeLogId;

  @ManyToOne(fetch = FetchType.LAZY, targetEntity = LibraryImpl.class)
  @JoinColumn(name = "libraryId", nullable = false, updatable = false)
  private Identifiable library;

  @Override
  public Long getId() {
    return library.getId();
  }

  @Override
  public void setId(Long id) {
    library.setId(id);
  }

  public Long getLibraryChangeLogId() {
    return libraryChangeLogId;
  }

  public Identifiable getLibrary() {
    return library;
  }

  public void setLibrary(Library library) {
    this.library = library;
  }

  public void setLibrary(LibraryBoxableView library) {
    this.library = library;
  }

}
