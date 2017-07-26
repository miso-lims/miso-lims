/*
 * Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
 * MISO project contacts: Robert Davey @ TGAC
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

RunGraph.metricProcessors.push(function(metrics, width) {
  return metrics.filter(function(metric) {
    return metric.type == "statsdb-per-position-base-content";
  }).map(function(metric) {
    var node = document.createElement('DIV');
    return {
      dom : node,
      span : false,
      render : function() {
        new Highcharts.Chart({
          chart : {
            type : 'line',
            renderTo : node,
            zoomType : 'x',
            spacingRight : 20
          },
          title : {
            text : "StatsDB: Base Content"
          },
          xAxis : {},
          yAxis : {
            title : {
              text : "Content"
            }
          },
          plotOptions : {
            line : {
              lineWidth : 1,
              marker : {
                enabled : false
              },
              shadow : false
            },
            series : {
              animation : false,
              tooltip : {
                valueDecimals : 2
              }
            }
          },
          // TODO: There's also a series labelled `base`.
          series : [ "A", "C", "G", "T" ].map(function(base) {
            return {
              name : base,
              data : metric.data.map(function(x) {
                return x[base];
              })
            };
          })
        });
      }
    };
  });
});
RunGraph.metricProcessors.push(function(metrics, width) {
  return metrics.filter(function(metric) {
    return metric.type == "statsdb-per-position-sequence-quality";
  }).map(function(metric) {
    // TODO: These axis and series labels are not useful
    var node = document.createElement('DIV');
    return {
      dom : node,
      span : false,
      render : function() {
        new Highcharts.Chart({
          chart : {
            type : 'boxplot',
            renderTo : node,
            zoomType : 'x',
            spacingRight : 20,
            width : width
          },
          title : {
            text : 'StatsDB: Sequence Quality'
          },
          xAxis : {},
          yAxis : {
            title : {
              text : 'Quality'
            }
          },
          plotOptions : {
            boxplot : {
              medianColor : 'red'
            },
            series : {
              animation : false,
              tooltip : {
                valueDecimals : 2
              }
            }
          },
          series : [ {
            name : "Quality",
            data : metric.data
          } ]
        });
      }
    };
  });
});
