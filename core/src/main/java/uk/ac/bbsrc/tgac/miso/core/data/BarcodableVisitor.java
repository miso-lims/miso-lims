package uk.ac.bbsrc.tgac.miso.core.data;

import uk.ac.bbsrc.tgac.miso.core.data.impl.DetailedLibraryAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencingContainerModel;

public interface BarcodableVisitor<T> {
  default T visitBox(Box box) {
    return null;
  }

  default T visitContainer(SequencerPartitionContainer container) {
    return null;
  }

  default T visitContainerModel(SequencingContainerModel model) {
    return null;
  }

  default T visitKit(Kit kit) {
    return null;
  }

  default T visitLibrary(Library library) {
    return null;
  }

  default T visitLibraryAliquot(LibraryAliquot libraryAliquot) {
    return null;
  }

  default T visitLibraryAliquotDetailed(DetailedLibraryAliquot libraryAliquot) {
    return this.visitLibraryAliquot(libraryAliquot);
  }

  default T visitLibraryDetailed(DetailedLibrary library) {
    return this.visitLibrary(library);
  }

  default T visitPool(Pool pool) {
    return null;
  }

  default T visitSample(Sample sample) {
    return null;
  }

  default T visitSampleAliquot(SampleAliquot sample) {
    return this.visitSampleDetailed(sample);
  }

  default T visitSampleDetailed(DetailedSample sample) {
    return this.visitSample(sample);
  }

  default T visitSampleIdentity(SampleIdentity sample) {
    return this.visitSampleDetailed(sample);
  }

  default T visitSampleStock(SampleStock sample) {
    return this.visitSampleDetailed(sample);
  }

  default T visitSampleTissue(SampleTissue sample) {
    return this.visitSampleDetailed(sample);
  }

  default T visitSampleTissueProcessing(SampleTissueProcessing sample) {
    return this.visitSampleDetailed(sample);
  }
}
