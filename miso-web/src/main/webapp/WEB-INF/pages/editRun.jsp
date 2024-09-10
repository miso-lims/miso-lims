<%@ include file="../header.jsp" %>

<div id="maincontent">

<div id="contentcolumn">
<h1>
  <c:choose>
    <c:when test="${run.id != 0}">Edit</c:when>
    <c:otherwise>Create</c:otherwise>
  </c:choose> Run
  <button type="button" id="save" class="fg-button ui-state-default ui-corner-all">Save</button>
</h1>
<div class="right fg-toolbar ui-helper-clearfix paging_full_numbers">
  <c:if test="${run.id != 0}">
    <c:forEach items="<%=uk.ac.bbsrc.tgac.miso.core.util.SampleSheet.values()%>" var="sheet">
      <c:if test="${sheet.allowedFor(run)}">
        <a href="<c:url value='/rest/runs/${run.id}/samplesheet/${sheet.name()}'/>" class="ui-button ui-state-default">Download Sample Sheet (${sheet.alias()})</a>
      </c:if>
    </c:forEach>
    <span></span>
  </c:if>
</div>
<div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#note_arrowclick'), 'notediv');">Quick Help
  <div id="note_arrowclick" class="toggleLeft"></div>
</div>
<div id="notediv" class="note" style="display:none;">A Run contains the sequencing results from sequencing Experiments.
  Each run partition (lane/chamber) holds a Pool which is linked to a number of Experiments to facilitate multiplexing
  if required.
</div>

<form:form id="runForm" data-parsley-validate="" autocomplete="off" acceptCharset="utf-8"></form:form>
<script type="text/javascript">
  jQuery(document).ready(function () {
    FormUtils.createForm('runForm', 'save', ${runDto}, 'run', ${formConfig});
  });
  Utils.ui.updateHelpLink(FormTarget.run.getUserManualUrl());
</script>
      
<c:if test="${run.id != 0 && !runReportLinks.isEmpty()}">
  <table class="in">
    <tr>
      <td class="h">External Links:</td>
      <td>
      <c:forEach items="${runReportLinks}" var="runReportLink">
        <span><a href="<c:out value="${runReportLink.value}"/>">${runReportLink.key}</a></span><br/>
      </c:forEach>
      </td>
    </tr>
  </table>
</c:if>

<c:if test="${run.id != 0}">
  <div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#notes_arrowclick'), 'notes');">Notes
    <div id="notes_arrowclick" class="toggleLeftDown"></div>
  </div>
  <div id="notes">
    <h1>Notes</h1>
    <ul class="sddm">
      <li>
        <a onmouseover="mopen('notesmenu')" onmouseout="mclosetime()">Options
          <span style="float:right" class="ui-icon ui-icon-triangle-1-s"></span>
        </a>

        <div id="notesmenu"
             onmouseover="mcancelclosetime()"
             onmouseout="mclosetime()">
          <a onclick="Utils.notes.showNoteDialog('run', ${run.id});" href="javascript:void(0);" class="add">Add Note</a>
        </div>
      </li>
    </ul>
    <c:if test="${fn:length(run.notes) > 0}">
      <div id="notelist" class="note" style="clear:both">
        <c:forEach items="${run.notes}" var="note" varStatus="n">
          <div class="exppreview" id="run-notes-${n.count}">
            <b>${note.creationDate}</b>: ${note.text}
          <span class="float-right"
                style="font-weight:bold; color:#C0C0C0;">${note.owner.loginName}
            <c:if test="${miso:isCurrentUser(note.owner.loginName) or miso:isAdmin()}">
              <span style="color:#000000">
                <a href='#' onclick="Utils.notes.deleteNote('run', '${run.id}', '${note.id}'); return false;">
                  <span class="ui-icon ui-icon-trash note-delete-icon"></span>
                </a>
              </span>
            </c:if>
          </span>
          </div>
        </c:forEach>
      </div>
    </c:if>
  </div>
  <br/>
  
  <miso:attachments item="${run}"/>
  
  <c:if test="${issueTrackerEnabled}">
    <div id="issues">
      <c:choose>
        <c:when test="${issueLookupError}">
          <p class="big big-error">Error retrieving issues</p>
        </c:when>
        <c:otherwise>
          <miso:list-section id="list_issue" name="Related Issues" target="issue" alwaysShow="true" items="${issues}" config="{}"/>
        </c:otherwise>
      </c:choose>
    </div>
  </c:if>
  
  <div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#metrix_arrowclick'), 'metrix');">Metrics
    <div id="metrix_arrowclick" class="toggleLeft"></div>
  </div>
  <div id="metrix">
    <h1>Metrics</h1>
    <div id="metricsdiv"></div>
  </div>
  <script type="text/javascript">
    jQuery(document).ready(function () {
      RunGraph.renderMetrics(${metrics}, ${partitionNames});
    });
  </script>
  <div id="containers">
    <miso:list-section id="list_container" name="${run.platformType.containerName}" target="run_position" items="${runPositions}" alwaysShow="true" config="${partitionConfig}"/>
  </div>
  <div id="partitions">
    <miso:list-section id="list_partition" name="${run.platformType.pluralPartitionName}" target="partition" items="${runPartitions}" config="${partitionConfig}"/>
  </div>
  <div id="aliquots">
    <miso:list-section id="list_aliquot" name="Run-Libraries" target="runaliquot" items="${runAliquots}" config="{runId: ${run.id}}"/>
  </div>
  <div id="experiments">
    <miso:list-section id="list_experiment" name="Experiments" target="experiment" alwaysShow="true" items="${experiments}" config="${experimentConfiguration}"/>
  </div>
</c:if>
<miso:changelog item="${run}"/>
</div>
</div>

<%@ include file="adminsub.jsp" %>

<%@ include file="../footer.jsp" %>
