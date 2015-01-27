<%--
  ~ Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
  ~ MISO project contacts: Robert Davey, Mario Caccamo @ TGAC
  ~ **********************************************************************
  ~
  ~ This file is part of MISO.
  ~
  ~ MISO is free software: you can redistribute it and/or modify
  ~ it under the terms of the GNU General Public License as published by
  ~ the Free Software Foundation, either version 3 of the License, or
  ~ (at your option) any later version.
  ~
  ~ MISO is distributed in the hope that it will be useful,
  ~ but WITHOUT ANY WARRANTY; without even the implied warranty of
  ~ MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  ~ GNU General Public License for more details.
  ~
  ~ You should have received a copy of the GNU General Public License
  ~ along with MISO.  If not, see <http://www.gnu.org/licenses/>.
  ~
  ~ **********************************************************************
  --%>
<%@ include file="../header.jsp" %>
<script type="text/javascript" src="<c:url value='/scripts/jquery/js/jquery.breadcrumbs.popup.js'/>"></script>
<script type="text/javascript" src="<c:url value='/scripts/plate_ajax.js?ts=${timestamp.time}'/>"></script>
<script type="text/javascript" src="<c:url value='/scripts/import_export_ajax.js?ts=${timestamp.time}'/>"></script>

<div id="maincontent">
<div id="contentcolumn">
<form:form action="/miso/plate" method="POST" commandName="plate" autocomplete="off">
<sessionConversation:insertSessionConversationId attributeName="plate"/>
<nav class="navbar navbar-default" role="navigation">
    <div class="navbar-header">
      <span class="navbar-brand navbar-center">
        <c:choose>
            <c:when test="${plate.id != 0}">Edit</c:when>
            <c:otherwise>Create</c:otherwise>
        </c:choose> Plate
      </span>
    </div>
    <div class="navbar-right container-fluid">

        <c:choose>
            <c:when test="${plate.id != 0}">
                <button type="button" class="btn btn-default navbar-btn"
                        onclick="return validate_plate(this.form,${plate.id});">Save Plate
                </button>
            </c:when>
            <c:otherwise>
                <button type="button" class="btn btn-default navbar-btn" onclick="return validate_plate(this.form);">
                    Save Plate
                </button>
            </c:otherwise>
        </c:choose>

    </div>
</nav>

<h2>Plate Information</h2>

<div class="barcodes">
    <div class="barcodeArea ui-corner-all" style="width:220px">
        <c:choose>
            <c:when test="${empty plate.locationBarcode}">
                <span style="float: left; font-size: 24px; font-weight: bold; color:#BBBBBB">Location</span>
                <form:input path="locationBarcode" size="8" class="form-control float-right"/>
            </c:when>
            <c:otherwise>
                <span style="float: left; font-size: 24px; font-weight: bold; color:#BBBBBB">Location</span>
                <ul class="barcode-ddm">
                    <li>
                        <a onmouseover="mopen('locationBarcodeMenu')" onmouseout="mclosetime()">
                            <span style="float:right; margin-top:6px;" class="ui-icon ui-icon-triangle-1-s"></span>
              <span id="locationBarcode" style="float:right; margin-top:6px; padding-bottom: 11px;">
                      ${plate.locationBarcode}
              </span>
                        </a>

                        <div id="locationBarcodeMenu"
                             onmouseover="mcancelclosetime()"
                             onmouseout="mclosetime()">
                            <a href="javascript:void(0);"
                               onclick="Plate.barcode.showPlateLocationChangeDialog(${plate.id});">Change
                                location</a>
                        </div>
                    </li>
                </ul>
                <div id="changePlateLocationDialog" title="Change Plate Location"></div>
            </c:otherwise>
        </c:choose>
    </div>
    <div class="barcodeArea ui-corner-all">
        <span style="float: left; font-size: 24px; font-weight: bold; color:#BBBBBB">ID</span>
        <c:if test="${not empty plate.identificationBarcode}">
            <ul class="nav navbar-right">
                <li class="dropdown">
                    <a id="idBarcode" class="dropdown-toggle" data-toggle="dropdown" href="javascript:void(0);">
                        <i class="fa fa-caret-down"></i>
                    </a>
                    <ul class="dropdown-menu dropdown-tasks">
                        <li role="presentation"><a href="javascript:void(0);"
                                                   onclick="Plate.barcode.printPlateBarcodes(${plate.id});">Print</a>
                        </li>
                    </ul>
                </li>
            </ul>
            <script type="text/javascript">
                jQuery(document).ready(function () {
                    Fluxion.doAjax(
                            'plateControllerHelperService',
                            'getPlateBarcode',
                            {'plateId':${plate.id},
                                'url': ajaxurl
                            },
                            {'doOnSuccess': function (json) {
                                jQuery('#idBarcode').prepend("<img style='height:30px; border:0; padding-right:4px' src='<c:url value='/temp/'/>" + json.img + "'/>");
                            }
                            });
                });
            </script>
        </c:if>
    </div>
    <div id="printServiceSelectDialog" title="Select a Printer"></div>
</div>
<div>
    <table class="in">
        <tr>
            <td class="h">Plate ID:</td>
            <td>
                <c:choose>
                    <c:when test="${plate.id != 0}">${plate.id}</c:when>
                    <c:otherwise><i>Unsaved</i></c:otherwise>
                </c:choose>
            </td>
        </tr>
        <tr>
            <td>Description:</td>
            <td>
                <div class="input-group">
                    <form:input path="description" class="form-control"/>
                    <span id="descriptioncounter" class="input-group-addon"></span>
                </div>
            </td>
        </tr>
        <tr>
            <td>Creation Date:</td>
            <td>
                <form:input path="creationDate" id="creationdatepicker" class="form-control"/>
                <script type="text/javascript">
                    Utils.ui.addMaxDatePicker("creationdatepicker", 0);
                </script>
            </td>
        </tr>
        <tr>
            <td>Size:</td>
            <td>
                <form:input path="size" id="size" class="form-control"/>
            </td>
        </tr>
        <tr>
            <c:choose>
                <c:when test="${plate.id == 0 or empty plate.plateMaterialType}">
                    <td>Plate Material Type:</td>
                    <td>
                        <form:radiobuttons id="plateMaterialType" path="plateMaterialType"/>
                    </td>
                </c:when>
                <c:otherwise>
                    <td>Plate Material Type</td>
                    <td>${plate.plateMaterialType}</td>
                </c:otherwise>
            </c:choose>
        </tr>
    </table>
    </form:form>

    <a name="plate_elements"></a>


    <nav class="navbar navbar-default" role="navigation">
        <div class="navbar-header">
      <span class="navbar-brand navbar-center">
       Elements
      </span>
        </div>
        <%--<div class="navbar-right container-fluid">--%>
        <%--<button class="btn btn-default navbar-btn" id="saveElements"--%>
        <%--onclick="Plate.ui.saveElements('${plate.id}');">Save elements--%>
        <%--</button>--%>
        <%--</div>--%>
    </nav>

    <br/>


    <c:choose>
    <c:when test="${plate.id == 0}">
        <i>Please save plate info above first, you can then populate elements...</i>
    </c:when>
    <c:otherwise>

    <br/>
    <br/>

    <div id="plateformholder">

        <form id="elementsForm">

        </form>
        <br/>

            <%--<button type="button" id="createPoolButton" onClick="addBulkSamples();"--%>
            <%--class="btn btn-default navbar-btn">Add Selected Samples--%>
            <%--</button>--%>
        <table cellpadding="0" cellspacing="0" border="0" class="table table-striped table-bordered display"
               id="sampleSelectionTable">
        </table>
    </div>
