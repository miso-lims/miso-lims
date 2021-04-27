<%@ include file="../header.jsp" %>
<script src="<c:url value='/scripts/jquery/datatables/js/jquery.dataTables.min.js'/>" type="text/javascript"></script>
<script src="<c:url value='/scripts/jquery/editable/jquery.jeditable.mini.js'/>" type="text/javascript"></script>
<script src="<c:url value='/scripts/jquery/editable/jquery.jeditable.datepicker.js'/>" type="text/javascript"></script>
<script src="<c:url value='/scripts/jquery/editable/jquery.jeditable.checkbox.js'/>" type="text/javascript"></script>

<script type="text/javascript" src="<c:url value='/scripts/jquery/timepicker/js/jquery-ui-timepicker-addon.min.js'/>"></script>
<link rel="stylesheet" href="<c:url value='/scripts/jquery/timepicker/css/jquery-ui-timepicker-addon.min.css'/>"
        type="text/css">

<div id="maincontent">
<div id="contentcolumn">

<h1>
  <c:choose><c:when test="${pageMode eq 'create'}">Create</c:when><c:otherwise>Edit</c:otherwise></c:choose> Transfer
  <button id="save" type="button" class="fg-button ui-state-default ui-corner-all">Save</button>
</h1>

<div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#note_arrowclick'), 'notediv');">Quick Help
  <div id="note_arrowclick" class="toggleLeft"></div>
</div>
<div id="notediv" class="note" style="display:none;">
  <p>
    A transfer shows the change of custody for a set of items. There are three types of transfers,
    and the type that a transfer is classified as depends on the sender and recipient:
  </p>
  <table>
    <thead>
      <tr>
        <th>Type</th><th>Sender</th><th>Recipient</th>
      </tr>
    </thead>
    <tbody>
      <tr>
        <td>Receipt</td><td>External Lab</td><td>Internal Group</td>
      </tr>
      <tr>
        <td>Internal</td><td>Internal Group</td><td>Internal Group</td>
      </tr>
      <tr>
        <td>Distribution</td><td>Internal Group</td><td>External Entity</td>
      </tr>
    </tbody>
  </table>
</div>

<form:form id="transferForm" data-parsley-validate="" autocomplete="off" acceptCharset="utf-8"></form:form>

<br>
<h1>Items</h1>
<div id="transferForm_itemsError"></div>
<div id="listItems"></div>
<br>

<c:choose>
  <c:when test="${pageMode eq 'edit'}">
    <c:if test="${notificationsEnabled}">
      <h1>Notifications</h1>
      <div id="listNotifications"></div>
      <br>
    </c:if>
    <miso:changelog item="${transfer}"/>
  </c:when>
  <c:otherwise>
    <c:if test="${notificationsEnabled}">
      <p>(Notifications may be added after the transfer is saved.)</p>
    </c:if>
  </c:otherwise>
</c:choose>

<script type="text/javascript">
  jQuery(document).ready(function () {
    var transfer = ${transferDto};
    var formConfig = ${formConfig};
    var form = FormUtils.createForm('transferForm', 'save', transfer, 'transfer', formConfig);
    Transfer.setForm(form);
    Transfer.setItemsListConfig(${itemsListConfig});
    Transfer.setItems(transfer.items);
    if (formConfig.pageMode === 'edit' && ${notificationsEnabled}) {
      Transfer.setNotificationsListConfig({
        transferId: ${transfer.id}
      });
      Transfer.setNotifications(${notifications});
    }
    Utils.ui.updateHelpLink(FormTarget.transfer.getUserManualUrl());
  });
</script>

</div>
</div>

<%@ include file="adminsub.jsp" %>
<%@ include file="../footer.jsp" %>
