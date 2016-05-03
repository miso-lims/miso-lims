package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleClass;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;

public interface SampleDao {

  List<Sample> getSample() throws IOException;

  Sample getSample(Long id) throws IOException;

  Long addSample(Sample sample) throws IOException, MisoNamingException, SQLException;

  void deleteSample(Sample sample);

  void update(Sample sample) throws IOException;

  int getNextSiblingNumber(Sample parent, SampleClass childClass) throws IOException;
  
  /**
   * Determines whether an alias exists already
   * 
   * @param alias
   *          See if this alias already exists.
   * @return True if the alias already exists.
   * @throws IOException
   *           If there are difficulties reading from the database.
   */
  boolean aliasExists(String alias) throws IOException;

}