</div>

<script type="text/javascript">
    var selected = [];

    jQuery(document).ready(function () {
        Plate.ui.createPlateElementsUI('${plate.id}', '${plate.size}');
        Plate.ui.createSampleSelectionTable();
        Plate.ui.makeElementDraggable();
        jQuery(".plateWell").droppable({
                                           accept: '#formbox div',
                                           hoverClass: 'hovered',
                                           drop: handleDropEvent
                                       });
//        jQuery(".plateWell").on("mousedown", function () {
//            jQuery(this).droppable("option", "disabled", true);
//            jQuery(this).css("background-color","#F78181");
//
//        });
        jQuery(".plateWell").tooltip({
                                         animated: 'fade',
                                         placement: 'bottom',
                                         html : true
                                     });
        jQuery("[data-toggle='popover']").popover({
                                         html : true
                                     });
    });

    function addBulkSamples() {
        if (confirm("Are you sure you want to add selected samples?")) {
            for (var i = 0; i < selected.length; i++) {
                var sample = selected[i][0];
                var project = selected[i][1];
                Plate.ui.insertSampleNextAvailable(sample, project);
            }
            selectnorow();
        }
    }

    function handleDropEvent(event, ui) {

        ui.draggable.position({ of: jQuery(this), my: 'left top', at: 'left top' });

        var element = ui.draggable.parent().parent().html();
        console.info(element);
        jQuery(this).html(element);
        var dragFrom = ui.draggable.parent();
        dragFrom.html('');
        dragFrom.droppable("option", "disabled", false);
        jQuery(this).find(".plateElement").position({ of: jQuery(this), my: 'left top', at: 'left top' });
        jQuery(this).droppable("option", "disabled", true);

        Plate.ui.makeElementDraggable();
    }

    function selectallrows() {
        jQuery.each(dilutions, function (index, value) {
            if (jQuery.inArray(value.id, ldselected) == -1) {
                ldselected[ldselected.length++] = value.id;
            }
        });
        jQuery.each(jQuery('#dlTable tbody tr'), function (index, value) {
            jQuery(this).addClass('row_selected');
        });
    }

    function selectnorow() {
        selected = [];
        jQuery.each(jQuery('#sampleSelectionTable tbody tr'), function (index, value) {
            jQuery(this).removeClass('row_selected');
        });
    }
</script>


<style>
    .plateElement {
        width: 20px;
        height: 20px;
        background: lawngreen;
        border-radius: 50%;
        text-align: right;
        z-index: 998;
    }

    .plateWell {
        border-radius: 50%;
        border: 1px solid grey;
        margin-right: 10px;
        height: 20px;
        width: 20px;
        z-index: 997;
    }

    .plateElementText {

    }

    .move-right {
        margin-right: -10px;
    }

    div.hovered {
        background: #aaa;
    }

    .ui-state-disabled, .ui-widget-content .ui-state-disabled, .ui-widget-header .ui-state-disabled {
        opacity: 1 ! important;
    }
</style>
</c:otherwise>
</c:choose>

</div>


<%--sturcture holder--%>

<div id="plate96structure" style="display: none;">
<div id="formbox" style="border:1px solid grey;width:620px;height:480px;" class="ui-corner-all">
<div id="column1" class="exportcolumn">
    A1<br/>

    <div class="plateWell" id="A1"></div>
    <br/>
    B1<br/>

    <div class="plateWell" id="B1"></div>
    <br/>
    C1<br/>

    <div class="plateWell" id="C1"></div>
    <br/>
    D1<br/>

    <div class="plateWell" id="D1"></div>
    <br/>
    E1<br/>

    <div class="plateWell" id="E1"></div>
    <br/>
    F1<br/>

    <div class="plateWell" id="F1"></div>
    <br/>
    G1<br/>

    <div class="plateWell" id="G1"></div>
    <br/>
    H1<br/>

    <div class="plateWell" id="H1"></div>
    <br/>
</div>
<div id="column2" class="exportcolumn">
    A2<br/>

    <div class="plateWell" id="A2"></div>
    <br/>
    B2<br/>

    <div class="plateWell" id="B2"></div>
    <br/>
    C2<br/>

    <div class="plateWell" id="C2"></div>
    <br/>
    D2<br/>

    <div class="plateWell" id="D2"></div>
    <br/>
    E2<br/>

    <div class="plateWell" id="E2"></div>
    <br/>
    F2<br/>

    <div class="plateWell" id="F2"></div>
    <br/>
    G2<br/>

    <div class="plateWell" id="G2"></div>
    <br/>
    H2<br/>

    <div class="plateWell" id="H2"></div>
    <br/>
</div>
<div id="column3" class="exportcolumn">
    A3<br/>

    <div class="plateWell" id="A3"></div>
    <br/>
    B3<br/>

    <div class="plateWell" id="B3"></div>
    <br/>
    C3<br/>

    <div class="plateWell" id="C3"></div>
    <br/>
    D3<br/>

    <div class="plateWell" id="D3"></div>
    <br/>
    E3<br/>

    <div class="plateWell" id="E3"></div>
    <br/>
    F3<br/>

    <div class="plateWell" id="F3"></div>
    <br/>
    G3<br/>

    <div class="plateWell" id="G3"></div>
    <br/>
    H3<br/>

    <div class="plateWell" id="H3"></div>
    <br/>
