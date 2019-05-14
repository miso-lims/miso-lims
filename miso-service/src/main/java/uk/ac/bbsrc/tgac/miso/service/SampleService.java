package uk.ac.bbsrc.tgac.miso.service;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.Barcodable.EntityType;
import uk.ac.bbsrc.tgac.miso.core.service.SaveService;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleIdentity;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;

public interface SampleService
    extends PaginatedDataSource<Sample>, BarcodableService<Sample>, DeleterService<Sample>, NoteService<Sample>, SaveService<Sample> {

  @Override
  default EntityType getEntityType() {
    return EntityType.SAMPLE;
  }

  List<Sample> list() throws IOException;

  List<Sample> getByAlias(String alias) throws IOException;

  Long countAll() throws IOException;

  Collection<SampleIdentity> getIdentitiesByExternalNameOrAliasAndProject(String externalName, Long projectId, boolean exactMatch)
      throws IOException;

  void confirmExternalNameUniqueForProjectIfRequired(String externalNames, Sample sample) throws IOException;

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

  /**
   * @return a List of all non-archived SampleTypes
   * @throws IOException
   */
  Collection<String> listSampleTypes() throws IOException;

  Sample save(Sample sample) throws IOException;

}