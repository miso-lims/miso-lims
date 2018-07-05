package uk.ac.bbsrc.tgac.miso.core.data;

public enum QcCorrespondingField {
  CONCENTRATION {

    @Override
    public void updateField(Pool pool, double value) {
      pool.setConcentration(value);
    }

    @Override
    public void updateField(Library library, double value) {
      library.setInitialConcentration(value);
    }

    @Override
    public void updateField(DetailedSample sample, double value) {
      sample.setConcentration(value);
    }

  },
  VOLUME {

    @Override
    public void updateField(Pool pool, double value) {
      pool.setVolume(value);
    }

    @Override
    public void updateField(Library library, double value) {
      library.setVolume(value);
    }

    @Override
    public void updateField(Sample sample, double value) {
      sample.setVolume(value);
    }

  },
  NONE;

  public void updateField(Pool pool, double value) {
  }

  public void updateField(Library library, double value) {
  }

  public void updateField(DetailedSample sample, double value) {
    updateField((Sample) sample, value);
  }

  public void updateField(Sample sample, double value) {
  }

  public void updateField(Run run, double value) {
  }

  public void updateField(SequencerPartitionContainer container, double value) {
  }

}

