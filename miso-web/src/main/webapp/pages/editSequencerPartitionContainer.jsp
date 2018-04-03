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
<form:form id="container-form" data-parsley-validate="" action="/miso/container" method="POST" commandName="container" autocomplete="off">
<sessionConversation:insertSessionConversationId attributeName="container"/>
  <h1>
    <c:choose>
      <c:when test="${container.id != 0}">Edit</c:when>
      <c:otherwise>Create</c:otherwise>
    </c:choose> ${container.model.platformType.containerName}
    <button type="button" id="save" class="fg-button ui-state-default ui-corner-all"
          onclick="return Container.validateContainer();">Save</button>
  </h1>
  <div class="right fg-toolbar ui-helper-clearfix paging_full_numbers">
    <c:if test="${container.id != 0 && not empty container.identificationBarcode}"><span class="ui-button ui-state-default" onclick="Utils.printDialog('container', [${container.id}]);">Print Barcode</span></c:if>
  </div>

  <div class="bs-callout bs-callout-warning hidden">
    <h2>Oh snap!</h2>
    <p>This form seems to be invalid!</p>
  </div>

  <table class="in">
    <tr>
      <td class="h">Container ID:</td>
      <td id="containerId">
        <c:choose>
          <c:when test="${container.id != 0}">${container.id}</c:when>
          <c:otherwise><i>Unsaved</i></c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Serial Number:*</td>
      <td><form:input id="identificationBarcode" path="identificationBarcode"/> </td>
    </tr>
    <tr>
      <td>Container Model:</td>
      <td><span id="model">${container.model.alias}</span></td>
    </tr>
    <tr>
      <td>Clustering Kit:</td>
      <td><miso:select id="clusteringKit" path="clusteringKit" items="${clusteringKits}" itemLabel="name" itemValue="id" defaultLabel="(None)" defaultValue="" /></td>
    </tr>
    <tr>
      <td>Multiplexing Kit:</td>
      <td><miso:select id="multiplexingKit" path="multiplexingKit" items="${multiplexingKits}" itemLabel="name" itemValue="id" defaultLabel="(None)" defaultValue="" /></td>
    </tr>
    
    <c:if test="${miso:instanceOf(container, 'uk.ac.bbsrc.tgac.miso.core.data.impl.OxfordNanoporeContainer')}">
      <tr>
        <td>Flow Cell Version:</td>
        <td><miso:select id="flowCellVersion" path="flowCellVersion" items="${flowCellVersions}" itemLabel="alias" itemValue="id" defaultLabel="(Unknown)" defaultValue="" /></td>
      </tr>
      <tr>
        <td>Pore Version:</td>
        <td><miso:select id="poreVersion" path="poreVersion" items="${poreVersions}" itemLabel="alias" itemValue="id" defaultLabel="(Unknown)" defaultValue="" /></td>
      </tr>
      <tr>
        <td>Received Date:*</td>
        <td><form:input path="receivedDate" id="receivedDate" /></td>
      </tr>
      <tr>
        <td>Returned Date:</td>
        <td><form:input path="returnedDate" id="returnedDate" /></td>
      </tr>
      <script type="text/javascript">
      Utils.ui.addDatePicker("receivedDate");
      Utils.ui.addDatePicker("returnedDate");
      </script>
    </c:if>
    
  </table>
</form:form>

  <c:if test="${container.id != 0}">
    <miso:qcs id="list_qcs" item="${container}"/>
    <miso:list-section id="list_partition" name="${container.model.platformType.partitionName}" target="partition" items="${containerPartitions}" config="{ 'platformType' : '${container.model.platformType.name()}', 'showContainer' : false }"/>
  </c:if>
  <miso:list-section id="list_run" name="Runs" target="run" items="${containerRuns}"/>
  <miso:changelog item="${container}"/>
</div>
</div>

<%@ include file="adminsub.jsp" %>
<%@ include file="../footer.jsp" %>
