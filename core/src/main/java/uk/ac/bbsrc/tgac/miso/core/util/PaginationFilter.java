package uk.ac.bbsrc.tgac.miso.core.util;

public class PaginationFilter {
  private Long projectId;
  private String query;

  public Long getProjectId() {
    return projectId;
  }

  public String getQuery() {
    return query;
  }

  /**
   * Restrict the results to only member of a project.
   */
  public void setProjectId(Long projectId) {
    this.projectId = projectId;
  }

  /**
   * Restrict the results to only items that match a particular search query.
   */
  public void setQuery(String query) {
    this.query = query;
  }
}
