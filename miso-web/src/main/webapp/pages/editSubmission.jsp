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
  Time: 15:08:42

--%>
<%@ include file="../header.jsp" %>

<script type="text/javascript" src="<c:url value='/scripts/jquery/js/jquery.jstree.js'/>"></script>

<div id="maincontent">
<div id="contentcolumn">
<form:form action="/miso/submission" method="POST" commandName="submission" autocomplete="off"
           onsubmit="return validate_submission(this);">
  <sessionConversation:insertSessionConversationId attributeName="submission"/>
  <h1>
    <c:choose>
      <c:when test="${submission.id != 0}">Edit</c:when>
      <c:otherwise>Create</c:otherwise>
    </c:choose> Submission
    <c:if test="${submission.id != 0}">
      <input type="button" value="Save" class="fg-button ui-state-default ui-corner-all"
             onclick="Submission.saveSubmission(${submission.id},jQuery(form))"/>
    </c:if>
    <c:if test="${submission.id == 0}">
      <input type="button" value="Save" class="fg-button ui-state-default ui-corner-all"
             onclick="Submission.saveSubmission(-1,jQuery(form))"/>
    </c:if>
  </h1>

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
      <td class="h">Name:</td>
      <td>
        <c:choose>
          <c:when test="${submission.id != 0}">${submission.name}</c:when>
          <c:otherwise><i>Unsaved</i></c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td class="h">Title:</td>
      <td><form:input path="title"/><span id="titlecounter" class="counter"></span></td>
        <%--<td><a href="void(0);" onclick="popup('help/submissionTitle.html');">Help</a></td>--%>
    </tr>
    <tr>
      <td class="h">Alias:</td>
      <td><form:input path="alias"/><span id="aliascounter" class="counter"></span></td>
        <%--<td><a href="void(0);" onclick="popup('help/submissionAlias.html');">Help</a></td>--%>
    </tr>
    <tr>
      <td class="h">Description:</td>
      <td><form:input path="description"/><span id="descriptioncounter" class="counter"></span></td>
        <%--<td><a href="void(0);" onclick="popup('help/submissionDescription.html');">Help</a></td>--%>
    </tr>
    <c:if test="${not empty submission.accession}">
      <tr>
        <td class="h">Accession:</td>
        <td><a href="http://www.ebi.ac.uk/ena/data/view/${submission.accession}"
               target="_blank">${submission.accession}</a>
        </td>
          <%--<td><a href="void(0);" onclick="popup('help/submissionAccession.html');">Help</a></td>--%>
      </tr>
    </c:if>
    <tr>
      <td>Action:</td>
      <td>
        <form:radiobuttons id="submissionActionType" path="submissionActionType"/>
      </td>
    </tr>
  </table>

  <c:if test="${submission.id != 0}">
    <span style="float:right">
      <a href="javascript:void(0);" onclick="Submission.ui.previewSubmissionMetadata(${submission.id});"> Preview Submission Metadata</a>
    </span><br/>
    <span style="float:right">
      <a href="javascript:void(0);" onclick="Submission.ui.downloadSubmissionMetadata(${submission.id});">Download Submission Metadata</a>
    </span><br/>
    <span style="float:right">
      <a href="javascript:void(0);" onclick="Submission.validateSubmissionMetadata(${submission.id});">Validate Submission Metadata</a>
    </span><br/>
    <span style="float:right">
      <a href="javascript:void(0);" onclick="Submission.submitSubmissionMetadata(${submission.id});">Submit Submission Metadata</a>
    </span><br/>
    <%-- <span style="float:right"><a href="javascript:void(0);" onclick="Submission.submitSequenceData(${submission.id});">Submit Sequence Data</a></span> --%>
    <div id="submissionreport"></div>

    <c:if test="${not empty prettyMetadata}">
      <h3>Submission Metadata</h3>
      ${prettyMetadata}
      <br/>
    </c:if>
    <%--
    <c:if test="${not empty studyxmls}">
        <c:forEach items="${studyxmls}" var="xml">
            <h3>${xml.key}</h3>
            <pre class="note">
                ${xml.value}
            </pre>
        </c:forEach>
    </c:if>
    <c:if test="${not empty samplexmls}">
        <c:forEach items="${samplexmls}" var="xml">
            <h3>${xml.key}</h3>
            <pre class="note">
                ${xml.value}
            </pre>
        </c:forEach>
    </c:if>
    <c:if test="${not empty experimentxmls}">
        <c:forEach items="${experimentxmls}" var="xml">
            <h3>${xml.key}</h3>
            <pre class="note">
                ${xml.value}
            </pre>
        </c:forEach>
    </c:if>
    <c:if test="${not empty runxmls}">
        <c:forEach items="${runxmls}" var="xml">
            <h3>${xml.key}</h3>
            <pre class="note">
                ${xml.value}
            </pre>
        </c:forEach>
    </c:if>
    --%>
  </c:if>

  <h2>Submittable Elements</h2>

  <div id="submissionTree">
    <ul>
        <%--
         <c:choose>
                     <c:when test="${not empty submission.submissionId}">${submission.submissionId}</c:when>
                     <c:otherwise><i>Unsaved</i></c:otherwise>
                 </c:choose>
                 --%>
      <c:forEach items="${projects}" var="project">
        <li id="project${project.id}" class="jstree-closed">
          <c:if test="${submission.id != 0}">
            <p style="cursor: pointer" id="projectTitle${project.id}"
               onclick="Submission.ui.populateSubmissionProject(${project.id},${submission.id});">
              <strong>${project.name}</strong> : ${project.description}</p>
          </c:if>
          <c:if test="${submission.id == 0}">
            <p style="cursor: pointer" id="projectTitle${project.id}"
               onclick="Submission.ui.populateSubmissionProject(${project.id});"><strong>${project.name}</strong>
              : ${project.description}</p>
          </c:if>
          <ul id="projectSubmission${project.id}"></ul>
        </li>
      </c:forEach>
    </ul>
  </div>
  <input type="hidden" value="on" name="_submissionElements">
</form:form>
<br/>
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

    <c:if test="${submission.id != 0}">
    Submission.ui.openSubmissionProjectNodes(${submission.id});
    </c:if>
  });
</script>

<%@ include file="adminsub.jsp" %>
<%@ include file="../footer.jsp" %>