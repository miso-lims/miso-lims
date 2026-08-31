package uk.ac.bbsrc.tgac.miso.webapp.util.form;

import java.util.Collection;

import org.springframework.web.servlet.tags.RequestContextAwareTag;

import tools.jackson.databind.json.JsonMapper;

@SuppressWarnings({"serial", "squid:S1948"})
public class PaneTag extends RequestContextAwareTag {
  private boolean alwaysShow;

  private Object items;

  private String target;

  @Override
  protected int doStartTagInternal() throws Exception {
    if (items == null) {
      return SKIP_BODY;
    }
    @SuppressWarnings("unchecked")
    Collection<Object> items = (Collection<Object>) this.items;
    if (items.size() == 0 && !alwaysShow) {
      return SKIP_BODY;
    }
    JsonMapper mapper = TagUtils.getJsonMapper(pageContext);

    pageContext.getOut().append(String.format(
        "<div id='%1$s'></div><script type='text/javascript'>jQuery(document).ready(function () { Pane.createPane('%1$s', PaneTarget.%2$s, %3$s);});</script>",
        getId(), target, mapper.writeValueAsString(items)));
    return SKIP_BODY;
  }

  public Object getItems() {
    return items;
  }

  public String getTarget() {
    return target;
  }

  public boolean isAlwaysShow() {
    return alwaysShow;
  }

  public void setAlwaysShow(boolean alwaysShow) {
    this.alwaysShow = alwaysShow;
  }

  public void setItems(Object items) {
    this.items = items;
  }

  public void setTarget(String target) {
    this.target = target;
  }
}
