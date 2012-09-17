/*
 * Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
 * MISO project contacts: Robert Davey, Mario Caccamo @ TGAC
 * *********************************************************************
 *
 * This file is part of MISO.
 *
 * MISO is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MISO is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MISO.  If not, see <http://www.gnu.org/licenses/>.
 *
 * *********************************************************************
 */

var Stats = Stats || {
  getRunStats : function(runId) {
    jQuery('#summarydiv').html("<img src='/styles/images/ajax-loader.gif'/>");

    Fluxion.doAjax(
      'statsControllerHelperService',
      'getRunStats',
      {'runId':runId, 'url':ajaxurl},
      {'doOnSuccess':
        function(json) {
          jQuery('#summarydiv').html("");
          if (json.runSummary) {
            jQuery('#summarydiv').append("<table class='list' id='runSummaryTable'><thead><tr><th>Key</th><th>Value</th></tr></thead><tbody></tbody></table>");
            var rs = json.runSummary;
            for (var i = 0; i < rs.length; i++) {
              if (rs[i][0] != "description") {
                jQuery('#runSummaryTable > tbody:last')
                  .append(jQuery('<tr>').append("<td>"+rs[i][0]+"</td><td>"+rs[i][1]+"</td>")
                );
              }
            }
          }
        },
        'doOnError':
        function(json) {
          jQuery('#summarydiv').html(json.error);
        }
      }
    );
  },

  getPartitionStats : function(runId, partitionNumber) {
    Fluxion.doAjax(
      'statsControllerHelperService',
      'getPartitionStats',
      {'runId':runId, 'partitionNumber':partitionNumber,'url':ajaxurl},
      {'doOnSuccess':
        function(json) {
          if (json.partitionSummary) {
            var text = "<table class='list' id='partitionSummaryTable'><thead><tr><th>Key</th><th>Value</th></tr></thead><tbody>";
            var ps = json.partitionSummary;
            for (var i = 0; i < ps.length; i++) {
              if (ps[i][0] != "description") {
                text += "<tr><td>"+ps[i][0]+"</td><td>"+ps[i][1]+"</td></tr>";
              }
            }
            text += "</tbody></table>";
            jQuery("#statstable").html(text);
            jQuery.colorbox({width:"90%",height:"100%",html:jQuery("#graphpanel").html()});
//                        jQuery.colorbox({width:"90%",html:text});
          }
        },
        'doOnError':
        function(json) {
          jQuery("#statstable").html(text);
          jQuery.colorbox({width:"90%",height:"100%",html:jQuery("#graphpanel").html()});
//              jQuery.colorbox({width:"90%",html:json.error});
        }
      }
    );
  }
};
