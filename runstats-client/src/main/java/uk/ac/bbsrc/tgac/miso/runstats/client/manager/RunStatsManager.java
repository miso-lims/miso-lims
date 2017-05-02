/*
 * Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
 * MISO project contacts: Robert Davey @ TGAC
 * *********************************************************************
 *
 * This file is part of MISO.
 *
 * MISO is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MISO is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MISO.  If not, see <http://www.gnu.org/licenses/>.
 *
 * *********************************************************************
 */

package uk.ac.bbsrc.tgac.miso.runstats.client.manager;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import uk.ac.bbsrc.tgac.miso.core.data.Index;
import uk.ac.bbsrc.tgac.miso.core.data.Partition;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.PoolableElementView;
import uk.ac.bbsrc.tgac.miso.runstats.client.RunStatsException;
import uk.ac.tgac.statsdb.exception.ConsumerException;
import uk.ac.tgac.statsdb.run.ReportTable;
import uk.ac.tgac.statsdb.run.Reports;
import uk.ac.tgac.statsdb.run.ReportsDecorator;
import uk.ac.tgac.statsdb.run.RunProperty;
import uk.ac.tgac.statsdb.run.consumer.D3PlotConsumer;

/**
 * uk.ac.bbsrc.tgac.miso.runstats.client.manager
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @date 13/03/12
 * @since 0.1.6
 */
public class RunStatsManager {
  protected static final Logger log = LoggerFactory.getLogger(RunStatsManager.class);

  Reports reports;

  ReportsDecorator reportsDecorator;

  public RunStatsManager(DataSource dataSource) {
    this.reports = new Reports(dataSource);
    this.reportsDecorator = new ReportsDecorator(reports);
  }

  public RunStatsManager(JdbcTemplate template) {
    this(template.getDataSource());
  }

  public List<String> listPerBaseSummaryAnalyses() throws RunStatsException {
    try {
      return reports.listPerBaseSummaryAnalyses();
    } catch (SQLException e) {
      log.error("list per base summary analyses", e);
      throw new RunStatsException("Cannot retrieve the list of per-base summary analyses: " + e.getMessage());
    }
  }

  public List<String> listGlobalRunAnalyses() throws RunStatsException {
    try {
      return reports.listGlobalAnalyses();
    } catch (SQLException e) {
      log.error("list global analyses", e);
      throw new RunStatsException("Cannot retrieve the list of global run-based analyses: " + e.getMessage());
    }
  }

  public boolean hasStatsForRun(Run run) throws RunStatsException {
    Map<RunProperty, String> map = new HashMap<>();
    map.put(RunProperty.run, run.getAlias());
    try {
      ReportTable rt = reports.getAverageValues(map);
      return rt != null && !rt.isEmpty();
    } catch (SQLException e) {
      log.error("has stats for run", e);
      return false;
    }
  }

  public JSONObject getSummaryStatsForRun(Run run) throws RunStatsException {
    JSONObject report = new JSONObject();
    ReportTable rt;

    Map<RunProperty, String> map = new HashMap<>();
    map.put(RunProperty.run, run.getAlias());
    try {
      rt = reports.getAverageValues(map);
      if (rt == null) {
        return null;
      }
      report.put("runSummary", JSONArray.fromObject(rt.toJSON()));
    } catch (SQLException e) {
      log.error("get summary stats for run", e);
    } catch (IOException e) {
      log.error("get summary stats for run", e);
    }

    if (!run.getSequencerPartitionContainers().isEmpty()) {
      JSONObject containers = new JSONObject();
      for (SequencerPartitionContainer container : run.getSequencerPartitionContainers()) {
        JSONObject f = new JSONObject();
        f.put("idBarcode", container.getIdentificationBarcode());

        JSONArray partitions = new JSONArray();
        for (Partition part : container.getPartitions()) {
          JSONObject partition = new JSONObject();

          map.put(RunProperty.lane, Integer.toString(part.getPartitionNumber()));

          try {
            rt = reports.getAverageValues(map);
            if (rt != null) {
              partition.put("partitionSummary", JSONArray.fromObject(rt.toJSON()));
            }
          } catch (SQLException e) {
            log.error("get summary stats for run", e);
          } catch (IOException e) {
            log.error("get summary stats for run", e);
          }

          // clear any previous barcode query
          map.remove(RunProperty.barcode);
          if (part.getPool() != null) {
            Pool pool = part.getPool();
            for (PoolableElementView d : pool.getPoolableElementViews()) {
              if (!d.getIndices().isEmpty()) {
                for (Index index : d.getIndices()) {
                  map.remove(RunProperty.barcode);
                  try {
                    map.put(RunProperty.barcode, index.getSequence());
                    rt = reports.getAverageValues(map);
                    if (rt != null) {
                      partition.put(index.getSequence(), JSONArray.fromObject(rt.toJSON()));
                    }
                  } catch (SQLException e) {
                    log.error("get summary stats for run", e);
                  } catch (IOException e) {
                    log.error("get summary stats for run", e);
                  }
                }
              }
            }
          }

          partitions.add(part.getPartitionNumber() - 1, partition);
        }
        f.put("partitions", partitions);
        containers.put(container.getId(), f);
      }
      report.put("containers", containers);
    }
    return report;
  }