</div>
<div id="column4" class="exportcolumn">
    A4<br/>

    <div class="plateWell" id="A4"></div>
    <br/>
    B4<br/>

    <div class="plateWell" id="B4"></div>
    <br/>
    C4<br/>

    <div class="plateWell" id="C4"></div>
    <br/>
    D4<br/>

    <div class="plateWell" id="D4"></div>
    <br/>
    E4<br/>

    <div class="plateWell" id="E4"></div>
    <br/>
    F4<br/>

    <div class="plateWell" id="F4"></div>
    <br/>
    G4<br/>

    <div class="plateWell" id="G4"></div>
    <br/>
    H4<br/>

    <div class="plateWell" id="H4"></div>
    <br/>
</div>
<div id="column5" class="exportcolumn">
    A5<br/>

    <div class="plateWell" id="A5"></div>
    <br/>
    B5<br/>

    <div class="plateWell" id="B5"></div>
    <br/>
    C5<br/>

    <div class="plateWell" id="C5"></div>
    <br/>
    D5<br/>

    <div class="plateWell" id="D5"></div>
    <br/>
    E5<br/>

    <div class="plateWell" id="E5"></div>
    <br/>
    F5<br/>

    <div class="plateWell" id="F5"></div>
    <br/>
    G5<br/>

    <div class="plateWell" id="G5"></div>
    <br/>
    H5<br/>

    <div class="plateWell" id="H5"></div>
    <br/>
</div>
<div id="column6" class="exportcolumn">
    A6<br/>

    <div class="plateWell" id="A6"></div>
    <br/>
    B6<br/>

    <div class="plateWell" id="B6"></div>
    <br/>
    C6<br/>

    <div class="plateWell" id="C6"></div>
    <br/>
    D6<br/>

    <div class="plateWell" id="D6"></div>
    <br/>
    E6<br/>

    <div class="plateWell" id="E6"></div>
    <br/>
    F6<br/>

    <div class="plateWell" id="F6"></div>
    <br/>
    G6<br/>

    <div class="plateWell" id="G6"></div>
    <br/>
    H6<br/>

    <div class="plateWell" id="H6"></div>
    <br/>
</div>
<div id="column7" class="exportcolumn">
    A7<br/>

    <div class="plateWell" id="A7"></div>
    <br/>
    B7<br/>

    <div class="plateWell" id="B7"></div>
    <br/>
    C7<br/>

    <div class="plateWell" id="C7"></div>
    <br/>
    D7<br/>

    <div class="plateWell" id="D7"></div>
    <br/>
    E7<br/>

    <div class="plateWell" id="E7"></div>
    <br/>
    F7<br/>

    <div class="plateWell" id="F7"></div>
    <br/>
    G7<br/>

    <div class="plateWell" id="G7"></div>
    <br/>
    H7<br/>

    <div class="plateWell" id="H7"></div>
    <br/>
</div>
<div id="column8" class="exportcolumn">
    A8<br/>

    <div class="plateWell" id="A8"></div>
    <br/>
    B8<br/>

    <div class="plateWell" id="B8"></div>
    <br/>
    C8<br/>

    <div class="plateWell" id="C8"></div>
    <br/>
    D8<br/>

    <div class="plateWell" id="D8"></div>
    <br/>
    E8<br/>

    <div class="plateWell" id="E8"></div>
    <br/>
    F8<br/>

    <div class="plateWell" id="F8"></div>
    <br/>
    G8<br/>

    <div class="plateWell" id="G8"></div>
    <br/>
    H8<br/>

    <div class="plateWell" id="H8"></div>
    <br/>
</div>
<div id="column9" class="exportcolumn">
    A9<br/>

    <div class="plateWell" id="A9"></div>
    <br/>
    B9<br/>

    <div class="plateWell" id="B9"></div>
    <br/>
    C9<br/>

    <div class="plateWell" id="C9"></div>
    <br/>
    D9<br/>

    <div class="plateWell" id="D9"></div>
    <br/>
    E9<br/>

    <div class="plateWell" id="E9"></div>
    <br/>
    F9<br/>

    <div class="plateWell" id="F9"></div>
    <br/>
    G9<br/>

    <div class="plateWell" id="G9"></div>
    <br/>
    H9<br/>

    <div class="plateWell" id="H9"></div>
    <br/>
</div>
<div id="column10" class="exportcolumn">
    A10<br/>

    <div class="plateWell" id="A10"></div>
    <br/>
    B10<br/>

    <div class="plateWell" id="B10"></div>
    <br/>
    C10<br/>

    <div class="plateWell" id="C10"></div>
    <br/>
    D10<br/>

    <div class="plateWell" id="D10"></div>
    <br/>
    E10<br/>

    <div class="plateWell" id="E10"></div>
    <br/>
    F10<br/>

    <div class="plateWell" id="F10"></div>
    <br/>
    G10<br/>

    <div class="plateWell" id="G10"></div>
    <br/>
    H10<br/>

    <div class="plateWell" id="H10"></div>
    <br/>
</div>
<div id="column11" class="exportcolumn">
    A11<br/>

    <div class="plateWell" id="A11"></div>
    <br/>
    B11<br/>

    <div class="plateWell" id="B11"></div>
    <br/>
    C11<br/>

    <div class="plateWell" id="C11"></div>
    <br/>
    D11<br/>

    <div class="plateWell" id="D11"></div>
    <br/>
    E11<br/>

    <div class="plateWell" id="E11"></div>
    <br/>
    F11<br/>

    <div class="plateWell" id="F11"></div>
    <br/>
    G11<br/>

    <div class="plateWell" id="G11"></div>
    <br/>
    H11<br/>

    <div class="plateWell" id="H11"></div>
    <br/>
</div>
<div id="column12" class="exportcolumn" style="border: 0px ! important;">
    A12<br/>

    <div class="plateWell" id="A12"></div>
    <br/>
    B12<br/>

    <div class="plateWell" id="B12"></div>
    <br/>
    C12<br/>

    <div class="plateWell" id="C12"></div>
    <br/>
    D12<br/>

    <div class="plateWell" id="D12"></div>
    <br/>
    E12<br/>

    <div class="plateWell" id="E12"></div>
    <br/>
    F12<br/>

    <div class="plateWell" id="F12"></div>
    <br/>
    G12<br/>

    <div class="plateWell" id="G12"></div>
    <br/>
    H12<br/>

    <div class="plateWell" id="H12"></div>
    <br/>
</div>
</div>
</div>


