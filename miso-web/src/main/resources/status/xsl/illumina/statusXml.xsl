<!--
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
  ~ MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
  ~ GNU General Public License for more details.
  ~
  ~ You should have received a copy of the GNU General Public License
  ~ along with MISO. If not, see <http://www.gnu.org/licenses/>.
  ~
  ~ **********************************************************************
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="html" />

  <xsl:template match="/">
    <div class="note">
      <h3>Status</h3>
      <xsl:value-of select="Status/Software" />
      <br />
      <xsl:value-of select="Status/InstrumentName" />
      <br />
      <xsl:value-of select="Status/RunStarted" />
      <br />

      <table class="list">
        <tr bgcolor="#CCDDFF">
          <th>Cycles</th>
          <th>Image Cycle</th>
          <th>Score Cycle</th>
          <th>Call Cycle</th>
        </tr>
        <tr>
          <td>
            <xsl:value-of select="Status/NumCycles" />
          </td>
          <td>
            <xsl:value-of select="Status/ImgCycle" />
          </td>
          <td>
            <xsl:value-of select="Status/ScoreCycle" />
          </td>
          <td>
            <xsl:value-of select="Status/CallCycle" />
          </td>
        </tr>
      </table>
    </div>
  </xsl:template>
</xsl:stylesheet>
