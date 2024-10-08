package uk.ac.bbsrc.tgac.miso.core.data.qc;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl;

@Entity
@Table(name = "LibraryQC")
public class LibraryQC extends QC {

  private static final long serialVersionUID = 1L;

  @ManyToOne(targetEntity = LibraryImpl.class)
  @JoinColumn(name = "library_libraryId")
  private Library library;

  @OneToMany(mappedBy = "qc", cascade = CascadeType.REMOVE)
  private List<LibraryQcControlRun> controls;

  public Library getLibrary() {
    return library;
  }

  public void setLibrary(Library library) {
    this.library = library;
  }

  @Override
  public QualityControllable<?> getEntity() {
    return library;
  }

  @Override
  public List<LibraryQcControlRun> getControls() {
    if (controls == null) {
      controls = new ArrayList<>();
    }
    return controls;
  }

}
