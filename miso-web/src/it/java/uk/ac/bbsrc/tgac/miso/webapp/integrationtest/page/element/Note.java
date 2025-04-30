package uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.element;

import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Note {

  private static final Pattern textPattern = Pattern.compile("^(\\d{4}-\\d{2}-\\d{2}): (.*)\\n(.*)$");

  private final LocalDate date;
  private final String text;
  private final String user;

  public Note(String elementText) {
    Matcher m = textPattern.matcher(elementText);
    if (!m.matches()) {
      throw new IllegalArgumentException("Invalid elementText");
    }
    date = LocalDate.parse(m.group(1));
    text = m.group(2);
    user = m.group(3);
  }

  public Note(String text, String user, String dateString) {
    this.text = text;
    this.user = user;
    this.date = LocalDate.parse(dateString);
  }

  public LocalDate getDate() {
    return date;
  }

  public String getText() {
    return text;
  }

  public String getUser() {
    return user;
  }

}
