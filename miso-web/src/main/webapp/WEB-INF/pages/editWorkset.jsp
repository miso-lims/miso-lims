<%@ include file="../header.jsp" %>

<div id="maincontent">
<div id="contentcolumn">

<h1>
  <c:choose>
    <c:when test="${pageMode eq 'create'}">Create</c:when>
    <c:otherwise>Edit</c:otherwise>
  </c:choose> Workset
  <button id="save" type="button" class="fg-button ui-state-default ui-corner-all">Save</button>
</h1>

<form:form id="worksetForm" data-parsley-validate="" autocomplete="off" acceptCharset="utf-8"></form:form>
<script type="text/javascript">
  jQuery(document).ready(function () {
    FormUtils.createForm('worksetForm', 'save', ${empty worksetJson ? '{}' : worksetJson}, 'workset', {});
    Utils.ui.updateHelpLink(FormTarget.workset.getUserManualUrl());
  });
</script>

<c:if test="${pageMode eq 'edit'}">
  <miso:list-section-ajax id="list_samples" name="Samples" target="sample" config="{worksetId: ${worksetId}}"/>
  <miso:list-section-ajax id="list_libraries" name="Libraries" target="library" config="{worksetId: ${worksetId}}"/>
  <miso:list-section-ajax id="list_libraryAliquots" name="Library Aliquots" target="libraryaliquot" config="{worksetId: ${worksetId}}"/>
  <miso:changelog item="${workset}"/>
</c:if>

</div>
</div>

<%@ include file="adminsub.jsp" %>
<%@ include file="../footer.jsp" %>
