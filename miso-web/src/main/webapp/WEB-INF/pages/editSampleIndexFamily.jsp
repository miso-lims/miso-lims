<%@ include file="../header.jsp" %>

<div id="maincontent">
<div id="contentcolumn">

<h1>
  <c:choose><c:when test="${pageMode eq 'create'}">Create</c:when><c:otherwise>Edit</c:otherwise></c:choose> Sample Index Family
  <button id="save" type="button" class="fg-button ui-state-default ui-corner-all">Save</button>
</h1>

<form:form id="indexFamilyForm" data-parsley-validate="" autocomplete="off" acceptCharset="utf-8"></form:form>

<br/>
<c:choose>
  <c:when test="${pageMode eq 'create'}">
    <h1>Indices</h1>
    <p>Indices can be added once the index family is saved.</p>
  </c:when>
  <c:otherwise>
    <miso:list-section id="list_indices" name="Indices" target="sampleindex" items="${indices}" config="{ isAdmin: ${miso:isAdmin()}, indexFamilyId: ${indexFamilyId} }" alwaysShow="true"/>
  </c:otherwise>
</c:choose>

<script type="text/javascript">
  jQuery(document).ready(function () {
    var dto = ${indexFamilyDto};
    var form = FormUtils.createForm('indexFamilyForm', 'save', dto, 'sampleindexfamily', {
      pageMode: '${pageMode}',
      isAdmin: ${miso:isAdmin()}
    });
    Utils.ui.updateHelpLink(FormTarget.sampleindexfamily.getUserManualUrl());
  });
</script>

</div>
</div>

<%@ include file="adminsub.jsp" %>
<%@ include file="../footer.jsp" %>
