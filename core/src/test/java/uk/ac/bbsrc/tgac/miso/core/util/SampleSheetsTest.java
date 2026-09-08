package uk.ac.bbsrc.tgac.miso.core.util;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import org.junit.jupiter.api.Test;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.node.ObjectNode;
import uk.ac.bbsrc.tgac.miso.core.data.InstrumentModel;
import uk.ac.bbsrc.tgac.miso.core.data.InstrumentPosition;
import uk.ac.bbsrc.tgac.miso.core.data.Partition;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.SequencingParameters;
import uk.ac.bbsrc.tgac.miso.core.data.impl.samplesheet.LibraryAliquotProperty;
import uk.ac.bbsrc.tgac.miso.core.data.impl.samplesheet.PoolProperty;
import uk.ac.bbsrc.tgac.miso.core.data.impl.samplesheet.SampleSheet;
import uk.ac.bbsrc.tgac.miso.core.data.impl.samplesheet.SampleSheetFieldCommonSource;
import uk.ac.bbsrc.tgac.miso.core.data.impl.samplesheet.SampleSheetFieldSource;
import uk.ac.bbsrc.tgac.miso.core.data.impl.samplesheet.SampleSheetParameter;
import uk.ac.bbsrc.tgac.miso.core.data.impl.samplesheet.SampleSheetParameter.MultivalueType;
import uk.ac.bbsrc.tgac.miso.core.data.impl.samplesheet.SampleSheetParameter.ParameterType;
import uk.ac.bbsrc.tgac.miso.core.data.impl.samplesheet.SequencingParametersProperty;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ListLibraryAliquotView;

public class SampleSheetsTest {

  private final JsonMapper mapper = JsonMapper.builder().build();

  @Test
  public void testGetMultiValueNone() {
    SampleSheetFieldSource source = mock(SampleSheetFieldSource.class);
    String value = SampleSheets.getMultiValue(source, Collections.<String>emptySet(), (fieldSource, object) -> object);
    assertNull(value);
  }

  @Test
  public void testGetMultiValueSingle() {
    SampleSheetFieldSource source = mock(SampleSheetFieldSource.class);
    String value = SampleSheets.getMultiValue(source, Arrays.asList("a"), (fieldSource, object) -> object);
    assertEquals("a", value);
  }

  @Test
  public void testGetMultiValueMulti() {
    SampleSheetFieldSource source = mock(SampleSheetFieldSource.class);
    String value = SampleSheets.getMultiValue(source, Arrays.asList("a", "b", "c"), (fieldSource, object) -> object);
    assertEquals("a; b; c", value);
  }

  @Test
  public void testGetParameterValueSingle() {
    final String parameterName = "Test Parameter";
    final String value = "Test Value";

    SampleSheet sampleSheet = mock(SampleSheet.class);

    SampleSheetParameter parameter = mock(SampleSheetParameter.class);
    when(parameter.getName()).thenReturn(parameterName);
    when(parameter.getType()).thenReturn(ParameterType.TEXT);
    when(sampleSheet.getParameters()).thenReturn(Collections.singletonList(parameter));

    SampleSheetFieldSource source = mock(SampleSheetFieldSource.class);
    when(source.getSource()).thenReturn(parameterName);

    SampleSheetInput input = mock(SampleSheetInput.class);
    ObjectNode customParameters = mapper.createObjectNode();
    customParameters.put(parameterName, value);
    when(input.getCustomParameters()).thenReturn(customParameters);

    assertEquals(value, SampleSheets.getParameterValue(sampleSheet, source, input, null));
  }

  @Test
  public void testGetParameterValueMultiForPosition() {
    final String parameterName = "Test Parameter";
    final String value = "Test Value A";
    final String instrumentPosition = "A";

    SampleSheet sampleSheet = mock(SampleSheet.class);

    SampleSheetParameter parameter = mock(SampleSheetParameter.class);
    when(parameter.getName()).thenReturn(parameterName);
    when(parameter.getType()).thenReturn(ParameterType.TEXT);
    when(parameter.getMultivalue()).thenReturn(MultivalueType.INSTRUMENT_POSITION);
    when(sampleSheet.getParameters()).thenReturn(Collections.singletonList(parameter));

    SampleSheetFieldSource source = mock(SampleSheetFieldSource.class);
    when(source.getSource()).thenReturn(parameterName);

    SampleSheetInput input = mock(SampleSheetInput.class);
    ObjectNode customParameters = mapper.createObjectNode();
    ObjectNode valuesByPosition = customParameters.putObject(parameterName);
    valuesByPosition.put(instrumentPosition, value);

    when(input.getCustomParameters()).thenReturn(customParameters);

    assertEquals(value, SampleSheets.getParameterValue(sampleSheet, source, input, instrumentPosition));
  }

