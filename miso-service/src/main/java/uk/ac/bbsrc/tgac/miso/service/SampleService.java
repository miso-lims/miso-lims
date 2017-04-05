package uk.ac.bbsrc.tgac.miso.service;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.hibernate.exception.ConstraintViolationException;

import com.eaglegenomics.simlims.core.Note;

import uk.ac.bbsrc.tgac.miso.core.data.Identity;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleQC;
import uk.ac.bbsrc.tgac.miso.core.data.type.QcType;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;

public interface SampleService extends PaginatedDataSource<Sample, PaginationFilter> {

  Sample get(Long sampleId) throws IOException;

  Long create(Sample sample) throws IOException;

  void update(Sample sample) throws IOException;

  List<Sample> getAll() throws IOException;

  List<Sample> getBySearch(String querystr) throws IOException;

  List<Sample> getByAlias(String alias) throws IOException;

  void delete(Long sampleId) throws IOException;

  Long countAll() throws IOException;

  Collection<Identity> getIdentitiesByExternalNameOrAlias(String externalName) throws IOException;

  void confirmExternalNameUniqueForProjectIfRequired(String externalNames, Sample sample) throws IOException, ConstraintViolationException;

  Collection<Identity> getIdentitiesByExternalNameAndProject(String externalName, Long projectId) throws IOException;

  public void addNote(Sample sample, Note note) throws IOException;

  public void deleteNote(Sample sample, Long noteId) throws IOException;

  Sample getByBarcode(String barcode) throws IOException;

  void addQc(Sample sample, SampleQC qc) throws IOException;

  void bulkAddQcs(Sample sample) throws IOException;

  public void deleteQc(Sample sample, Long qcId) throws IOException;

  Collection<QcType> listSampleQcTypes() throws IOException;

}