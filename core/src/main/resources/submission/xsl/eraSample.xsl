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
      <h3>Samples</h3>
      <table class="list">
        <tr bgcolor="#CCDDFF">
          <th>Alias</th>
          <th>Title</th>
          <th>Scientific Name</th>
          <th>Description</th>
        </tr>
        <xsl:choose>
          <xsl:when test="SAMPLE_SET">
            <xsl:for-each select="SAMPLE_SET">
              <xsl:apply-templates select="SAMPLE" />
            </xsl:for-each>
          </xsl:when>
          <xsl:otherwise>
            <xsl:apply-templates select="SAMPLE" />
          </xsl:otherwise>
        </xsl:choose>
      </table>
    </div>
  </xsl:template>

  <xsl:template match="SAMPLE">
    <tr>
      <td>
        <xsl:value-of select="@alias" />
      </td>
      <td>
        <xsl:value-of select="TITLE" />
      </td>
      <td>
        <xsl:value-of select="SCIENTIFIC_NAME" />
      </td>
      <td>
        <xsl:value-of select="DESCRIPTION" />
      </td>
    </tr>
  </xsl:template>
</xsl:stylesheet>