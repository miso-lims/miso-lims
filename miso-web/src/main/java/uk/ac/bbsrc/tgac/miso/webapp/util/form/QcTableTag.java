package uk.ac.bbsrc.tgac.miso.webapp.util.form;

import java.util.stream.Collectors;

import org.springframework.web.servlet.tags.RequestContextAwareTag;

import tools.jackson.databind.json.JsonMapper;
import uk.ac.bbsrc.tgac.miso.core.data.qc.QualityControllable;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;

@SuppressWarnings("serial")
public class QcTableTag extends RequestContextAwareTag {
  private transient Object item;

  @Override
  protected int doStartTagInternal() throws Exception {

    QualityControllable<?> qcItem = (QualityControllable<?>) item;
    JsonMapper mapper = TagUtils.getJsonMapper(pageContext);

    pageContext.getOut().append(String.format(
        "<br/><h1>QCs</h1><table id='%1$s' class='display no-border ui-widget-content'></table><script type='text/javascript'>jQuery(document).ready(function () { ListUtils.createStaticTable('%1$s', ListTarget.qc('%2$s'), { entityId : %3$d }, %4$s);});</script>",
        getId(), qcItem.getQcTarget(), qcItem.getId(),
        mapper.writeValueAsString(qcItem.getQCs().stream().map(Dtos::asDto).collect(Collectors.toList()))));
    return SKIP_BODY;
  }

  public Object getItem() {
    return item;
  }

  public void setItem(Object item) {
    this.item = item;
  }

}
