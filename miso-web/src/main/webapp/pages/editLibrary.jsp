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
  Time: 15:09:06

--%>
<%@ include file="../header.jsp" %>
<script src="<c:url value='/scripts/jquery/js/jquery.breadcrumbs.popup.js'/>" type="text/javascript"></script>
<script src="<c:url value='/scripts/jquery/datatables/js/jquery.dataTables.min.js'/>" type="text/javascript"></script>
<link href="<c:url value='/scripts/jquery/datatables/css/jquery.dataTables.css'/>" rel="stylesheet" type="text/css">
<link rel="stylesheet" href="<c:url value='/scripts/jquery/datatables/css/jquery.dataTables_themeroller.css'/>">

<div id="maincontent">
<div id="contentcolumn">

<form:form id="library-form" data-parsley-validate="" action="/miso/library" method="POST" commandName="library" autocomplete="off" acceptCharset="utf-8">
<sessionConversation:insertSessionConversationId attributeName="library"/>
<h1>
  <c:choose>
    <c:when test="${library.id != 0}">Edit</c:when>
    <c:otherwise>Create</c:otherwise>
  </c:choose> Library
  <button id="save" type="button" class="fg-button ui-state-default ui-corner-all"
      onclick="return Library.validateLibrary(${detailedSample && (library.hasNonStandardAlias() || library.sample.hasNonStandardAlias())});">
    Save
  </button>
</h1>
<div class="right fg-toolbar ui-helper-clearfix paging_full_numbers">
  <c:if test="${library.id != 0 && not empty library.identificationBarcode}"><span class="ui-button ui-state-default" onclick="Utils.printDialog('library', [${library.id}]);">Print Barcode</span></c:if>
</div>
<div class="breadcrumbs">
  <ul>
    <li>
      <a href='<c:url value="/"/>'>Home</a>
    </li>
    <li>
      <div class="breadcrumbsbubbleInfo">
        <div class="trigger">
          <a href='<c:url value="/miso/project/${library.sample.project.id}"/>'>${library.sample.project.alias}</a>
        </div>
        <div class="breadcrumbspopup">
            ${library.sample.project.name}
        </div>
      </div>
    </li>
    <li>
      <div class="breadcrumbsbubbleInfo">
        <div class="trigger">
          <a href='<c:url value="/miso/sample/${library.sample.id}"/>'>${library.sample.alias}</a>
        </div>
        <div class="breadcrumbspopup">
            ${library.sample.name}
        </div>
      </div>
    </li>
  </ul>
  <c:if test="${not empty nextLibrary}">
    <span style="float:right; padding-top: 5px; padding-left: 6px">
      <a class='arrowright' href='<c:url value="/miso/library/${nextLibrary.id}"/>'>Next Library <b>${nextLibrary.alias}</b></a>
    </span>
  </c:if>
  <c:if test="${not empty previousLibrary}">
    <span style="float:right; padding-top: 5px">
      <a class='arrowleft' href='<c:url value="/miso/library/${previousLibrary.id}"/>'>Previous Library <b>${previousLibrary.alias}</b></a>
    </span>
  </c:if>
</div>
<div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#note_arrowclick'), 'notediv');">Quick Help
  <div id="note_arrowclick" class="toggleLeft"></div>
</div>
<div id="notediv" class="note" style="display:none;">A Library is the first step in constructing sequenceable
  material from an initial Sample. A Library is then diluted down to a Dilution, and put in a Pool.
</div>

<div class="bs-callout bs-callout-warning hidden">
  <h2>Oh snap!</h2>
  <p>This form seems to be invalid</p>
</div>

<h2>Library Information</h2>

<table class="in">
<tr>
  <td class="h">Library ID:</td>
  <td id="libraryId">
    <c:choose>
      <c:when test="${library.id != 0}">${library.id}</c:when>
      <c:otherwise><i>Unsaved</i></c:otherwise>
    </c:choose>
  </td>
</tr>
<tr>
  <td class="h">Name:</td>
  <td id="name">
    <c:choose>
      <c:when test="${library.id != 0}">${library.name}</c:when>
      <c:otherwise><i>Unsaved</i></c:otherwise>
    </c:choose>
  </td>
</tr>
<tr>
  <td>Parent Sample:</td>
  <td>
    <c:choose>
      <c:when test='${empty library.sample}'>
        <i>Unassigned</i>
      </c:when>
      <c:otherwise>
        <a href='<c:url value="/miso/sample/${library.sample.id}"/>'>${library.sample.alias} (${library.sample.name})</a>
      </c:otherwise>
    </c:choose>
  </td>
