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
    case "DILUTION":
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

}
