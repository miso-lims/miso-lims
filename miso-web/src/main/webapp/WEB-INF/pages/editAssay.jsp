<%@ include file="../header.jsp" %>

<div id="maincontent">
<div id="contentcolumn">

<h1>
  <c:choose><c:when test="${pageMode eq 'create'}">Create</c:when><c:otherwise>Edit</c:otherwise></c:choose> Assay
  <c:if test="${isAdmin}">
    <button id="save" type="button" class="fg-button ui-state-default ui-corner-all">Save</button>
  </c:if>
</h1>

<form:form id="assayForm" data-parsley-validate="" autocomplete="off" acceptCharset="utf-8"></form:form>

<br>
<h1>Tests</h1>
<div id="assayForm_testsError"></div>
<div id="listTests"></div>
<br>

<br>
<h1>Metrics</h1>
<div id="assayForm_metricsError"></div>
<div id="listMetrics"></div>
<br>

<script type="text/javascript">
  jQuery(document).ready(function () {
    var assay = ${assayDto};
    var config = {
      pageMode: '${pageMode}',
      isAdmin: ${isAdmin},
      assayId: assay.id,
      libraryQualificationMethods: ${libraryQualificationMethods}
    };
    var form = FormUtils.createForm('assayForm', config.pageMode === 'view' ? null : 'save', assay, 'assay', config);
    if (config.isAdmin && config.pageMode === 'view') {
      $('#save').text('Edit').click(function() {
        var dialogText = [
          'Editing the assay will affect existing requisitions. This should be used to correct mistakes only.',
          'If the assay has changed, a new version should be added instead.'
        ];
        Utils.showConfirmDialog('Warning', 'Edit', dialogText, function() {
          Utils.page.reloadWithParams({
            locked: false
          });
        });
      });
    }
    Assay.setForm(form);
    Assay.setListConfig(config);
    Assay.setMetrics(assay.metrics);
    Assay.setTests(assay.tests);
    Utils.ui.updateHelpLink(FormTarget.assay.getUserManualUrl());
  });
</script>

</div>
</div>

<%@ include file="adminsub.jsp" %>
<%@ include file="../footer.jsp" %>