</tr>
<tr>
  <td class="h">Alias:
    <c:choose>
      <c:when test="${!aliasGenerationEnabled || library.id != 0}">
        *
      </c:when>
      <c:when test="${detailedSample && library.sample.hasNonStandardAlias()}">
        * (cannot auto-generate since parent has non-standard alias)
      </c:when>
      <c:otherwise>
        (blank to auto-generate)
      </c:otherwise>
    </c:choose>
  </td>
  <td>
    <form:input id="alias" path="alias" class="validateable"/>
    <span id="aliasCounter" class="counter"></span>
    <c:if test="${detailedSample && library.hasNonStandardAlias()}">
      <ul class="parsley-errors-list filled" id="nonStandardAlias">
        <li class="parsley-custom-error-message">
        Double-check this alias -- it will be saved even if it is duplicated or does not follow the naming standard!
        </li>
      </ul>
    </c:if>
  </td>
</tr>
<tr>
  <td>Description:</td>
  <td>
    <%-- FIX MISO-107
    <c:choose>
      <c:when test='${not empty library.sample}'>
        <form:input path="description" value="${library.sample.description}" class="validateable"/>
      </c:when>
      <c:otherwise>
        <form:input path="description" class="validateable"/>
      </c:otherwise>
    </c:choose>
    --%>
    <form:input id="description" path="description" class="validateable"/>
    <span id="descriptionCounter" class="counter"></span></td>
</tr>
<c:if test="${not autoGenerateIdBarcodes}">
  <tr>
    <td class="h">Matrix Barcode:</td>
    <td><form:input id="identificationBarcode" path="identificationBarcode" name="identificationBarcode"/></td>
  </tr>
</c:if>
<tr>
  <td>Date of receipt:</td>
  <td>
    <form:input path="receivedDate" id="receiveddatepicker" placeholder="YYYY-MM-DD"/>
    <script type="text/javascript">
      Utils.ui.addDatePicker("receiveddatepicker");
    </script>
  </td>
</tr>
<tr>
  <td class="h">Creation date:</td>
  <td id="creationDate"><fmt:formatDate pattern="yyyy-MM-dd" type="date" value="${library.creationDate}"/></td>
</tr>
<c:if test="${not empty library.accession}">
  <tr>
    <td class="h">Accession:</td>
    <td><a href="http://www.ebi.ac.uk/ena/data/view/${library.accession}">${library.accession}</a>
    </td>
  </tr>
</c:if>
<c:choose>
  <c:when test="${!empty library.sample && detailedSample}">
    <input type="hidden" value="true" name="paired" id="paired"/>
  </c:when>
  <c:otherwise>
	<tr>
	  <td><label for="paired">Paired:</label></td>
	  <td>
	    <c:choose>
	      <c:when test="${library.id != 0}">
	        <form:checkbox id="paired" path="paired" checked="checked"/>
	      </c:when>
	      <c:otherwise>
	        <form:checkbox id="paired" path="paired"/>
	      </c:otherwise>
	    </c:choose>
	  </td>
	</tr>
  </c:otherwise>
</c:choose>

<tr>
  <c:choose>
    <c:when test="${library.id == 0 or empty library.libraryType or fn:contains(SPRING_SECURITY_CONTEXT.authentication.principal.authorities,'ROLE_ADMIN')
        or fn:contains(SPRING_SECURITY_CONTEXT.authentication.principal.authorities,'ROLE_TECH')}">
      <td>Platform - Library Type:</td>
      <td>
        <form:select id="platformTypes" path="platformType" items="${platformTypes}"
                     onchange="Library.ui.changePlatformType(null);" class="validateable"/>
        <form:select id="libraryTypes" path="libraryType"/>
      </td>
      <script type="text/javascript">
        jQuery(document).ready(function () {
          Library.ui.changePlatformType(<c:out value="${library.libraryType.id}" default="0"/>, function() {
            <c:if test="${not empty library.libraryType}">jQuery('#libraryTypes').val('${library.libraryType.id}');</c:if>
            Library.setOriginalIndices();
          });
        });
      </script>
    </c:when>
    <c:otherwise>
      <td>Platform - Library Type</td>
      <td>${library.platformType} - ${library.libraryType.description}</td>
    </c:otherwise>
  </c:choose>
</tr>
<tr>
<c:if test="${!empty library.sample && detailedSample}">
  <tr>
    <td>Library Design:</td>
    <td>
      <miso:select id="libraryDesignTypes" path="libraryDesign" items="${libraryDesigns}" itemLabel="name" itemValue="id" defaultLabel="(None)" defaultValue="-1" onchange="Library.ui.changeDesign()"/>
      &nbsp;&nbsp;&nbsp;Design Code: <miso:select id="libraryDesignCodes" path="libraryDesignCode" items="${libraryDesignCodes}" itemLabel="code" itemValue="id" defaultLabel="(None)" defaultValue="-1"/>
    </td>
  </tr>
