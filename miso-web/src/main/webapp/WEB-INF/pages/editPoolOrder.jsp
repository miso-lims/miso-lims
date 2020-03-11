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
<script src="<c:url value='/scripts/jquery/editable/jquery.jeditable.mini.js'/>" type="text/javascript"></script>
<script src="<c:url value='/scripts/jquery/editable/jquery.jeditable.datepicker.js'/>" type="text/javascript"></script>
<script src="<c:url value='/scripts/jquery/editable/jquery.jeditable.checkbox.js'/>" type="text/javascript"></script>
<link href="<c:url value='/scripts/jquery/datatables/css/jquery.dataTables.css'/>" rel="stylesheet" type="text/css">
<link rel="stylesheet" href="<c:url value='/scripts/jquery/datatables/css/jquery.dataTables_themeroller.css'/>">

<div id="maincontent">
<div id="contentcolumn">

<h1>
  <c:choose><c:when test="${pageMode eq 'create'}">Create</c:when><c:otherwise>Edit</c:otherwise></c:choose> Pool Order
  <button id="save" type="button" class="fg-button ui-state-default ui-corner-all">Save</button>
</h1>

<form:form id="orderForm" data-parsley-validate="" autocomplete="off" acceptCharset="utf-8"></form:form>

<br/>
<h1>Library Aliquots</h1>
<div id="listAliquotsContainer"></div>

<script type="text/javascript">
  jQuery(document).ready(function () {
    var order = ${orderDto};
    var form = FormUtils.createForm('orderForm', 'save', order, 'poolorder', {});
    PoolOrder.setForm(form);
    PoolOrder.setAliquots(order.orderAliquots, order.duplicateIndicesSequences, order.nearDuplicateIndicesSequences);
    Utils.ui.updateHelpLink(FormTarget.poolorder.getUserManualUrl());
  });
</script>

<div class="noPrint">
  <miso:changelog item="${poolOrder}"/>
</div>

</div>
</div>

<%@ include file="adminsub.jsp" %>
<%@ include file="../footer.jsp" %>
