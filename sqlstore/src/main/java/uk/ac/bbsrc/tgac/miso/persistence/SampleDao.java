package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.Identity;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleClass;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;
import uk.ac.bbsrc.tgac.miso.core.store.SampleStore;

public interface SampleDao extends SampleStore {

  List<Sample> getSample() throws IOException;

  Sample getSample(Long id) throws IOException;

  Long addSample(Sample sample) throws IOException, MisoNamingException, SQLException;

  void deleteSample(Sample sample);

  void update(Sample sample) throws IOException;

  int getNextSiblingNumber(Sample parent, SampleClass childClass) throws IOException;

  /**
   * Determines whether an alias exists already
   * 
   * @param alias See if this alias already exists.
   * @return True if the alias already exists.
   * @throws IOException If there are difficulties reading from the database.
   */
  boolean aliasExists(String alias) throws IOException;

  /**
   * List all Samples by desired page with given page size.
   *
   * @param page of type int
   * @param size of type int
   * @param sortDir of type String
   * @return Collection<Sample>
   * @throws IOException
   */
  Collection<Sample> listByOffsetAndNumResults(int page, int size, String sortCol, String sortDir) throws IOException;

  List<Sample> listBySearchOffsetAndNumResults(int offset, int resultsPerPage, String querystr, String sortCol, String sortDir)
      throws IOException;

  Long countAll() throws IOException;

  Long countBySearch(String querystr) throws IOException;

  Identity getIdentityByExternalName(String externalName) throws IOException;

}