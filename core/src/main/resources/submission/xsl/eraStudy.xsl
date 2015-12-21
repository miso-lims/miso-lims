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
      <h3>Studies</h3>
      <xsl:choose>
        <xsl:when test="STUDY_SET">
          <xsl:for-each select="STUDY_SET">
            <xsl:apply-templates select="STUDY" />
          </xsl:for-each>
        </xsl:when>
        <xsl:otherwise>
          <xsl:apply-templates select="STUDY" />
        </xsl:otherwise>
      </xsl:choose>
    </div>
  </xsl:template>

  <xsl:template match="STUDY">
    <h3>
      Study
      <xsl:if test="@accession">
        <xsl:value-of select="@accession" />
      </xsl:if>
    </h3>
    <table class="list">
      <tr bgcolor="#CCDDFF">
        <th>Alias</th>
        <th>Title</th>
        <th>Type</th>
        <th>Description</th>
      </tr>
      <tr>
        <td>
          <xsl:value-of select="@alias" />
        </td>
        <td>
          <xsl:value-of select="DESCRIPTOR/STUDY_TITLE" />
        </td>
        <td>
          <xsl:value-of select="DESCRIPTOR/STUDY_TYPE/@existing_study_type" />
        </td>
        <td>
          <xsl:value-of select="DESCRIPTOR/STUDY_DESCRIPTION" />
        </td>
      </tr>
    </table>

  </xsl:template>
</xsl:stylesheet>
