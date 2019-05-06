package uk.ac.bbsrc.tgac.miso.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeName;

import uk.ac.bbsrc.tgac.miso.core.data.SampleTissueProcessing;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeName(value = SampleTissueProcessing.CATEGORY_NAME)
public class SampleTissueProcessingDto extends SampleTissueDto {

}
