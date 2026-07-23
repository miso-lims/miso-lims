package uk.ac.bbsrc.tgac.miso.webapp.util.form;

import java.util.stream.Collectors;

import org.springframework.web.servlet.tags.RequestContextAwareTag;

import tools.jackson.databind.json.JsonMapper;
import uk.ac.bbsrc.tgac.miso.core.data.ChangeLoggable;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;

@SuppressWarnings("serial")
public class ChangeLogTag extends RequestContextAwareTag {
  private Object item;

  @Override
  protected int doStartTagInternal() throws Exception {
    ChangeLoggable item = (ChangeLoggable) this.item;
    if (item.getChangeLog().isEmpty()) {
      return SKIP_BODY;
    }
    JsonMapper mapper = TagUtils.getJsonMapper(pageContext);

    pageContext.getOut().append(String.format(
        "<br/><h1>Changes</h1><table id='changelog' class='display no-border ui-widget-content'></table><script type='text/javascript'>jQuery(document).ready(function () { ListUtils.createStaticTable('changelog', ListTarget.changelog, {}, %1$s);});</script>",
        mapper.writeValueAsString(item.getChangeLog().stream().map(Dtos::asDto).collect(Collectors.toList()))));
    return SKIP_BODY;
  }

  public Object getItem() {
    return item;
  }

  public void setItem(Object item) {
    this.item = item;
  }

}
