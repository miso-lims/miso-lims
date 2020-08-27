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
  Date: 12/01/12
  Time: 12:07
 --%>
<%@ include file="../header.jsp" %>
<script src="<c:url value='/scripts/jquery/datatables/js/jquery.dataTables.min.js'/>" type="text/javascript"></script>
<link href="<c:url value='/scripts/jquery/datatables/css/jquery.dataTables.css'/>" rel="stylesheet" type="text/css">
<link rel="stylesheet" href="<c:url value='/scripts/jquery/datatables/css/jquery.dataTables_themeroller.css'/>">

<div id="maincontent">
<div id="contentcolumn">
<h1>
  <c:choose>
    <c:when test="${container.id != 0}">Edit</c:when>
    <c:otherwise>Create</c:otherwise>
  </c:choose> ${container.model.platformType.containerName}
  <button type="button" id="save" class="fg-button ui-state-default ui-corner-all">Save</button>
</h1>
<div class="right fg-toolbar ui-helper-clearfix paging_full_numbers">
  <c:if test="${container.id != 0}"><span class="ui-button ui-state-default" onclick="Utils.printDialog('container', [${container.id}]);">Print Barcode</span></c:if>
</div>

<form:form id="containerForm" data-parsley-validate="" autocomplete="off" acceptCharset="utf-8"></form:form>
<script type="text/javascript">
  jQuery(document).ready(function () {
    FormUtils.createForm('containerForm', 'save', ${containerJSON}, 'container', {
      isNew: ${container.id == 0},
      platformType: '${container.model.platformType}'
    });
    Utils.ui.updateHelpLink(FormTarget.container.getUserManualUrl());
  });
</script>

<c:if test="${container.id != 0}">
  <miso:qcs id="list_qcs" item="${container}"/>
  <miso:list-section id="list_partition" name="${container.model.platformType.pluralPartitionName}" target="partition" items="${containerPartitions}" config="{ 'platformType' : '${container.model.platformType.name()}', 'showContainer' : false , 'showPool' : true}"/>
</c:if>
<miso:list-section id="list_run" name="Runs" target="run" items="${containerRuns}"/>
<miso:changelog item="${container}"/>

</div>
</div>

<%@ include file="adminsub.jsp" %>
<%@ include file="../footer.jsp" %>