</c:if>
  <c:choose>
    <c:when test="${library.id == 0
                  or empty library.librarySelectionType
                  or fn:contains(SPRING_SECURITY_CONTEXT.authentication.principal.authorities,'ROLE_ADMIN')
                  or fn:contains(SPRING_SECURITY_CONTEXT.authentication.principal.authorities,'ROLE_TECH')}">
      <td>Library Selection Type:</td>
      <td>
        <miso:select id="librarySelectionTypes" path="librarySelectionType" items="${librarySelectionTypes}"
                     itemLabel="name" itemValue="id" defaultLabel="(None)" defaultValue="-1" />
      </td>
    </c:when>
    <c:otherwise>
      <td>Library Selection Type:</td>
      <td>${library.librarySelectionType.name}</td>
    </c:otherwise>
  </c:choose>
</tr>
<tr>
  <c:choose>
    <c:when
        test="${library.id == 0
              or empty library.libraryStrategyType
              or fn:contains(SPRING_SECURITY_CONTEXT.authentication.principal.authorities,'ROLE_ADMIN')
              or fn:contains(SPRING_SECURITY_CONTEXT.authentication.principal.authorities,'ROLE_TECH')}">
      <td>Library Strategy Type:</td>
      <td>
        <miso:select id="libraryStrategyTypes" path="libraryStrategyType" items="${libraryStrategyTypes}"
                     itemLabel="name" itemValue="id" defaultLabel="(None)" defaultValue="-1" />
      </td>
    </c:when>
    <c:otherwise>
      <td>Library Strategy Type:</td>
      <td>${library.libraryStrategyType.name}</td>
    </c:otherwise>
  </c:choose>
</tr>
<tr>
  <td>Index Family:</td>
  <td>
    <miso:select id='indexFamily' name='indexFamily' path="currentFamily" items="${indexFamilies}" itemLabel="name" itemValue="id" onchange='Library.ui.updateIndices();'/>
  </td>
</tr>

<tr>
  <td>Indices:</td>
  <td id="indicesDiv">
  </td>
</tr>

<tr bgcolor="yellow">
  <td>QC Passed:*</td>
  <td>
    <form:radiobutton path="qcPassed" value="" label="Unknown"/>
    <form:radiobutton path="qcPassed" value="true" label="True"/>
    <form:radiobutton path="qcPassed" value="false" label="False"/>
  </td>
</tr>

<tr>
  <td style="color:#a93232"><label for="lowQuality">Low Quality Sequencing:</label></td>
  <td>
    <form:checkbox path="lowQuality" id="lowQuality"/>
  </td>
</tr>

<tr>
  <td>Size (bp):</td>
  <td><form:input id="dnaSize" path="dnaSize"/></td>
