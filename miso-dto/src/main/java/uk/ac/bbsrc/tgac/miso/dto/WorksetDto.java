package uk.ac.bbsrc.tgac.miso.dto;

import java.util.List;
import java.util.Set;

import com.eaglegenomics.simlims.core.Note;

public class WorksetDto {

  private Long id;
  private String alias;
  private String description;
  private Long categoryId;
  private Long stageId;
  private List<Long> sampleIds;
  private List<Long> libraryIds;
  private List<Long> libraryAliquotIds;
  private String creator;
  private String lastModified;
  private Set<Note> notes;

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

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Long getCategoryId() {
    return categoryId;
  }

  public void setCategoryId(Long categoryId) {
    this.categoryId = categoryId;
  }

  public Long getStageId() {
    return stageId;
  }

  public void setStageId(Long stageId) {
    this.stageId = stageId;
  }

  public List<Long> getSampleIds() {
    return sampleIds;
  }

  public void setSampleIds(List<Long> sampleIds) {
    this.sampleIds = sampleIds;
  }

  public List<Long> getLibraryIds() {
    return libraryIds;
  }

  public void setLibraryIds(List<Long> libraryIds) {
    this.libraryIds = libraryIds;
  }

  public List<Long> getLibraryAliquotIds() {
    return libraryAliquotIds;
  }

  public void setLibraryAliquotIds(List<Long> libraryAliquotIds) {
    this.libraryAliquotIds = libraryAliquotIds;
  }

  public String getCreator() {
    return creator;
  }

  public void setCreator(String creator) {
    this.creator = creator;
  }

  public String getLastModified() {
    return lastModified;
  }

  public void setLastModified(String lastModified) {
    this.lastModified = lastModified;
  }

  public Set<Note> getNotes() {
    return notes;
  }

  public void setNotes(Set<Note> note) {
    this.notes = notes;
  }

}
