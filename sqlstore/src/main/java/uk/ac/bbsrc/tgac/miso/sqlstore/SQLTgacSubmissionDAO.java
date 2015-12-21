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

package uk.ac.bbsrc.tgac.miso.sqlstore;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;

import uk.ac.bbsrc.tgac.miso.core.data.Dilution;
import uk.ac.bbsrc.tgac.miso.core.data.Experiment;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.Poolable;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPoolPartition;
import uk.ac.bbsrc.tgac.miso.core.data.Study;
import uk.ac.bbsrc.tgac.miso.core.data.Submission;
import uk.ac.bbsrc.tgac.miso.core.data.Submittable;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PartitionImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SubmissionImpl;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;
import uk.ac.bbsrc.tgac.miso.core.factory.DataObjectFactory;
import uk.ac.bbsrc.tgac.miso.core.service.naming.MisoNamingScheme;
import uk.ac.bbsrc.tgac.miso.core.service.naming.NamingSchemeAware;
import uk.ac.bbsrc.tgac.miso.core.store.ExperimentStore;
import uk.ac.bbsrc.tgac.miso.core.store.LibraryDilutionStore;
import uk.ac.bbsrc.tgac.miso.core.store.PartitionStore;
import uk.ac.bbsrc.tgac.miso.core.store.RunStore;
import uk.ac.bbsrc.tgac.miso.core.store.SampleStore;
import uk.ac.bbsrc.tgac.miso.core.store.Store;
import uk.ac.bbsrc.tgac.miso.core.store.StudyStore;
import uk.ac.bbsrc.tgac.miso.sqlstore.util.DbUtils;

/**
 * uk.ac.bbsrc.tgac.miso.sqlstore
 * <p/>
 * Info
 *
 * @author Rob Davey
 * @since 0.0.2
 */
public class SQLTgacSubmissionDAO implements Store<Submission>, NamingSchemeAware<Submission> {
  private static final String TABLE_NAME = "Submission";

  public static final String SUBMISSION_SELECT = "SELECT submissionId, creationDate, submittedDate, name, alias, title, description, accession, verified, completed "
      + "FROM " + TABLE_NAME;

  public static final String SUBMISSION_DELETE = "DELETE FROM " + TABLE_NAME + " WHERE submissionId=:submissionId";

  public static final String SUBMISSION_SELECT_BY_ID = SUBMISSION_SELECT + " WHERE submissionId = ?";

  public static final String SUBMISSION_UPDATE = "UPDATE " + TABLE_NAME + " "
      + "SET creationDate=:creationDate, submittedDate=:submittedDate, name=:name, alias=:alias, title=:title, "
      + "description=:description, accession=:accession, verified=:verified, completed=:completed " + "WHERE submissionId=:submissionId";

  public static final String SUBMISSION_ELEMENTS_DELETE = "DELETE sexp, ssam, sstu, ssla FROM " + TABLE_NAME + " s "
      + "LEFT JOIN Submission_Experiment AS sexp ON s.submissionId = sexp.submission_submissionId "
      + "LEFT JOIN Submission_Sample AS ssam ON s.submissionId = ssam.submission_submissionId "
      + "LEFT JOIN Submission_Study AS sstu ON s.submissionId = sstu.submission_submissionId "
      + "LEFT JOIN Submission_Partition_Dilution AS ssla ON s.submissionId = ssla.submission_submissionId "
      + "WHERE s.submissionId=:submissionId";

  public static final String SUBMISSION_DILUTION_SELECT = "SELECT dilution_dilutionId " + "FROM Submission_Partition_Dilution "
      + "WHERE submission_submissionId = ? AND partition_partitionId = ?";

  protected static final Logger log = LoggerFactory.getLogger(SQLTgacSubmissionDAO.class);

  private JdbcTemplate template;
  private LibraryDilutionStore libraryDilutionDAO;
  private ExperimentStore experimentDAO;
  private PartitionStore partitionDAO;
  private RunStore runDAO;
  private StudyStore studyDAO;
  private SampleStore sampleDAO;

  @Autowired
  private MisoNamingScheme<Submission> namingScheme;

