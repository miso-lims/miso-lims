package uk.ac.bbsrc.tgac.miso.core.util;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import tools.jackson.databind.JsonNode;
import uk.ac.bbsrc.tgac.miso.core.data.InstrumentModel;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.SequencingParameters;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Requisition;
import uk.ac.bbsrc.tgac.miso.core.data.impl.samplesheet.LibraryAliquotProperty;
import uk.ac.bbsrc.tgac.miso.core.data.impl.samplesheet.PoolProperty;
import uk.ac.bbsrc.tgac.miso.core.data.impl.samplesheet.RequisitionProperty;
import uk.ac.bbsrc.tgac.miso.core.data.impl.samplesheet.SampleSheet;
import uk.ac.bbsrc.tgac.miso.core.data.impl.samplesheet.SampleSheetField;
import uk.ac.bbsrc.tgac.miso.core.data.impl.samplesheet.SampleSheetFieldCommonSource;
import uk.ac.bbsrc.tgac.miso.core.data.impl.samplesheet.SampleSheetFieldSource;
import uk.ac.bbsrc.tgac.miso.core.data.impl.samplesheet.SampleSheetFieldSource.Aggregation;
import uk.ac.bbsrc.tgac.miso.core.data.impl.samplesheet.SampleSheetParameter;
import uk.ac.bbsrc.tgac.miso.core.data.impl.samplesheet.SampleSheetSection;
import uk.ac.bbsrc.tgac.miso.core.data.impl.samplesheet.SequencingParametersProperty;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.GrandparentSample;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ListLibraryAliquotView;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ParentLibrary;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ParentSample;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.PoolElement;

public class SampleSheets {

  private static final String DEFAULT_INSTRUMENT_POS = "*";

  private static final Set<String> validDateFormats = Collections.unmodifiableSet(Set.of("yyyy-MM-dd", "M/d/yyyy"));
  private static final Set<String> validDateTimeFormats =
      Collections.unmodifiableSet(Set.of("yyyy-MM-dd", "M/d/yyyy", "yyyyMMddHHmmss"));

  private SampleSheets() {
    // static util class not intended for instantiation
  }

  /**
   * Generate a sample sheet based on a sample sheet definition combined with user input. A valid
   * definition and matching valid input are assumed, and any invalid data will result in a runtime
   * exception.
   * 
   * @param sampleSheet the sample sheet format definition
   * @param input user input
   * @return the bytes of a String intended for writing as a CSV file
   */
  public static byte[] generate(SampleSheet sampleSheet, SampleSheetInput input) {
    List<List<String>> sampleSheetRows = generateData(sampleSheet, input);
    return writeCsv(sampleSheetRows);
  }

  private static List<List<String>> generateData(SampleSheet sampleSheet, SampleSheetInput input) {
    List<List<String>> sampleSheetRows = new ArrayList<>();
    boolean firstSection = true;
    for (SampleSheetSection section : sampleSheet.getSections()) {
      if (Objects.equals(section.getOptional(), Boolean.TRUE)
          && Objects.equals(input.getIncludeSections().get(section.getName()), Boolean.FALSE)) {
        continue;
      }
      List<List<String>> unformattedRows = generateUnformattedSectionData(sampleSheet, section, input);

      if (firstSection) {
        firstSection = false;
      } else {
        sampleSheetRows.add(Collections.emptyList());
      }
      addFormatedSectionData(section, unformattedRows, sampleSheetRows);
    }
    return sampleSheetRows;
  }

