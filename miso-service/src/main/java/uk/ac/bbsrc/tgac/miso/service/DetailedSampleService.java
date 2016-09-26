package uk.ac.bbsrc.tgac.miso.service;

import java.io.IOException;
import java.util.Set;

import uk.ac.bbsrc.tgac.miso.core.data.DetailedSample;

public interface DetailedSampleService {

  DetailedSample get(Long detailedSampleId) throws IOException;

  Set<DetailedSample> getAll() throws IOException;

  void delete(Long detailedSampleId) throws IOException;

  /**
   * copies all editable properties from one DetailedSample instance to another
   * 
   * @param target
   *          the persisted DetailedSample to copy changes into
   * @param source
   *          the modified DetailedSample to copy changes from
   * @throws IOException
   */
  void applyChanges(DetailedSample target, DetailedSample source) throws IOException;
  
  /**
   * loads the contained objects into the target object from the database using IDs that are already present in the target
   * 
   * @param target
   *          the DetailedSample whose attributes are to be populated from the database. Must already contain the IDs to be used for lookup
   * @throws IOException
   */
  void loadMembers(DetailedSample target) throws IOException;
  
  /**
   * loads the contained objects into the target object from the database using IDs found in the source object
   * 
   * @param target
   *          the DetailedSample whose attributes are to be populated from the database
   * @param source
   *          the DetailedSample containing IDs to be used for lookup
   * @throws IOException
   */
  void loadMembers(DetailedSample target, DetailedSample source) throws IOException;

}