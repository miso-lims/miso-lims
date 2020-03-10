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
  <c:choose><c:when test="${pageMode eq 'create'}">Create</c:when><c:otherwise>Edit</c:otherwise></c:choose> Instrument Model
  <button id="save" type="button" class="fg-button ui-state-default ui-corner-all">Save</button>
</h1>

<form:form id="instrumentModelForm" data-parsley-validate="" autocomplete="off" acceptCharset="utf-8"></form:form>

<br/>
<c:if test="${pageMode eq 'create' || instrumentType eq 'SEQUENCER'}">
  <h1>Run Positions</h1>
  <div id="listInstrumentPositionsContainer"></div>
</c:if>

<br/>
<c:if test="${pageMode eq 'create' || instrumentType eq 'SEQUENCER'}">
  <h1>Container Models</h1>
  <c:choose>
    <c:when test="${pageMode eq 'create'}">
      <p>Container models may be added to sequencer models after saving.</p>
    </c:when>
    <c:otherwise>
      <div id="listContainerModelsContainer"></div>
    </c:otherwise>
  </c:choose>
</c:if>

<script type="text/javascript">
  jQuery(document).ready(function () {
    var model = ${modelDto};
    var form = FormUtils.createForm('instrumentModelForm', 'save', model, 'instrumentmodel', {
      pageMode: '${pageMode}',
      isAdmin: ${miso:isAdmin()}
    });
    InstrumentModel.setAdmin(${miso:isAdmin()});
    InstrumentModel.setForm(form);
    if ('${pageMode}' === 'create' || '${instrumentType}' === 'SEQUENCER') {
      InstrumentModel.setInstrumentPositions(model.positions);
    }
    if ('${pageMode}' === 'edit' && '${instrumentType}' === 'SEQUENCER') {
      InstrumentModel.setContainerModels(model.containerModels);
    }
    Utils.ui.updateHelpLink(FormTarget.instrumentmodel.getUserManualUrl());
  });
</script>

</div>
</div>

<%@ include file="adminsub.jsp" %>
<%@ include file="../footer.jsp" %>
