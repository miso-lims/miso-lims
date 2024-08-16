package uk.ac.bbsrc.tgac.miso.core.data.impl;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryDesign;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryDesignCode;

@Entity
public class DetailedLibraryTemplate extends LibraryTemplate {

  private static final long serialVersionUID = 1L;

  @ManyToOne
  @JoinColumn(name = "libraryDesignId")
  private LibraryDesign libraryDesign;

  @ManyToOne
  @JoinColumn(name = "libraryDesignCodeId")
  private LibraryDesignCode libraryDesignCode;

  public LibraryDesign getLibraryDesign() {
    return libraryDesign;
  }

  public void setLibraryDesign(LibraryDesign libraryDesign) {
    this.libraryDesign = libraryDesign;
  }

  public LibraryDesignCode getLibraryDesignCode() {
    return libraryDesignCode;
  }

  public void setLibraryDesignCode(LibraryDesignCode libraryDesignCode) {
    this.libraryDesignCode = libraryDesignCode;
  }

}
