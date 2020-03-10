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
  <c:choose><c:when test="${pageMode eq 'create'}">Create</c:when><c:otherwise>Edit</c:otherwise></c:choose> Sample Class
  <button id="save" type="button" class="fg-button ui-state-default ui-corner-all">Save</button>
</h1>

<form:form id="sampleClassForm" data-parsley-validate="" autocomplete="off" acceptCharset="utf-8"></form:form>

<br/>
<h1>Parent Relationships</h1>
<div id="sampleClassForm_parentRelationshipsError" class="errorContainer"></div>
<div id="listParentsContainer"></div>

<br/>
<h1>Child Relationships</h1>
<div id="listChildrenContainer"></div>

<script type="text/javascript">
  jQuery(document).ready(function () {
    var sampleClass = ${sampleClassDto};
    var form = FormUtils.createForm('sampleClassForm', 'save', sampleClass, 'sampleclass', {
      pageMode: '${pageMode}',
      isAdmin: ${miso:isAdmin()}
    });
    
    SampleClass.setForm(form);
    SampleClass.setAdmin(${miso:isAdmin()});
    SampleClass.setParents(sampleClass.parentRelationships);
    SampleClass.setChildren(sampleClass.childRelationships);
    Utils.ui.updateHelpLink(FormTarget.sampleclass.getUserManualUrl());
  });
</script>

</div>
</div>

<%@ include file="adminsub.jsp" %>
<%@ include file="../footer.jsp" %>