  @Override
  public MisoNamingScheme<Submission> getNamingScheme() {
    return namingScheme;
  }

  @Override
  public void setNamingScheme(MisoNamingScheme<Submission> namingScheme) {
    this.namingScheme = namingScheme;
  }

  @Autowired
  private DataObjectFactory dataObjectFactory;

  public void setDataObjectFactory(DataObjectFactory dataObjectFactory) {
    this.dataObjectFactory = dataObjectFactory;
  }

  public void setDilutionDAO(LibraryDilutionStore libraryDilutionDAO) {
    this.libraryDilutionDAO = libraryDilutionDAO;
  }

  public void setExperimentDAO(ExperimentStore experimentDAO) {
    this.experimentDAO = experimentDAO;
  }

  public void setRunDAO(RunStore runDAO) {
    this.runDAO = runDAO;
  }

  public void setStudyDAO(StudyStore studyDAO) {
    this.studyDAO = studyDAO;
  }

  public void setSampleDAO(SampleStore sampleDAO) {
    this.sampleDAO = sampleDAO;
  }

  public void setPartitionDAO(PartitionStore partitionDAO) {
    this.partitionDAO = partitionDAO;
  }

  public JdbcTemplate getJdbcTemplate() {
    return template;
  }

  public void setJdbcTemplate(JdbcTemplate template) {
    this.template = template;
  }

