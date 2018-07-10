package uk.ac.bbsrc.tgac.miso.core.data;

public enum QcCorrespondingField {
  CONCENTRATION {

    @Override
    public void updateField(Pool pool, double value, String units) {
      pool.setConcentration(value);
      pool.setConcentrationUnits(units);
    }

    @Override
    public void updateField(Library library, double value, String units) {
      library.setInitialConcentration(value);
      library.setConcentrationUnits(units);
    }

    @Override
    public void updateField(Sample sample, double value, String units) {
      sample.setConcentration(value);
      sample.setConcentrationUnits(units);
    }

  },
  VOLUME {

    @Override
    public void updateField(Pool pool, double value, String units) {
      pool.setVolume(value);
      pool.setVolumeUnits(units);
    }

    @Override
    public void updateField(Library library, double value, String units) {
      library.setVolume(value);
      library.setVolumeUnits(units);
    }

    @Override
    public void updateField(Sample sample, double value, String units) {
      sample.setVolume(value);
      sample.setVolumeUnits(units);
    }

  },
  NONE;

  public void updateField(Pool pool, double value, String units) {
  }

  public void updateField(Library library, double value, String units) {
  }

  public void updateField(DetailedSample sample, double value, String units) {
    updateField((Sample) sample, value, units);
  }

  public void updateField(Sample sample, double value, String units) {
  }

  public void updateField(Run run, double value, String units) {
  }

  public void updateField(SequencerPartitionContainer container, double value, String units) {
  }

}

