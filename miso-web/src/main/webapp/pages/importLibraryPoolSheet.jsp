<%--
  Created by IntelliJ IDEA.
  User: bianx
  Date: 04/12/2013
  Time: 11:16
  To change this template use File | Settings | File Templates.
--%>

<%@ include file="../header.jsp" %>
<script src="<c:url value='/scripts/jquery/datatables/js/jquery.dataTables.min.js'/>" type="text/javascript"></script>
<link rel="stylesheet" href="<c:url value='/scripts/jquery/datatables/css/jquery.dataTables.css'/>" type="text/css">
<link rel="stylesheet" href="<c:url value='/scripts/jquery/datatables/css/jquery.dataTables_themeroller.css'/>"
      type="text/css">

<div id="maincontent">
    <div id="contentcolumn">

        <h1>
            Library &amp; Pool Sheet Import
        </h1>

        <div id="librarypoolsheetformdiv" class="simplebox ui-corner-all">
            <table class="in">
                <tr>
                    <td>
                        <form method='post'
                              id='librarypoolsheet_upload_form'
                              action='<c:url value="/miso/upload/importexport/librarypoolsheet"/>'
                              enctype="multipart/form-data"
                              target="plateform_target_upload"
                              onsubmit="Utils.fileUpload.fileUploadProgress('librarypoolsheet_upload_form', 'librarypoolsheet_statusdiv', ImportExport.libraryPoolSheetUploadSuccess);">
                            <input type="file" name="file"/>
                            <button type="submit" class="br-button ui-state-default ui-corner-all">Upload</button>
                            <button type="button" class="br-button ui-state-default ui-corner-all"
                                    onclick="ImportExport.cancelLibraryPoolSheetUpload();">Cancel
                            </button>
                        </form>
                    </td>
                </tr>
            </table>
        </div>
        <div id="librarypoolsheet_statusdiv"></div>
    </div>
</div>
<%@ include file="adminsub.jsp" %>

<%@ include file="../footer.jsp" %>