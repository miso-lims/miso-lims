package uk.ac.bbsrc.tgac.miso.dto;

import static uk.ac.bbsrc.tgac.miso.dto.Dtos.*;

import com.eaglegenomics.simlims.core.Note;

import uk.ac.bbsrc.tgac.miso.core.data.HierarchyEntity;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;

public class NoteDto {
  private Long id;
  private String text;
  private boolean internalOnly;
  private String creationDate;
  private String ownerName;
  private String entityName;
  private String entityAlias;
  private Long entityId;
  private String entityType;
  private String source;

  public static NoteDto from(Note note, HierarchyEntity entity) {
    NoteDto dto = new NoteDto();
    setLong(dto::setId, note.getId(), true);
    setString(dto::setText, note.getText());
    setBoolean(dto::setInternalOnly, note.isInternalOnly(), false);
    setDateString(dto::setCreationDate, note.getCreationDate());
    setString(dto::setOwnerName, note.getOwner() != null ? note.getOwner().getFullName() : "Unknown");
    setLong(dto::setEntityId, entity.getId(), true);
    setString(dto::setEntityType, entity.getEntityType().getLabel());

    if (entity instanceof Sample) {
      Sample sample = (Sample) entity;
      dto.setEntityName(sample.getName());
      dto.setEntityAlias(sample.getAlias());
    } else if (entity instanceof Library) {
      Library library = (Library) entity;
      dto.setEntityName(library.getName());
      dto.setEntityAlias(library.getAlias());
    } else {
      dto.setEntityName("Unknown");
      dto.setEntityAlias("");
    }

    return dto;
  }

  public static NoteDto from(Note note, Sample sample) {
    NoteDto dto = new NoteDto();
    setLong(dto::setId, note.getId(), true);
    setString(dto::setText, note.getText());
    setBoolean(dto::setInternalOnly, note.isInternalOnly(), false);
    setDateString(dto::setCreationDate, note.getCreationDate());
    setString(dto::setOwnerName, note.getOwner() != null ? note.getOwner().getFullName() : "Unknown");
    setLong(dto::setEntityId, sample.getId(), true);
    setString(dto::setEntityType, sample.getEntityType().getLabel());
    dto.setEntityName(sample.getName());
    dto.setEntityAlias(sample.getAlias());
    return dto;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public boolean isInternalOnly() {
    return internalOnly;
  }

  public void setInternalOnly(boolean internalOnly) {
    this.internalOnly = internalOnly;
  }

  public String getCreationDate() {
    return creationDate;
  }

  public void setCreationDate(String creationDate) {
    this.creationDate = creationDate;
  }

  public String getOwnerName() {
    return ownerName;
  }

  public void setOwnerName(String ownerName) {
    this.ownerName = ownerName;
  }

  public String getEntityName() {
    return entityName;
  }

  public void setEntityName(String entityName) {
    this.entityName = entityName;
  }

  public String getEntityAlias() {
    return entityAlias;
  }

  public void setEntityAlias(String entityAlias) {
    this.entityAlias = entityAlias;
  }

  public Long getEntityId() {
    return entityId;
  }

  public void setEntityId(Long entityId) {
    this.entityId = entityId;
  }

  public String getEntityType() {
    return entityType;
  }

  public void setEntityType(String entityType) {
    this.entityType = entityType;
  }

  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
  }
}
