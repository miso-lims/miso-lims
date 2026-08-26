package uk.ac.bbsrc.tgac.miso.webapp.context;

import tools.jackson.core.SerializableString;
import tools.jackson.core.io.CharacterEscapes;

public class JsonCharacterEscapes extends CharacterEscapes {

  private final int[] escapes;

  public JsonCharacterEscapes() {
    escapes = CharacterEscapes.standardAsciiEscapesForJSON();
    escapes['>'] = CharacterEscapes.ESCAPE_STANDARD;
    escapes['<'] = CharacterEscapes.ESCAPE_STANDARD;
    escapes['&'] = CharacterEscapes.ESCAPE_STANDARD;
  }

  @Override
  public int[] getEscapeCodesForAscii() {
    return escapes;
  }

  @Override
  public SerializableString getEscapeSequence(int i) {
    return null;
  }
}
