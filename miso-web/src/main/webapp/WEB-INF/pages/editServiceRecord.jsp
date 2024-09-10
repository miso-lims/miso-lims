<%@ include file="../header.jsp" %>

<div id="maincontent">
  <div id="contentcolumn">
    <h1>
      <c:choose>
        <c:when test="${serviceRecord.id != 0}">Edit</c:when>
        <c:otherwise>Create</c:otherwise>
      </c:choose>
      Service Record
      <button id="save" type="button" class="fg-button ui-state-default ui-corner-all">Save</button>
    </h1>
    <div class="breadcrumbs">
      <ul>
        <li>
          <a href="<c:url value='/'/>">Home</a>
        </li>
        <c:if test="${instrument.id != null}">
          <li>
            <a href='<c:url value="/instruments"/>'>Instruments</a>
          </li>
          <li>
            <a href='<c:url value="/instrument/${instrument.id}"/>'>${instrument.name}</a>
          </li>
        </c:if>
        <c:if test="${freezer.id != null}">
          <li>
            <a href='<c:url value="/storagelocations"/>'>Storage Locations</a>
          </li>
          <li>
            <a href='<c:url value="/freezer/${freezer.id}"/>'>${freezer.alias}</a>
          </li>
        </c:if>

      </ul>
    </div>
    <div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#noteArrowClick'), 'noteDiv');">Quick Help
      <div id="noteArrowClick" class="toggleLeft"></div>
    </div>
    <div id="noteDiv" class="note" style="display:none;">A Service Record is a record of maintenance performed 
    on an instrument
    </div>
    
    <form:form id="serviceRecordForm" data-parsley-validate="" autocomplete="off" acceptCharset="utf-8"></form:form>	
    <script type="text/javascript">	
      
      jQuery(document).ready(function () {	
        <c:choose>
          <c:when test="${instrument.id != null}">
            var config = {instrumentId: ${instrument.id}, instrumentPositions: ${instrumentPositions}};
          </c:when> 
          <c:otherwise>
            var config = {freezerId: ${freezer.id}};	
          </c:otherwise>
        </c:choose>

        FormUtils.createForm('serviceRecordForm', 'save', ${serviceRecordDto}, 'servicerecord', 	
        config);	
        Utils.ui.updateHelpLink(FormTarget.servicerecord.getUserManualUrl());	
      });	
    </script>
    <br>
    
    <c:choose>
      <c:when test="${serviceRecord.id != 0}">
        <miso:attachments item="${serviceRecord}"/>
      </c:when>
      <c:otherwise>
        You can attach files after the service record has been saved.
      </c:otherwise>
    </c:choose>
    
  </div>
</div>

<%@ include file="adminsub.jsp" %>

<%@ include file="../footer.jsp" %>