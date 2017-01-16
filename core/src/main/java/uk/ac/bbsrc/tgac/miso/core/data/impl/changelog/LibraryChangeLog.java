package uk.ac.bbsrc.tgac.miso.core.data.impl.changelog;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Index;
import org.hibernate.annotations.Table;

import uk.ac.bbsrc.tgac.miso.core.data.AbstractChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl;

@Entity
@Table(appliesTo = "LibraryChangeLog", indexes = {
    @Index(name = "LibraryChangeLog_libraryId_changeTime", columnNames = { "libraryId", "changeTime" }) })
public class LibraryChangeLog extends AbstractChangeLog {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long libraryChangeLogId;

  @ManyToOne(fetch = FetchType.LAZY, targetEntity = LibraryImpl.class)
  @JoinColumn(name = "libraryId", nullable = false, updatable = false)
  private Library library;

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

}
