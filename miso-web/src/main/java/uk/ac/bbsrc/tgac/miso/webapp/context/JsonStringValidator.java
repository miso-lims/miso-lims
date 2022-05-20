package uk.ac.bbsrc.tgac.miso.webapp.context;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationException;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

import java.io.IOException;

public class JsonStringValidator extends SimpleModule {

  public JsonStringValidator() {
    addDeserializer(String.class, new StdScalarDeserializer<>(String.class) {
      @Override
      public String deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
          throws IOException, JacksonException {
        // remove tabs and trim white space
        String string = jsonParser.getValueAsString()
            .replaceAll("[\\t]", " ")
            .trim();
        if (LimsUtils.isStringBlankOrNull(string)) {
          // convert empty to null
          return null;
        } else if (!string.matches("^[^<>]*$")) {
          // never allow <> in Strings
          throw new ValidationException(new ValidationError(jsonParser.getCurrentName(),
              "Cannot contain the characters <>"));
        } else {
          return string;
        }
      }
    });
  }

}
