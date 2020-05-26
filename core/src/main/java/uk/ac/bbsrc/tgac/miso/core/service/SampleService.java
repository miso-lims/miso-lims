package uk.ac.bbsrc.tgac.miso.core.service;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.Barcodable.EntityType;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleIdentity;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.EntityReference;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;

public interface SampleService
    extends PaginatedDataSource<Sample>, BarcodableService<Sample>, DeleterService<Sample>, NoteService<Sample>, BulkSaveService<Sample> {

  @Override
  public default EntityType getEntityType() {
    return EntityType.SAMPLE;
  }

  public List<Sample> list() throws IOException;

  public List<Sample> getByAlias(String alias) throws IOException;

  public Long countAll() throws IOException;

  public Collection<SampleIdentity> getIdentitiesByExternalNameOrAliasAndProject(String externalName, Long projectId, boolean exactMatch)
      throws IOException;

  public void confirmExternalNameUniqueForProjectIfRequired(String externalNames, Sample sample) throws IOException;

  public Sample getByBarcode(String barcode) throws IOException;

  public Collection<Sample> listByProjectId(long projectId) throws IOException;

  /**
   * Throws AuthorizationException if user cannot read one of the requested samples
   * 
   * @param idList
   * @return
   * @throws IOException
   */
  public List<Sample> listByIdList(List<Long> idList) throws IOException;

  public Sample save(Sample sample) throws IOException;

  public EntityReference getNextInProject(Sample sample);

  public EntityReference getPreviousInProject(Sample sample);

}