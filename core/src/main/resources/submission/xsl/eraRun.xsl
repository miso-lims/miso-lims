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
    <h3>Runs</h3>
    <xsl:choose>
      <xsl:when test="RUN_SET">
        <xsl:for-each select="RUN_SET">
          <xsl:apply-templates select="RUN" />
        </xsl:for-each>
      </xsl:when>
      <xsl:otherwise>
        <xsl:apply-templates select="RUN" />
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="RUN">
    <div class="note">
      <b>
        Run
        <xsl:if test="@accession">
          <xsl:value-of select="@accession" />
        </xsl:if>
      </b>
      <table class="list">
        <tr bgcolor="#CCDDFF">
          <th>Alias</th>
          <th>Run Date</th>
          <th>Related Experiments</th>
        </tr>
        <tr>
          <td>
            <xsl:value-of select="@alias" />
          </td>
          <td>
            <xsl:value-of select="@run_date" />
          </td>
          <td>
            <xsl:for-each select="EXPERIMENT_REF">
              <xsl:value-of select="@refname" />
              <br />
            </xsl:for-each>
          </td>
        </tr>
      </table>

      <xsl:for-each select="DATA_BLOCK">
        <b>
          Data Block
          <xsl:value-of select="@sector" />
        </b>
        <table class="list">
          <tr bgcolor="#CCDDFF">
            <th>Filename</th>
            <th>Type</th>
          </tr>
          <xsl:for-each select="FILES/FILE">
            <tr>
              <td>
                <xsl:value-of select="@filename" />
              </td>
              <td>
                <xsl:value-of select="@filetype" />
              </td>
            </tr>
          </xsl:for-each>
        </table>
      </xsl:for-each>
    </div>
  </xsl:template>
</xsl:stylesheet>