<div id="plate384structure" style="display: none;">
<div id="formbox384" style="border:1px solid grey;width:100%;height:1290px;overflow-x: scroll;" class="ui-corner-all">
<div style="width:1730px;height:1290px;">
<div class="exportcolumn">
    A1<br/>

    <div class="plateWell" id="A1"></div>
    <br/>
    B1<br/>

    <div class="plateWell" id="B1"></div>
    <br/>
    C1<br/>

    <div class="plateWell" id="C1"></div>
    <br/>
    D1<br/>

    <div class="plateWell" id="D1"></div>
    <br/>
    E1<br/>

    <div class="plateWell" id="E1"></div>
    <br/>
    F1<br/>

    <div class="plateWell" id="F1"></div>
    <br/>
    G1<br/>

    <div class="plateWell" id="G1"></div>
    <br/>
    H1<br/>

    <div class="plateWell" id="H1"></div>
    <br/>
    I1<br/>

    <div class="plateWell" id="I1"></div>
    <br/>
    J1<br/>

    <div class="plateWell" id="J1"></div>
    <br/>
    K1<br/>

    <div class="plateWell" id="K1"></div>
    <br/>
    L1<br/>

    <div class="plateWell" id="L1"></div>
    <br/>
    M1<br/>

    <div class="plateWell" id="M1"></div>
    <br/>
    N1<br/>

    <div class="plateWell" id="N1"></div>
    <br/>
    O1<br/>

    <div class="plateWell" id="O1"></div>
    <br/>
    P1<br/>

    <div class="plateWell" id="P1"></div>
    <br/>
</div>
<div class="exportcolumn">
    A2<br/>

    <div class="plateWell" id="A2"></div>
    <br/>
    B2<br/>

    <div class="plateWell" id="B2"></div>
    <br/>
    C2<br/>

    <div class="plateWell" id="C2"></div>
    <br/>
    D2<br/>

    <div class="plateWell" id="D2"></div>
    <br/>
    E2<br/>

    <div class="plateWell" id="E2"></div>
    <br/>
    F2<br/>

    <div class="plateWell" id="F2"></div>
    <br/>
    G2<br/>

    <div class="plateWell" id="G2"></div>
    <br/>
    H2<br/>

    <div class="plateWell" id="H2"></div>
    <br/>
    I2<br/>

    <div class="plateWell" id="I2"></div>
    <br/>
    J2<br/>

    <div class="plateWell" id="J2"></div>
    <br/>
    K2<br/>

    <div class="plateWell" id="K2"></div>
    <br/>
    L2<br/>

    <div class="plateWell" id="L2"></div>
    <br/>
    M2<br/>

    <div class="plateWell" id="M2"></div>
    <br/>
    N2<br/>

    <div class="plateWell" id="N2"></div>
    <br/>
    O2<br/>

    <div class="plateWell" id="O2"></div>
    <br/>
    P2<br/>

    <div class="plateWell" id="P2"></div>
    <br/>
</div>
<div class="exportcolumn">
    A3<br/>

    <div class="plateWell" id="A3"></div>
    <br/>
    B3<br/>

    <div class="plateWell" id="B3"></div>
    <br/>
    C3<br/>

    <div class="plateWell" id="C3"></div>
    <br/>
    D3<br/>

    <div class="plateWell" id="D3"></div>
    <br/>
    E3<br/>

    <div class="plateWell" id="E3"></div>
    <br/>
    F3<br/>

    <div class="plateWell" id="F3"></div>
    <br/>
    G3<br/>

    <div class="plateWell" id="G3"></div>
    <br/>
    H3<br/>

    <div class="plateWell" id="H3"></div>
    <br/>
    I3<br/>

    <div class="plateWell" id="I3"></div>
    <br/>
    J3<br/>

    <div class="plateWell" id="J3"></div>
    <br/>
    K3<br/>

    <div class="plateWell" id="K3"></div>
    <br/>
    L3<br/>

    <div class="plateWell" id="L3"></div>
    <br/>
    M3<br/>

    <div class="plateWell" id="M3"></div>
    <br/>
    N3<br/>

    <div class="plateWell" id="N3"></div>
    <br/>
    O3<br/>

    <div class="plateWell" id="O3"></div>
    <br/>
    P3<br/>

    <div class="plateWell" id="P3"></div>
    <br/>
</div>
<div class="exportcolumn">
    A4<br/>

    <div class="plateWell" id="A4"></div>
    <br/>
    B4<br/>

    <div class="plateWell" id="B4"></div>
    <br/>
    C4<br/>

    <div class="plateWell" id="C4"></div>
    <br/>
    D4<br/>

    <div class="plateWell" id="D4"></div>
    <br/>
    E4<br/>

    <div class="plateWell" id="E4"></div>
    <br/>
    F4<br/>

    <div class="plateWell" id="F4"></div>
    <br/>
    G4<br/>

    <div class="plateWell" id="G4"></div>
    <br/>
    H4<br/>

    <div class="plateWell" id="H4"></div>
    <br/>
    I4<br/>

    <div class="plateWell" id="I4"></div>
    <br/>
    J4<br/>

    <div class="plateWell" id="J4"></div>
    <br/>
    K4<br/>

    <div class="plateWell" id="K4"></div>
    <br/>
    L4<br/>

    <div class="plateWell" id="L4"></div>
    <br/>
    M4<br/>

    <div class="plateWell" id="M4"></div>
    <br/>
    N4<br/>

    <div class="plateWell" id="N4"></div>
    <br/>
    O4<br/>

    <div class="plateWell" id="O4"></div>
    <br/>
    P4<br/>

    <div class="plateWell" id="P4"></div>
    <br/>
</div>
<div class="exportcolumn">
    A5<br/>

    <div class="plateWell" id="A5"></div>
    <br/>
    B5<br/>

    <div class="plateWell" id="B5"></div>
    <br/>
    C5<br/>

    <div class="plateWell" id="C5"></div>
    <br/>
    D5<br/>

    <div class="plateWell" id="D5"></div>
    <br/>
    E5<br/>

    <div class="plateWell" id="E5"></div>
    <br/>
    F5<br/>

    <div class="plateWell" id="F5"></div>
    <br/>
    G5<br/>

    <div class="plateWell" id="G5"></div>
    <br/>
    H5<br/>

    <div class="plateWell" id="H5"></div>
    <br/>
    I5<br/>

    <div class="plateWell" id="I5"></div>
    <br/>
    J5<br/>

    <div class="plateWell" id="J5"></div>
    <br/>
    K5<br/>

    <div class="plateWell" id="K5"></div>
    <br/>
    L5<br/>

    <div class="plateWell" id="L5"></div>
    <br/>
    M5<br/>

    <div class="plateWell" id="M5"></div>
    <br/>
    N5<br/>

    <div class="plateWell" id="N5"></div>
    <br/>
    O5<br/>

    <div class="plateWell" id="O5"></div>
    <br/>
    P5<br/>

    <div class="plateWell" id="P5"></div>
    <br/>
