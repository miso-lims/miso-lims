<%@ include file="../header.jsp" %>

<div id="maincontent">
<div id="contentcolumn">

<h1>
  <c:choose><c:when test="${pageMode eq 'create'}">Create</c:when><c:otherwise>Edit</c:otherwise></c:choose> Library Index Family
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
    var form = FormUtils.createForm('indexFamilyForm', 'save', dto, 'libraryindexfamily', {
      pageMode: '${pageMode}',
      isAdmin: ${miso:isAdmin()}
    });
    LibraryIndexFamily.setAdmin(${miso:isAdmin()});
    if ('${pageMode}' === 'edit') {
      LibraryIndexFamily.setIndices(dto.indices);
    }
    Utils.ui.updateHelpLink(FormTarget.libraryindexfamily.getUserManualUrl());
  });
</script>

</div>
</div>

<%@ include file="adminsub.jsp" %>
<%@ include file="../footer.jsp" %>
