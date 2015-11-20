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
    <h3>Experiments</h3>
    <xsl:choose>
      <xsl:when test="EXPERIMENT_SET">
        <xsl:for-each select="EXPERIMENT_SET">
          <xsl:apply-templates select="EXPERIMENT" />
        </xsl:for-each>
      </xsl:when>
      <xsl:otherwise>
        <xsl:apply-templates select="EXPERIMENT" />
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="EXPERIMENT">
    <div class="note">
      <h3>
        Experiment
        <xsl:if test="@accession">
          <xsl:value-of select="@accession" />
        </xsl:if>
      </h3>
      <table class="list">
        <tr bgcolor="#CCDDFF">
          <th>Alias</th>
          <th>Title</th>
          <th>Related Study</th>
        </tr>
        <tr>
          <td>
            <xsl:value-of select="@alias" />
          </td>
          <td>
            <xsl:value-of select="TITLE" />
          </td>
          <td>
            <xsl:for-each select="STUDY_REF">
              <xsl:value-of select="@refname" />
              <br />
            </xsl:for-each>
          </td>
        </tr>
      </table>

      <b>Library Design</b>
      <table class="list">
        <tr bgcolor="#CCDDFF">
          <th>Description</th>
          <th>Related Sample</th>
        </tr>
        <tr>
          <td>
            <xsl:value-of select="DESIGN/DESIGN_DESCRIPTION" />
          </td>
          <td>
            <xsl:value-of select="DESIGN/SAMPLE_DESCRIPTOR/@refname" />
          </td>
        </tr>
      </table>

      <b>Library Description</b>
      <table class="list">
        <tr bgcolor="#CCDDFF">
          <th>Library Name</th>
          <th>Library Strategy</th>
          <th>Library Source</th>
          <th>Library Selection</th>
          <th>Library Layout</th>
        </tr>
        <tr>
          <td>
            <xsl:value-of select="DESIGN/LIBRARY_DESCRIPTOR/LIBRARY_NAME" />
          </td>
          <td>
            <xsl:value-of select="DESIGN/LIBRARY_DESCRIPTOR/LIBRARY_STRATEGY" />
          </td>
          <td>
            <xsl:value-of select="DESIGN/LIBRARY_DESCRIPTOR/LIBRARY_SOURCE" />
          </td>
          <td>
            <xsl:value-of select="DESIGN/LIBRARY_DESCRIPTOR/LIBRARY_SELECTION" />
          </td>
          <td>
            <xsl:choose>
              <xsl:when test="DESIGN/LIBRARY_LAYOUT/SINGLE">
                Single
              </xsl:when>
              <xsl:otherwise>
                Paired
                <br />
                <ul>
                  <xsl:if test="DESIGN/LIBRARY_LAYOUT/PAIRED/@NOMINAL_LENGTH">
                    <li>
                      Nominal Insert Length:
                      <xsl:value-of select="DESIGN/LIBRARY_LAYOUT/PAIRED/@NOMINAL_LENGTH" />
                    </li>
                  </xsl:if>
                  <xsl:if test="DESIGN/LIBRARY_LAYOUT/PAIRED/@NOMINAL_SDEV">
                    <li>
                      Nominal Standard Deviation:
                      <xsl:value-of select="DESIGN/LIBRARY_LAYOUT/PAIRED/@NOMINAL_SDEV" />
                    </li>
                  </xsl:if>
                  <xsl:if test="DESIGN/LIBRARY_LAYOUT/PAIRED/@ORIENTATION">
                    <li>
                      Orientation:
                      <xsl:value-of select="DESIGN/LIBRARY_LAYOUT/PAIRED/@ORIENTATION" />
                    </li>
                  </xsl:if>
                </ul>
              </xsl:otherwise>
            </xsl:choose>
          </td>
        </tr>
      </table>

      <b>Read Specification</b>
      <table class="list">
        <tr bgcolor="#CCDDFF">
          <th>Read Index</th>
          <th>Read Class</th>
          <th>Read Type</th>
          <th>Ordering</th>
        </tr>
        <xsl:for-each select="DESIGN/SPOT_DESCRIPTOR/SPOT_DECODE_SPEC/READ_SPEC">
          <tr>
            <td>
              <xsl:value-of select="READ_INDEX" />
            </td>
            <td>
              <xsl:value-of select="READ_CLASS" />
            </td>
            <td>
              <xsl:value-of select="READ_TYPE" />
            </td>
            <td>
              <xsl:if test="BASE_COORD">
                <xsl:value-of select="BASE_COORD" />
              </xsl:if>
              <xsl:if test="RELATIVE_ORDER">
                <xsl:value-of select="RELATIVE_ORDER/@follows_read_index" />
              </xsl:if>
            </td>
          </tr>
        </xsl:for-each>
      </table>

      <b>Platform</b>
      <table class="list">
        <tr bgcolor="#CCDDFF">
          <xsl:for-each select="PLATFORM/*/*">
            <th>
              <xsl:value-of select="name(.)" />
            </th>
          </xsl:for-each>
        </tr>
        <tr>
          <xsl:for-each select="PLATFORM/*/*">
            <td>
              <xsl:value-of select="." />
            </td>
          </xsl:for-each>
        </tr>

      </table>

      <b>Processing</b>
      <table class="list">
        <tr bgcolor="#CCDDFF">
          <th>Base Caller</th>
          <th>Quality Scorer</th>
        </tr>
        <tr>
          <td>
            <xsl:value-of select="PROCESSING/BASE_CALLS/BASE_CALLER" />
            <xsl:text> (</xsl:text>
            <xsl:value-of select="PROCESSING/BASE_CALLS/SEQUENCE_SPACE" />
            <xsl:text>)</xsl:text>
          </td>
          <td>
            <xsl:value-of select="PROCESSING/QUALITY_SCORES/QUALITY_SCORER" />
          </td>
        </tr>
      </table>
    </div>
  </xsl:template>
</xsl:stylesheet>
