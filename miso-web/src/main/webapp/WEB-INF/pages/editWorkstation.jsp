<%@ include file="../header.jsp" %>

<div id="maincontent">
  <div id="contentcolumn">
    <h1>
      <c:choose>
        <c:when test="${miso:isAdmin()}">Edit</c:when>
        <c:otherwise>View</c:otherwise>
      </c:choose>
      Workstation
    </h1>

    <c:if test="${miso:isAdmin()}">
        <button id="save" type="button" class="fg-button ui-state-default ui-corner-all">Save</button>
    </c:if>

    <div class="breadcrumbs">
      <ul>
        <li>
          <a href="<c:url value='/'/>">Home</a>
        </li>
        <li>
          <a href='<c:url value="/workstation/list"/>'>Workstations</a>
        </li>
      </ul>
    </div>
    <br>


    <form:form id="workstationForm" data-parsley-validate="" autocomplete="off" acceptCharset="utf-8">
    </form:form>

    <br>
    <h1>Instruments</h1>
    <table id="instruments_table"></table>
    <br>

    <br>
    <h1>Libraries</h1>
    <table id="libraries_table"></table>
    <br>

    <script type="text/javascript">
      jQuery(document).ready(function () {
        FormUtils.createForm('workstationForm', 'save', ${workstationDto}, 'workstation', {isAdmin: ${miso:isAdmin()}});

        Utils.ui.updateHelpLink(FormTarget.workstation.getUserManualUrl());
        ListUtils.createTable('instruments_table', ListTarget.instrument, null, {workstationId : ${workstation.id}});
        ListUtils.createTable('libraries_table', ListTarget.library, null, {workstationId: ${workstation.id}});
      });
    </script>
  </div>
</div>

<%@ include file="adminsub.jsp" %>

<%@ include file="../footer.jsp" %>