  private static List<List<String>> generateUnformattedSectionData(SampleSheet sampleSheet, SampleSheetSection section,
      SampleSheetInput input) {
    List<List<String>> rows = new ArrayList<>();
    for (SampleSheetField field : section.getFields()) {
      List<String> values = new ArrayList<>();
      rows.add(values);
      if (section.getMultivalue() == null) {
        StringBuilder sb = new StringBuilder();
        for (SampleSheetFieldSource source : field.getSources()) {
          String value = generateSingleValue(sampleSheet, source, input);
          if (value != null) {
            sb.append(value);
          }
        }
        values.add(sb.isEmpty() ? null : sb.toString());
      } else {
        switch (section.getMultivalue()) {
          case INSTRUMENT_POSITIONS:
            input.getPoolLayout().forEach((instrumentPos, poolsByPartition) -> {
              String realPos = Objects.equals(instrumentPos, DEFAULT_INSTRUMENT_POS) ? null : instrumentPos;
              StringBuilder sb = new StringBuilder();
              for (SampleSheetFieldSource source : field.getSources()) {
                String value = generateValueForInstrumentPos(sampleSheet, source, input, realPos, poolsByPartition);
                if (value != null) {
                  sb.append(value);
                }
              }
              values.add(sb.isEmpty() ? null : sb.toString());
            });
            break;
          case LIBRARY_ALIQUOTS:
            input.getPoolLayout().forEach((instrumentPos, poolsByPartition) -> {
              String realPos = Objects.equals(instrumentPos, DEFAULT_INSTRUMENT_POS) ? null : instrumentPos;
              poolsByPartition.forEach((partitionNumber, pool) -> {
                if (pool == null) {
                  return;
                }
                pool.getPoolContents().forEach(poolElement -> {
                  StringBuilder sb = new StringBuilder();
                  for (SampleSheetFieldSource source : field.getSources()) {
                    String value = generateValueForLibraryAliquot(sampleSheet, source, input, realPos,
                        partitionNumber, pool, poolElement.getAliquot());
                    if (value != null) {
                      sb.append(value);
                    }
                  }
                  values.add(sb.isEmpty() ? null : sb.toString());
                });
              });
            });
            break;
          case DISTINCT_LIBRARY_ALIQUOTS:
            Set<Long> includedAliquotIds = new HashSet<>();
            input.getPoolLayout().forEach((instrumentPos, poolsByPartition) -> {
              String realPos = Objects.equals(instrumentPos, DEFAULT_INSTRUMENT_POS) ? null : instrumentPos;
              poolsByPartition.forEach((partitionNumber, pool) -> {
                if (pool == null) {
                  return;
                }
                pool.getPoolContents().forEach(poolElement -> {
                  if (!includedAliquotIds.add(poolElement.getAliquot().getId())) {
                    return;
                  }
                  StringBuilder sb = new StringBuilder();
                  for (SampleSheetFieldSource source : field.getSources()) {
                    String value = generateValueForLibraryAliquot(sampleSheet, source, input, realPos,
                        null, pool, poolElement.getAliquot());
                    if (value != null) {
                      sb.append(value);
                    }
                  }
                  values.add(sb.isEmpty() ? null : sb.toString());
                });
              });
            });
            break;
          default:
            throw new IllegalArgumentException(
                "Unexpected section multivalue type: %s".formatted(section.getMultivalue()));
        }
      }
    }
    return rows;
  }

  private static void addFormatedSectionData(SampleSheetSection section, List<List<String>> unformattedRows,
      List<List<String>> sampleSheetRows) {
    List<String> headerRow = new ArrayList<>();
    headerRow.add("[" + section.getName() + "]");
    sampleSheetRows.add(headerRow);
    boolean invadeHeader = Objects.equals(section.getInvadeHeader(), Boolean.TRUE);
    boolean omitFieldNames = Objects.equals(section.getOmitFieldNames(), Boolean.TRUE);
    switch (section.getFormat()) {
      case ROWS:
        for (int i = 0; i < section.getFields().size(); i++) {
          SampleSheetField field = section.getFields().get(i);
          if (omitRow(unformattedRows.get(i), field)) {
            continue;
          }
          if (invadeHeader) {
            // first field goes into the header row (with the section name in place of the field name)
            headerRow.addAll(unformattedRows.get(i));
            invadeHeader = false;
          }
          List<String> sampleSheetRow = new ArrayList<>();
          if (!omitFieldNames) {
            sampleSheetRow.add(field.getName());
          }
          sampleSheetRow.addAll(unformattedRows.get(i));
          sampleSheetRows.add(sampleSheetRow);
        }
        break;
      case COLUMNS:
        // Note: invadeHeader is not valid for COLUMNS
        List<String> headingsRow = new ArrayList<>();
        List<List<String>> sectionDataRows = new ArrayList<>();
        for (int i = 0; i < unformattedRows.get(0).size(); i++) {
          sectionDataRows.add(new ArrayList<>());
        }
        for (int i = 0; i < section.getFields().size(); i++) {
          SampleSheetField field = section.getFields().get(i);
          if (!omitRow(unformattedRows.get(i), field)) {
            headingsRow.add(field.getName());
            // "rows" need to be rearranged into columns
            for (int j = 0; j < unformattedRows.get(i).size(); j++) {
              sectionDataRows.get(j).add(unformattedRows.get(i).get(j));
            }
          }
        }
        if (!omitFieldNames) {
          sampleSheetRows.add(headingsRow);
        }
        sampleSheetRows.addAll(sectionDataRows);
        break;
      default:
        throw new IllegalArgumentException(
            "Unexpected section format type: %s".formatted(section.getFormat()));
    }
  }

