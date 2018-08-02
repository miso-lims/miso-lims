package uk.ac.bbsrc.tgac.miso.core.data;

public enum QcCorrespondingField {
  CONCENTRATION {

    @Override
    public void updateField(Pool pool, double value, String units) {
      pool.setConcentration(value);
      ConcentrationUnit concUnit = ConcentrationUnit.getFromString(units);
      if (concUnit != null) {
        pool.setConcentrationUnits(concUnit);
      }
    }

    @Override
    public void updateField(Library library, double value, String units) {
      library.setInitialConcentration(value);
      ConcentrationUnit concUnit = ConcentrationUnit.getFromString(units);
      if (concUnit != null) {
        library.setConcentrationUnits(concUnit);
      }
    }

    @Override
    public void updateField(Sample sample, double value, String units) {
      sample.setConcentration(value);
      ConcentrationUnit concUnit = ConcentrationUnit.getFromString(units);
      if (concUnit != null) {
        sample.setConcentrationUnits(concUnit);
      }
    }

  },
  VOLUME {

    @Override
    public void updateField(Pool pool, double value, String units) {
      pool.setVolume(value);
      VolumeUnit volUnit = VolumeUnit.getFromString(units);
      if (volUnit != null) {
        pool.setVolumeUnits(volUnit);
      }
    }

    @Override
    public void updateField(Library library, double value, String units) {
      library.setVolume(value);
      VolumeUnit volUnit = VolumeUnit.getFromString(units);
      if (volUnit != null) {
        library.setVolumeUnits(volUnit);
      }
    }

    @Override
    public void updateField(Sample sample, double value, String units) {
      sample.setVolume(value);
      VolumeUnit volUnit = VolumeUnit.getFromString(units);
      if (volUnit != null) {
        sample.setVolumeUnits(volUnit);
      }
    }

  },
  NONE;

  public void updateField(Pool pool, double value, String units) {
    throw new UnsupportedOperationException("Method not implemented for unspecified field");
  }

  public void updateField(Library library, double value, String units) {
    throw new UnsupportedOperationException("Method not implemented for unspecified field");
  }

  public void updateField(Sample sample, double value, String units) {
    throw new UnsupportedOperationException("Method not implemented for unspecified field");
  }

  public void updateField(Run run, double value, String units) {
    throw new UnsupportedOperationException("Method not implemented for unspecified field");
  }

  public void updateField(SequencerPartitionContainer container, double value, String units) {
    throw new UnsupportedOperationException("Method not implemented for unspecified field");
  }

}

