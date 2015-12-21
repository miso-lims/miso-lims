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
      <b>Date created:</b>
      <xsl:value-of select="Run/dateCreated" />
      <br />
      <b>Date started:</b>
      <xsl:value-of select="Run/dateStarted" />
      <br />
      <b>Date completed:</b>
      <xsl:value-of select="Run/dateCompleted" />
      <br />

      <table class="list">
        <tr bgcolor="#CCDDFF">
          <th>Sample Name</th>
          <th>Pri. Anlysis</th>
          <th>Sec. Analysis</th>
          <th>Library Info</th>
        </tr>
        <xsl:for-each select="Run/sampleInfoList/SampleInfo">
          <tr>
            <td>
              <xsl:value-of select="name" />
            </td>
            <td>
              <xsl:value-of select="primaryAnalysisStatus" />
            </td>
            <td>
              <xsl:value-of select="secondaryAnalysisStatus" />
            </td>
            <td>
              <xsl:for-each select="libraryInfoList/LibraryInfo/allPrimaryAnalysisResults">
                <xsl:value-of select="string" />
              </xsl:for-each>
            </td>
          </tr>
        </xsl:for-each>
      </table>
    </div>
  </xsl:template>
</xsl:stylesheet>
