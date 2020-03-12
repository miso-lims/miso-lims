package uk.ac.bbsrc.tgac.miso.core.service.printing;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import uk.ac.bbsrc.tgac.miso.core.data.Barcodable;

@JsonTypeInfo(property = "element", use = JsonTypeInfo.Id.NAME)
@JsonSubTypes(value = { @Type(value = LabelElement1DBarcode.class, name = "1dbarcode"),
    @Type(value = LabelElement2DBarcode.class, name = "2dbarcode"),
    @Type(value = LabelElementTextBlock.class, name = "textblock"),
    @Type(value = LabelElementText.class, name = "text") })
public abstract class LabelElement {
  public abstract void draw(LabelCanvas canvas, Barcodable barcodable);
}
