package ca.on.oicr.pinery.lims.miso;

import java.util.HashSet;
import java.util.Set;

import ca.on.oicr.pinery.api.Box;
import ca.on.oicr.pinery.api.BoxPosition;
import ca.on.oicr.pinery.lims.DefaultBox;
import ca.on.oicr.pinery.lims.DefaultBoxPosition;

public class MisoBoxPosition extends DefaultBox implements BoxPosition {

  private String position;
  private String sampleId;

  @Override
  public String getPosition() {
    return position;
  }

  @Override
  public void setPosition(String position) {
    this.position = position;
  }

  @Override
  public String getSampleId() {
    return sampleId;
  }

  @Override
  public void setSampleId(String sampleId) {
    this.sampleId = sampleId;
  }

  @Override
  public Set<BoxPosition> getPositions() {
    throw new UnsupportedOperationException("Unintended use of temporary object");
  }

  @Override
  public void setPositions(Set<BoxPosition> positions) {
    throw new UnsupportedOperationException("Unintended use of temporary object");
  }

  public void setSampleId(String targetType, Long targetId) {
    this.sampleId = getIdPrefix(targetType) + targetId;
  }

  private static String getIdPrefix(String targetType) {
    switch (targetType) {
    case "POOL":
      return "IPO";
    case "SAMPLE":
      return "SAM";
    case "LIBRARY":
      return "LIB";
    case "LIBRARY_ALIQUOT":
      return "LDI";
    default:
      throw new IllegalArgumentException("TargetType does not seem to be a Sample, Library, or Pool");
    }
  }

  public Box getBox() {
    Box box = new DefaultBox();
    box.setId(this.getId());
    box.setName(this.getName());
    box.setDescription(this.getDescription());
    box.setLocation(this.getLocation());
    box.setRows(this.getRows());
    box.setColumns(this.getColumns());
    box.setPositions(new HashSet<BoxPosition>());
    return box;
  }

  public BoxPosition getBoxPosition() {
    BoxPosition pos = new DefaultBoxPosition();
    pos.setPosition(this.getPosition());
    pos.setSampleId(this.getSampleId());
    return pos;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((position == null) ? 0 : position.hashCode());
    result = prime * result + ((sampleId == null) ? 0 : sampleId.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    MisoBoxPosition other = (MisoBoxPosition) obj;
    if (position == null) {
      if (other.position != null) return false;
    } else if (!position.equals(other.position)) return false;
    if (sampleId == null) {
      if (other.sampleId != null) return false;
    } else if (!sampleId.equals(other.sampleId)) return false;
    return true;
  }

}