  @Test
  public void testGetParameterValueMultiForAll() {
    final String parameterName = "Test Parameter";

    SampleSheet sampleSheet = mock(SampleSheet.class);

    SampleSheetParameter parameter = mock(SampleSheetParameter.class);
    when(parameter.getName()).thenReturn(parameterName);
    when(parameter.getType()).thenReturn(ParameterType.TEXT);
    when(parameter.getMultivalue()).thenReturn(MultivalueType.INSTRUMENT_POSITION);
    when(sampleSheet.getParameters()).thenReturn(Collections.singletonList(parameter));

    SampleSheetFieldSource source = mock(SampleSheetFieldSource.class);
    when(source.getSource()).thenReturn(parameterName);
    when(source.getSeparator()).thenReturn("/");

    SampleSheetInput input = mock(SampleSheetInput.class);
    ObjectNode customParameters = mapper.createObjectNode();
    ObjectNode valuesByPosition = customParameters.putObject(parameterName);
    valuesByPosition.put("A", "Value A");
    valuesByPosition.put("B", "Value B");

    when(input.getCustomParameters()).thenReturn(customParameters);

    assertEquals("Value A/Value B", SampleSheets.getParameterValue(sampleSheet, source, input, null));
  }

  @Test
  public void testGetValueFromInputText() {
    final String parameterName = "Test Parameter";
    final String value = "success";

    ObjectNode customParameters = mapper.createObjectNode();
    customParameters.put(parameterName, value);
    JsonNode inputValue = customParameters.get(parameterName);

    SampleSheetParameter parameter = mock(SampleSheetParameter.class);
    when(parameter.getName()).thenReturn(parameterName);
    when(parameter.getType()).thenReturn(ParameterType.TEXT);

    SampleSheetFieldSource source = mock(SampleSheetFieldSource.class);

    assertEquals(value, SampleSheets.getValueFromInput(inputValue, source, parameter));
  }

  @Test
  public void testGetValueFromInputDropdownValue() {
    final String parameterName = "Test Parameter";
    final String selectedValue = "option 2";

    ObjectNode customParameters = mapper.createObjectNode();
    customParameters.put(parameterName, selectedValue);
    JsonNode inputValue = customParameters.get(parameterName);

    SampleSheetParameter parameter = mock(SampleSheetParameter.class);
    when(parameter.getName()).thenReturn(parameterName);
    when(parameter.getType()).thenReturn(ParameterType.DROPDOWN);
    when(parameter.getSource()).thenReturn(mapper.createArrayNode());
    ObjectNode option1 = parameter.getSource().addObject();
    option1.put("value", "option 1");
    ObjectNode option2 = parameter.getSource().addObject();
    option2.put("value", selectedValue);

    SampleSheetFieldSource source = mock(SampleSheetFieldSource.class);

    assertEquals(selectedValue, SampleSheets.getValueFromInput(inputValue, source, parameter));
  }

  @Test
  public void testGetValueFromInputDropdownCustomField() {
    final String parameterName = "Test Parameter";
    final String customField = "otherThing";
    final String selectedValue = "option 2";
    final String selectedCustomValue = "thing 2";

    ObjectNode customParameters = mapper.createObjectNode();
    customParameters.put(parameterName, selectedValue);
    JsonNode inputValue = customParameters.get(parameterName);

    SampleSheetParameter parameter = mock(SampleSheetParameter.class);
    when(parameter.getName()).thenReturn(parameterName);
    when(parameter.getType()).thenReturn(ParameterType.DROPDOWN);
    when(parameter.getSource()).thenReturn(mapper.createArrayNode());
    ObjectNode option1 = parameter.getSource().addObject();
    option1.put("value", "option 1");
    option1.put(customField, "thing 1");
    ObjectNode option2 = parameter.getSource().addObject();
    option2.put("value", selectedValue);
    option2.put(customField, selectedCustomValue);

    SampleSheetFieldSource source = mock(SampleSheetFieldSource.class);
    when(source.getSourceProperty()).thenReturn(customField);

    assertEquals(selectedCustomValue, SampleSheets.getValueFromInput(inputValue, source, parameter));
  }

