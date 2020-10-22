package uk.ac.bbsrc.tgac.miso.dto.dashi;

import java.util.List;

import uk.ac.bbsrc.tgac.miso.dto.QcNodeDto;

public class QcHierarchyNodeDto extends QcNodeDto {

  private List<QcHierarchyNodeDto> children;

  public List<QcHierarchyNodeDto> getChildren() {
    return children;
  }

  public void setChildren(List<QcHierarchyNodeDto> children) {
    this.children = children;
  }

}
