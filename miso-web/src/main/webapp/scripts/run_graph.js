var RunGraph = (function () {
  // This is an example metric graph. It should never occur in real data coming
  // from run scanner.
  var example = function (metrics, width) {
    // Scan the metrics for anything we can graph
    return metrics
      .filter(function (metric) {
        return metric.type == "example";
      })
      .map(function (metric) {
        // Output a graph definition
        var node = document.createElement("P");
        return {
          dom: node,
          span: false, // Indicator of whether the graph spans the entire width
          // of the row.
          render: function () {
            // Callback to render the graph after the DOM node is inserted.
            node.innerText = "Look at me. I am gorgeous.";
          },
        };
      });
  };
  var errorMessage = function (metrics, width) {
    return metrics
      .filter(function (metric) {
        return metric.type == "message";
      })
      .map(function (metric) {
        var node = document.createElement("P");
        node.setAttribute("class", "parsley-error");
        node.innerText = metric.message;
        return {
          dom: node,
          render: function () {},
        };
      });
  };
  var externalLink = function (metrics, width) {
    return metrics
      .filter(function (metric) {
        return metric.type == "link";
      })
      .map(function (metric) {
        var link = document.createElement("A");
        link.innerText = metric.name;
        link.href = metric.href;
        link.target = "_blank";
        return {
          dom: link,
          span: false,
          render: function () {},
        };
      });
  };

  var chart = function (metrics, width) {
    return metrics
      .filter(function (metric) {
        return metric.type == "chart";
      })
      .map(function (metric) {
        var node = document.createElement("TABLE");
        return {
          dom: node,
          span: false,
          render: function () {
            jQuery(node).dataTable({
              bJQueryUI: true,
              bAutoWidth: false,
              aoColumns: [
                {
                  sTitle: "Name",
                  mData: "name",
                },
                {
                  sTitle: "Value",
                  mData: "value",
                },
              ],
              aaData: metric.values,
              sDom: "t",
              fnDrawCallback: function () {
                jQuery(node).find("thead").remove();
              },
            });
          },
        };
      });
  };
  var summaryTable = function (metrics, width, renamePartitions) {
    return metrics
      .filter(function (metric) {
        return metric.type == "table";
      })
      .map(function (metric) {
        var node = document.createElement("TABLE");
        return {
          dom: node,
          span: true,
          render: function () {
            var dt = jQuery(node).dataTable({
              bJQueryUI: true,
              aoColumns: metric.columns.map(function (column) {
                return {
                  sTitle: column.name,
                  mData: column.property,
                  mRender: function (data) {
                    return renamePartitions(data, false);
                  },
                };
              }),
              sDom: '<"datatable-scroll"t><"F"ip>',
              aaData: metric.rows,
            });
            dt.parents("div.dataTables_wrapper").css("width", width * 2 + "px");
          },
        };
      });
  };

  // Line graph starting at 0 on x axis
  var lineGraph = function (typeName, title, yLabel, xLabel) {
    return lineGraph_CustomX(typeName, title, yLabel, xLabel, 0);
  };

  // Line graph with a custom starting point on the x axis
  var lineGraph_CustomX = function (typeName, title, yLabel, xLabel, xStart) {
    return function (metrics, width, renamePartitions) {
      return metrics
        .filter(function (metric) {
          return metric.type == typeName;
        })
        .map(function (metric) {
          metric.series.forEach(function (series) {
            series.visible = !/{\d+}/.test(series.name);
            series.name = renamePartitions(series.name, true);
          });
          var node = document.createElement("DIV");
          return {
            dom: node,
            span: false,
            render: function () {
              new Highcharts.Chart({
                chart: {
                  type: "line",
                  renderTo: node,
                  zoomType: "x",
                  spacingRight: 20,
                  width: width,
                },
                title: {
                  text: title,
                },
                xAxis: {
                  allowDecimals: false,
                  floor: 0,
                  title: {
                    text: xLabel || null,
                  },
                },
                yAxis: {
                  floor: 0,
                  title: {
                    text: yLabel,
                  },
                },
                plotOptions: {
                  line: {
                    lineWidth: 1,
                    marker: {
                      enabled: false,
                    },
                    shadow: false,
                  },
                  series: {
                    animation: false,
                    tooltip: {
                      valueDecimals: 2,
                    },
                    pointStart: xStart,
                  },
                },
                series: metric.series,
              });
            },
          };
        });
    };
  };
  var barGraph = function (typeName, title, yLabel, xLabel) {
    return function (metrics, width, renamePartitions) {
      return metrics
        .filter(function (metric) {
          return metric.type == typeName;
        })
        .map(function (metric) {
          metric.series.forEach(function (series) {
            series.name = renamePartitions(series.name, true);
          });
          var node = document.createElement("DIV");
          return {
            dom: node,
            span: false,
            render: function () {
              new Highcharts.Chart({
                chart: {
                  type: "bar",
                  renderTo: node,
                  zoomType: "x",
                  spacingRight: 20,
                  width: width,
                },
                title: {
                  text: title,
                },
                xAxis: {
                  categories: metric.categories,
                  title: {
                    text: xLabel || null,
                  },
                },
                yAxis: {
                  title: {
                    text: yLabel,
                  },
                },
                plotOptions: {
                  line: {
                    lineWidth: 1,
                    marker: {
                      enabled: false,
                    },
                    shadow: false,
                  },
                  series: {
                    animation: false,
                    tooltip: {
                      valueDecimals: 2,
                    },
                  },
                },
                series: metric.series,
              });
            },
          };
        });
    };
  };

  var boxPlot = function (metric, title, yLabel, xLabel, width) {
    var node = document.createElement("DIV");
    return {
      dom: node,
      span: false,
      render: function () {
        new Highcharts.Chart({
          chart: {
            type: "boxplot",
            renderTo: node,
            zoomType: "x",
            spacingRight: 20,
            width: width,
          },
          title: {
            text: title,
          },
          xAxis: {
            allowDecimals: false,
            floor: 0,
            title: {
              text: xLabel || null,
            },
          },
          yAxis: {
            min: 1,
            title: {
              text: yLabel,
            },
          },
          plotOptions: {
            boxplot: {
              medianColor: "red",
            },
            series: {
              animation: false,
              tooltip: {
                valueDecimals: 2,
              },
            },
          },
          series: metric.series,
        });
      },
    };
  };

  var illuminaPerCyclePlot = function (typeName, title, yLabel, xLabel) {
    return function (metrics, width, renamePartitions) {
      return metrics
        .filter(function (metric) {
          return metric.type == typeName;
        })
        .map(function (metric) {
          metric.series.forEach(function (series) {
            series.tooltip = {
              headerFormat: "<em>Cycle {point.key}</em><br/>",
            };
            series.visible = !/{\d+}/.test(series.name);
            series.name = renamePartitions(series.name, true);
          });
          return boxPlot(metric, title, yLabel, xLabel, width);
        });
    };
  };

  var illuminaPerLanePlot = function (typeName, title, yLabel, xLabel) {
    return function (metrics, width, renamePartitions) {
      return metrics
        .filter(function (metric) {
          return metric.type == typeName;
        })
        .map(function (metric) {
          metric.series.forEach(function (series) {
            series.name = renamePartitions(series.name, true);
          });
          return boxPlot(metric, title, yLabel, xLabel);
        });
    };
  };

  return {
    // This is a list of standard metric processors for metrics produced by run
    // scanner. Additional processors maybe added in separate files and appended
    // to this list.
    metricProcessors: [
      example,
      externalLink,
      errorMessage,
      chart,
      summaryTable,
      illuminaPerCyclePlot("illumina-q30-by-cycle", "> Q30 by Cycle", "% Bases >Q30", "Cycle"),
      lineGraph(
        "illumina-called-intensity-by-cycle",
        "Called Intensity by Cycle",
        "Average Intensity per Cycle",
        "Cycle"
      ),
      lineGraph("illumina-base-percent-by-cycle", "Base % by Cycle", "Percentage", "Cycle"),
      illuminaPerLanePlot(
        "illumina-cluster-density-by-lane",
        "Cluster Density by Lane",
        "Density (K/mm²)",
        "Lane"
      ),
      lineGraph_CustomX("illumina-clusters-by-lane", "Clusters by Lane", "Clusters", "Lane", 1),
      barGraph("illumina-yield-by-read", "Yields per Read", "Yield (gb)"),
    ],
    // Takes a list of metrics and renders them to #metricsdiv
    renderMetrics: function (metrics, partitionNames) {
      var container = document.getElementById("metricsdiv");

      var renderAll = function () {
        while (container.hasChildNodes()) {
          container.removeChild(container.lastChild);
        }
        var realWidth = jQuery(container).width();
        var forceSpan = realWidth < 550;
        var width = forceSpan ? realWidth : Math.round(realWidth * 0.49);
        // Start with graphs we know how to make (see the example for a
        // template). Then filter them as appropriate for the data we have.
        var graphs = RunGraph.metricProcessors
          .map(function (graph) {
            return graph(metrics, width, function (str, includePrefix) {
              return ("" + str).replace(/{(\d+)}/, function (match, partitionNumber) {
                var index = parseInt(partitionNumber) - 1;
                if (partitionNames[index]) {
                  return (
                    (includePrefix ? "Lane " + partitionNumber + ": " : "") + partitionNames[index]
                  );
                } else {
                  return includePrefix ? "Lane " + partitionNumber : "N/A";
                }
              });
            });
          })
          .reduce(function (a, b) {
            return a.concat(b);
          });
        if (graphs.length == 0) {
          container.innerText = "No graphs available.";
          return;
        }
        var css = "width: " + width + "px";
        var table = document.createElement("TABLE");
        container.appendChild(table);
        var row = null;
        graphs.forEach(function (graph) {
          var cell = document.createElement("TD");
          cell.style.cssText = css;
          cell.appendChild(graph.dom);
          if (graph.span || forceSpan) {
            cell.colSpan = 2;
            var spanRow = document.createElement("TR");
            spanRow.appendChild(cell);
            table.appendChild(spanRow);
          } else if (row == null) {
            row = document.createElement("TR");
            row.appendChild(cell);
            table.appendChild(row);
          } else {
            row.appendChild(cell);
            row = null;
          }
        });
        graphs.forEach(function (graph) {
          graph.render();
        });
      };

      var timer_id;
      jQuery(window).resize(function () {
        clearTimeout(timer_id);
        timer_id = setTimeout(function () {
          renderAll();
        }, 300);
      });
      renderAll();
    },
  };
})();
