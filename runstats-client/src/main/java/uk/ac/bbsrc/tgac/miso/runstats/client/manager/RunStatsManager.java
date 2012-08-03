/*
 * Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
 * MISO project contacts: Robert Davey, Mario Caccamo @ TGAC
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

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import uk.ac.bbsrc.tgac.miso.core.data.*;
import uk.ac.bbsrc.tgac.miso.core.data.impl.RunImpl;
import uk.ac.bbsrc.tgac.miso.runstats.client.RunStatsException;
import uk.ac.bbsrc.tgac.qc.run.ReportTable;
import uk.ac.bbsrc.tgac.qc.run.Reports;
import uk.ac.bbsrc.tgac.qc.run.ReportsDecorator;
import uk.ac.bbsrc.tgac.qc.run.RunProperty;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

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
    }
    catch (SQLException e) {
      throw new RunStatsException("Cannot retrieve the list of per-base summary analyses: " + e.getMessage());
    }
  }

  public List<String> listGlobalRunAnalyses() throws RunStatsException {
    try {
      return reports.listGlobalAnalyses();
    }
    catch (SQLException e) {
      throw new RunStatsException("Cannot retrieve the list of global run-based analyses: " + e.getMessage());
    }
  }

  public boolean hasStatsForRun(Run run) throws RunStatsException {
    Map<RunProperty, String> map = new HashMap<RunProperty, String>();
    map.put(RunProperty.run, run.getAlias());
    try {
      ReportTable rt = reports.getAverageValues(map);
      return rt != null && !rt.isEmpty();
    }
    catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  public JSONObject getSummaryStatsForRun(Run run) throws RunStatsException {
    JSONObject report = new JSONObject();
    ReportTable rt;

    Map<RunProperty, String> map = new HashMap<RunProperty, String>();
    map.put(RunProperty.run, run.getAlias());
    try {
      rt = reports.getAverageValues(map);
      if (rt == null) {
        return null;
      }
      report.put("runSummary", JSONArray.fromObject(rt.toJSON()));
    }
    catch (SQLException e) {
      e.printStackTrace();
    }
    catch (IOException e) {
      e.printStackTrace();
    }

    if (!((RunImpl) run).getSequencerPartitionContainers().isEmpty()) {
      JSONObject containers = new JSONObject();
      for (SequencerPartitionContainer<SequencerPoolPartition> container : ((RunImpl) run).getSequencerPartitionContainers()) {
        JSONObject f = new JSONObject();
        f.put("idBarcode", container.getIdentificationBarcode());

        JSONArray partitions = new JSONArray();
        for (SequencerPoolPartition part : container.getPartitions()) {
          JSONObject partition = new JSONObject();

          map.put(RunProperty.lane, Integer.toString(part.getPartitionNumber()));

          try {
            rt = reports.getAverageValues(map);
            if (rt != null) {
              partition.put("partitionSummary", JSONArray.fromObject(rt.toJSON()));
            }
          }
          catch (SQLException e) {
            e.printStackTrace();
          }
          catch (IOException e) {
            e.printStackTrace();
          }

          //clear any previous barcode query
          map.remove(RunProperty.barcode);

          if (part.getPool() != null) {
            Pool<? extends Poolable> pool = part.getPool();
            for (Dilution d : pool.getDilutions()) {
              Library l = d.getLibrary();
              if (l.getTagBarcode() != null) {
                try {
                  map.put(RunProperty.barcode, l.getTagBarcode().getSequence());
                  rt = reports.getAverageValues(map);
                  if (rt != null) {
                    partition.put(l.getTagBarcode().getSequence(), JSONArray.fromObject(rt.toJSON()));
                  }
                }
                catch (SQLException e) {
                  e.printStackTrace();
                }
                catch (IOException e) {
                  e.printStackTrace();
                }
              }
            }
          }

          partitions.add(part.getPartitionNumber() - 1, partition);
        }
        f.put("partitions", partitions);
        containers.put(container.getContainerId(), f);
      }
      report.put("containers", containers);
    }
    return report;
  }

  public JSONObject getSummaryStatsForLane(Run run, int laneNumber) throws RunStatsException {
    Map<RunProperty, String> map = new HashMap<RunProperty, String>();
    map.put(RunProperty.run, run.getAlias());
    map.put(RunProperty.lane, String.valueOf(laneNumber));
    ReportTable rt;

    JSONObject partition = new JSONObject();
    try {
      rt = reports.getAverageValues(map);
      if (rt != null) {
        partition.put("partitionSummary", JSONArray.fromObject(rt.toJSON()));
      }
    }
    catch (SQLException e) {
      e.printStackTrace();
    }
    catch (IOException e) {
      e.printStackTrace();
    }

    //clear any previous barcode query
    map.remove(RunProperty.barcode);
    if (!((RunImpl) run).getSequencerPartitionContainers().isEmpty()) {
      for (SequencerPartitionContainer<SequencerPoolPartition> container : ((RunImpl) run).getSequencerPartitionContainers()) {
        SequencerPoolPartition part = container.getPartitionAt(laneNumber);
        if (part.getPartitionNumber() == laneNumber) {
          if (part.getPool() != null) {
            Pool<? extends Poolable> pool = part.getPool();
            for (Dilution d : pool.getDilutions()) {
              Library l = d.getLibrary();
              if (l.getTagBarcode() != null) {
                try {
                  map.put(RunProperty.barcode, l.getTagBarcode().getSequence());
                  rt = reports.getAverageValues(map);
                  if (rt != null) {
                    partition.put(l.getTagBarcode().getSequence(), JSONArray.fromObject(rt.toJSON()));
                  }
                }
                catch (SQLException e) {
                  e.printStackTrace();
                }
                catch (IOException e) {
                  e.printStackTrace();
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
    Map<RunProperty, String> map = new HashMap<RunProperty, String>();
    map.put(RunProperty.run, run.getAlias());
    map.put(RunProperty.lane, String.valueOf(laneNumber));
    String pair;
    if (run.getPairedEnd()) {
      pair = "1";
    }
    else {
      pair = "2";
    }
    map.put(RunProperty.pair, pair);

    JSONObject laneQuality = new JSONObject();
    Map<String, ReportTable> resultMap = new HashMap<String, ReportTable>();
    try {
      resultMap = reportsDecorator.getPerPositionBaseSequenceQuality(map);

//      for (Map.Entry<String, ReportTable> entry : resultMap.entrySet()) {
//        System.out.println("Key : " + entry.getKey()
//                           + " Value : " + entry.getValue().toCSV() + "\n");
//      }

//      JSONArray quality_lower_quartile = JSONArray.fromObject(resultMap.get("quality_lower_quartile").toJSON());
//      JSONArray quality_10th_percentile = JSONArray.fromObject(resultMap.get("quality_10th_percentile").toJSON());
//      JSONArray quality_90th_percentile = JSONArray.fromObject(resultMap.get("quality_90th_percentile").toJSON());
//      JSONArray quality_mean = JSONArray.fromObject(resultMap.get("quality_mean").toJSON());
//      JSONArray quality_median = JSONArray.fromObject(resultMap.get("quality_median").toJSON());
//      JSONArray quality_upper_quartile = JSONArray.fromObject(resultMap.get("quality_upper_quartile").toJSON());

      ArrayList<String> quality_lower_quartile = new ArrayList<String>(Arrays.asList(resultMap.get("quality_lower_quartile").toCSV().split("\n")));
      ArrayList<String> quality_10th_percentile = new ArrayList<String>(Arrays.asList(resultMap.get("quality_10th_percentile").toCSV().split("\n")));
      ArrayList<String> quality_90th_percentile = new ArrayList<String>(Arrays.asList(resultMap.get("quality_90th_percentile").toCSV().split("\n")));
      ArrayList<String> quality_mean = new ArrayList<String>(Arrays.asList(resultMap.get("quality_mean").toCSV().split("\n")));
      ArrayList<String> quality_median = new ArrayList<String>(Arrays.asList(resultMap.get("quality_median").toCSV().split("\n")));
      ArrayList<String> quality_upper_quartile = new ArrayList<String>(Arrays.asList(resultMap.get("quality_upper_quartile").toCSV().split("\n")));

      if (quality_lower_quartile.size() > 1) {
        JSONArray jsonArray = new JSONArray();
        for (int index = 1; index < quality_lower_quartile.size(); index++) {
          JSONObject eachBase = new JSONObject();
          eachBase.put("base", quality_lower_quartile.get(index).split(",")[0]);
          eachBase.put("mean", quality_mean.get(index).split(",")[2]);
          eachBase.put("median", quality_median.get(index).split(",")[2]);
          eachBase.put("lowerquartile", quality_lower_quartile.get(index).split(",")[2]);
          eachBase.put("upperquartile", quality_upper_quartile.get(index).split(",")[2]);
          eachBase.put("tenthpercentile", quality_10th_percentile.get(index).split(",")[2]);
          eachBase.put("ninetiethpercentile", quality_90th_percentile.get(index).split(",")[2]);
          jsonArray.add(eachBase);
        }
        laneQuality.put("stats", jsonArray);
      }
      else {
        laneQuality.put("stats", JSONArray.fromObject("[]"));
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    return laneQuality;
  }
}