  @Override
  @Transactional(readOnly = false, rollbackFor = IOException.class)
  public long save(Submission submission) throws IOException {
    SimpleJdbcInsert insert = new SimpleJdbcInsert(template).withTableName("Submission");

    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("alias", submission.getAlias());
    params.addValue("accession", submission.getAccession());
    params.addValue("description", submission.getDescription());
    params.addValue("title", submission.getTitle());
    params.addValue("creationDate", submission.getCreationDate());
    params.addValue("submittedDate", submission.getSubmissionDate());
    params.addValue("verified", submission.isVerified());
    params.addValue("completed", submission.isCompleted());

    // if a submission already exists then delete all the old rows first, and repopulate.
    // easier than trying to work out which rows need to be updated and which don't
    if (submission.getId() != Submission.UNSAVED_ID) {
      try {
        if (namingScheme.validateField("name", submission.getName())) {
          MapSqlParameterSource delparams = new MapSqlParameterSource();
          delparams.addValue("submissionId", submission.getId());
          NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(template);
          log.debug("Deleting Submission elements for " + submission.getId());
          namedTemplate.update(SUBMISSION_ELEMENTS_DELETE, delparams);

          params.addValue("submissionId", submission.getId());
          params.addValue("name", submission.getName());
          namedTemplate.update(SUBMISSION_UPDATE, params);
        } else {
          throw new IOException("Cannot save Submission - invalid field:" + submission.toString());
        }
      } catch (MisoNamingException e) {
        throw new IOException("Cannot save Submission - issue with naming scheme", e);
      }
    } else {
      insert.usingGeneratedKeyColumns("submissionId");
      try {
        submission.setId(DbUtils.getAutoIncrement(template, TABLE_NAME));

        String name = namingScheme.generateNameFor("name", submission);
        submission.setName(name);

        if (namingScheme.validateField("name", submission.getName())) {
          params.addValue("name", name);
          params.addValue("creationDate", new Date());

          Number newId = insert.executeAndReturnKey(params);
          if (newId.longValue() != submission.getId()) {
            log.error("Expected Submission ID doesn't match returned value from database insert: rolling back...");
            new NamedParameterJdbcTemplate(template).update(SUBMISSION_DELETE,
                new MapSqlParameterSource().addValue("submissionId", newId.longValue()));
            throw new IOException("Something bad happened. Expected Submission ID doesn't match returned value from DB insert");
          }
        } else {
          throw new IOException("Cannot save Submission - invalid field:" + submission.toString());
        }
      } catch (MisoNamingException e) {
        throw new IOException("Cannot save Submission - issue with naming scheme", e);
      }
    }

    if (submission.getSubmissionElements() != null) {
      Collection<Submittable<Document>> docs = submission.getSubmissionElements();
      for (Submittable s : docs) {
        String tableName = "Submission_";
        String priKey = null;
        Long priValue = null;
        boolean process = true;

        if (s instanceof Sample) {
          tableName += "Sample";
          priKey = "samples_sampleId";
          priValue = ((Sample) s).getId();
        } else if (s instanceof Study) {
          tableName += "Study";
          priKey = "studies_studyId";
          priValue = ((Study) s).getId();
        } else if (s instanceof Experiment) {
          tableName += "Experiment";
          priKey = "experiments_experimentId";
          priValue = ((Experiment) s).getId();
        } else if (s instanceof SequencerPoolPartition) {
          SequencerPoolPartition l = (SequencerPoolPartition) s;
          tableName += "Partition_Dilution";
          priKey = "partitions_partitionId";
          priValue = l.getId();
          process = false;

          if (l.getPool() != null) {
            Collection<Experiment> exps = l.getPool().getExperiments();
            for (Experiment experiment : exps) {
              SimpleJdbcInsert pInsert = new SimpleJdbcInsert(template).withTableName("Submission_Experiment");
              try {
                MapSqlParameterSource poParams = new MapSqlParameterSource();
                poParams.addValue("submission_submissionId", submission.getId());
                poParams.addValue("experiments_experimentId", experiment.getId());
                pInsert.execute(poParams);
              } catch (DuplicateKeyException dke) {
                log.error("This Submission_Experiment combination already exists - not inserting", dke);
              }

              Study study = experiment.getStudy();
              SimpleJdbcInsert sInsert = new SimpleJdbcInsert(template).withTableName("Submission_Study");
              try {
                MapSqlParameterSource poParams = new MapSqlParameterSource();
                poParams.addValue("submission_submissionId", submission.getId());
                poParams.addValue("studies_studyId", study.getId());
                sInsert.execute(poParams);
              } catch (DuplicateKeyException dke) {
                log.error("This Submission_Study combination already exists - not inserting", dke);
              }
            }

            Collection<? extends Dilution> dils = l.getPool().getDilutions();
            for (Dilution dil : dils) {
              Sample sample = dil.getLibrary().getSample();
              SimpleJdbcInsert sInsert = new SimpleJdbcInsert(template).withTableName("Submission_Sample");
              try {
                MapSqlParameterSource poParams = new MapSqlParameterSource();
                poParams.addValue("submission_submissionId", submission.getId());
                poParams.addValue("samples_sampleId", sample.getId());
                sInsert.execute(poParams);
              } catch (DuplicateKeyException dke) {
                log.error("This Submission_Sample combination already exists - not inserting", dke);
              }

              // Adds Submission_Partition_Dilution info to DB table.

              sInsert = new SimpleJdbcInsert(template).withTableName("Submission_Partition_Dilution");
              try {
                MapSqlParameterSource poParams = new MapSqlParameterSource();
                poParams.addValue("submission_submissionId", submission.getId());
                poParams.addValue("partition_partitionId", l.getId());
                poParams.addValue("dilution_dilutionId", dil.getId());
                sInsert.execute(poParams);

              } catch (DuplicateKeyException dke) {
                log.error("This Submission_Partition_Dilution combination already exists - not inserting", dke);
              }
            }
          }
        }

        if (process) {
          if (priKey != null && priValue != null) {
            SimpleJdbcInsert pInsert = new SimpleJdbcInsert(template).withTableName(tableName);
            try {
              MapSqlParameterSource poParams = new MapSqlParameterSource();
              poParams.addValue("submission_submissionId", submission.getId());
              poParams.addValue(priKey, priValue);
              pInsert.execute(poParams);
            } catch (DuplicateKeyException dke) {
              log.error("This " + tableName + " combination already exists - not inserting", dke);
            }
          } else {
            throw new IOException("Null parameter key/value detected. Cannot insert.");
          }
        }
      }
    } else {
      throw new IOException("No defined Submittable elements available");
    }

    return submission.getId();
  }

  @Override
  public SubmissionImpl get(long id) throws IOException {
    List eResults = template.query(SUBMISSION_SELECT_BY_ID, new Object[] { id }, new TgacSubmissionMapper());
    SubmissionImpl e = eResults.size() > 0 ? (SubmissionImpl) eResults.get(0) : null;
    return e;
  }

