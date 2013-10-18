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

<%--
  Created by IntelliJ IDEA.
  User: bianx

--%>
<%@ include file="../header.jsp" %>

<script type="text/javascript" src="<c:url value='/scripts/plate_ajax.js?ts=${timestamp.time}'/>"></script>

<div id="maincontent">

<h1>
    Export Plate
</h1>


<div class="sectionDivider" onclick="Utils.ui.toggleLeftInfo(jQuery('#note_arrowclick'), 'notediv');">Quick Help
    <div id="note_arrowclick" class="toggleLeft"></div>
</div>
<div id="notediv" class="note" style="display:none;">help text
</div>



<div style="float:right;">
    <h2>Available Samples</h2>

    <div align="right" >
        Filter:
        <input id="searchSamples" type="text" name="searchSamples" size="8"/>
    </div>
    <div id="sampleList" class="elementList ui-corner-all"  style="height:800px;width:200px;" align="right"></div>

    <script type="text/javascript">
        Utils.timer.typewatchFunc(jQuery('#searchSamples'), function () {
            Plate.ui.searchSamples(jQuery('#searchSamples').val())
        }, 300, 2);
    </script>
</div>

<form  id="plateExportForm">
<table width="800" id="plateExportTable" style="border:1px solid grey;" class="ui-corner-all">
<tr>
    <td>A1</td>
    <td>
        <div class="plateWell" id="A1"></div>
    </td>
    <td>A2</td>
    <td>
        <div class="plateWell" id="A2"></div>
    </td>
    <td>A3</td>
    <td>
        <div class="plateWell" id="A3"></div>
    </td>
    <td>A4</td>
    <td>
        <div class="plateWell" id="A4"></div>
    </td>
    <td>A5</td>
    <td>
        <div class="plateWell" id="A5"></div>
    </td>
    <td>A6</td>
    <td>
        <div class="plateWell" id="A6"></div>
    </td>
    <td>A7</td>
    <td>
        <div class="plateWell" id="A7"></div>
    </td>
    <td>A8</td>
    <td>
        <div class="plateWell" id="A8"></div>
    </td>
    <td>A9</td>
    <td>
        <div class="plateWell" id="A9"></div>
    </td>
    <td>A10</td>
    <td>
        <div class="plateWell" id="A10"></div>
    </td>
    <td>A11</td>
    <td>
        <div class="plateWell" id="A11"></div>
    </td>
    <td>A12</td>
    <td>
        <div class="plateWell" id="A12"></div>
    </td>
</tr>
    <tr>
        <td>B1</td>
        <td>
            <div class="plateWell" id="B1"></div>
        </td>
        <td>B2</td>
        <td>
            <div class="plateWell" id="B2"></div>
        </td>
        <td>B3</td>
        <td>
            <div class="plateWell" id="B3"></div>
        </td>
        <td>B4</td>
        <td>
            <div class="plateWell" id="B4"></div>
        </td>
        <td>B5</td>
        <td>
            <div class="plateWell" id="B5"></div>
        </td>
        <td>B6</td>
        <td>
            <div class="plateWell" id="B6"></div>
        </td>
        <td>B7</td>
        <td>
            <div class="plateWell" id="B7"></div>
        </td>
        <td>B8</td>
        <td>
            <div class="plateWell" id="B8"></div>
        </td>
        <td>B9</td>
        <td>
            <div class="plateWell" id="B9"></div>
        </td>
        <td>B10</td>
        <td>
            <div class="plateWell" id="B10"></div>
        </td>
        <td>B11</td>
        <td>
            <div class="plateWell" id="B11"></div>
        </td>
        <td>B12</td>
        <td>
            <div class="plateWell" id="B12"></div>
        </td>
    </tr>
    <tr>
        <td>C1</td>
        <td>
            <div class="plateWell" id="C1"></div>
        </td>
        <td>C2</td>
        <td>
            <div class="plateWell" id="C2"></div>
        </td>
        <td>C3</td>
        <td>
            <div class="plateWell" id="C3"></div>
        </td>
        <td>C4</td>
        <td>
            <div class="plateWell" id="C4"></div>
        </td>
        <td>C5</td>
        <td>
            <div class="plateWell" id="C5"></div>
        </td>
        <td>C6</td>
        <td>
            <div class="plateWell" id="C6"></div>
        </td>
        <td>C7</td>
        <td>
            <div class="plateWell" id="C7"></div>
        </td>
        <td>C8</td>
        <td>
            <div class="plateWell" id="C8"></div>
        </td>
        <td>C9</td>
        <td>
            <div class="plateWell" id="C9"></div>
        </td>
        <td>C10</td>
        <td>
            <div class="plateWell" id="C10"></div>
        </td>
        <td>C11</td>
        <td>
            <div class="plateWell" id="C11"></div>
        </td>
        <td>C12</td>
        <td>
            <div class="plateWell" id="C12"></div>
        </td>
    </tr>
<tr>
    <td>D1</td>
    <td>
        <div class="plateWell" id="D1"></div>
    </td>
    <td>D2</td>
    <td>
        <div class="plateWell" id="D2"></div>
    </td>
    <td>D3</td>
    <td>
        <div class="plateWell" id="D3"></div>
    </td>
    <td>D4</td>
    <td>
        <div class="plateWell" id="D4"></div>
    </td>
    <td>D5</td>
    <td>
        <div class="plateWell" id="D5"></div>
    </td>
    <td>D6</td>
    <td>
        <div class="plateWell" id="D6"></div>
    </td>
    <td>D7</td>
    <td>
        <div class="plateWell" id="D7"></div>
    </td>
    <td>D8</td>
    <td>
        <div class="plateWell" id="D8"></div>
    </td>
    <td>D9</td>
    <td>
        <div class="plateWell" id="D9"></div>
    </td>
    <td>D10</td>
    <td>
        <div class="plateWell" id="D10"></div>
    </td>
    <td>D11</td>
    <td>
        <div class="plateWell" id="D11"></div>
    </td>
    <td>D12</td>
    <td>
        <div class="plateWell" id="D12"></div>
    </td>