  public JSONObject getSummaryStatsForLane(Run run, int laneNumber) throws RunStatsException {
    Map<RunProperty, String> map = new HashMap<>();
    map.put(RunProperty.run, run.getAlias());
    map.put(RunProperty.lane, String.valueOf(laneNumber));
    ReportTable rt;

    JSONObject partition = new JSONObject();
    try {
      rt = reports.getAverageValues(map);
      if (rt != null) {
        partition.put("partitionSummary", JSONArray.fromObject(rt.toJSON()));
      }
    } catch (SQLException e) {
      log.error("get summary stats for lane", e);
    } catch (IOException e) {
      log.error("get summary stats for lane", e);
    }

    // clear any previous barcode query
    map.remove(RunProperty.barcode);
    if (!run.getSequencerPartitionContainers().isEmpty()) {
      for (SequencerPartitionContainer container : run.getSequencerPartitionContainers()) {
        Partition part = container.getPartitionAt(laneNumber);
        if (part.getPartitionNumber() == laneNumber) {
          if (part.getPool() != null) {
            Pool pool = part.getPool();
            for (PoolableElementView d : pool.getPoolableElementViews()) {
              if (!d.getIndices().isEmpty()) {
                for (Index index : d.getIndices()) {
                  map.remove(RunProperty.barcode);
                  try {
                    map.put(RunProperty.barcode, index.getSequence());
                    rt = reports.getAverageValues(map);
                    if (rt != null) {
                      partition.put(index.getSequence(), JSONArray.fromObject(rt.toJSON()));
                    }
                  } catch (SQLException e) {
                    log.error("get summary stats for lane", e);
                  } catch (IOException e) {
                    log.error("get summary stats for lane", e);
                  }
                }
              }
            }
          }
          break;
        }
      }
    }

    return partition;
  }

  public JSONObject getCompleteStatsForLane(String runAlias, int laneNumber) throws RunStatsException {
    return null;
  }

  public JSONObject getPerPositionBaseSequenceQualityForLane(Run run, int laneNumber) throws RunStatsException {
    D3PlotConsumer d3p = new D3PlotConsumer(reportsDecorator);
    try {
      return d3p.getPerPositionBaseSequenceQualityForLane(run.getAlias(), run.getPairedEnd(), laneNumber);
    } catch (ConsumerException e) {
      log.error("cannot generate D3 plot JSON for run " + run.getAlias(), e);
      throw new RunStatsException("Cannot generate D3 plot JSON for run " + run.getAlias() + ": " + e.getMessage());
    }
  }

  public JSONObject getPerPositionBaseContentForLane(Run run, int laneNumber) throws RunStatsException {
    D3PlotConsumer d3p = new D3PlotConsumer(reportsDecorator);
    try {
      return d3p.getPerPositionBaseContentForLane(run.getAlias(), run.getPairedEnd(), laneNumber);
    } catch (ConsumerException e) {
      log.error("cannot generate D3 plot JSON for run " + run.getAlias(), e);
      throw new RunStatsException("Cannot generate D3 plot JSON for run " + run.getAlias() + ": " + e.getMessage());
    }
  }
}