  @Override
  public Submission lazyGet(long id) throws IOException {
    return get(id);
  }

  @Override
  public Collection<Submission> listAll() throws IOException {
    return template.query(SUBMISSION_SELECT, new TgacSubmissionMapper());
  }

  @Override
  public int count() throws IOException {
    return template.queryForInt("SELECT count(*) FROM " + TABLE_NAME);
  }

  // sets the values of the new Submission object based on those in the SubmissionMapper
  public class TgacSubmissionMapper implements RowMapper<Submission> {
    @Override
    public SubmissionImpl mapRow(ResultSet rs, int rowNum) throws SQLException {
      SubmissionImpl t = (SubmissionImpl) dataObjectFactory.getSubmission();
      t.setId(rs.getLong("submissionId"));
      t.setAccession(rs.getString("accession"));
      t.setAlias(rs.getString("alias"));
      t.setCreationDate(rs.getDate("creationDate"));
      t.setDescription(rs.getString("description"));
      t.setName(rs.getString("name"));
      t.setSubmissionDate(rs.getDate("submittedDate"));
      t.setTitle(rs.getString("title"));
      t.setVerified(rs.getBoolean("verified"));
      t.setCompleted(rs.getBoolean("completed"));

      try {
        // process submittables
        for (Study study : studyDAO.listBySubmissionId(rs.getLong("submissionId"))) {
          t.addSubmissionElement(study);
          log.debug(t.getName() + ": added " + study.getName());
        }

        for (Sample sample : sampleDAO.listBySubmissionId(rs.getLong("submissionId"))) {
          t.addSubmissionElement(sample);
          log.debug(t.getName() + ": added " + sample.getName());
        }

        for (SequencerPoolPartition partition : partitionDAO.listBySubmissionId(rs.getLong("submissionId"))) {
          // for each partition, lists all the runs on the flowcell/container
          SequencerPoolPartition newPartition = new PartitionImpl();
          newPartition.setId(partition.getId());
          newPartition.setSequencerPartitionContainer(partition.getSequencerPartitionContainer());
          newPartition.setPartitionNumber(partition.getPartitionNumber());

          Pool<Dilution> newPool = new PoolImpl<Dilution>();
          Pool<? extends Poolable> oldPool = partition.getPool();
          newPool.setId(oldPool.getId());
          newPool.setExperiments(oldPool.getExperiments());

          List<Run> runs = new ArrayList<Run>(
              runDAO.listBySequencerPartitionContainerId(partition.getSequencerPartitionContainer().getId()));
          // if there is 1 run for the flowcell/container, sets the run for that container to the first on on the list
          if (runs.size() == 1) {
            partition.getSequencerPartitionContainer().setRun(runs.get(0));
          }

          List<Long> dilutionIdList = template.queryForList(SUBMISSION_DILUTION_SELECT, Long.class,
              new Object[] { rs.getLong("submissionId"), partition.getId() });

          log.debug("dilutionIdList for partition " + partition.getId() + "from DB table:" + dilutionIdList.toString());
          for (Long id : dilutionIdList) {
            Dilution dil = libraryDilutionDAO.getLibraryDilutionByIdAndPlatform(id, partition.getPool().getPlatformType());
            try {
              newPool.addPoolableElement(dil);
            } catch (Exception e) {
              log.error("TGAC submission row mapper", e);
            }
          }
          // adds the new pool to the partition
          newPartition.setPool(newPool);

          // replace any existing experiment-linked pools with the new pool
          for (Experiment experiment : experimentDAO.listBySubmissionId(rs.getLong("submissionId"))) {
            if (experiment.getPool().getId() == newPool.getId()) {
              experiment.setPool(newPool);
              t.addSubmissionElement(experiment);
              log.debug(t.getName() + ": added " + experiment.getName());
              break;
            }
          }

          // adds the partition to the submission
          log.debug("submission " + t.getId() + " new partition " + newPartition.getId() + " contains dilutions "
              + newPartition.getPool().getDilutions().toString());
          t.addSubmissionElement(newPartition);
        }
      } catch (IOException ie) {
        log.error("Cannot map submission", ie);
      }

      return t;
    }
  }
}
