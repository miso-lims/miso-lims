package uk.ac.bbsrc.tgac.miso.core.util;

import java.util.Map;

import tools.jackson.databind.node.ObjectNode;
import uk.ac.bbsrc.tgac.miso.core.data.InstrumentModel;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.core.data.SequencingParameters;

public class SampleSheetInput {

  private InstrumentModel instrumentModel;
  private SequencingParameters sequencingParameters;
  private Map<String, SequencingParameters> sequencingParametersByInstrumentPosition;
  private ObjectNode customParameters;
  private Map<String, Map<Integer, Pool>> poolLayout;
  private Map<String, Boolean> includeSections;
  private Map<String, SequencerPartitionContainer> containersByInstrumentPosition;

  public InstrumentModel getInstrumentModel() {
    return instrumentModel;
  }

  public void setInstrumentModel(InstrumentModel instrumentModel) {
    this.instrumentModel = instrumentModel;
  }

  public SequencingParameters getSequencingParameters() {
    return sequencingParameters;
  }

  public void setSequencingParameters(SequencingParameters sequencingParameters) {
    this.sequencingParameters = sequencingParameters;
  }

  public Map<String, SequencingParameters> getSequencingParametersByInstrumentPosition() {
    return sequencingParametersByInstrumentPosition;
  }

  public void setSequencingParametersByInstrumentPosition(
      Map<String, SequencingParameters> sequencingParametersByInstrumentPosition) {
    this.sequencingParametersByInstrumentPosition = sequencingParametersByInstrumentPosition;
  }

  public ObjectNode getCustomParameters() {
    return customParameters;
  }

  public void setCustomParameters(ObjectNode customParameters) {
    this.customParameters = customParameters;
  }

  public Map<String, Map<Integer, Pool>> getPoolLayout() {
    return poolLayout;
  }

  public void setPoolLayout(Map<String, Map<Integer, Pool>> poolLayout) {
    this.poolLayout = poolLayout;
  }

  public Map<String, Boolean> getIncludeSections() {
    return includeSections;
  }

  public void setIncludeSections(Map<String, Boolean> includeSections) {
    this.includeSections = includeSections;
  }

  public Map<String, SequencerPartitionContainer> getContainersByInstrumentPosition() {
    return containersByInstrumentPosition;
  }

  public void setContainersByInstrumentPosition(
      Map<String, SequencerPartitionContainer> containersByInstrumentPosition) {
    this.containersByInstrumentPosition = containersByInstrumentPosition;
  }

}