</tr>
<tr>
    <td>E1</td>
    <td>
        <div class="plateWell" id="E1"></div>
    </td>
    <td>E2</td>
    <td>
        <div class="plateWell" id="E2"></div>
    </td>
    <td>E3</td>
    <td>
        <div class="plateWell" id="E3"></div>
    </td>
    <td>E4</td>
    <td>
        <div class="plateWell" id="E4"></div>
    </td>
    <td>E5</td>
    <td>
        <div class="plateWell" id="E5"></div>
    </td>
    <td>E6</td>
    <td>
        <div class="plateWell" id="E6"></div>
    </td>
    <td>E7</td>
    <td>
        <div class="plateWell" id="E7"></div>
    </td>
    <td>E8</td>
    <td>
        <div class="plateWell" id="E8"></div>
    </td>
    <td>E9</td>
    <td>
        <div class="plateWell" id="E9"></div>
    </td>
    <td>E10</td>
    <td>
        <div class="plateWell" id="E10"></div>
    </td>
    <td>E11</td>
    <td>
        <div class="plateWell" id="E11"></div>
    </td>
    <td>E12</td>
    <td>
        <div class="plateWell" id="E12"></div>
    </td>
</tr>
<tr>
    <td>F1</td>
    <td>
        <div class="plateWell" id="F1"></div>
    </td>
    <td>F2</td>
    <td>
        <div class="plateWell" id="F2"></div>
    </td>
    <td>F3</td>
    <td>
        <div class="plateWell" id="F3"></div>
    </td>
    <td>F4</td>
    <td>
        <div class="plateWell" id="F4"></div>
    </td>
    <td>F5</td>
    <td>
        <div class="plateWell" id="F5"></div>
    </td>
    <td>F6</td>
    <td>
        <div class="plateWell" id="F6"></div>
    </td>
    <td>F7</td>
    <td>
        <div class="plateWell" id="F7"></div>
    </td>
    <td>F8</td>
    <td>
        <div class="plateWell" id="F8"></div>
    </td>
    <td>F9</td>
    <td>
        <div class="plateWell" id="F9"></div>
    </td>
    <td>F10</td>
    <td>
        <div class="plateWell" id="F10"></div>
    </td>
    <td>F11</td>
    <td>
        <div class="plateWell" id="F11"></div>
    </td>
    <td>F12</td>
    <td>
        <div class="plateWell" id="F12"></div>
    </td>
</tr>
<tr>
    <td>G1</td>
    <td>
        <div class="plateWell" id="G1"></div>
    </td>
    <td>G2</td>
    <td>
        <div class="plateWell" id="G2"></div>
    </td>
    <td>G3</td>
    <td>
        <div class="plateWell" id="G3"></div>
    </td>
    <td>G4</td>
    <td>
        <div class="plateWell" id="G4"></div>
    </td>
    <td>G5</td>
    <td>
        <div class="plateWell" id="G5"></div>
    </td>
    <td>G6</td>
    <td>
        <div class="plateWell" id="G6"></div>
    </td>
    <td>G7</td>
    <td>
        <div class="plateWell" id="G7"></div>
    </td>
    <td>G8</td>
    <td>
        <div class="plateWell" id="G8"></div>
    </td>
    <td>G9</td>
    <td>
        <div class="plateWell" id="G9"></div>
    </td>
    <td>G10</td>
    <td>
        <div class="plateWell" id="G10"></div>
    </td>
    <td>G11</td>
    <td>
        <div class="plateWell" id="G11"></div>
    </td>
    <td>G12</td>
    <td>
        <div class="plateWell" id="G12"></div>
    </td>
</tr>
<tr>
    <td>H1</td>
    <td>
        <div class="plateWell" id="H1"></div>
    </td>
    <td>H2</td>
    <td>
        <div class="plateWell" id="H2"></div>
    </td>
    <td>H3</td>
    <td>
        <div class="plateWell" id="H3"></div>
    </td>
    <td>H4</td>
    <td>
        <div class="plateWell" id="H4"></div>
    </td>
    <td>H5</td>
    <td>
        <div class="plateWell" id="H5"></div>
    </td>
    <td>H6</td>
    <td>
        <div class="plateWell" id="H6"></div>
    </td>
    <td>H7</td>
    <td>
        <div class="plateWell" id="H7"></div>
    </td>
    <td>H8</td>
    <td>
        <div class="plateWell" id="H8"></div>
    </td>
    <td>H9</td>
    <td>
        <div class="plateWell" id="H9"></div>
    </td>
    <td>H10</td>
    <td>
        <div class="plateWell" id="H10"></div>
    </td>
    <td>H11</td>
    <td>
        <div class="plateWell" id="H11"></div>
    </td>
    <td>H12</td>
    <td>
        <div class="plateWell" id="H12"></div>
    </td>
</tr>
</table>
</form>
<button id="exportSampleForm" onclick="Plate.ui.exportSampleForm();">Export Excel</button>
<script type="text/javascript">

    jQuery(document).ready(function () {
        Plate.ui.searchSamples(jQuery('#searchSamples').val());
    });

</script>

</div>

<%@ include file="../footer.jsp" %>