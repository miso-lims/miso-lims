package uk.ac.bbsrc.tgac.miso.dto;

import static uk.ac.bbsrc.tgac.miso.dto.Dtos.*;

import uk.ac.bbsrc.tgac.miso.core.data.LibraryDesignCode;
import uk.ac.bbsrc.tgac.miso.core.data.impl.AssayTest;
import uk.ac.bbsrc.tgac.miso.core.data.impl.AssayTest.LibraryQualificationMethod;
import uk.ac.bbsrc.tgac.miso.core.data.impl.AssayTest.PermittedSamples;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleClassImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.TissueOriginImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.TissueTypeImpl;

public class AssayTestDto {

  private Long id;
  private String alias;
  private Long tissueOriginId;
  private boolean negateTissueOrigin;
  private Long tissueTypeId;
  private boolean negateTissueType;
  private Long extractionClassId;
  private Long libraryDesignCodeId;
  private String libraryQualificationMethod;
  private Long libraryQualificationDesignCodeId;
  private boolean repeatPerTimepoint;
  private String permittedSamples;

  public static AssayTestDto from(AssayTest from) {
    AssayTestDto to = new AssayTestDto();
    setLong(to::setId, from.getId(), true);
    setString(to::setAlias, from.getAlias());
    Dtos.setId(to::setTissueOriginId, from.getTissueOrigin());
    setBoolean(to::setNegateTissueOrigin, from.isNegateTissueOrigin(), false);
    Dtos.setId(to::setTissueTypeId, from.getTissueType());
    setBoolean(to::setNegateTissueType, from.isNegateTissueType(), false);
    Dtos.setId(to::setExtractionClassId, from.getExtractionClass());
    Dtos.setId(to::setLibraryDesignCodeId, from.getLibraryDesignCode());
    setString(to::setLibraryQualificationMethod, maybeGetProperty(from.getLibraryQualificationMethod(),
        LibraryQualificationMethod::name));
    Dtos.setId(to::setLibraryQualificationDesignCodeId, from.getLibraryQualificationDesignCode());
    setBoolean(to::setRepeatPerTimepoint, from.isRepeatPerTimepoint(), false);
    setString(to::setPermittedSamples, maybeGetProperty(from.getPermittedSamples(), PermittedSamples::name));
    return to;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getAlias() {
    return alias;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  public Long getTissueOriginId() {
    return tissueOriginId;
  }

  public void setTissueOriginId(Long tissueOriginId) {
    this.tissueOriginId = tissueOriginId;
  }

  public boolean isNegateTissueOrigin() {
    return negateTissueOrigin;
  }

  public void setNegateTissueOrigin(boolean negateTissueOrigin) {
    this.negateTissueOrigin = negateTissueOrigin;
  }

  public Long getTissueTypeId() {
    return tissueTypeId;
  }

  public void setTissueTypeId(Long tissueTypeId) {
    this.tissueTypeId = tissueTypeId;
  }

  public boolean isNegateTissueType() {
    return negateTissueType;
  }

  public void setNegateTissueType(boolean negateTissueType) {
    this.negateTissueType = negateTissueType;
  }

  public Long getExtractionClassId() {
    return extractionClassId;
  }

  public void setExtractionClassId(Long extractionClassId) {
    this.extractionClassId = extractionClassId;
  }

  public Long getLibraryDesignCodeId() {
    return libraryDesignCodeId;
  }

  public void setLibraryDesignCodeId(Long libraryDesignCodeId) {
    this.libraryDesignCodeId = libraryDesignCodeId;
  }

  public String getLibraryQualificationMethod() {
    return libraryQualificationMethod;
  }

  public void setLibraryQualificationMethod(String libraryQualificationMethod) {
    this.libraryQualificationMethod = libraryQualificationMethod;
  }

  public Long getLibraryQualificationDesignCodeId() {
    return libraryQualificationDesignCodeId;
  }

  public void setLibraryQualificationDesignCodeId(Long libraryQualificationDesignCodeId) {
    this.libraryQualificationDesignCodeId = libraryQualificationDesignCodeId;
  }

  public boolean isRepeatPerTimepoint() {
    return repeatPerTimepoint;
  }

  public void setRepeatPerTimepoint(boolean repeatPerTimepoint) {
    this.repeatPerTimepoint = repeatPerTimepoint;
  }

  public String getPermittedSamples() {
    return permittedSamples;
  }

  public void setPermittedSamples(String permittedSamples) {
    this.permittedSamples = permittedSamples;
  }

  public AssayTest to() {
    AssayTest to = new AssayTest();
    setLong(to::setId, getId(), false);
    setString(to::setAlias, getAlias());
    setObject(to::setTissueOrigin, TissueOriginImpl::new, getTissueOriginId());
    setBoolean(to::setNegateTissueOrigin, isNegateTissueOrigin(), false);
    setObject(to::setTissueType, TissueTypeImpl::new, getTissueTypeId());
    setBoolean(to::setNegateTissueType, isNegateTissueType(), false);
    setObject(to::setExtractionClass, SampleClassImpl::new, getExtractionClassId());
    setBoolean(to::setNegateTissueType, isNegateTissueType(), false);
    setObject(to::setLibraryDesignCode, LibraryDesignCode::new, getLibraryDesignCodeId());
    setObject(to::setLibraryQualificationMethod, getLibraryQualificationMethod(), LibraryQualificationMethod::valueOf);
    setObject(to::setLibraryQualificationDesignCode, LibraryDesignCode::new, getLibraryQualificationDesignCodeId());
    setBoolean(to::setRepeatPerTimepoint, isRepeatPerTimepoint(), false);
    setObject(to::setPermittedSamples, getPermittedSamples(), PermittedSamples::valueOf);
    return to;
  }
}
