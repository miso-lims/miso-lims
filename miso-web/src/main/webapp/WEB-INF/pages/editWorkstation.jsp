<%@ include file="../header.jsp" %>

<div id="maincontent">
  <div id="contentcolumn">
    <h1>
      View Workstation
    </h1>

    <form:form id="workstationForm" data-parslet-validate="" autocomplete="off" acceptCharset="utf-8">
    </form:form>
    
    <script type="text/javascript">
      jQuery(document).ready(function () {
        FormUtils.createForm('workstationForm', 'save', ${workstationDto}, 'workstation', {
          isAdmin: ${miso:isAdmin()}
        });

        Utils.ui.updateHelpLink(FormTarget.workstation.getUserManualUrl());
      });
    </script>
  </div>
</div>

<%@ include file="adminsub.jsp" %>

<%@ include file="../footer.jsp" %>