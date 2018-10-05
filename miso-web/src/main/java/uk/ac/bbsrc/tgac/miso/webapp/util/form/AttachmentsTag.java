package uk.ac.bbsrc.tgac.miso.webapp.util.form;

import java.util.stream.Collectors;

import org.springframework.web.servlet.tags.RequestContextAwareTag;

import com.fasterxml.jackson.databind.ObjectMapper;

import uk.ac.bbsrc.tgac.miso.core.data.Attachable;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;

@SuppressWarnings({ "squid:S1948" }) // non-Serializable fields
public class AttachmentsTag extends RequestContextAwareTag {

  private static final long serialVersionUID = 1L;

  private Object item;

  private Long projectId;

  @Override
  protected int doStartTagInternal() throws Exception {
    Attachable attachable = (Attachable) this.item;
    ObjectMapper mapper = new ObjectMapper();

    String projectConfig = projectId == null ? "" : (", projectId: " + projectId);
    pageContext.getOut().append(String.format(
        "<br/><h1>Attachments</h1><table id='attachments' class='display no-border ui-widget-content'></table><script type='text/javascript'>jQuery(document).ready(function () { ListUtils.createStaticTable('attachments', ListTarget.attachment, {entityType: '%1$s', entityId: %2$s"
            + projectConfig + "}, %3$s);});</script>",
        attachable.getAttachmentsTarget(), attachable.getId(),
        mapper.writeValueAsString(attachable.getAttachments().stream().map(Dtos::asDto).collect(Collectors.toList()))));
    return SKIP_BODY;
  }

  public Object getItem() {
    return item;
  }

  public void setItem(Object item) {
    this.item = item;
  }

  public Long getProjectId() {
    return projectId;
  }

  public void setProjectId(Long projectId) {
    this.projectId = projectId;
  }

}