  @Test
  public void testGetInstrumentModelValue() {
    final String alias = "UG100";

    SampleSheetFieldSource source = mock(SampleSheetFieldSource.class);
    when(source.getSource()).thenReturn(SampleSheetFieldCommonSource.INSTRUMENT_MODEL.name());

    InstrumentModel model = mock(InstrumentModel.class);
    when(model.getAlias()).thenReturn(alias);

    assertEquals(alias, SampleSheets.getInstrumentModelValue(source, model));
  }

  @Test
  public void testGetInstrumentPositionValue() {
    final String alias = "X";

    SampleSheetFieldSource source = mock(SampleSheetFieldSource.class);
    when(source.getSource()).thenReturn(SampleSheetFieldCommonSource.INSTRUMENT_POSITION.name());

    InstrumentPosition position = mock(InstrumentPosition.class);
    when(position.getAlias()).thenReturn(alias);

    assertEquals(alias, SampleSheets.getInstrumentPositionValue(source, null, alias));
  }

  @Test
  public void testGetLibraryAliquotValue() {
    final String projectCode = "ASDF";

    SampleSheetFieldSource source = mock(SampleSheetFieldSource.class);
    when(source.getSource()).thenReturn(SampleSheetFieldCommonSource.LIBRARY_ALIQUOT.name());
    when(source.getSourceProperty()).thenReturn(LibraryAliquotProperty.PROJECT_CODE.name());

    ListLibraryAliquotView aliquot = mock(ListLibraryAliquotView.class);
    when(aliquot.getProjectCode()).thenReturn(projectCode);

    assertEquals(projectCode, SampleSheets.getLibraryAliquotValue(source, aliquot));
  }

  @Test
  public void testGetPartitionValue() {
    final Integer partitionNumber = 3;

    SampleSheetFieldSource source = mock(SampleSheetFieldSource.class);
    when(source.getSource()).thenReturn(SampleSheetFieldCommonSource.PARTITION.name());

    Partition partition = mock(Partition.class);
    when(partition.getPartitionNumber()).thenReturn(partitionNumber);

    assertEquals(partitionNumber.toString(), SampleSheets.getPartitionValue(source, partitionNumber));
  }

  @Test
  public void testGetPoolValue() {
    final String alias = "My Pool";

    SampleSheetFieldSource source = mock(SampleSheetFieldSource.class);
    when(source.getSource()).thenReturn(SampleSheetFieldCommonSource.POOL.name());
    when(source.getSourceProperty()).thenReturn(PoolProperty.ALIAS.name());

    Pool pool = mock(Pool.class);
    when(pool.getAlias()).thenReturn(alias);

    assertEquals(alias, SampleSheets.getPoolValue(source, pool));
  }

  @Test
  public void testGetSequencingParametersValue() {
    final int readLength = 123;

    SampleSheetFieldSource source = mock(SampleSheetFieldSource.class);
    when(source.getSource()).thenReturn(SampleSheetFieldCommonSource.SEQUENCING_PARAMETERS.name());
    when(source.getSourceProperty()).thenReturn(SequencingParametersProperty.READ_1_LENGTH.name());

    SequencingParameters params = mock(SequencingParameters.class);
    when(params.getReadLength()).thenReturn(readLength);

    assertEquals(Integer.toString(readLength), SampleSheets.getSequencingParametersValue(source, params));
  }

  @Test
  public void testGetSequencingParametersValue2() {
    final String instrumentPosition = "A";
    final int readLength = 123;

    SampleSheetFieldSource source = mock(SampleSheetFieldSource.class);
    when(source.getSource()).thenReturn(SampleSheetFieldCommonSource.SEQUENCING_PARAMETERS.name());
    when(source.getSourceProperty()).thenReturn(SequencingParametersProperty.READ_1_LENGTH.name());

    SequencingParameters params = mock(SequencingParameters.class);
    when(params.getReadLength()).thenReturn(123);

    SampleSheetInput input = mock(SampleSheetInput.class);
    when(input.getSequencingParametersByInstrumentPosition()).thenReturn(new HashMap<>());
    input.getSequencingParametersByInstrumentPosition().put(instrumentPosition, params);

    assertEquals(Integer.toString(readLength),
        SampleSheets.getSequencingParametersValue(source, input, instrumentPosition));
  }
}
