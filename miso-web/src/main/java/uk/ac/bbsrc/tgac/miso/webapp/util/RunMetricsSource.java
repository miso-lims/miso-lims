package uk.ac.bbsrc.tgac.miso.webapp.util;

import uk.ac.bbsrc.tgac.miso.core.data.Run;

/**
 * Get metrics JSON from a source (database or remote system) for display
 */
public interface RunMetricsSource {
  /**
   * Fetch the metrics for a run
   * 
   * @param run The run to scan. This run will have a run ID and alias
   * @return a string containing a JSON array or null
   */
  String fetchMetrics(Run run);
}
