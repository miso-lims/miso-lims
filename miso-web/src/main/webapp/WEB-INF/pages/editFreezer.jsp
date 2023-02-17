<%@ include file="../header.jsp" %>

<div id="maincontent">
<div id="contentcolumn">

<h1>
  <c:choose><c:when test="${pageMode eq 'create'}">Create</c:when><c:otherwise>Edit</c:otherwise></c:choose> Freezer
  <button id="save" type="button" class="fg-button ui-state-default ui-corner-all">Save</button>
</h1>

<div class="sectionDivider"  onclick="Utils.ui.toggleLeftInfo(jQuery('#note_arrowclick'), 'notediv');">Quick Help
  <div id="note_arrowclick" class="toggleLeft"></div>
</div>
<div id="notediv" class="note" style="display:none;">
  Freezers can be divided into several storage spaces which hold boxes. For full descriptions of the different storage
  spaces, see the User Manual:
  <a id="addingStorageLink" target="_blank" rel="noopener noreferrer">Adding Storage to a Freezer</a>
</div>

<c:if test="${freezer.isOutOfService()}">
  <p class="big-warning">Out of Service</p>
</c:if>

<form:form id="freezerForm" data-parsley-validate="" autocomplete="off" acceptCharset="utf-8"></form:form>
<script type="text/javascript">
  jQuery(document).ready(function () {
    jQuery('#addingStorageLink').attr('href', Urls.external.userManual('freezers_and_rooms', 'adding-storage-to-a-freezer'));
    var dto = ${empty freezerJson ? '{}' : freezerJson};
    Freezer.setFreezerJson(dto);
    FormUtils.createForm('freezerForm', 'save', dto, 'freezer', {
      rooms: ${rooms},
      locationMaps: ${locationMaps},
      storageLabels: ${storageLabels}
    });
    Utils.ui.updateHelpLink(FormTarget.freezer.getUserManualUrl());
    $('#storageComponentLabel').append(${storageLabels}.map(function(x) {
      return $('<option>').val(x.id).text(x.label);
    }));
  });
</script>

<c:if test="${freezer.id != 0}">
  <div id="recordsHider" class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#records_arrowclick'), 'recordsdiv');">
    <c:choose>
      <c:when test="${fn:length(serviceRecords) == 1}">1 Service Record</c:when>
      <c:otherwise>${fn:length(serviceRecords)} Service Records</c:otherwise>
    </c:choose>
    <div id="records_arrowclick" class="toggleLeft"></div>
  </div>
  <div id="recordsdiv" class="expandable_section" style="display:none;">
    <miso:list-section id="list_servicerecords" name="Service Records" target="servicerecord" alwaysShow="true" items="${serviceRecords}" config="{freezerId: ${freezer.id}, userIsAdmin: ${miso:isAdmin()}}"/>
  </div>
</c:if>

<c:if test="${pageMode eq 'edit'}">
  <br/>
  <div id="recordsHider" class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#records_arrowclick'), 'recordsdiv');">
    <c:choose>
      <c:when test="${fn:length(serviceRecords) == 1}">1 Service Record</c:when>
      <c:otherwise>${fn:length(serviceRecords)} Service Records</c:otherwise>
    </c:choose>
    <div id="records_arrowclick" class="toggleLeft"></div>
  </div>
  <div id="recordsdiv" class="expandable_section" style="display:none;">
    <miso:list-section id="list_servicerecords" name="Service Records" target="servicerecord" alwaysShow="true" items="${serviceRecords}" config="{freezerId: ${freezer.id}, retired: ${freezer.retired}, hasPositions: false, userIsAdmin: ${miso:isAdmin()}}"/>
  </div>

  <br/>
  <h1>Layout</h1>
  <div id="layoutSection" style="overflow:auto">
  
    <div id="freezerLayoutContainer" class="storageComponentContainer unselectable">
      <span class="storageComponentLabel">Freezer</span>
      <button type="button" class="ui-state-default storageComponentButton" onclick="Freezer.addFreezerStorage()">Add Storage</button>
      <div class="clearfix"></div>
      <table id="freezerLayout" class="storageComponent"></table>
    </div>
    
    <div id="editStorageComponentContainer" class="storageComponentContainer">
      <form:form id="freezerComponent-form" data-parsley-validate="" autocomplete="off" acceptCharset="utf-8">
        <div class="bs-callout bs-callout-warning hidden">
          <h2>Oh snap!</h2>
          <p>This form seems to be invalid</p>
          <div class="generalErrors"></div>
        </div>
        <span class="storageComponentLabel">Edit <span id="storageComponentAlias"></span></span>
        <div id="storageComponentActionContainer">
          <button id="deleteStorageComponent" type="button" class="fg-button ui-state-default ui-corner-all" onclick="Freezer.deleteComponent()">Delete</button>
          <button id="saveStorageComponent" type="button" class="fg-button ui-state-default ui-corner-all" onclick="Freezer.validateAndSaveComponent()">Save</button>
        </div>
        <br />
        <table>
          <tbody>
            <tr>
              <td class="h">Location ID:</td>
              <td>
                <span id="storageComponentId"></span>
              </td>
            </tr>
            <tr id="storageComponentBarcodeRow">
              <td class="h">Barcode:</td>
              <td>
                <input id="storageComponentBarcode" type="text">
                <div id="freezerComponent-form_identificationBarcodeError" class="errorContainer"></div>
              </td>
            </tr>
            <tr id="storageComponentLabelRow">
              <td class="h">Label:</td>
              <td>
                <select id="storageComponentLabel">
                  <option value=''>None</option>
                </select>
                <div id="freezerComponent-form_labelError" class="errorContainer"></div>
              </td>
            </tr>
          </tbody>
        </table>
      </form:form>
    </div>
    
    <div id="levelTwoStorageContainer" class="storageComponentContainer unselectable">
      <span id="levelTwoStorageAlias" class="storageComponentLabel"></span>
      <div class="clearfix"></div>
      <table id="levelTwoStorageLayout" class="storageComponent"></table>
    </div>
    
  </div>

  <miso:list-section id="list_box" name="Boxes" target="box" items="${boxes}" config="{'showFreezerLocation':true, 'boxUse':false, 'showStorageLocation':false}"/>
  
  <br/>
  <miso:changelog item="${freezer}"/>
</c:if>
</div>
</div>

<%@ include file="adminsub.jsp" %>
<%@ include file="../footer.jsp" %>
