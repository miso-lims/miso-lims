<%--
  ~ Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
  ~ MISO project contacts: Robert Davey, Mario Caccamo @ TGAC
  ~ **********************************************************************
  ~
  ~ This file is part of MISO.
  ~
  ~ MISO is free software: you can redistribute it and/or modify
  ~ it under the terms of the GNU General Public License as published by
  ~ the Free Software Foundation, either version 5 of the License, or
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

<%--
  Created by IntelliJ IDEA.
  User: bianx

--%>
<%@ include file="../header.jsp" %>

<script type="text/javascript" src="<c:url value='/scripts/import_export_ajax.js?ts=${timestamp.time}'/>"></script>

<div id="maincontent">

<h1>
    Export Samples
</h1>


<div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#note_arrowclick'), 'notediv');">Quick Help
    <div id="note_arrowclick" class="toggleLeft"></div>
</div>
<div id="notediv" class="note" style="display:none;">help text
</div>


<div id="sample-box" style="float:right;">
    <h2>Available Samples</h2>

    <div align="right">
        Filter:
        <input id="searchSamples" type="text" name="searchSamples" size="8"/>
    </div>
    <div id="sampleList" class="elementList ui-corner-all" style="height:800px;width:200px;" align="right">Select sample
        sheet type on the left first...
    </div>

    <script type="text/javascript">
        Utils.timer.typewatchFunc(jQuery('#searchSamples'), function () {
            ImportExport.searchSamples(jQuery('#searchSamples').val())
        }, 300, 2);
    </script>
</div>

<p>Sample Sheet Type: <input type="radio" id="selectplateform" name="selectForm" onclick="selectForm('plate');"/>Plate
    | <input type="radio" id="selecttubeform" name="selectForm" onclick="selectForm('tube');"/> Tubes</p>

<form id="sampleExportForm">

</form>
<button id="exportSampleForm" onclick="ImportExport.exportSampleForm();">Export Excel</button>
<script type="text/javascript">

    //    jQuery(document).ready(function () {
    //    });

    function selectForm(selection) {
        ImportExport.searchSamples(jQuery('#searchSamples').val());
        if (selection == 'tube') {
            jQuery('#sampleExportForm').html(jQuery('#tubeformholder').html());
        }
        else if (selection == 'plate') {
            jQuery('#sampleExportForm').html(jQuery('#plateformholder').html());
        }
    }

</script>


<div id="tubeformholder" style="display:none;">
    <div id="tubeformbox" style="border:1px solid grey;width:300px;height:620px;" class="ui-corner-all">
        <div class="exportcolumn">
            Tube 1
            <div class="plateWell" id="t1"></div>
            <br/>
            Tube 2
            <div class="plateWell" id="t2"></div>
            <br/>
            Tube 3
            <div class="plateWell" id="t3"></div>
            <br/>
            Tube 4
            <div class="plateWell" id="t4"></div>
            <br/>
            Tube 5
            <div class="plateWell" id="t5"></div>
            <br/>
            Tube 6
            <div class="plateWell" id="t6"></div>
            <br/>
            Tube 7
            <div class="plateWell" id="t7"></div>
            <br/>
            Tube 8
            <div class="plateWell" id="t8"></div>
            <br/>
            Tube 9
            <div class="plateWell" id="t9"></div>
            <br/>
            Tube 10
            <div class="plateWell" id="t10"></div>
            <br/>
        </div>
        <div class="exportcolumn">
            Tube 11
            <div class="plateWell" id="t11"></div>
            <br/>
            Tube 12
            <div class="plateWell" id="t12"></div>
            <br/>
            Tube 13
            <div class="plateWell" id="t13"></div>
            <br/>
            Tube 14
            <div class="plateWell" id="t14"></div>
            <br/>
            Tube 15
            <div class="plateWell" id="t15"></div>
            <br/>
            Tube 16
            <div class="plateWell" id="t16"></div>
            <br/>
            Tube 17
            <div class="plateWell" id="t17"></div>
            <br/>
            Tube 18
            <div class="plateWell" id="t18"></div>
            <br/>
            Tube 19
            <div class="plateWell" id="t19"></div>
            <br/>
            Tube 20
            <div class="plateWell" id="t20"></div>
            <br/>
        </div>
        <div class="exportcolumn">
            Tube 21
            <div class="plateWell" id="t21"></div>
            <br/>
            Tube 22
            <div class="plateWell" id="t22"></div>
            <br/>
            Tube 23
            <div class="plateWell" id="t23"></div>
            <br/>
            Tube 24
            <div class="plateWell" id="t24"></div>
            <br/>
            Tube 25
            <div class="plateWell" id="t25"></div>
            <br/>
            Tube 26
            <div class="plateWell" id="t26"></div>
            <br/>
            Tube 27
            <div class="plateWell" id="t27"></div>
            <br/>
            Tube 28
            <div class="plateWell" id="t28"></div>
            <br/>
            Tube 29
            <div class="plateWell" id="t29"></div>
            <br/>
            Tube 30
            <div class="plateWell" id="t30"></div>
            <br/>
        </div>
        <div class="exportcolumn">
            Tube 31
            <div class="plateWell" id="t31"></div>
            <br/>
            Tube 32
            <div class="plateWell" id="t32"></div>
            <br/>
            Tube 33
            <div class="plateWell" id="t33"></div>
            <br/>
            Tube 34
            <div class="plateWell" id="t34"></div>
            <br/>
            Tube 35
            <div class="plateWell" id="t35"></div>
            <br/>
            Tube 36
            <div class="plateWell" id="t36"></div>
            <br/>
            Tube 37
            <div class="plateWell" id="t37"></div>
            <br/>
            Tube 38
            <div class="plateWell" id="t38"></div>
            <br/>
            Tube 39
            <div class="plateWell" id="t39"></div>
            <br/>
            Tube 40
            <div class="plateWell" id="t40"></div>
            <br/>
        </div>
    </div>
</div>

<div id="plateformholder" style="display:none;">

<br/>

<div id="formbox" style="border:1px solid grey;width:900px;height:500px;" class="ui-corner-all">
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
<div id="column12" class="exportcolumn">
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

</div>

<%@ include file="../footer.jsp" %>