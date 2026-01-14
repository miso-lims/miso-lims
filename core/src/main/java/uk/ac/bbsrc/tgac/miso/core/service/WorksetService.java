package uk.ac.bbsrc.tgac.miso.core.service;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ListWorksetView;
import uk.ac.bbsrc.tgac.miso.core.data.impl.workset.Workset;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;

public interface WorksetService
    extends PaginatedDataSource<ListWorksetView>, DeleterService<Workset>, SaveService<Workset>, NoteService<Workset> {

  public List<ListWorksetView> listBySearch(String query) throws IOException;

  public List<Workset> listBySample(long sampleId) throws IOException;

  public List<Workset> listByLibrary(long libraryId) throws IOException;

  public List<Workset> listByLibraryAliquot(long aliquotId) throws IOException;

  public List<Workset> listByPool(long poolId) throws IOException;

  public void addSamples(Workset workset, Collection<Sample> items) throws IOException;

  public void addLibraries(Workset workset, Collection<Library> items) throws IOException;

  public void addLibraryAliquots(Workset workset, Collection<LibraryAliquot> items) throws IOException;

  public void addPools(Workset workset, Collection<Pool> items) throws IOException;

  public void removeSamples(Workset workset, Collection<Sample> items) throws IOException;

  public void removeLibraries(Workset workset, Collection<Library> items) throws IOException;

  public void removeLibraryAliquots(Workset workset, Collection<LibraryAliquot> items) throws IOException;

  public void removePools(Workset workset, Collection<Pool> items) throws IOException;

  public void moveSamples(Workset from, Workset to, Collection<Sample> items) throws IOException;

  public void moveLibraries(Workset from, Workset to, Collection<Library> items) throws IOException;

  public void moveLibraryAliquots(Workset from, Workset to, Collection<LibraryAliquot> items) throws IOException;

  public void movePools(Workset from, Workset to, Collection<Pool> items) throws IOException;

  public Map<Long, Date> getSampleAddedTimes(long worksetId) throws IOException;

  public Map<Long, Date> getLibraryAddedTimes(long worksetId) throws IOException;

  public Map<Long, Date> getLibraryAliquotAddedTimes(long worksetId) throws IOException;

  public Map<Long, Date> getPoolAddedTimes(long worksetId) throws IOException;

}
