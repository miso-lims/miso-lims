package uk.ac.bbsrc.tgac.miso.core.data;

public interface GroupIdentifiable extends Aliasable {

  public String getGroupId();

  public void setGroupId(String groupId);

  public String getGroupDescription();

  public void setGroupDescription(String groupDescription);

  public GroupIdentifiable getGroupIdentifiableParent();

  /**
   * Searches the hierarchy until a Group ID is found. Returns null if no entity has a Group ID.
   * 
   * @return nearest entity with a non-empty group ID. May be the current entity.
   */
  public default GroupIdentifiable getEffectiveGroupIdEntity() {
    if (getGroupId() != null) {
      return this;
    }
    for (GroupIdentifiable parent = getGroupIdentifiableParent(); parent != null; parent = parent.getGroupIdentifiableParent()) {
      if (parent.getGroupId() != null) {
        return parent;
      }
    }
    return null;
  }

}
