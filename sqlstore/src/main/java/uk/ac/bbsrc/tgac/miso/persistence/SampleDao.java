package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.Identity;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;
import uk.ac.bbsrc.tgac.miso.core.store.SampleStore;

public interface SampleDao extends SampleStore {

  List<Sample> list() throws IOException;

  Sample getSample(Long id) throws IOException;
  
  Sample getByPreMigrationId(Long id) throws IOException;

  Long addSample(Sample sample) throws IOException, MisoNamingException, SQLException;

  void deleteSample(Sample sample);

  void update(Sample sample) throws IOException;

  Long countAll() throws IOException;

  /**
   * List all the identities which have at least one external name which (partially) matches the input String.
   * The input String must be a single non-comma-separated external name or alias. If there are multiple comma-separated external names to
   * search against, they must each be queried through this function.
   * 
   * @param externalName
   *          a single external name/alias string
   * @return Collection<Identity> set of Identities which have an external name which matches the input string
   * @throws IOException
   */
  Collection<Identity> getIdentitiesByExternalNameOrAlias(String externalName) throws IOException;

  /**
   * List all the identities associated with a given project which have at least one external name which exactly matches the input String.
   * The input String must be a single non-comma-separated external name.
   * 
   * @param externalName a single external name String
   * @param projectId Long
   * @return Collection<Sample> set of Identities belonging to a given project which have an external name that matches the input string
   * @throws IOException
   */
  Collection<Identity> getIdentitiesByExternalNameAndProject(String externalName, Long projectId) throws IOException;

}