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

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.pub.IRODSAccessObjectFactory;
import org.irods.jargon.core.pub.IRODSFileSystem;
import org.irods.jargon.core.pub.IRODSGenQueryExecutor;
import org.irods.jargon.core.query.GenQueryBuilderException;
import org.irods.jargon.core.query.GenQueryOrderByField;
import org.irods.jargon.core.query.IRODSGenQueryBuilder;
import org.irods.jargon.core.query.IRODSGenQueryFromBuilder;
import org.irods.jargon.core.query.IRODSQueryResultRow;
import org.irods.jargon.core.query.IRODSQueryResultSet;
import org.irods.jargon.core.query.JargonQueryException;
import org.irods.jargon.core.query.QueryConditionOperators;
import org.irods.jargon.core.query.RodsGenQueryEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.fluxion.spi.ServiceProvider;
import uk.ac.bbsrc.tgac.miso.core.data.Dilution;
import uk.ac.bbsrc.tgac.miso.core.data.Experiment;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.Poolable;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPoolPartition;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.exception.SubmissionException;
import uk.ac.bbsrc.tgac.miso.core.service.submission.FilePathGenerator;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

/**
 * User: davey Date: 08/10/12 Time: 15:15
 */
@ServiceProvider
public class IRODSFilepathGenerator implements FilePathGenerator {
  protected static final Logger log = LoggerFactory.getLogger(IRODSFilepathGenerator.class);

  String basePath = "";
  private IRODSFileSystem irodsFileSystem;
  private IRODSAccount account;
  private IRODSAccessObjectFactory irodsAccessObjectFactory;
  private IRODSGenQueryExecutor queryExecutorAO;

  public IRODSFilepathGenerator() {
    // no-arg constructor
    // TODO this will result in borked behaviour, but means the resolver service can instantiate it
  }

  public IRODSFilepathGenerator(IRODSFileSystem irodsFileSystem, IRODSAccount account) {
    this.irodsFileSystem = irodsFileSystem;
    this.account = account;

    try {
      this.irodsAccessObjectFactory = irodsFileSystem.getIRODSAccessObjectFactory();
      this.queryExecutorAO = irodsAccessObjectFactory.getIRODSGenQueryExecutor(account);
    } catch (JargonException e) {
      log.error("Cannot create IRODSFilepathGenerator instance", e);
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
                .addConditionAsGenQueryField(RodsGenQueryEnum.COL_META_DATA_ATTR_NAME, QueryConditionOperators.EQUAL, "run_alias")
                .addConditionAsGenQueryField(RodsGenQueryEnum.COL_META_DATA_ATTR_VALUE, QueryConditionOperators.EQUAL,
                    partition.getSequencerPartitionContainer().getRun().getAlias())
                .addConditionAsGenQueryField(RodsGenQueryEnum.COL_DATA_NAME, QueryConditionOperators.LIKE, l.getName() + "%")
                .addOrderByGenQueryField(RodsGenQueryEnum.COL_DATA_NAME, GenQueryOrderByField.OrderByType.ASC);
            IRODSGenQueryFromBuilder irodsQuery = builder.exportIRODSQueryFromBuilder(1);
            collateResults(queryExecutorAO.executeIRODSQuery(irodsQuery, 0), filePaths);
            log.info(LimsUtils.join(filePaths, " , "));
          } catch (GenQueryBuilderException e) {
            log.error("error building query", e);
            throw new JargonException("error building query", e);
          } catch (JargonQueryException jqe) {
            log.error("error executing query", jqe);
            throw new JargonException("error executing query", jqe);
          }
        } catch (JargonException e) {
          log.error("error executing query", e);
        }

        for (String fp : filePaths) {
          fps.add(new File(fp));
        }
        return fps;
      } else {
        throw new SubmissionException("No experiments");
      }
    } else {
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
    if ((partition.getSequencerPartitionContainer().getRun().getFilePath()) == null) {
      throw new SubmissionException("No valid run filepath!");
    }

    Pool<? extends Poolable> pool = partition.getPool();
    if (pool == null) {
      throw new SubmissionException("partition.getPool=null!");
    } else {
      Collection<Experiment> experiments = pool.getExperiments();
      if (experiments.isEmpty()) {
        throw new SubmissionException("Collection or experiments is empty");
      } else {
        Collection<? extends Dilution> libraryDilutions = pool.getDilutions();
        if (libraryDilutions.isEmpty()) {
          throw new SubmissionException("Collection of libraryDilutions is empty");
        } else {
          for (Dilution l : libraryDilutions) {
            Set<File> files = generateFilePath(partition, l);
            filePaths.addAll(files);
          }
        }
      }
    }
    return filePaths;
  }

  @Override
  public String getName() {
    return "IRODS Illumina File Path Generator";
  }

  @Override
  public PlatformType generatesFilePathsFor() {
    return null; // return PlatformType.ILLUMINA;
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
