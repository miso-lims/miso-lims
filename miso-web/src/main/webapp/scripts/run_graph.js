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
        span : false, // Indicator of whether the graph spans the entire width
        // of the row.
        render : function() {
          // Callback to render the graph after the DOM node is inserted.
          node.innerText = 'Look at me. I am gorgeous.';
        }
      };
      
    });
  };
  var errorMessage = function(metrics, width) {
    return metrics.filter(function(metric) {
      return metric.type == 'message';
    }).map(function(metric) {
      var node = document.createElement('P');
      node.setAttribute('class', 'parsley-error');
      node.innerText = metric.message;
      return {
        dom : node,
        render : function() {
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
        span : false,
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
        span : false,
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
  var summaryTable = function(metrics, width, renamePartitions) {
    return metrics.filter(function(metric) {
      return metric.type == 'table';
    }).map(
        function(metric) {
          var node = document.createElement('TABLE');
          return {
            dom : node,
            span : true,
            render : function() {
              var dt = jQuery(node).dataTable({
                'bJQueryUI' : true,
                'aoColumns' : metric.columns.map(function(column) {
                  return {
                    "sTitle" : column.name,
                    "mData" : column.property,
                    "mRender" : function(data) {
                      return renamePartitions(data, false);
                    }
                  };
                }),
                'sDom' : '<"datatable-scroll"t><"F"ip>',
                'aaData' : metric.rows
              });
              dt.parents("div.dataTables_wrapper").css("width",
                  (width * 2) + "px");
            }
          };
        });
  };
  var lineGraph = function(typeName, title, yLabel) {
    return function(metrics, width, renamePartitions) {
      return metrics.filter(function(metric) {
        return metric.type == typeName;
      }).map(function(metric) {
        metric.series.forEach(function(series) {
          series.visible = !/{\d+}/.test(series.name);
          series.name = renamePartitions(series.name, true);
        });
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
                  animation : false,
                  tooltip : {
                    valueDecimals : 2
                  }
                }
              },
              series : metric.series
            });
          }
        };
      });
    };
  };
  var barGraph = function(typeName, title, yLabel) {
    return function(metrics, width, renamePartitions) {
      return metrics.filter(function(metric) {
        return metric.type == typeName;
      }).map(function(metric) {
        metric.series.forEach(function(series) {
          series.name = renamePartitions(series.name, true);
        });
        var node = document.createElement('DIV');
        return {
          dom : node,
          span : false,
          render : function() {
            new Highcharts.Chart({
              chart : {
                type : 'bar',
                renderTo : node,
                zoomType : 'x',
                spacingRight : 20,
                width : width
              },
              title : {
                text : title
              },
              xAxis : {
                categories : metric.categories,
                title : {
                  text : null
                }
              },
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
                  animation : false,
                  tooltip : {
                    valueDecimals : 2
                  }
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
              animation : false,
              tooltip : {
                valueDecimals : 2
              }
            }
          },
          series : metric.series
        });
      }
    };
  };
  
  var illuminaPerCyclePlot = function(typeName, title, yLabel) {
    return function(metrics, width, renamePartitions) {
      return metrics.filter(function(metric) {
        return metric.type == typeName;
      }).map(function(metric) {
        metric.series.forEach(function(series) {
          series.tooltip = {
            headerFormat : '<em>Cycle {point.key}</em><br/>'
          };
          series.visible = !/{\d+}/.test(series.name);
          series.name = renamePartitions(series.name, true);
        });
        return boxPlot(metric, title, yLabel, width);
      });
    };
  };
  
  var illuminaPerLanePlot = function(typeName, title, yLabel) {
    return function(metrics, width, renamePartitions) {
      return metrics.filter(function(metric) {
        return metric.type == typeName;
      }).map(function(metric) {
        metric.series.forEach(function(series) {
          series.name = renamePartitions(series.name, true);
        });
        return boxPlot(metric, title, yLabel);
      });
    };
  };
  
  return {
    // This is a list of standard metric processors for metrics produced by run
    // scanner. Additional processors maybe added in separate files and appended
    // to this list.
    metricProcessors : [
        example,
        externalLink,
        errorMessage,
        chart,
        summaryTable,
        illuminaPerCyclePlot('illumina-q30-by-cycle', '> Q30', '% Bases >Q30'),
        lineGraph('illumina-called-intensity-by-cycle', 'Called Intensity',
            'Average Intensity per Cycle'),
        lineGraph('illumina-base-percent-by-cycle', 'Base %', 'Percentage'),
        illuminaPerLanePlot('illumina-cluster-density-by-lane',
            'Cluster Density', 'Density (K/mm²)'),
        barGraph('illumina-yield-by-read', 'Yields', 'Yield (gb)') ],
    // Takes a list of metrics and renders them to #metricsdiv
    renderMetrics : function(metrics, partitionNames) {
      var container = document.getElementById('metricsdiv');
      
      var renderAll = function() {
        while (container.hasChildNodes()) {
          container.removeChild(container.lastChild);
        }
        var realWidth = jQuery(container).width();
        var forceSpan = realWidth < 550;
        var width = forceSpan ? realWidth : Math.round(realWidth * 0.49);
        // Start with graphs we know how to make (see the example for a
        // template). Then filter them as appropriate for the data we have.
        var graphs = RunGraph.metricProcessors.map(
            function(graph) {
              return graph(metrics, width, function(str, includePrefix) {
                return ("" + str).replace(/{(\d+)}/, function(match,
                    partitionNumber) {
                  var index = parseInt(partitionNumber) - 1;
                  if (partitionNames[index]) {
                    return (includePrefix ? "Lane " + partitionNumber + ": "
                        : "") + partitionNames[index];
                  } else {
                    return includePrefix ? ("Lane " + partitionNumber) : "N/A";
                  }
                });
              });
            }).reduce(function(a, b) {
          return a.concat(b);
        });
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
          if (graph.span || forceSpan) {
            cell.colSpan = 2;
            var spanRow = document.createElement('TR');
            spanRow.appendChild(cell);
            table.appendChild(spanRow);
          } else if (row == null) {
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