</div>
<div class="exportcolumn">
    A6<br/>

    <div class="plateWell" id="A6"></div>
    <br/>
    B6<br/>

    <div class="plateWell" id="B6"></div>
    <br/>
    C6<br/>

    <div class="plateWell" id="C6"></div>
    <br/>
    D6<br/>

    <div class="plateWell" id="D6"></div>
    <br/>
    E6<br/>

    <div class="plateWell" id="E6"></div>
    <br/>
    F6<br/>

    <div class="plateWell" id="F6"></div>
    <br/>
    G6<br/>

    <div class="plateWell" id="G6"></div>
    <br/>
    H6<br/>

    <div class="plateWell" id="H6"></div>
    <br/>
    I6<br/>

    <div class="plateWell" id="I6"></div>
    <br/>
    J6<br/>

    <div class="plateWell" id="J6"></div>
    <br/>
    K6<br/>

    <div class="plateWell" id="K6"></div>
    <br/>
    L6<br/>

    <div class="plateWell" id="L6"></div>
    <br/>
    M6<br/>

    <div class="plateWell" id="M6"></div>
    <br/>
    N6<br/>

    <div class="plateWell" id="N6"></div>
    <br/>
    O6<br/>

    <div class="plateWell" id="O6"></div>
    <br/>
    P6<br/>

    <div class="plateWell" id="P6"></div>
    <br/>
</div>
<div class="exportcolumn">
    A7<br/>

    <div class="plateWell" id="A7"></div>
    <br/>
    B7<br/>

    <div class="plateWell" id="B7"></div>
    <br/>
    C7<br/>

    <div class="plateWell" id="C7"></div>
    <br/>
    D7<br/>

    <div class="plateWell" id="D7"></div>
    <br/>
    E7<br/>

    <div class="plateWell" id="E7"></div>
    <br/>
    F7<br/>

    <div class="plateWell" id="F7"></div>
    <br/>
    G7<br/>

    <div class="plateWell" id="G7"></div>
    <br/>
    H7<br/>

    <div class="plateWell" id="H7"></div>
    <br/>
    I7<br/>

    <div class="plateWell" id="I7"></div>
    <br/>
    J7<br/>

    <div class="plateWell" id="J7"></div>
    <br/>
    K7<br/>

    <div class="plateWell" id="K7"></div>
    <br/>
    L7<br/>

    <div class="plateWell" id="L7"></div>
    <br/>
    M7<br/>

    <div class="plateWell" id="M7"></div>
    <br/>
    N7<br/>

    <div class="plateWell" id="N7"></div>
    <br/>
    O7<br/>

    <div class="plateWell" id="O7"></div>
    <br/>
    P7<br/>

    <div class="plateWell" id="P7"></div>
    <br/>
</div>
<div class="exportcolumn">
    A8<br/>

    <div class="plateWell" id="A8"></div>
    <br/>
    B8<br/>

    <div class="plateWell" id="B8"></div>
    <br/>
    C8<br/>

    <div class="plateWell" id="C8"></div>
    <br/>
    D8<br/>

    <div class="plateWell" id="D8"></div>
    <br/>
    E8<br/>

    <div class="plateWell" id="E8"></div>
    <br/>
    F8<br/>

    <div class="plateWell" id="F8"></div>
    <br/>
    G8<br/>

    <div class="plateWell" id="G8"></div>
    <br/>
    H8<br/>

    <div class="plateWell" id="H8"></div>
    <br/>
    I8<br/>

    <div class="plateWell" id="I8"></div>
    <br/>
    J8<br/>

    <div class="plateWell" id="J8"></div>
    <br/>
    K8<br/>

    <div class="plateWell" id="K8"></div>
    <br/>
    L8<br/>

    <div class="plateWell" id="L8"></div>
    <br/>
    M8<br/>

    <div class="plateWell" id="M8"></div>
    <br/>
    N8<br/>

    <div class="plateWell" id="N8"></div>
    <br/>
    O8<br/>

    <div class="plateWell" id="O8"></div>
    <br/>
    P8<br/>

    <div class="plateWell" id="P8"></div>
    <br/>
</div>
<div class="exportcolumn">
    A9<br/>

    <div class="plateWell" id="A9"></div>
    <br/>
    B9<br/>

    <div class="plateWell" id="B9"></div>
    <br/>
    C9<br/>

    <div class="plateWell" id="C9"></div>
    <br/>
    D9<br/>

    <div class="plateWell" id="D9"></div>
    <br/>
    E9<br/>

    <div class="plateWell" id="E9"></div>
    <br/>
    F9<br/>

    <div class="plateWell" id="F9"></div>
    <br/>
    G9<br/>

    <div class="plateWell" id="G9"></div>
    <br/>
    H9<br/>

    <div class="plateWell" id="H9"></div>
    <br/>
    I9<br/>

    <div class="plateWell" id="I9"></div>
    <br/>
    J9<br/>

    <div class="plateWell" id="J9"></div>
    <br/>
    K9<br/>

    <div class="plateWell" id="K9"></div>
    <br/>
    L9<br/>

    <div class="plateWell" id="L9"></div>
    <br/>
    M9<br/>

    <div class="plateWell" id="M9"></div>
    <br/>
    N9<br/>

    <div class="plateWell" id="N9"></div>
    <br/>
    O9<br/>

    <div class="plateWell" id="O9"></div>
    <br/>
    P9<br/>

    <div class="plateWell" id="P9"></div>
    <br/>
</div>
<div class="exportcolumn">
    A10<br/>

    <div class="plateWell" id="A10"></div>
    <br/>
    B10<br/>

    <div class="plateWell" id="B10"></div>
    <br/>
    C10<br/>

    <div class="plateWell" id="C10"></div>
    <br/>
    D10<br/>

    <div class="plateWell" id="D10"></div>
    <br/>
    E10<br/>

    <div class="plateWell" id="E10"></div>
    <br/>
    F10<br/>

    <div class="plateWell" id="F10"></div>
    <br/>
    G10<br/>

    <div class="plateWell" id="G10"></div>
    <br/>
    H10<br/>

    <div class="plateWell" id="H10"></div>
    <br/>
    I10<br/>

    <div class="plateWell" id="I10"></div>
    <br/>
    J10<br/>

    <div class="plateWell" id="J10"></div>
    <br/>
    K10<br/>

    <div class="plateWell" id="K10"></div>
    <br/>
    L10<br/>

    <div class="plateWell" id="L10"></div>
    <br/>
    M10<br/>

    <div class="plateWell" id="M10"></div>
    <br/>
    N10<br/>

    <div class="plateWell" id="N10"></div>
    <br/>
    O10<br/>

    <div class="plateWell" id="O10"></div>
    <br/>
    P10<br/>

    <div class="plateWell" id="P10"></div>
    <br/>
