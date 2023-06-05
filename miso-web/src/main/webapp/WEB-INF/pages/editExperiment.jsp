<%--
  Created by IntelliJ IDEA.
  User: davey
  Date: 15-Feb-2010
  Time: 15:08:52

--%>
<%@ include file="../header.jsp" %>

<div id="maincontent">
<div id="contentcolumn">
<h1>
  <c:choose><c:when test="${experiment.id != 0}">Edit</c:when><c:otherwise>Create</c:otherwise></c:choose> Experiment
  <button id="save" type="button" class="fg-button ui-state-default ui-corner-all">Save</button>
</h1>
<div class="breadcrumbs">
  <ul>
    <li>
      <a href="/">Home</a>
    </li>
    <li>
      <div class="breadcrumbsbubbleInfo">
        <div class="trigger">
          <a href='<c:url value="/miso/project/${experiment.study.project.id}"/>'>${experiment.study.project.title}</a>
        </div>
        <div class="breadcrumbspopup">
            ${experiment.study.project.name}
        </div>
      </div>
    </li>
    <li>
      <div class="breadcrumbsbubbleInfo">
        <div class="trigger">
          <a href='<c:url value="/miso/study/${experiment.study.id}"/>'>${experiment.study.alias}</a>
        </div>
        <div class="breadcrumbspopup">
            ${experiment.study.name}
        </div>
      </div>
    </li>
  </ul>
</div>
<div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#note_arrowclick'), 'notediv');">Quick Help
  <div id="note_arrowclick" class="toggleLeft"></div>
</div>
<div id="notediv" class="note" style="display:none;">An experiment contains design information about the
  sequencing experiment. Experiments are associated with Runs which contain the actual sequencing results.
  A Pool is attached to an Experiment which is then assigned to an instrument partition (lane/chamber).
</div>

<form:form id="experimentForm" data-parsley-validate="" autocomplete="off" acceptCharset="utf-8"></form:form>
<script type="text/javascript">
  jQuery(document).ready(function () {
    FormUtils.createForm('experimentForm', 'save', ${experimentDto}, 'experiment', {});
    Utils.ui.updateHelpLink(FormTarget.experiment.getUserManualUrl());
  });
</script>

<miso:list-section id="list_part_lane" alwaysShow="true" name="${experiment.instrumentModel.platformType.pluralPartitionName}" target="experiment_run_partition" items="${runPartitions}" config="{}"/>
<miso:list-section id="list_consumable" alwaysShow="true" name="Consumables" target="kit_consumable" items="${consumables}" config="${consumableConfig}"/>
<miso:changelog item="${experiment}"/>
</div>
</div>

<%@ include file="adminsub.jsp" %>

<%@ include file="../footer.jsp" %>
