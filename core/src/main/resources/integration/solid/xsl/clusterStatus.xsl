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
      <table class="list">
        <tr bgcolor="#CCDDFF">
          <th>Name</th>
          <th>Images Usage</th>
          <th>Results Usage</th>
          <th>Average Load</th>
        </tr>
        <tr>
          <td>
            <xsl:value-of select="ClusterStatus/clusterName" />
          </td>
          <td>
            <xsl:value-of select="ClusterStatus/diskSpaceImagesString" />
          </td>
          <td>
            <xsl:value-of select="ClusterStatus/diskSpaceResultsString" />
          </td>
          <td>
            <xsl:value-of select="ClusterStatus/avgLoad5" />
          </td>
        </tr>
      </table>
    </div>
  </xsl:template>
</xsl:stylesheet>

<!--
  <ClusterStatus>
  <avgLoad5>0.0</avgLoad5>
  <clusterName>solid0020.sequencer</clusterName>
  <diskSpaceImagesTotal>9744739729408</diskSpaceImagesTotal>
  <diskSpaceImagesString>8.86 TB / 8.86 TB (100%) (290,414 cycle-panels)</diskSpaceImagesString>
  <diskSpaceImagesUsable>9744680144896</diskSpaceImagesUsable>
  <diskSpaceResultsString>3.93 TB / 3.93 TB (100%)</diskSpaceResultsString>
  <diskSpaceResultsTotal>4316555378688</diskSpaceResultsTotal>
  <diskSpaceResultsUsable>4316554833920</diskSpaceResultsUsable>
  <numOfCPUs>24</numOfCPUs>
  <numOfComputeNodes>0</numOfComputeNodes>
  </ClusterStatus>
-->