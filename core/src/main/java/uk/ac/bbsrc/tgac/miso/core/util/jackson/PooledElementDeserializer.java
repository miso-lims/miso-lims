package uk.ac.bbsrc.tgac.miso.core.util.jackson;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.bbsrc.tgac.miso.core.data.Dilution;

/**
 * uk.ac.bbsrc.tgac.miso.core.util.jackson
 * <p/>
 * Deserializer class to help with processing pooled elements
 * 
 * @author Rob Davey
 * @date 08/01/13
 * @since 0.1.9
 */
public class PooledElementDeserializer extends JsonDeserializer<Set<Dilution>> {
  protected static final Logger log = LoggerFactory.getLogger(PooledElementDeserializer.class);

  static final TypeReference<Dilution> type = new TypeReference<Dilution>() {
  };

  @Override
  public Set<Dilution> deserialize(JsonParser jp, DeserializationContext arg1) throws IOException, JsonProcessingException {
    Set<Dilution> poolables = new HashSet<>();
    JsonNode node = jp.readValueAsTree();
    ObjectMapper o = new ObjectMapper();
    for (JsonNode element : node) {
      // this should be the poolable, i.e. plate, library or dilution
      log.debug("Element: " + element.toString());
      Dilution d = o.readValue(element, type);
      poolables.add(d);
    }
    return poolables;
  }
}
