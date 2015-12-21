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

  <xsl:template match="SUBMISSION">

    <div class="note">
      <h3>
        Submission
        <xsl:if test="@accession">
          <xsl:value-of select="@accession" />
        </xsl:if>
      </h3>
      <table class="list">
        <tr bgcolor="#CCDDFF">
          <th>Alias</th>
          <th>Title</th>
          <th>Contacts</th>
          <th>Submission Date</th>
        </tr>
        <tr>
          <td>
            <xsl:value-of select="@alias" />
          </td>
          <td>
            <xsl:value-of select="TITLE" />
          </td>
          <td>
            <xsl:for-each select="CONTACT">
              <xsl:value-of select="@name" />
              <br />
            </xsl:for-each>
          </td>
          <td>
            <xsl:value-of select="@submission_date" />
          </td>
        </tr>
      </table>

      <b>Actions</b>
      <table class="list">
        <tr bgcolor="#CCDDFF">
          <th>Type</th>
          <th>Schema</th>
          <th>Source</th>
        </tr>
        <xsl:for-each select="ACTIONS/ACTION//*">
          <tr>
            <td>
              <xsl:value-of select="name(.)" />
            </td>
            <td>
              <xsl:value-of select="@schema" />
            </td>
            <td>
              <xsl:value-of select="@source" />
            </td>
          </tr>
        </xsl:for-each>
      </table>

      <b>Files</b>
      <table class="list">
        <tr bgcolor="#CCDDFF">
          <th>Filename</th>
          <th>Checksum</th>
          <th>Checksum Method</th>
        </tr>
        <xsl:for-each select="FILES/FILE">
          <tr>
            <td>
              <xsl:value-of select="@filename" />
            </td>
            <td>
              <xsl:value-of select="@checksum" />
            </td>
            <td>
              <xsl:value-of select="@checksum_method" />
            </td>
          </tr>
        </xsl:for-each>
      </table>
    </div>
  </xsl:template>
</xsl:stylesheet>
