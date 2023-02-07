<%@ include file="../header.jsp" %>

<div id="maincontent">
<div id="contentcolumn">

<h1>
  <c:choose><c:when test="${pageMode eq 'create'}">Create</c:when><c:otherwise>Edit</c:otherwise></c:choose> Array Run
  <button id="save" type="button" class="fg-button ui-state-default ui-corner-all">Save</button>
</h1>

<form:form id="arrayrunForm" data-parsley-validate="" autocomplete="off" acceptCharset="utf-8"></form:form>
<script>
jQuery(document).ready(function() {
  var config = {
    isAdmin: ${miso:isAdmin()}
  };
  <c:if test="${pageMode eq 'create'}">
    config.instruments = ${arrayScanners};
  </c:if>
  FormUtils.createForm('arrayrunForm', 'save', ${pageMode eq 'create' ? '{}' : arrayRunJson}, 'arrayrun', config);
  <c:if test="${pageMode eq 'edit'}">
    SampleArray.updateSamplesTable(${arrayJson});
  </c:if>
  Utils.ui.updateHelpLink(FormTarget.arrayrun.getUserManualUrl());
});
</script>

<br/>
<h1>Samples</h1>
<table id="listingSamplesTable" class="display"></table>

<c:if test="${pageMode eq 'edit'}">
  <miso:changelog item="${arrayRun}"/>
</c:if>

</div>
</div>

<%@ include file="adminsub.jsp" %>
<%@ include file="../footer.jsp" %>
