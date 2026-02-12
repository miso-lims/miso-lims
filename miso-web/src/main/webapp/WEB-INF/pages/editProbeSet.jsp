<%@ include file="../header.jsp" %>

<div id="maincontent">
<div id="contentcolumn">

<h1>
  <c:choose><c:when test="${pageMode eq 'create'}">Create</c:when><c:otherwise>Edit</c:otherwise></c:choose> Probe Set
  <button id="save" type="button" class="fg-button ui-state-default ui-corner-all">Save</button>
</h1>

<form:form id="probeSetForm" data-parsley-validate="" autocomplete="off" acceptCharset="utf-8"></form:form>

<br>
<h1>Probes</h1>

<c:choose>
  <c:when test="${pageMode eq 'create'}">
    <p>Probes can be added once the probe set is saved.</p>
  </c:when>
  <c:otherwise>
    <div id="probeSetForm_probesError"></div>
    <div id="listProbes"></div>
  </c:otherwise>
</c:choose>
<br>

<script type="text/javascript">
  jQuery(document).ready(function () {
    var probeSet = ${probeSetDto};
    var config = {
      pageMode: '${pageMode}'
    };
    var form = FormUtils.createForm('probeSetForm', 'save', probeSet, 'probeset', config);
    
    ProbeSet.setForm(form);
    ProbeSet.setListConfig({
      probeSet: probeSet
    });
    if ('${pageMode}' === 'edit') {
      ProbeSet.setProbes(probeSet.probes);
    }
    Utils.ui.updateHelpLink(FormTarget.probeset.getUserManualUrl());
  });
</script>

</div>
</div>

<%@ include file="adminsub.jsp" %>
<%@ include file="../footer.jsp" %>
