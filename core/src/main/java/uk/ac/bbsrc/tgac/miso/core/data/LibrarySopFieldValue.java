package uk.ac.bbsrc.tgac.miso.core.data;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import uk.ac.bbsrc.tgac.miso.core.data.LibrarySopFieldValue.LibrarySopFieldValueId;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

@Entity
@Table(name = "LibrarySopFieldValue")
@IdClass(LibrarySopFieldValueId.class)
public class LibrarySopFieldValue extends SopFieldValue {

  public static class LibrarySopFieldValueId implements Serializable {
    public static final long serialVersionUID = 1L;
    private Library library;
    private SopField sopField;

    public Library getLibrary() {
      return library;
    }

    public void setLibrary(Library library) {
      this.library = library;
    }

    public SopField getSopField() {
      return sopField;
    }

    public void setSopField(SopField sopField) {
      this.sopField = sopField;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      }
      if (obj == null || getClass() != obj.getClass()) {
        return false;
      }
      LibrarySopFieldValueId other = (LibrarySopFieldValueId) obj;
      return Objects.equals(library, other.library) && Objects.equals(sopField, other.sopField);
    }

    @Override
    public int hashCode() {
      return Objects.hash(library, sopField);
    }

  }

  private static final long serialVersionUID = 1L;

  @Id
  @ManyToOne(targetEntity = LibraryImpl.class)
  @JoinColumn(name = "libraryId")
  private Library library;

  public Library getLibrary() {
    return library;
  }

  public void setLibrary(Library library) {
    this.library = library;
  }

  @Override
  public boolean equals(Object obj) {
    return super.equals(obj) && LimsUtils.equals(this, obj, LibrarySopFieldValue::getLibrary);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), library);
  }

}