  private static boolean omitRow(List<String> row, SampleSheetField field) {
    return Objects.equals(field.getOmitIfEmpty(), Boolean.TRUE) && row.stream().noneMatch(Objects::nonNull);
  }

  private static String generateSingleValue(SampleSheet sampleSheet, SampleSheetFieldSource source,
      SampleSheetInput input) {
    if (source.getValue() != null) {
      return source.getValue();
    }
    SampleSheetFieldCommonSource commonSource = SampleSheetFieldCommonSource.get(source.getSource());
    if (commonSource == null) {
      return getParameterValue(sampleSheet, source, input, null);
    }
    switch (commonSource) {
      case INSTRUMENT_MODEL:
        return getInstrumentModelValue(source, input.getInstrumentModel());
      case SEQUENCING_PARAMETERS:
        if (input.getInstrumentModel().getPlatformType().hasContainerLevelParameters()) {
          Collection<SequencingParameters> params = input.getSequencingParametersByInstrumentPosition().values();
          return getMultiValue(source, params, SampleSheets::getSequencingParametersValue);
        } else {
          return getSequencingParametersValue(source, input.getSequencingParameters());
        }
      case INSTRUMENT_POSITION:
        return getMultiValue(source, input.getPoolLayout().keySet(), SampleSheets::getInstrumentPositionValue);
      case PARTITION:
        List<Integer> partitionNumbers =
            input.getPoolLayout().values().stream().flatMap(map -> map.keySet().stream()).toList();
        return getMultiValue(source, partitionNumbers, SampleSheets::getPartitionValue);
      case POOL:
        List<Pool> pools = input.getPoolLayout().values().stream()
            .flatMap(map -> map.values().stream())
            .filter(Objects::nonNull)
            .toList();
        return getMultiValue(source, pools, SampleSheets::getPoolValue);
      case LIBRARY_ALIQUOT:
        List<ListLibraryAliquotView> aliquots = input.getPoolLayout().values().stream()
            .flatMap(map -> map.values().stream().filter(Objects::nonNull))
            .flatMap(pool -> pool.getPoolContents() == null ? Stream.empty() : pool.getPoolContents().stream())
            .map(PoolElement::getAliquot)
            .toList();
        return getMultiValue(source, aliquots, SampleSheets::getLibraryAliquotValue);
      case REQUISITION:
        List<ListLibraryAliquotView> requisitionAliquots = input.getPoolLayout().values().stream()
            .flatMap(map -> map.values().stream().filter(Objects::nonNull))
            .flatMap(pool -> pool.getPoolContents() == null ? Stream.empty() : pool.getPoolContents().stream())
            .map(PoolElement::getAliquot)
            .toList();
        return getMultiValue(source, requisitionAliquots, SampleSheets::getRequisitionValue);
      case CONTAINER:
        return getMultiValue(source, input.getContainerIdentificationBarcode() == null
            ? Collections.emptyList()
            : input.getContainerIdentificationBarcode().values(),
            SampleSheets::getContainerValue);

      case CURRENT_TIME:
        return formatCurrentDateTime(source.getDateFormat());
      default:
        throw new IllegalArgumentException("Generator undefined for common source '%s'".formatted(commonSource));
    }
  }

