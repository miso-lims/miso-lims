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

var RunGraph = (function() {
  // This is an example metric graph. It should never occur in real data coming
  // from run scanner.
  var example = function(metrics, width) {
    // Scan the metrics for anything we can graph
    return metrics.filter(function(metric) {
      return metric.type == 'example';
    }).map(function(metric) {
      // Output a graph definition
      var node = document.createElement('P');
      return {
        dom : node,
        render : function() {
          // Callback to render the graph after the DOM node is inserted.
          node.innerText = 'Look at me. I am gorgeous.';
        }
      };
      
    });
  };
  var externalLink = function(metrics, width) {
    return metrics.filter(function(metric) {
      return metric.type == 'link';
    }).map(function(metric) {
      var link = document.createElement('A');
      link.innerText = metric.name;
      link.href = metric.href;
      link.target = "_blank";
      return {
        dom : link,
        render : function() {
        }
      };
    });
  };
  
  var chart = function(metrics, width) {
    return metrics.filter(function(metric) {
      return metric.type == 'chart';
    }).map(function(metric) {
      var node = document.createElement('TABLE');
      return {
        dom : node,
        render : function() {
          jQuery(node).dataTable({
            'bJQueryUI' : true,
            'bAutoWidth' : false,
            'aoColumns' : [ {
              "sTitle" : "Name",
              "mData" : "name",
            }, {
              "sTitle" : "Value",
              "mData" : "value",
            }, ],
            'aaData' : metric.values,
            'sDom' : 't',
            'fnDrawCallback' : function() {
              jQuery(node).find("thead").remove();
            }
          });
        }
      };
    });
  };
  var summaryTable = function(metrics, width) {
    return metrics.filter(function(metric) {
      return metric.type == 'table';
    }).map(function(metric) {
      var node = document.createElement('TABLE');
      return {
        dom : node,
        render : function() {
          var dt = jQuery(node).dataTable({
            'bJQueryUI' : true,
            'aoColumns' : metric.columns.map(function(column) {
              return {
                "sTitle" : column.name,
                "mData" : column.property,
              };
            }),
            'sDom' : '<"datatable-scroll"t><"F"ip>',
            'aaData' : metric.rows
          });
          dt.parents("div.dataTables_wrapper").css("width", width + "px");
        }
      };
    });
  };
  var lineGraph = function(typeName, title, yLabel) {
    return function(metrics, width) {
      return metrics.filter(function(metric) {
        return metric.type == typeName;
      }).map(function(metric) {
        metric.series.forEach(function(series) {
          series.visible = !series.name.includes("Lane");
        });
        var node = document.createElement('DIV');
        return {
          dom : node,
          render : function() {
            new Highcharts.Chart({
              chart : {
                type : 'line',
                renderTo : node,
                zoomType : 'x',
                spacingRight : 20,
                width : width
              },
              title : {
                text : title
              },
              xAxis : {},
              yAxis : {
                title : {
                  text : yLabel
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
                  animation : false
                }
              },
              series : metric.series
            });
          }
        };
      });
    };
  };
  
  var boxPlot = function(metric, title, yLabel, width) {
    var node = document.createElement('DIV');
    return {
      dom : node,
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
            text : title
          },
          xAxis : {},
          yAxis : {
            title : {
              text : yLabel
            }
          },
          plotOptions : {
            boxplot : {
              medianColor : 'red'
            },
            series : {
              animation : false
            }
          },
          series : metric.series
        });
      }
    };
  };
  
  var illuminaPerCyclePlot = function(typeName, title, yLabel) {
    return function(metrics, width) {
      return metrics.filter(function(metric) {
        return metric.type == typeName;
      }).map(function(metric) {
        metric.series.forEach(function(series) {
          series.tooltip = {
            headerFormat : '<em>Cycle {point.key}</em><br/>'
          };
          series.visible = !series.name.includes("Lane");
        });
        return boxPlot(metric, title, yLabel, width);
      });
    };
  };
  
  var illuminaPerLanePlot = function(typeName, title, yLabel) {
    return function(metrics) {
      return metrics.filter(function(metric) {
        return metric.type == typeName;
      }).map(function(metric) {
        return boxPlot(metric, title, yLabel);
      });
    };
  };
  
  return {
    // Takes a list of metrics and renders them to #metricsdiv
    renderMetrics : function(metrics) {
      var container = document.getElementById('metricsdiv');
      
      var renderAll = function() {
        while (container.hasChildNodes()) {
          container.removeChild(container.lastChild);
        }
        var width = Math.round(jQuery(container).width() * 0.49);
        // Start with graphs we know how to make (see the example for a
        // template).
        // Then filter them as appropriate for the data we have.
        var graphs = metrics
            ? [
                example,
                externalLink,
                chart,
                summaryTable,
                illuminaPerCyclePlot('illumina-q30-by-cycle', '> Q30',
                    '% Bases >Q30'),
                lineGraph('illumina-called-intensity-by-cycle',
                    'Called Intensity', 'Average Intensity per Cycle'),
                lineGraph('illumina-base-percent-by-cycle', 'Base %',
                    'Percentage'),
                illuminaPerLanePlot('illumina-cluster-density-by-lane',
                    'Cluster Density', 'Density (K/mm²)') ].map(
                function(graph) {
                  return graph(metrics, width);
                }).reduce(function(a, b) {
              return a.concat(b);
            }) : [];
        if (graphs.length == 0) {
          container.innerText = "No graphs available.";
          return;
        }
        var css = "width: " + width + "px";
        var table = document.createElement('TABLE');
        container.appendChild(table);
        var row = null;
        graphs.forEach(function(graph) {
          var cell = document.createElement('TD');
          cell.style.cssText = css;
          cell.appendChild(graph.dom);
          if (row == null) {
            row = document.createElement('TR');
            row.appendChild(cell);
            table.appendChild(row);
          } else {
            row.appendChild(cell);
            row = null;
          }
        });
        graphs.forEach(function(graph) {
          graph.render();
        });
      };
      
      var timer_id;
      jQuery(window).resize(function() {
        clearTimeout(timer_id);
        timer_id = setTimeout(function() {
          renderAll();
        }, 300);
      });
      renderAll();
    }
  };
})();