</div>
<div class="exportcolumn">
    A11<br/>

    <div class="plateWell" id="A11"></div>
    <br/>
    B11<br/>

    <div class="plateWell" id="B11"></div>
    <br/>
    C11<br/>

    <div class="plateWell" id="C11"></div>
    <br/>
    D11<br/>

    <div class="plateWell" id="D11"></div>
    <br/>
    E11<br/>

    <div class="plateWell" id="E11"></div>
    <br/>
    F11<br/>

    <div class="plateWell" id="F11"></div>
    <br/>
    G11<br/>

    <div class="plateWell" id="G11"></div>
    <br/>
    H11<br/>

    <div class="plateWell" id="H11"></div>
    <br/>
    I11<br/>

    <div class="plateWell" id="I11"></div>
    <br/>
    J11<br/>

    <div class="plateWell" id="J11"></div>
    <br/>
    K11<br/>

    <div class="plateWell" id="K11"></div>
    <br/>
    L11<br/>

    <div class="plateWell" id="L11"></div>
    <br/>
    M11<br/>

    <div class="plateWell" id="M11"></div>
    <br/>
    N11<br/>

    <div class="plateWell" id="N11"></div>
    <br/>
    O11<br/>

    <div class="plateWell" id="O11"></div>
    <br/>
    P11<br/>

    <div class="plateWell" id="P11"></div>
    <br/>
</div>
<div class="exportcolumn">
    A12<br/>

    <div class="plateWell" id="A12"></div>
    <br/>
    B12<br/>

    <div class="plateWell" id="B12"></div>
    <br/>
    C12<br/>

    <div class="plateWell" id="C12"></div>
    <br/>
    D12<br/>

    <div class="plateWell" id="D12"></div>
    <br/>
    E12<br/>

    <div class="plateWell" id="E12"></div>
    <br/>
    F12<br/>

    <div class="plateWell" id="F12"></div>
    <br/>
    G12<br/>

    <div class="plateWell" id="G12"></div>
    <br/>
    H12<br/>

    <div class="plateWell" id="H12"></div>
    <br/>
    I12<br/>

    <div class="plateWell" id="I12"></div>
    <br/>
    J12<br/>

    <div class="plateWell" id="J12"></div>
    <br/>
    K12<br/>

    <div class="plateWell" id="K12"></div>
    <br/>
    L12<br/>

    <div class="plateWell" id="L12"></div>
    <br/>
    M12<br/>

    <div class="plateWell" id="M12"></div>
    <br/>
    N12<br/>

    <div class="plateWell" id="N12"></div>
    <br/>
    O12<br/>

    <div class="plateWell" id="O12"></div>
    <br/>
    P12<br/>

    <div class="plateWell" id="P12"></div>
    <br/>
</div>

<div class="exportcolumn">
    A13<br/>

    <div class="plateWell" id="A13"></div>
    <br/>
    B13<br/>

    <div class="plateWell" id="B13"></div>
    <br/>
    C13<br/>

    <div class="plateWell" id="C13"></div>
    <br/>
    D13<br/>

    <div class="plateWell" id="D13"></div>
    <br/>
    E13<br/>

    <div class="plateWell" id="E13"></div>
    <br/>
    F13<br/>

    <div class="plateWell" id="F13"></div>
    <br/>
    G13<br/>

    <div class="plateWell" id="G13"></div>
    <br/>
    H13<br/>

    <div class="plateWell" id="H13"></div>
    <br/>
    I13<br/>

    <div class="plateWell" id="I13"></div>
    <br/>
    J13<br/>

    <div class="plateWell" id="J13"></div>
    <br/>
    K13<br/>

    <div class="plateWell" id="K13"></div>
    <br/>
    L13<br/>

    <div class="plateWell" id="L13"></div>
    <br/>
    M13<br/>

    <div class="plateWell" id="M13"></div>
    <br/>
    N13<br/>

    <div class="plateWell" id="N13"></div>
    <br/>
    O13<br/>

    <div class="plateWell" id="O13"></div>
    <br/>
    P13<br/>

    <div class="plateWell" id="P13"></div>
    <br/>
</div>

<div class="exportcolumn">
    A14<br/>

    <div class="plateWell" id="A14"></div>
    <br/>
    B14<br/>

    <div class="plateWell" id="B14"></div>
    <br/>
    C14<br/>

    <div class="plateWell" id="C14"></div>
    <br/>
    D14<br/>

    <div class="plateWell" id="D14"></div>
    <br/>
    E14<br/>

    <div class="plateWell" id="E14"></div>
    <br/>
    F14<br/>

    <div class="plateWell" id="F14"></div>
    <br/>
    G14<br/>

    <div class="plateWell" id="G14"></div>
    <br/>
    H14<br/>

    <div class="plateWell" id="H14"></div>
    <br/>
    I14<br/>

    <div class="plateWell" id="I14"></div>
    <br/>
    J14<br/>

    <div class="plateWell" id="J14"></div>
    <br/>
    K14<br/>

    <div class="plateWell" id="K14"></div>
    <br/>
    L14<br/>

    <div class="plateWell" id="L14"></div>
    <br/>
    M14<br/>

    <div class="plateWell" id="M14"></div>
    <br/>
    N14<br/>

    <div class="plateWell" id="N14"></div>
    <br/>
    O14<br/>

    <div class="plateWell" id="O14"></div>
    <br/>
    P14<br/>

    <div class="plateWell" id="P14"></div>
    <br/>
</div>