  private static String generateValueForInstrumentPos(SampleSheet sampleSheet, SampleSheetFieldSource source,
      SampleSheetInput input, String instrumentPos, Map<Integer, Pool> poolsByPartition) {
    if (source.getValue() != null) {
      return source.getValue();
    }
    SampleSheetFieldCommonSource commonSource = SampleSheetFieldCommonSource.get(source.getSource());
    if (commonSource == null) {
      return getParameterValue(sampleSheet, source, input, instrumentPos);
    }
    switch (commonSource) {
      case INSTRUMENT_MODEL:
        return getInstrumentModelValue(source, input.getInstrumentModel());
      case SEQUENCING_PARAMETERS:
        if (input.getInstrumentModel().getPlatformType().hasContainerLevelParameters()) {
          return getSequencingParametersValue(source, input, instrumentPos);
        } else {
          return getSequencingParametersValue(source, input.getSequencingParameters());
        }
      case INSTRUMENT_POSITION:
        return getInstrumentPositionValue(source, instrumentPos);
      case PARTITION:
        return getMultiValue(source, poolsByPartition.keySet(), SampleSheets::getPartitionValue);
      case POOL:
        return getMultiValue(source, poolsByPartition.values().stream().filter(Objects::nonNull).toList(),
            SampleSheets::getPoolValue);
      case LIBRARY_ALIQUOT:
        List<ListLibraryAliquotView> aliquots = poolsByPartition.values().stream()
            .filter(Objects::nonNull)
            .flatMap(pool -> pool.getPoolContents() == null ? Stream.empty() : pool.getPoolContents().stream())
            .map(PoolElement::getAliquot)
            .toList();
        return getMultiValue(source, aliquots, SampleSheets::getLibraryAliquotValue);
      case REQUISITION:
        List<ListLibraryAliquotView> requisitionAliquots = poolsByPartition.values().stream()
            .filter(Objects::nonNull)
            .flatMap(pool -> pool.getPoolContents() == null ? Stream.empty() : pool.getPoolContents().stream())
            .map(PoolElement::getAliquot)
            .toList();
        return getMultiValue(source, requisitionAliquots, SampleSheets::getRequisitionValue);
      case CONTAINER:
        return getContainerValue(source, getContainerBarcode(input, instrumentPos));
      case CURRENT_TIME:
        return formatCurrentDateTime(source.getDateFormat());
      default:
        throw new IllegalArgumentException("Generator undefined for common source '%s'".formatted(commonSource));
    }
  }

  private static String generateValueForLibraryAliquot(SampleSheet sampleSheet, SampleSheetFieldSource source,
      SampleSheetInput input, String instrumentPos, Integer partitionNumber, Pool pool,
      ListLibraryAliquotView libraryAliquot) {
    if (source.getValue() != null) {
      return source.getValue();
    }
    SampleSheetFieldCommonSource commonSource = SampleSheetFieldCommonSource.get(source.getSource());
    if (commonSource == null) {
      return getParameterValue(sampleSheet, source, input, instrumentPos);
    }
    switch (commonSource) {
      case INSTRUMENT_MODEL:
        return getInstrumentModelValue(source, input.getInstrumentModel());
      case SEQUENCING_PARAMETERS:
        if (input.getInstrumentModel().getPlatformType().hasContainerLevelParameters()) {
          return getSequencingParametersValue(source, input, instrumentPos);
        } else {
          return getSequencingParametersValue(source, input.getSequencingParameters());
        }
      case INSTRUMENT_POSITION:
        return getInstrumentPositionValue(source, instrumentPos);
      case PARTITION:
        if (partitionNumber == null) {
          throw new IllegalArgumentException(
              "'Partition' field source is invalid for 'distinct library aliquot' section multivalue type");
        }
        return getPartitionValue(source, partitionNumber);
      case POOL:
        return getPoolValue(source, pool);
      case LIBRARY_ALIQUOT:
        return getLibraryAliquotValue(source, libraryAliquot);
      case REQUISITION:
        return getRequisitionValue(source, libraryAliquot);
      case CONTAINER:
        return getContainerValue(source, getContainerBarcode(input, instrumentPos));
      case CURRENT_TIME:
        return formatCurrentDateTime(source.getDateFormat());
      default:
        throw new IllegalArgumentException("Generator undefined for common source '%s'".formatted(commonSource));
    }
  }

