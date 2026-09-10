package uk.ac.bbsrc.tgac.miso.core.data.impl.samplesheet;

import java.util.Objects;

import uk.ac.bbsrc.tgac.miso.core.data.SequencingParameters;

public enum SequencingParametersProperty {

  READ_1_LENGTH("Read 1 Length") {
    @Override
    public String extract(SequencingParameters sequencingParameters) {
      return sequencingParameters.getReadLength() == 0 ? null
          : Integer.toString(sequencingParameters.getReadLength());
    }
  },

  READ_2_LENGTH("Read 2 Length") {
    @Override
    public String extract(SequencingParameters sequencingParameters) {
      return sequencingParameters.getReadLength2() == 0 ? null
          : Integer.toString(sequencingParameters.getReadLength2());
    }
  },

  CHEMISTRY("Chemistry") {
    @Override
    public String extract(SequencingParameters sequencingParameters) {
      return Objects.toString(sequencingParameters.getChemistry());
    }
  },

  RUN_TYPE("Run Type") {
    @Override
    public String extract(SequencingParameters sequencingParameters) {
      return sequencingParameters.getRunType();
    }
  },

  MOVIE_TIME("Movie Time") {
    @Override
    public String extract(SequencingParameters sequencingParameters) {
      return Objects.toString(sequencingParameters.getMovieTime(), null);
    }
  },

  FLOWS("Flows") {
    @Override
    public String extract(SequencingParameters sequencingParameters) {
      return Objects.toString(sequencingParameters.getFlows(), null);
    }
  },

  PAIRED("Paired") {
    @Override
    public String extract(SequencingParameters sequencingParameters) {
      if (sequencingParameters.getReadLength2() > 0) {
        return "True";
      } else if (sequencingParameters.getReadLength() > 0) {
        return "False";
      } else {
        return null;
      }
    }
  };

  private final String label;

  private SequencingParametersProperty(String label) {
    this.label = label;
  }

  public String getLabel() {
    return label;
  }

  public abstract String extract(SequencingParameters sequencingParameters);
}