<div class="exportcolumn">
    A15<br/>

    <div class="plateWell" id="A15"></div>
    <br/>
    B15<br/>

    <div class="plateWell" id="B15"></div>
    <br/>
    C15<br/>

    <div class="plateWell" id="C15"></div>
    <br/>
    D15<br/>

    <div class="plateWell" id="D15"></div>
    <br/>
    E15<br/>

    <div class="plateWell" id="E15"></div>
    <br/>
    F15<br/>

    <div class="plateWell" id="F15"></div>
    <br/>
    G15<br/>

    <div class="plateWell" id="G15"></div>
    <br/>
    H15<br/>

    <div class="plateWell" id="H15"></div>
    <br/>
    I15<br/>

    <div class="plateWell" id="I15"></div>
    <br/>
    J15<br/>

    <div class="plateWell" id="J15"></div>
    <br/>
    K15<br/>

    <div class="plateWell" id="K15"></div>
    <br/>
    L15<br/>

    <div class="plateWell" id="L15"></div>
    <br/>
    M15<br/>

    <div class="plateWell" id="M15"></div>
    <br/>
    N15<br/>

    <div class="plateWell" id="N15"></div>
    <br/>
    O15<br/>

    <div class="plateWell" id="O15"></div>
    <br/>
    P15<br/>

    <div class="plateWell" id="P15"></div>
    <br/>
</div>


<div class="exportcolumn">
    A16<br/>

    <div class="plateWell" id="A16"></div>
    <br/>
    B16<br/>

    <div class="plateWell" id="B16"></div>
    <br/>
    C16<br/>

    <div class="plateWell" id="C16"></div>
    <br/>
    D16<br/>

    <div class="plateWell" id="D16"></div>
    <br/>
    E16<br/>

    <div class="plateWell" id="E16"></div>
    <br/>
    F16<br/>

    <div class="plateWell" id="F16"></div>
    <br/>
    G16<br/>

    <div class="plateWell" id="G16"></div>
    <br/>
    H16<br/>

    <div class="plateWell" id="H16"></div>
    <br/>
    I16<br/>

    <div class="plateWell" id="I16"></div>
    <br/>
    J16<br/>

    <div class="plateWell" id="J16"></div>
    <br/>
    K16<br/>

    <div class="plateWell" id="K16"></div>
    <br/>
    L16<br/>

    <div class="plateWell" id="L16"></div>
    <br/>
    M16<br/>

    <div class="plateWell" id="M16"></div>
    <br/>
    N16<br/>

    <div class="plateWell" id="N16"></div>
    <br/>
    O16<br/>

    <div class="plateWell" id="O16"></div>
    <br/>
    P16<br/>

    <div class="plateWell" id="P16"></div>
    <br/>
</div>


<div class="exportcolumn">
    A17<br/>

    <div class="plateWell" id="A17"></div>
    <br/>
    B17<br/>

    <div class="plateWell" id="B17"></div>
    <br/>
    C17<br/>

    <div class="plateWell" id="C17"></div>
    <br/>
    D17<br/>

    <div class="plateWell" id="D17"></div>
    <br/>
    E17<br/>

    <div class="plateWell" id="E17"></div>
    <br/>
    F17<br/>

    <div class="plateWell" id="F17"></div>
    <br/>
    G17<br/>

    <div class="plateWell" id="G17"></div>
    <br/>
    H17<br/>

    <div class="plateWell" id="H17"></div>
    <br/>
    I17<br/>

    <div class="plateWell" id="I17"></div>
    <br/>
    J17<br/>

    <div class="plateWell" id="J17"></div>
    <br/>
    K17<br/>

    <div class="plateWell" id="K17"></div>
    <br/>
    L17<br/>

    <div class="plateWell" id="L17"></div>
    <br/>
    M17<br/>

    <div class="plateWell" id="M17"></div>
    <br/>
    N17<br/>

    <div class="plateWell" id="N17"></div>
    <br/>
    O17<br/>

    <div class="plateWell" id="O17"></div>
    <br/>
    P17<br/>

    <div class="plateWell" id="P17"></div>
    <br/>
</div>


<div class="exportcolumn">
    A18<br/>

    <div class="plateWell" id="A18"></div>
    <br/>
    B18<br/>

    <div class="plateWell" id="B18"></div>
    <br/>
    C18<br/>

    <div class="plateWell" id="C18"></div>
    <br/>
    D18<br/>

    <div class="plateWell" id="D18"></div>
    <br/>
    E18<br/>

    <div class="plateWell" id="E18"></div>
    <br/>
    F18<br/>

    <div class="plateWell" id="F18"></div>
    <br/>
    G18<br/>

    <div class="plateWell" id="G18"></div>
    <br/>
    H18<br/>

    <div class="plateWell" id="H18"></div>
    <br/>
    I18<br/>

    <div class="plateWell" id="I18"></div>
    <br/>
    J18<br/>

    <div class="plateWell" id="J18"></div>
    <br/>
    K18<br/>

    <div class="plateWell" id="K18"></div>
    <br/>
    L18<br/>

    <div class="plateWell" id="L18"></div>
    <br/>
    M18<br/>

    <div class="plateWell" id="M18"></div>
    <br/>
    N18<br/>

    <div class="plateWell" id="N18"></div>
    <br/>
    O18<br/>

    <div class="plateWell" id="O18"></div>
    <br/>
    P18<br/>

    <div class="plateWell" id="P18"></div>
    <br/>
</div>

<div class="exportcolumn">
    A19<br/>

    <div class="plateWell" id="A19"></div>
    <br/>
    B19<br/>

    <div class="plateWell" id="B19"></div>
    <br/>
    C19<br/>

    <div class="plateWell" id="C19"></div>
    <br/>
    D19<br/>

    <div class="plateWell" id="D19"></div>
    <br/>
    E19<br/>

    <div class="plateWell" id="E19"></div>
    <br/>
    F19<br/>

    <div class="plateWell" id="F19"></div>
    <br/>
    G19<br/>

    <div class="plateWell" id="G19"></div>
    <br/>
    H19<br/>

    <div class="plateWell" id="H19"></div>
    <br/>
    I19<br/>

    <div class="plateWell" id="I19"></div>
    <br/>
    J19<br/>

    <div class="plateWell" id="J19"></div>
    <br/>
    K19<br/>

    <div class="plateWell" id="K19"></div>
    <br/>
    L19<br/>

    <div class="plateWell" id="L19"></div>
    <br/>
    M19<br/>

    <div class="plateWell" id="M19"></div>
    <br/>
    N19<br/>

    <div class="plateWell" id="N19"></div>
    <br/>
    O19<br/>

    <div class="plateWell" id="O19"></div>
    <br/>
    P19<br/>

    <div class="plateWell" id="P19"></div>
    <br/>
</div>