  protected static <T> String getMultiValue(SampleSheetFieldSource source, Collection<T> objects,
      BiFunction<SampleSheetFieldSource, T, String> getValue) {
    if (objects == null || objects.isEmpty()) {
      return null;
    }
    Stream<String> stream = objects.stream()
        .map(obj -> getValue.apply(source, obj))
        .filter(Objects::nonNull)
        .distinct();

    Aggregation aggregation = source.getAggregation() == null ? Aggregation.JOIN_DISTINCT : source.getAggregation();
    switch (aggregation) {
      case JOIN_DISTINCT:
        return stream.collect(Collectors.joining(source.getSeparator() == null ? "; " : source.getSeparator()));
      case MAX_LENGTH:
        OptionalInt max = stream.mapToInt(String::length).max();
        if (max.isPresent()) {
          return Integer.toString(max.getAsInt());
        } else {
          return null;
        }
      default:
        throw new IllegalArgumentException("Unexpected aggregation method: %s".formatted(aggregation));
    }

  }

  protected static String getInstrumentModelValue(SampleSheetFieldSource source,
      InstrumentModel model) {
    if (source.getSourceProperty() == null) {
      return model.getAlias();
    }
    throw new IllegalArgumentException(
        "Unexpected instrument model property: %s".formatted(source.getSourceProperty()));
  }

  protected static String getSequencingParametersValue(SampleSheetFieldSource source,
      SampleSheetInput input, String instrumentPosition) {
    SequencingParameters params = input.getSequencingParametersByInstrumentPosition().get(instrumentPosition);
    return getSequencingParametersValue(source, params);
  }

  protected static String getSequencingParametersValue(SampleSheetFieldSource source,
      SequencingParameters sequencingParameters) {
    SequencingParametersProperty property = SequencingParametersProperty.valueOf(source.getSourceProperty());
    return property.extract(sequencingParameters);
  }

  protected static String getInstrumentPositionValue(SampleSheetFieldSource source, String position) {
    if (source.getSourceProperty() == null) {
      return position;
    }
    throw new IllegalArgumentException(
        "Unexpected instrument position property: %s".formatted(source.getSourceProperty()));
  }

  protected static String getPartitionValue(SampleSheetFieldSource source, Integer partitionNumber) {
    if (source.getSourceProperty() == null) {
      return partitionNumber.toString();
    }
    throw new IllegalArgumentException("Unexpected partition property: %s".formatted(source.getSourceProperty()));
  }

  protected static String getPoolValue(SampleSheetFieldSource source, Pool pool) {
    PoolProperty property = PoolProperty.valueOf(source.getSourceProperty());
    return property.extract(pool);
  }

  protected static String getLibraryAliquotValue(SampleSheetFieldSource source, ListLibraryAliquotView aliquot) {
    LibraryAliquotProperty property = LibraryAliquotProperty.valueOf(source.getSourceProperty());
    return property.extract(aliquot);
  }

  protected static String getParameterValue(SampleSheet sampleSheet, SampleSheetFieldSource source,
      SampleSheetInput input, String instrumentPosition) {
    SampleSheetParameter parameter = sampleSheet.getParameters().stream()
        .filter(param -> Objects.equals(param.getName(), source.getSource()))
        .findAny()
        .orElseThrow(() -> new IllegalArgumentException("Invalid source: %s".formatted(source.getSource())));
    JsonNode inputValue = input.getCustomParameters().get(source.getSource());
    if (parameter.getMultivalue() == null) {
      // single value
      return getValueFromInput(inputValue, source, parameter);
    } else if (parameter.getMultivalue() == SampleSheetParameter.MultivalueType.INSTRUMENT_POSITION) {
      if (instrumentPosition == null) {
        // combine position-specific values into one value
        List<String> values = new ArrayList<>();
        inputValue.properties()
            .forEach(entry -> values.add(getValueFromInput(entry.getValue(), source, parameter)));
        return String.join(source.getSeparator() == null ? "; " : source.getSeparator(), values);
      } else {
        // single position-specific value
        inputValue = inputValue.get(instrumentPosition);
        return getValueFromInput(inputValue, source, parameter);
      }
    } else {
      throw new IllegalArgumentException(
          "Unexpected multivalue type: %s".formatted(parameter.getMultivalue()));
    }
  }

