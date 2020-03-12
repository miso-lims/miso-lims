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
  <c:choose><c:when test="${pageMode eq 'create'}">Create</c:when><c:otherwise>Edit</c:otherwise></c:choose> QC Type
  <button id="save" type="button" class="fg-button ui-state-default ui-corner-all">Save</button>
</h1>

<form:form id="qcTypeForm" data-parsley-validate="" autocomplete="off" acceptCharset="utf-8"></form:form>

<br/>
<h1>Controls</h1>
<div id="qcTypeForm_controlsError"></div>
<div id="listControls"></div>

<br/>
<h1>Kits</h1>
<div id="qcTypeForm_kitDescriptorsError"></div>
<div id="listKits"></div>

<script type="text/javascript">
  jQuery(document).ready(function () {
    var qcType = ${qcTypeDto};
    var form = FormUtils.createForm('qcTypeForm', 'save', qcType, 'qctype', {
      isAdmin: ${miso:isAdmin()}
    });
    QcType.setAdmin(${miso:isAdmin()});
    QcType.setForm(form);
    QcType.setControls(qcType.controls);
    QcType.setKits(qcType.kitDescriptors);
    Utils.ui.updateHelpLink(FormTarget.qctype.getUserManualUrl());
  });
</script>

</div>
</div>

<%@ include file="adminsub.jsp" %>
<%@ include file="../footer.jsp" %>
