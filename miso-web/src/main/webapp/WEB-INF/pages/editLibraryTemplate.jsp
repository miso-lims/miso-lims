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
  <c:choose><c:when test="${pageMode eq 'create'}">Create</c:when><c:otherwise>Edit</c:otherwise></c:choose> Library Template
  <button id="save" type="button" class="fg-button ui-state-default ui-corner-all">Save</button>
</h1>

<form:form id="libraryTemplateForm" data-parsley-validate="" autocomplete="off" acceptCharset="utf-8"></form:form>

<br/>
<h1>Projects</h1>
<div id="listProjectsContainer"></div>

<br/>
<h1>Indices</h1>
<c:choose>
  <c:when test="${pageMode eq 'create'}">
    <p>Indices may be added after saving.</p>
  </c:when>
  <c:when test="${empty template.indexFamily}">
    <p>Indices may be added after selecting an index kit and saving.</p>
  </c:when>
  <c:otherwise>
    <div id="listIndicesContainer"></div>
  </c:otherwise>
</c:choose>

<script type="text/javascript">
  jQuery(document).ready(function () {
    var template = ${templateDto};
    var form = FormUtils.createForm('libraryTemplateForm', 'save', template, 'librarytemplate', {});
    
    LibraryTemplate.setForm(form);
    LibraryTemplate.setProjects(${templateProjects});
    if (template.indexFamilyId) {
      LibraryTemplate.setIndicesFromTemplate(template);
    }
    Utils.ui.updateHelpLink(FormTarget.librarytemplate.getUserManualUrl());
  });
</script>

</div>
</div>

<%@ include file="adminsub.jsp" %>
<%@ include file="../footer.jsp" %>
