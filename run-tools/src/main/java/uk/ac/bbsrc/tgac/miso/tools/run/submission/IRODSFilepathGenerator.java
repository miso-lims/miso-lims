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

package uk.ac.bbsrc.tgac.miso.tools.run.submission;

import net.sourceforge.fluxion.spi.ServiceProvider;
import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.pub.*;
import org.irods.jargon.core.query.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.bbsrc.tgac.miso.core.data.*;
import uk.ac.bbsrc.tgac.miso.core.exception.SubmissionException;
import uk.ac.bbsrc.tgac.miso.core.service.submission.FilePathGenerator;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

import java.io.File;
import java.io.FilenameFilter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: davey
 * Date: 08/10/12
 * Time: 15:15
 */
@ServiceProvider
public class IRODSFilepathGenerator implements FilePathGenerator {
  protected static final Logger log = LoggerFactory.getLogger(IRODSFilepathGenerator.class);

  String basePath = "";
  private IRODSFileSystem irodsFileSystem;
  private IRODSAccount account;
  private IRODSAccessObjectFactory irodsAccessObjectFactory;
  private IRODSGenQueryExecutor queryExecutorAO;

  public IRODSFilepathGenerator(IRODSFileSystem irodsFileSystem, IRODSAccount account) {
    this.irodsFileSystem = irodsFileSystem;
    this.account = account;

    try {
      this.irodsAccessObjectFactory = irodsFileSystem.getIRODSAccessObjectFactory();
      this.queryExecutorAO = irodsAccessObjectFactory.getIRODSGenQueryExecutor(account);
    }
    catch (JargonException e) {
      log.error("Cannot create IRODSFilepathGenerator instance: " + e.getMessage());
      e.printStackTrace();
    }
  }

  public void setBaseReadPath(String basePath) {
    this.basePath = basePath;
  }

  @Override
  public Set<File> generateFilePath(SequencerPoolPartition partition, Dilution l) throws SubmissionException {
    Pool<? extends Poolable> pool = partition.getPool();
    if (pool != null) {
      if (pool.getExperiments() != null) {
        List<String> filePaths = new ArrayList<String>();
        Set<File> fps = new HashSet<File>();
        try {
          IRODSGenQueryBuilder builder = new IRODSGenQueryBuilder(true, null);
          try {
            builder.addSelectAsGenQueryValue(RodsGenQueryEnum.COL_DATA_NAME)
              .addConditionAsGenQueryField(
                      RodsGenQueryEnum.COL_META_DATA_ATTR_NAME,
                      QueryConditionOperators.EQUAL, "run_alias")
              .addConditionAsGenQueryField(
                      RodsGenQueryEnum.COL_META_DATA_ATTR_VALUE,
                      QueryConditionOperators.EQUAL, partition.getSequencerPartitionContainer().getRun().getAlias())
              .addConditionAsGenQueryField(
                      RodsGenQueryEnum.COL_DATA_NAME,
                      QueryConditionOperators.LIKE, l.getName()+"%")
              .addOrderByGenQueryField(
                      RodsGenQueryEnum.COL_DATA_NAME,
                      GenQueryOrderByField.OrderByType.ASC);
            IRODSGenQueryFromBuilder irodsQuery = builder.exportIRODSQueryFromBuilder(1);
            collateResults(queryExecutorAO.executeIRODSQuery(irodsQuery, 0), filePaths);
            log.info(LimsUtils.join(filePaths, " , "));
          }
          catch (GenQueryBuilderException e) {
            log.error("error building query", e);
            throw new JargonException("error building query", e);
          }
          catch (JargonQueryException jqe) {
            log.error("error executing query", jqe);
            throw new JargonException("error executing query", jqe);
          }
        }
        catch (JargonException e) {
          e.printStackTrace();
        }

        for (String fp : filePaths) {
          fps.add(new File(fp));
        }
        return fps;
      }
      else {
        throw new SubmissionException("No experiments");
      }
    }
    else {
      throw new SubmissionException("Collection of experiments is empty");
    }
  }

  private void collateResults(IRODSQueryResultSet resultSet, List<String> filePaths) throws JargonException, JargonQueryException {
    for (IRODSQueryResultRow row : resultSet.getResults()) {
      String col = row.getColumn(0);
      filePaths.add(col);
      log.info("Got: " + col);
    }
    if (resultSet.isHasMoreRecords()) {
      collateResults(queryExecutorAO.getMoreResults(resultSet), filePaths);
    }
  }

  @Override
  public Set<File> generateFilePaths(SequencerPoolPartition partition) throws SubmissionException {
    Set<File> filePaths = new HashSet<File>();
    if((partition.getSequencerPartitionContainer().getRun().getFilePath()) == null){
      throw new SubmissionException("No valid run filepath!");
    }

    Pool<? extends Poolable> pool = partition.getPool();
    if (pool == null) {
      throw new SubmissionException("partition.getPool=null!");
    }
    else {
      Collection<Experiment> experiments = pool.getExperiments();
      if (experiments.isEmpty()) {
        throw new SubmissionException("Collection or experiments is empty");
      }
      else {
        Collection<? extends Dilution> libraryDilutions = pool.getDilutions();
        if (libraryDilutions.isEmpty()) {
          throw new SubmissionException("Collection of libraryDilutions is empty");
        }
        else {
          for (Dilution l : libraryDilutions) {
            Set<File> files=generateFilePath(partition,l);
            filePaths.addAll(files);
          }
        }
      }
    }
    return filePaths;
  }

  private class IlluminaFilenameFilter implements FilenameFilter {
    @Override
    public boolean accept(File dir, String name) {
      Pattern pattern = Pattern.compile("LIB|SAM[\\d]+_[ACTG]+_L00[\\d]{1}_.*\\.fastq.gz");
      Matcher m = pattern.matcher(name);
      return m.matches();
    }
  }
}