<div class="exportcolumn">
    A20<br/>

    <div class="plateWell" id="A20"></div>
    <br/>
    B20<br/>

    <div class="plateWell" id="B20"></div>
    <br/>
    C20<br/>

    <div class="plateWell" id="C20"></div>
    <br/>
    D20<br/>

    <div class="plateWell" id="D20"></div>
    <br/>
    E20<br/>

    <div class="plateWell" id="E20"></div>
    <br/>
    F20<br/>

    <div class="plateWell" id="F20"></div>
    <br/>
    G20<br/>

    <div class="plateWell" id="G20"></div>
    <br/>
    H20<br/>

    <div class="plateWell" id="H20"></div>
    <br/>
    I20<br/>

    <div class="plateWell" id="I20"></div>
    <br/>
    J20<br/>

    <div class="plateWell" id="J20"></div>
    <br/>
    K20<br/>

    <div class="plateWell" id="K20"></div>
    <br/>
    L20<br/>

    <div class="plateWell" id="L20"></div>
    <br/>
    M20<br/>

    <div class="plateWell" id="M20"></div>
    <br/>
    N20<br/>

    <div class="plateWell" id="N20"></div>
    <br/>
    O20<br/>

    <div class="plateWell" id="O20"></div>
    <br/>
    P20<br/>

    <div class="plateWell" id="P20"></div>
    <br/>
</div>


<div class="exportcolumn">
    A21<br/>

    <div class="plateWell" id="A21"></div>
    <br/>
    B21<br/>

    <div class="plateWell" id="B21"></div>
    <br/>
    C21<br/>

    <div class="plateWell" id="C21"></div>
    <br/>
    D21<br/>

    <div class="plateWell" id="D21"></div>
    <br/>
    E21<br/>

    <div class="plateWell" id="E21"></div>
    <br/>
    F21<br/>

    <div class="plateWell" id="F21"></div>
    <br/>
    G21<br/>

    <div class="plateWell" id="G21"></div>
    <br/>
    H21<br/>

    <div class="plateWell" id="H21"></div>
    <br/>
    I21<br/>

    <div class="plateWell" id="I21"></div>
    <br/>
    J21<br/>

    <div class="plateWell" id="J21"></div>
    <br/>
    K21<br/>

    <div class="plateWell" id="K21"></div>
    <br/>
    L21<br/>

    <div class="plateWell" id="L21"></div>
    <br/>
    M21<br/>

    <div class="plateWell" id="M21"></div>
    <br/>
    N21<br/>

    <div class="plateWell" id="N21"></div>
    <br/>
    O21<br/>

    <div class="plateWell" id="O21"></div>
    <br/>
    P21<br/>

    <div class="plateWell" id="P21"></div>
    <br/>
</div>


<div class="exportcolumn">
    A22<br/>

    <div class="plateWell" id="A22"></div>
    <br/>
    B22<br/>

    <div class="plateWell" id="B22"></div>
    <br/>
    C22<br/>

    <div class="plateWell" id="C22"></div>
    <br/>
    D22<br/>

    <div class="plateWell" id="D22"></div>
    <br/>
    E22<br/>

    <div class="plateWell" id="E22"></div>
    <br/>
    F22<br/>

    <div class="plateWell" id="F22"></div>
    <br/>
    G22<br/>

    <div class="plateWell" id="G22"></div>
    <br/>
    H22<br/>

    <div class="plateWell" id="H22"></div>
    <br/>
    I22<br/>

    <div class="plateWell" id="I22"></div>
    <br/>
    J22<br/>

    <div class="plateWell" id="J22"></div>
    <br/>
    K22<br/>

    <div class="plateWell" id="K22"></div>
    <br/>
    L22<br/>

    <div class="plateWell" id="L22"></div>
    <br/>
    M22<br/>

    <div class="plateWell" id="M22"></div>
    <br/>
    N22<br/>

    <div class="plateWell" id="N22"></div>
    <br/>
    O22<br/>

    <div class="plateWell" id="O22"></div>
    <br/>
    P22<br/>

    <div class="plateWell" id="P22"></div>
    <br/>
</div>


<div class="exportcolumn">
    A23<br/>

    <div class="plateWell" id="A23"></div>
    <br/>
    B23<br/>

    <div class="plateWell" id="B23"></div>
    <br/>
    C23<br/>

    <div class="plateWell" id="C23"></div>
    <br/>
    D23<br/>

    <div class="plateWell" id="D23"></div>
    <br/>
    E23<br/>

    <div class="plateWell" id="E23"></div>
    <br/>
    F23<br/>

    <div class="plateWell" id="F23"></div>
    <br/>
    G23<br/>

    <div class="plateWell" id="G23"></div>
    <br/>
    H23<br/>

    <div class="plateWell" id="H23"></div>
    <br/>
    I23<br/>

    <div class="plateWell" id="I23"></div>
    <br/>
    J23<br/>

    <div class="plateWell" id="J23"></div>
    <br/>
    K23<br/>

    <div class="plateWell" id="K23"></div>
    <br/>
    L23<br/>

    <div class="plateWell" id="L23"></div>
    <br/>
    M23<br/>

    <div class="plateWell" id="M23"></div>
    <br/>
    N23<br/>

    <div class="plateWell" id="N23"></div>
    <br/>
    O23<br/>

    <div class="plateWell" id="O23"></div>
    <br/>
    P23<br/>

    <div class="plateWell" id="P23"></div>
    <br/>
</div>


<div class="exportcolumn" style="border: 0px ! important;">
    A24<br/>

    <div class="plateWell" id="A24"></div>
    <br/>
    B24<br/>

    <div class="plateWell" id="B24"></div>
    <br/>
    C24<br/>

    <div class="plateWell" id="C24"></div>
    <br/>
    D24<br/>

    <div class="plateWell" id="D24"></div>
    <br/>
    E24<br/>

    <div class="plateWell" id="E24"></div>
    <br/>
    F24<br/>

    <div class="plateWell" id="F24"></div>
    <br/>
    G24<br/>

    <div class="plateWell" id="G24"></div>
    <br/>
    H24<br/>

    <div class="plateWell" id="H24"></div>
    <br/>
    I24<br/>

    <div class="plateWell" id="I24"></div>
    <br/>
    J24<br/>

    <div class="plateWell" id="J24"></div>
    <br/>
    K24<br/>

    <div class="plateWell" id="K24"></div>
    <br/>
    L24<br/>

    <div class="plateWell" id="L24"></div>
    <br/>
    M24<br/>

    <div class="plateWell" id="M24"></div>
    <br/>
    N24<br/>

    <div class="plateWell" id="N24"></div>
    <br/>
    O24<br/>

    <div class="plateWell" id="O24"></div>
    <br/>
    P24<br/>

    <div class="plateWell" id="P24"></div>
    <br/>
</div>
</div>
</div>
</div>


</div>

<%@ include file="adminsub.jsp" %>

<%@ include file="../footer.jsp" %>