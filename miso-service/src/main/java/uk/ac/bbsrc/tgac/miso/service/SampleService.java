package uk.ac.bbsrc.tgac.miso.service;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.eaglegenomics.simlims.core.Note;

import uk.ac.bbsrc.tgac.miso.core.data.Barcodable.EntityType;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleIdentity;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;
import uk.ac.bbsrc.tgac.miso.service.impl.BoxableDeleterService;

public interface SampleService extends PaginatedDataSource<Sample>, BarcodableService<Sample>, BoxableDeleterService<Sample> {

  @Override
  default EntityType getEntityType() {
    return EntityType.SAMPLE;
  }

  Long create(Sample sample) throws IOException;

  void update(Sample sample) throws IOException;

  List<Sample> list() throws IOException;

  List<Sample> getByAlias(String alias) throws IOException;

  Long countAll() throws IOException;

  Collection<SampleIdentity> getIdentitiesByExternalNameOrAliasAndProject(String externalName, Long projectId, boolean exactMatch)
      throws IOException;

  void confirmExternalNameUniqueForProjectIfRequired(String externalNames, Sample sample) throws IOException;

  public void addNote(Sample sample, Note note) throws IOException;

  public void deleteNote(Sample sample, Long noteId) throws IOException;

  Sample getByBarcode(String barcode) throws IOException;

  Collection<Sample> listByProjectId(long projectId) throws IOException;

  /**
   * Throws AuthorizationException if user cannot read one of the requested samples
   * 
   * @param idList
   * @return
   * @throws IOException
   */
  Collection<Sample> listByIdList(List<Long> idList) throws IOException;

  Collection<String> listSampleTypes() throws IOException;

  Map<String, Integer> getSampleColumnSizes() throws IOException;

  Sample save(Sample sample) throws IOException;

}