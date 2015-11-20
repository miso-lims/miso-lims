package uk.ac.bbsrc.tgac.miso.core.data.impl;

import uk.ac.bbsrc.tgac.miso.core.data.Sample;

/**
 * uk.ac.bbsrc.tgac.miso.core.data.impl
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @date 22/10/13
 * @since 0.2.1-SNAPSHOT
 */
@Deprecated
public class OverviewSampleGroup extends EntityGroupImpl<ProjectOverview, Sample> {
  private ProjectOverview parent;
  private long groupId;
  private String name;

  @Override
  public ProjectOverview getParent() {
    return parent;
  }

  @Override
  public void setParent(ProjectOverview parent) {
    this.parent = parent;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public void setName(String name) {
    this.name = name;
  }

  @Override
  public long getId() {
    return groupId;
  }

  @Override
  public void setId(long groupId) {
    this.groupId = groupId;
  }
}
