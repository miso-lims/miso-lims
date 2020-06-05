package uk.ac.bbsrc.tgac.miso.core.data.spreadsheet;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.http.MediaType;

public enum SpreadSheetFormat {
  EXCEL("Microsoft Excel", "xlsx", "application", "vnd.ms-excel") {

    @Override
    public <T> byte[] generate(Stream<T> input, boolean isDetailedSample, Spreadsheet<T> format) {
      return input.collect(new ExcelCollector<>(getColumnsFiltered(format, isDetailedSample)));
    }
  },
  ODF("Open Document Format", "ods", "application", "vnd.oasis.opendocument.spreadsheet") {
    @Override
    public <T> byte[] generate(Stream<T> input, boolean isDetailedSample, Spreadsheet<T> format) {
      return input.collect(new OpenDocumentCollector<>(getColumnsFiltered(format, isDetailedSample)));
    }
  },
  CSV("Comma-delimited Data", "csv", "text", "csv") {

    @Override
    public <T> byte[] generate(Stream<T> input, boolean isDetailedSample, Spreadsheet<T> format) {
      StringBuilder builder = new StringBuilder();
      List<Column<T>> columns = getColumnsFiltered(format, isDetailedSample);
      builder.append(columns.stream().map(Column::name).collect(Collectors.joining(","))).append("\r\n");

      input.forEach(item -> {
        for (int i = 0; i < columns.size(); i++) {
          if (i > 0) {
            builder.append(',');
          }
          columns.get(i).appendCsv(builder, item);
        }
        builder.append("\r\n");
      });

      return builder.toString().getBytes(StandardCharsets.UTF_8);
    }

  };
  private final String description;

  private final String extension;

  private final MediaType mediaType;

  private SpreadSheetFormat(String description, String extension, String media1, String media2) {
    this.description = description;
    this.extension = extension;
    mediaType = new MediaType(media1, media2);
  }

  public String description() {
    return description;
  }

  public String extension() {
    return extension;
  }

  public MediaType mediaType() {
    return mediaType;
  }

  public abstract <T> byte[] generate(Stream<T> input, boolean isDetailedSample, Spreadsheet<T> format);

  public <T> List<Column<T>> getColumnsFiltered(Spreadsheet<T> format, boolean isDetailedSample) {
    return format.columns().stream()
        .filter(col -> isDetailedSample || !col.isDetailedSampleOnly())
        .collect(Collectors.toList());
  }

}