  protected static String getValueFromInput(JsonNode inputValue, SampleSheetFieldSource source,
      SampleSheetParameter parameter) {
    if (inputValue.isNull() || (inputValue.isString() && inputValue.asString().isBlank())) {
      return null;
    }
    switch (parameter.getType()) {
      case TEXT:
      case INT:
      case DECIMAL:
        return inputValue.asString();
      case DATE:
        return formatDate(inputValue.asString(), source.getDateFormat());
      case DROPDOWN:
        JsonNode valueJson = findByValue(parameter, inputValue.asString());
        if (source.getSourceProperty() == null) {
          return valueJson.get("value").asString();
        } else {
          return valueJson.get(source.getSourceProperty()).asString();
        }
      default:
        throw new IllegalArgumentException("Unexpected parameter type: %s".formatted(parameter.getType()));
    }
  }

  protected static Requisition getEffectiveRequisition(ListLibraryAliquotView aliquot) {
    ParentLibrary library = aliquot.getParentLibrary();
    if (library.getRequisition() != null) {
      return library.getRequisition();
    }
    ParentSample sample = library.getParentSample();
    if (sample == null) {
      return null;
    }
    if (sample.getRequisition() != null) {
      return sample.getRequisition();
    }
    GrandparentSample parent = sample.getParentSample();
    while (parent != null) {
      if (parent.getRequisition() != null) {
        return parent.getRequisition();
      }
      parent = parent.getParentSample();
    }
    return null;
  }

  protected static String getContainerValue(SampleSheetFieldSource source, String identificationBarcode) {
    if (source.getSourceProperty() != null) {
      throw new IllegalArgumentException("Unexpected container property: %s".formatted(source.getSourceProperty()));
    }
    return identificationBarcode;
  }

  private static String getContainerBarcode(SampleSheetInput input, String instrumentPos) {
    if (input.getContainerIdentificationBarcode() == null) {
      return null;
    }
    String key = instrumentPos == null ? DEFAULT_INSTRUMENT_POS : instrumentPos;
    return input.getContainerIdentificationBarcode().get(key);
  }

  protected static String getRequisitionValue(SampleSheetFieldSource source, ListLibraryAliquotView aliquot) {
    Requisition requisition = getEffectiveRequisition(aliquot);
    if (requisition == null) {
      return null;
    }
    RequisitionProperty property = RequisitionProperty.valueOf(source.getSourceProperty());
    return property.extract(requisition);
  }

  private static JsonNode findByValue(SampleSheetParameter parameter, String value) {
    for (JsonNode node : parameter.getSource()) {
      if (Objects.equals(node.get("value").asString(), value)) {
        return node;
      }
    }
    throw new IllegalArgumentException("Invalid value for '%s' parameter: %s".formatted(parameter.getName(), value));
  }

  private static String formatDate(String inputDate, String format) {
    LocalDate date = LimsUtils.parseLocalDate(inputDate);
    if (format == null) {
      return date.format(DateTimeFormatter.ISO_LOCAL_DATE);
    }
    if (validDateFormats.contains(format)) {
      return date.format(DateTimeFormatter.ofPattern("M/d/yyyy"));
    }
    throw new IllegalArgumentException("Unexpected date format: %s".formatted(format));
  }

  private static String formatCurrentDateTime(String format) {
    if (format == null) {
      return formatCurrentDateTime("yyyy-mm-dd");
    }
    if (validDateTimeFormats.contains(format)) {
      return new SimpleDateFormat(format).format(new Date());
    }
    throw new IllegalArgumentException("Unexpected date format: %s".formatted(format));
  }

  private static byte[] writeCsv(List<List<String>> rows) {
    StringBuilder sb = new StringBuilder();
    int columnCount = rows.stream().mapToInt(List::size).max().orElseThrow();
    for (List<String> row : rows) {
      for (int i = 0; i < columnCount; i++) {
        if (i > 0) {
          sb.append(",");
        }
        if (row.size() > i) {
          String value = row.get(i);
          if (value != null) {
            // Strings are not typically quoted in sample sheets, but it's necessary if they contain quotes or
            // commas
            if (value.contains("\"") || value.contains(",")) {
              sb.append("\"")
                  .append(value.replaceAll("\"", "\"\""))
                  .append("\"");
            } else {
              sb.append(value);
            }
          }
        }
      }
      sb.append("\r\n");
    }
    return sb.toString().getBytes(StandardCharsets.UTF_8);
  }

}
