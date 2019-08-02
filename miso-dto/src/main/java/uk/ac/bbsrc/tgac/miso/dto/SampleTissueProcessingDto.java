package uk.ac.bbsrc.tgac.miso.dto;

import com.fasterxml.jackson.annotation.JsonTypeName;

import uk.ac.bbsrc.tgac.miso.core.data.SampleTissueProcessing;

@JsonTypeName(value = SampleTissueProcessing.CATEGORY_NAME)
public class SampleTissueProcessingDto extends SampleTissueDto {

}
