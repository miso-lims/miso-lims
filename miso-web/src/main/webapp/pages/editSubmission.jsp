<%--
  ~ Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
  ~ MISO project contacts: Robert Davey, Mario Caccamo @ TGAC
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
<script type="text/javascript" src="<c:url value='/scripts/submission_ajax.js?ts=${timestamp.time}'/>"></script>

<div id="maincontent">
    <div id="contentcolumn">
        <form:form method="POST" commandName="submission" autocomplete="off" onsubmit="return validate_submission(this);">
          <sessionConversation:insertSessionConversationId attributeName="submission"/>
            <h1>
                <c:choose>
                    <c:when test="${not empty submission.submissionId}">Edit</c:when>
                    <c:otherwise>Create</c:otherwise>
                </c:choose> Submission
                <c:if test="${not empty submission.submissionId}">
                <input type="button" value="Save" class="fg-button ui-state-default ui-corner-all" onclick="saveSubmission(${submission.submissionId},jQuery(form).serializeArray())"/>
                </c:if>
                <c:if test="${empty submission.submissionId}">
                <input type="button" value="Save" class="fg-button ui-state-default ui-corner-all" onclick="saveSubmission(-1,jQuery(form).serializeArray())"/>
                </c:if>
            </h1>
            <div class="sectionDivider" onclick="toggleLeftInfo(jQuery('#note_arrowclick'), 'notediv');">Quick Help
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
                            <c:when test="${not empty submission.submissionId}">${submission.submissionId}</c:when>
                            <c:otherwise><i>Unsaved</i></c:otherwise>
                        </c:choose>
                    </td>
                </tr>
                <tr>
                    <td class="h">Name:</td>
                    <td>
                        <c:choose>
                            <c:when test="${not empty submission.submissionId}">${submission.name}</c:when>
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

            <c:if test="${not empty submission.submissionId}">
                <span style="float:right"><a href="javascript:void(0);" onclick="previewSubmissionMetadata(${submission.submissionId});">Preview Raw Submission Metadata</a></span><br/>
                <span style="float:right"><a href="javascript:void(0);" onclick="validateSubmissionMetadata(${submission.submissionId});">Validate Submission Metadata</a></span><br/>
                <span style="float:right"><a href="javascript:void(0);" onclick="submitSubmissionMetadata(${submission.submissionId});">Submit Submission Metadata</a></span><br/>
                <span style="float:right"><a href="javascript:void(0);" onclick="submitSequenceData(${submission.submissionId});">Submit Sequence Data</a></span>
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
                    <li id="project${project.projectId}" class="jstree-closed">
                        <c:if test="${not empty submission.submissionId}">
                            <p style="cursor: pointer" id="projectTitle${project.projectId}" onclick="populateSubmissionProject(${project.projectId},${submission.submissionId});"><strong>${project.name}</strong> : ${project.description}</p>
                        </c:if>
                        <c:if test="${empty submission.submissionId}">
                            <p style="cursor: pointer" id="projectTitle${project.projectId}" onclick="populateSubmissionProject(${project.projectId});"><strong>${project.name}</strong> : ${project.description}</p>
                        </c:if>
                        <ul id="projectSubmission${project.projectId}">

                        <c:forEach items="${project.studies}" var="study">
                            <li><form:checkbox id="study${study.studyId}_${project.projectId}" path="submissionElements" itemLabel="${study.name}"
                                               itemValue="${study.name}" value="${study.name}"/>
                                <a href="<c:url value='/miso/study/${study.studyId}'/>"><b>${study.name}</b> : ${study.description}</a>
                              <%--
                                <ul>
                                    <c:forEach items="${study.experiments}" var="experiment">
                                        <li><form:checkbox id="experiment${experiment.experimentId}_${study.studyId}" path="submissionElements" itemLabel="${experiment.name}"
                                                       itemValue="${experiment.name}" value="${experiment.name}"/>
                                            <a href="<c:url value='/miso/experiment/${experiment.experimentId}'/>"><b>${experiment.name}</b> : ${experiment.alias}</a>

                                            <ul>
                                                <c:choose>
                                                    <c:when test="${experiment.platform.platformType.key ne 'Illumina'}">
                                                        <c:forEach items="${experiment.pool.dilutions}" var="dilution">
                                                            <c:if test="${not empty dilution.emPCR.libraryDilution.library.sample}">
                                                                <c:set var="sample" value="${dilution.emPCR.libraryDilution.library.sample}"/>
                                                                <li><form:checkbox id="sample${sample.sampleId}_${experiment.experimentId}" path="submissionElements" itemLabel="${sample.name}"
                                                                   itemValue="${sample.name}" value="${sample.name}"/>
                                                                    <a href="<c:url value='/miso/sample/${sample.sampleId}'/>"><b>${sample.name}</b> : ${sample.alias}</a>
                                                                </li>
                                                            </c:if>
                                                        </c:forEach>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <c:forEach items="${experiment.pool.dilutions}" var="dilution">
                                                            <c:if test="${not empty dilution.library.sample}">
                                                                <c:set var="sample" value="${dilution.library.sample}"/>
                                                                <li><form:checkbox id="sample${sample.sampleId}_${experiment.experimentId}" path="submissionElements" itemLabel="${sample.name}"
                                                                   itemValue="${sample.name}" value="${sample.name}"/>
                                                                    <a href="<c:url value='/miso/sample/${sample.sampleId}'/>"><b>${sample.name}</b> : ${sample.alias}</a>
                                                                </li>
                                                            </c:if>
                                                        </c:forEach>
                                                    </c:otherwise>
                                                </c:choose>
                                            </ul>
                                        </li>
                                    </c:forEach>
                                </ul>
                                --%>
                            </li>
                        </c:forEach>

                        </ul>

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
     //added 21.12.11 - attempt to make runlist toggle in and out of view when project clicked
    /* jQuery(document).ready(function() {
     jQuery('#submissionTree').find(":contains('runList')").hide().end().find(":contains('project')").click(function() {
     jQuery(this).next().slideToggle();
   });
 });
     //added 22.12.11 - attempt to make runlist toggle in and out of view when project clicked
     $(document).ready(function(){
        $(":contains('project')").toggle(function(){
        $(":contains('runList')").hide('slow');
        },function(){
        $(":contains('runList')").show('fast');
   });
 });

    $('.jstree-open').click(function() {
        $('#runList' + projectId).hide();
    });
    */
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
    /*
    jQuery("#submissionTree").jstree({
      "themes" : { "theme" : "default", "dots" : true, "icons" : false },
      "plugins" : [ "themes", "html_data" ]
    });
    */
    <c:if test="${not empty submission.submissionId}">
      openSubmissionProjectNodes(${submission.submissionId});
    </c:if>
	});
</script>

<%@ include file="adminsub.jsp" %>
<%@ include file="../footer.jsp" %>