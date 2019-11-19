package uk.ac.bbsrc.tgac.miso.core.data.qc;

import java.math.BigDecimal;

import uk.ac.bbsrc.tgac.miso.core.data.ConcentrationUnit;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.core.data.VolumeUnit;

public enum QcCorrespondingField {
  CONCENTRATION {

    @Override
    public void updateField(Pool pool, BigDecimal value, String units) {
      pool.setConcentration(value);
      ConcentrationUnit concUnit = ConcentrationUnit.getFromString(units);
      if (concUnit != null) {
        pool.setConcentrationUnits(concUnit);
      }
    }

    @Override
    public void updateField(Library library, BigDecimal value, String units) {
      library.setConcentration(value);
      ConcentrationUnit concUnit = ConcentrationUnit.getFromString(units);
      if (concUnit != null) {
        library.setConcentrationUnits(concUnit);
      }
    }

    @Override
    public void updateField(Sample sample, BigDecimal value, String units) {
      sample.setConcentration(value);
      ConcentrationUnit concUnit = ConcentrationUnit.getFromString(units);
      if (concUnit != null) {
        sample.setConcentrationUnits(concUnit);
      }
    }

  },
  VOLUME {

    @Override
    public void updateField(Pool pool, BigDecimal value, String units) {
      pool.setVolume(value);
      VolumeUnit volUnit = VolumeUnit.getFromString(units);
      if (volUnit != null) {
        pool.setVolumeUnits(volUnit);
      }
    }

    @Override
    public void updateField(Library library, BigDecimal value, String units) {
      library.setVolume(value);
      VolumeUnit volUnit = VolumeUnit.getFromString(units);
      if (volUnit != null) {
        library.setVolumeUnits(volUnit);
      }
    }

    @Override
    public void updateField(Sample sample, BigDecimal value, String units) {
      sample.setVolume(value);
      VolumeUnit volUnit = VolumeUnit.getFromString(units);
      if (volUnit != null) {
        sample.setVolumeUnits(volUnit);
      }
    }

  },
  NONE;

  public void updateField(Pool pool, BigDecimal value, String units) {
    throw new UnsupportedOperationException("Method not implemented for unspecified field");
  }

  public void updateField(Library library, BigDecimal value, String units) {
    throw new UnsupportedOperationException("Method not implemented for unspecified field");
  }

  public void updateField(Sample sample, BigDecimal value, String units) {
    throw new UnsupportedOperationException("Method not implemented for unspecified field");
  }

  public void updateField(Run run, BigDecimal value, String units) {
    throw new UnsupportedOperationException("Method not implemented for unspecified field");
  }

  public void updateField(SequencerPartitionContainer container, BigDecimal value, String units) {
    throw new UnsupportedOperationException("Method not implemented for unspecified field");
  }

}