</tr>
<tr>
  <td>Volume (&#181;l):</td>
  <td><form:input id="volume" path="volume"/></td>
</tr>
<tr>
  <td><label for="discarded">Discarded:</label></td>
  <td><form:checkbox id="discarded" path="discarded"/></td>
</tr>
<tr>
  <td class="h"><label for="locationBarcode">Location:</label></td>
  <td><form:input id="locationBarcode" path="locationBarcode"/></td>
</tr>
<tr>
  <td class="h">Box Location:</td>
  <td id="boxLocation">
    <c:if test="${!empty library.box.locationBarcode}">${library.box.locationBarcode},</c:if>
    <c:if test="${!empty library.boxPosition}"><a href='<c:url value="/miso/box/${library.box.id}"/>'>${library.box.alias}, ${library.boxPosition}</a></c:if>
  </td>
</tr>
  <tr>
    <td>Library Kit:*</td>
    <td>
      <miso:select id="libraryKit" path="kitDescriptor" items="${prepKits}" itemLabel="name"
          itemValue="id" defaultLabel="SELECT" defaultValue=""/>
    </td>
  </tr>
</table>
<script type="text/javascript">
    Library = Library || {};
    Library.setOriginalIndices = function() {
      Library.lastIndexPosition = 0;
      jQuery('#indicesDiv').empty();
      document.getElementById('indexFamily').value = '${library.getCurrentFamily().id}';
      <c:forEach items="${library.indices}" var="index">
        <c:if test="${index.id != 0}">
          Library.ui.createIndexBox(${index.id});
        </c:if>
      </c:forEach>
      Library.ui.createIndexNextBox();
    };
    Library.setOriginalIndices();
  </script>
<%@ include file="volumeControl.jspf" %>

<c:if test="${detailedSample}">
<br/>
<br/>
<h2>Details</h2>
<table class="in">
  <tr>
    <td class="h">Group ID:</td>
    <td>
      <form:input id="groupId" path="groupId"/>
    </td>
  </tr>
  <tr>
    <td class="h">Group Description:</td>
    <td>
      <form:input id="groupDescription" path="groupDescription"/>
    </td>
  </tr>
  <tr>
    <td class="h"><label for="initialConcentration">Initial Concentration (<span id="concentrationUnits"></span>):</label></td>
    <td><form:input id="initialConcentration" path="initialConcentration"/></td>
  </tr>
  
  <tr>
    <td class="h"><label for="archived">Archived:</label></td>
    <td><form:checkbox id="archived" path="archived"/></td>
  </tr>
</table>
</c:if>
<script type="text/javascript">
  Library.ui.updateConcentrationUnits();
</script>

<c:choose>
  <c:when
      test="${!empty library.sample and library.securityProfile.profileId eq library.sample.project.securityProfile.profileId}">
    <table class="in">
    <tr>
      <td>Permissions</td>
      <td><i>Inherited from sample </i>
        <a href='<c:url value="/miso/sample/${library.sample.id}"/>'>${library.sample.name} (${library.sample.alias})</a>
        <input type="hidden" value="${library.sample.securityProfile.profileId}"
               name="securityProfile" id="securityProfile"/>
      </td>
    </tr>
    </table>
  </c:when>
  <c:otherwise>
    <%@ include file="permissions.jsp" %>
  </c:otherwise>
</c:choose>

<script type="text/javascript">
  jQuery(document).ready(function () {
    // Attach Parsley form validator
    Validate.attachParsley('#library-form');
  });
</script>

<c:if test="${library.id != 0}">
  <div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#notes_arrowclick'), 'notes');">Notes
    <div id="notes_arrowclick" class="toggleLeftDown"></div>
  </div>
  <div id="notes">
    <h1>Notes</h1>
    <ul class="sddm">
      <li>
        <a id="notesMenuHandle" onmouseover="mopen('notesMenu')" onmouseout="mclosetime()">Options
          <span style="float:right" class="ui-icon ui-icon-triangle-1-s"></span>
        </a>

        <div id="notesMenu" onmouseover="mcancelclosetime()" onmouseout="mclosetime()">
          <a onclick="Library.ui.showLibraryNoteDialog(${library.id});" href="javascript:void(0);" class="add">Add Note</a>
        </div>
      </li>
    </ul>
    <c:if test="${fn:length(library.notes) > 0}">
      <div class="note" style="clear:both">
        <c:forEach items="${library.notes}" var="note" varStatus="n">
          <div class="exppreview" id="library-notes-${n.count}">
            <b>${note.creationDate}</b>: ${note.text}
              <span class="float-right" style="font-weight:bold; color:#C0C0C0;">${note.owner.loginName}
                <c:if test="${(note.owner.loginName eq SPRING_SECURITY_CONTEXT.authentication.principal.username)
                                or fn:contains(SPRING_SECURITY_CONTEXT.authentication.principal.authorities,'ROLE_ADMIN')}">
                <span style="color:#000000"><a href='#' onclick="Library.ui.deleteLibraryNote('${library.id}', '${note.noteId}');">
                  <span class="ui-icon ui-icon-trash" style="clear: both; position: relative; float: right; margin-top: -15px;"></span></a></span>
                </c:if>
              </span>
          </div>
        </c:forEach>
      </div>
    </c:if>
    <div id="addNoteDialog" title="Create new Note"></div>
  </div>
</c:if>
</form:form>
<br/>
<c:if test="${library.id != 0}">
  <miso:qcs id="list_qcs" item="${library}"/>
  <miso:list-section id="list_dilution" name="Dilutions" target="dilution" items="${libraryDilutions}" config="${libraryDilutionsConfig}"/>
  <miso:list-section id="list_pool" name="Pools" target="pool" items="${libraryPools}"/>
  <miso:list-section id="list_run" name="Runs" target="run" items="${libraryRuns}"/>
  <miso:list-section id="list_experiment" name="Experiments" target="experiment" items="${experiments}" config="{ libraryId: ${library.id} }"/>
  <miso:changelog item="${library}"/>
</c:if>
</div>
</div>

<script type="text/javascript">
  jQuery(document).ready(function () {
    Library.ui.changeDesign(<c:out value="${library.libraryType.id}" default="0"/>, function() {
      Library.setOriginalIndices();
    });
    jQuery('#alias').simplyCountable({
      counter: '#aliasCounter',
      countType: 'characters',
      maxCount: ${maxLengths['alias']},
      countDirection: 'down'
    });

    jQuery('#description').simplyCountable({
      counter: '#descriptionCounter',
      countType: 'characters',
      maxCount: ${maxLengths['description']},
      countDirection: 'down'
    });
  });
</script>

<%@ include file="adminsub.jsp" %>

<%@ include file="../footer.jsp" %>
