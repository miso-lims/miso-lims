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
<%@ include file="../header.jsp" %>

<script src="<c:url value='/scripts/jquery/datatables/js/jquery.dataTables.min.js'/>" type="text/javascript"></script>
<link href="<c:url value='/scripts/jquery/datatables/css/jquery.dataTables.css'/>" rel="stylesheet" type="text/css">
<link rel="stylesheet" href="<c:url value='/scripts/jquery/datatables/css/jquery.dataTables_themeroller.css'/>">

<div id="maincontent">
<div id="contentcolumn">
<form:form id="submission-form" data-parsley-validate="" action="/miso/submission" method="POST" modelAttribute="submission" autocomplete="off">
  <sessionConversation:insertSessionConversationId attributeName="submission"/>
  <h1>
    <c:choose>
      <c:when test="${submission.id != 0}">Edit</c:when>
      <c:otherwise>Create</c:otherwise>
    </c:choose> Submission
    <button type="button" onclick="return Submission.validateSubmission();" class="fg-button ui-state-default ui-corner-all">Save</button>
  </h1>
<div class="right fg-toolbar ui-helper-clearfix paging_full_numbers">
  <c:if test="${submission.id != 0}">
    <a href="#" onclick="Submission.download(${submission.id})" class="ui-button ui-state-default">Download</a>
    <span></span>
  </c:if>
</div>

  <div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#note_arrowclick'), 'notediv');">Quick Help
    <div id="note_arrowclick" class="toggleLeft"></div>
  </div>
  <div id="notediv" class="note" style="display:none;">Submission help
  </div>
  <h2>Submission Information</h2>
  <table class="in">
    <tr>
      <td class="h">Submission ID:</td>
      <td>
        <c:choose>
          <c:when test="${submission.id != 0}">${submission.id}</c:when>
          <c:otherwise><i>Unsaved</i></c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td class="h">Title:</td>
      <td><form:input path="title"/><span id="titlecounter" class="counter"></span></td>
    </tr>
    <tr>
      <td class="h">Alias:</td>
      <td><form:input path="alias"/><span id="aliascounter" class="counter"></span></td>
    </tr>
    <tr>
      <td class="h">Description:</td>
      <td><form:input path="description"/><span id="descriptioncounter" class="counter"></span></td>
    </tr>
    <c:if test="${not empty submission.accession}">
      <tr>
        <td class="h">Accession:</td>
        <td><a href="http://www.ebi.ac.uk/ena/data/view/${submission.accession}"
               target="_blank">${submission.accession}</a>
        </td>
      </tr>
    </c:if>
  </table>

</form:form>
    <miso:list-section id="list_experiments" name="Experiments" target="experiment" items="${experiments}" config="{ inSubmission: true }"/>
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
