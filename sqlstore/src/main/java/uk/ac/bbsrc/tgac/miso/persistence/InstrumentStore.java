package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.Instrument;
import uk.ac.bbsrc.tgac.miso.core.data.ServiceRecord;
import uk.ac.bbsrc.tgac.miso.core.data.type.InstrumentType;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;

public interface InstrumentStore extends SaveDao<Instrument>, PaginatedDataSource<Instrument> {

  /**
   * Get an Instrument by a given name
   * 
   * @param name
   * @return the Instrument
   * @throws IOException
   */
  public Instrument getByName(String name) throws IOException;

  /**
   * Get the Instrument which was the pre-upgrade Instrument for the Instrument provided (by its id)
   * 
   * @param upgradedInstrumentId
   * @return the pre-upgrade Instrument if one exists; otherwise null
   * @throws IOException if there is more than one pre-upgrade Instrument for the provided Instrument,
   *         or there are any other IO errors
   */
  public Instrument getByUpgradedInstrument(long upgradedInstrumentId) throws IOException;

  public List<Instrument> listByType(InstrumentType type) throws IOException;

  public long getUsageByRuns(Instrument instrument) throws IOException;

  public long getUsageByArrayRuns(Instrument instrument) throws IOException;

  public long getUsageByQcs(Instrument instrument) throws IOException;

  public Instrument getByServiceRecord(ServiceRecord record) throws IOException;

}
