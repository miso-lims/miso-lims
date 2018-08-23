package uk.ac.bbsrc.tgac.miso.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeName;

import uk.ac.bbsrc.tgac.miso.core.data.SampleAliquotSingleCell;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeName(value = SampleAliquotSingleCell.SAMPLE_CLASS_NAME)
public class SampleAliquotSingleCellDto extends SampleAliquotDto {

  private String inputIntoLibrary;

  public String getInputIntoLibrary() {
    return inputIntoLibrary;
  }

  public void setInputIntoLibrary(String inputIntoLibrary) {
    this.inputIntoLibrary = inputIntoLibrary;
  }

}
