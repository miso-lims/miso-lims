<%--
  ~ Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
  ~ MISO project contacts: Robert Davey @ TGAC
  ~ **********************************************************************
  ~
  ~ This file is part of MISO.
  ~
  ~ MISO is free software: you can redistribute it and/or modify
  ~ it under the terms of the GNU General Public License as published by
  ~ the Free Software Foundation, either version 3 of the License, or
  ~ (at your option) any later version.
  ~
  ~ MISO is distributed in the hope that it will be useful,
  ~ but WITHOUT ANY WARRANTY; without even the implied warranty of
  ~ MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  ~ GNU General Public License for more details.
  ~
  ~ You should have received a copy of the GNU General Public License
  ~ along with MISO.  If not, see <http://www.gnu.org/licenses/>.
  ~
  ~ **********************************************************************
  --%>

<%--
  Created by IntelliJ IDEA.
  User: davey
  Date: 15-Feb-2010
  Time: 15:08:52

--%>
<%@ include file="../header.jsp" %>

<script src="<c:url value='/scripts/jquery/datatables/js/jquery.dataTables.min.js'/>" type="text/javascript"></script>
<script type="text/javascript" src="<c:url value='/scripts/jquery/js/jquery.breadcrumbs.popup.js'/>"></script>
<link rel="stylesheet" href="<c:url value='/scripts/jquery/datatables/css/jquery.dataTables.css'/>" type="text/css">
<link rel="stylesheet" href="<c:url value='/scripts/jquery/datatables/css/jquery.dataTables_themeroller.css'/>">

<div id="maincontent">
<div id="contentcolumn">
<form:form id="experiment-form" data-parsley-validate="" action="/miso/experiment" method="POST" commandName="experiment"
  autocomplete="off">
<sessionConversation:insertSessionConversationId attributeName="experiment"/>
<h1>
  <c:choose>
    <c:when test="${experiment.id != 0}">Edit</c:when>
    <c:otherwise>Create</c:otherwise>
  </c:choose> Experiment
  <button onclick="return Experiment.validateExperiment();" class="fg-button ui-state-default ui-corner-all">Save</button>
</h1>
<div class="breadcrumbs">
  <ul>
    <li>
      <a href="/">Home</a>
    </li>
    <li>
      <div class="breadcrumbsbubbleInfo">
        <div class="trigger">
          <a href='<c:url value="/miso/project/${experiment.study.project.id}"/>'>${experiment.study.project.alias}</a>
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

<div class="bs-callout bs-callout-warning hidden">
  <h2>Oh snap!</h2>
  <p>This form seems to be invalid!</p>
</div>

<h2>Experiment Information</h2>
<table class="in">
<tr>
  <td class="h">Experiment ID:</td>
  <td>
    <c:choose>
      <c:when test="${experiment.id != 0}">${experiment.id}</c:when>
      <c:otherwise><i>Unsaved</i></c:otherwise>
    </c:choose>
  </td>
</tr>
<tr>
  <td class="h">Name:</td>
  <td>
    <c:choose>
      <c:when test="${experiment.id != 0}">${experiment.name}</c:when>
      <c:otherwise><i>Unsaved</i></c:otherwise>
    </c:choose>
  </td>
</tr>
<tr>
  <td class="h">Title:*</td>
  <td><form:input path="title"/><span id="titlecounter" class="counter"></span></td>
</tr>
<tr>
  <td class="h">Alias:*</td>
  <td><form:input path="alias" class="validateable"/><span id="aliascounter" class="counter"></span></td>
</tr>
<tr>
  <td class="h">Description:</td>
  <td><form:input path="description" class="validateable"/><span id="descriptioncounter" class="counter"></span></td>
</tr>
<c:if test="${not empty experiment.accession}">
  <tr>
    <td class="h">Accession:</td>
    <td><a href="http://www.ebi.ac.uk/ena/data/view/${experiment.accession}"
           target="_blank">${experiment.accession}</a>
    </td>
  </tr>
</c:if>
<tr>
  <td class="h">Study:</td>
  <td><a href="/miso/study/${experiment.study.id}">${experiment.study.name} (${experiment.study.alias})</a></td>
</tr>
<tr>
   <td>Platform:</td>
   <td>${experiment.platform.platformType.key} - ${experiment.platform.instrumentModel}</td>
</tr>
<tr>
   <td>Library:</td>
   <td><a href="/miso/library/${experiment.library.id}">${experiment.library.name} (${experiment.library.alias})</td>
</tr>
<c:choose>
  <c:when
      test="${!empty experiment.study and experiment.securityProfile.profileId eq experiment.study.securityProfile.profileId}">
    <tr>
      <td>Permissions</td>
      <td><i>Inherited from study </i><a
          href='<c:url value="/miso/study/${experiment.study.id}"/>'>${experiment.study.name}</a>
        <input type="hidden" value="${experiment.study.securityProfile.profileId}"
               name="securityProfile" id="securityProfile"/>
      </td>
    </tr>
    </table>
  </c:when>
  <c:otherwise>
    </table>
    <%@ include file="permissions.jsp" %>
  </c:otherwise>
</c:choose>

<script type="text/javascript">
  jQuery(document).ready(function() {
    // Attaches a Parsley form validator. 
    Validate.attachParsley('#experiment-form');
  });
</script>

</form:form>
<miso:list-section id="list_part_lane" alwaysShow="true" name="${experiment.platform.platformType.pluralPartitionName}" target="experiment_run_partition" items="${runPartitions}" config="{}"/>
<miso:list-section id="list_consumable" alwaysShow="true" name="Consumables" target="kit_consumable" items="${consumables}" config="${consumableConfig}"/>
<miso:changelog item="${experiment}"/>
</div>
</div>

<script type="text/javascript">
  jQuery(document).ready(function () {
    jQuery('#title').simplyCountable({
      counter: '#titlecounter',
      countType: 'characters',
      maxCount: ${maxLengths['title']},
      countDirection: 'down'
    });

    jQuery('#alias').simplyCountable({
      counter: '#aliascounter',
      countType: 'characters',
      maxCount: ${maxLengths['alias']},
      countDirection: 'down'
    });

    jQuery('#description').simplyCountable({
      counter: '#descriptioncounter',
      countType: 'characters',
      maxCount: ${maxLengths['description']},
      countDirection: 'down'
    });
  });
</script>

<%@ include file="adminsub.jsp" %>

<%@ include file="../footer.jsp" %>
