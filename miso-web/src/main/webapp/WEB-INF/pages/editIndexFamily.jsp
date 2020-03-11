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
  <c:choose><c:when test="${pageMode eq 'create'}">Create</c:when><c:otherwise>Edit</c:otherwise></c:choose> Index Family
  <button id="save" type="button" class="fg-button ui-state-default ui-corner-all">Save</button>
</h1>

<form:form id="indexFamilyForm" data-parsley-validate="" autocomplete="off" acceptCharset="utf-8"></form:form>

<br/>
<h1>Indices</h1>
<c:choose>
  <c:when test="${pageMode eq 'create'}">
    <p>Indices can be added once the index family is saved.</p>
  </c:when>
  <c:otherwise>
    <div id="listIndicesContainer"></div>
  </c:otherwise>
</c:choose>

<script type="text/javascript">
  jQuery(document).ready(function () {
    var dto = ${indexFamilyDto};
    var form = FormUtils.createForm('indexFamilyForm', 'save', dto, 'indexfamily', {
      pageMode: '${pageMode}',
      isAdmin: ${miso:isAdmin()}
    });
    IndexFamily.setAdmin(${miso:isAdmin()});
    if ('${pageMode}' === 'edit') {
      IndexFamily.setIndices(dto.indices);
    }
    Utils.ui.updateHelpLink(FormTarget.indexfamily.getUserManualUrl());
  });
</script>

</div>
</div>

<%@ include file="adminsub.jsp" %>
<%@ include file="../footer.jsp" %>
