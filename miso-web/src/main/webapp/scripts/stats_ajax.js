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
  checkRunProgress : function(runAlias, platformType) {
    Fluxion.doAjax(
      'statsControllerHelperService',
      'updateRunProgress',
      {'runAlias':runAlias, 'platformType':platformType, 'url':ajaxurl},
      {'doOnSuccess':
        function(json) {
          if (json.progress) {
            jQuery('input[name=status\\.health][value=' + json.progress + ']').prop('checked', true);
          }
          if (json.response) {
            console.log(json.response);
          }
        }
      }
    );
  },

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
    jQuery("#chartSample").html(runId);
    jQuery("#chartPartition").html(partitionNumber);
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
            jQuery.colorbox({width:"90%",height:"120%",html:jQuery("#graphpanel").html()});
          }
        },
        'doOnError':
        function(json) {
          jQuery("#statstable").html(text);
          jQuery.colorbox({width:"90%",height:"100%",html:jQuery("#graphpanel").html()});
        }
      }
    );
  },

  getInterOpMetricsForLane : function(runAlias, platformType, laneNum) {
    Fluxion.doAjax(
      'statsControllerHelperService',
      'getInterOpMetricsForLane',
      {'runAlias':runAlias, 'platformType':platformType, 'lane':laneNum, 'url':ajaxurl},
      {'doOnSuccess':
        function(json) {
          var out = "";
          out += "<h2>Metrics</h2>";
          out += "<table class='list' id='metricsTable'><thead><tr id='qheaders'><th>Lane No.</th></tr></thead><tbody></tbody></table>";

          out += "<div id='metrixchartsdiv'><table id='metricsPlotTable'><tbody><tr><td style='width:26%' id='densityBoxPlot'></td><td style='width:26%' id='qualityScorePlot'></td><td style='width:26%' id='qualityQ30ByCyclePlot'></td></tr></tbody></table></div>";

          jQuery.colorbox({width:"90%",height:"90%",html:out});

          for (var lane = 1; lane < json.summary.numLanes+1; lane++) {
            if (lane == laneNum) {
              jQuery('#metricsTable > tbody').append('<tr id="lane'+lane+'"><td>Lane '+lane+'</td></tr>');
            }
          }

          var cd = json.tileMetrics.clusterDensities;
          jQuery.each(cd, function(index, obj) {
            jQuery.each(obj, function(key, value) {
              if (obj.lane == laneNum) {
                if (key == "density") {
                  if (jQuery('#qheaders th[id=density]').length == 0) {
                    jQuery('#qheaders').append("<th id='density'>Density "+obj.units+"</th>");
                  }
                  jQuery('#metricsTable tr:last').append('<td>'+obj.density+' &plusmn; '+obj.densitySD+'</td>');
                }

                if (key == "densityPassingFilter") {
                  if (jQuery('#qheaders th[id=densityPassingFilter]').length == 0) {
                    jQuery('#qheaders').append("<th id='densityPassingFilter'>Density PF "+obj.units+"</th>");
                  }
                  jQuery('#metricsTable tr:last').append('<td>'+obj.densityPassingFilter+' &plusmn; '+obj.densityPassingFilterSD+'</td>');
                }

                if (key == "densityPercentPassed") {
                  if (jQuery('#qheaders th[id=densityPercentPassed]').length == 0) {
                    jQuery('#qheaders').append("<th id='densityPercentPassed'>Density Passed %</th>");
                  }
                  jQuery('#metricsTable tr:last').append('<td>'+obj.densityPercentPassed+'</td>');
                }
              }
            });
          });

          var qm = json.qualityMetrics.perLaneQualityScores;
          //"perLaneQualityScores":[{"lane":1,">Q20":96.64105035802294,">Q30":88.9216603526773,">Q40":20.68262353622945,"raw":[{"5":"46492415"},{"6":"20471476"},{"7":"143103000"},{"8":"55734604"},{"9":"12254404"},{"10":"67201395"},{"11":"22793269"},{"12":"22546252"},{"13":"23825791"},{"14":"12566510"},{"15":"65735037"},{"17":"52210617"},{"16":"26216691"},{"19":"67752596"},{"18":"85193076"},{"21":"34161879"},{"20":"132913093"},{"23":"139165499"},{"22":"48120848"},{"25":"193049152"},{"24":"163817129"},{"27":"273124858"},{"26":"186226529"},{"29":"368992273"},{"28":"124516853"},{"31":"779584637"},{"30":"455167706"},{"34":"2119454108"},{"35":"4050550629"},{"32":"572390834"},{"33":"1281578553"},{"38":"1008199209"},{"39":"1631854913"},{"36":"867398267"},{"37":"1944280122"},{"40":"1723703686"},{"41":"2734900938"}]}
          jQuery.each(qm, function(index, obj) {
            jQuery.each(obj, function(key, value) {
              if (obj.lane == laneNum) {
                if (key != "lane" && key != "raw") {
                  if (jQuery('#qheaders th[id='+key.replace(">", "")+']').length == 0) {
                    jQuery('#qheaders').append("<th id='"+key.replace(">", "")+"'>Quality "+key.replace(">", "&gt;")+"</th>");
                    jQuery('#metricsTable tr:last').append('<td>'+value+'</td>');
                  }
                  else {
                    jQuery('#metricsTable tr:last').append('<td>'+value+'</td>');
                  }
                }
              }
            });
          });

          //phasing metrics
          //"phasingMetrics":[{"lane":1,"1":{"phasing":"0.2","prephasing":"0.254"},"Index":{"phasing":"0","prephasing":"0"}}]
          var pm = json.tileMetrics.phasingMetrics;
          jQuery.each(pm, function(index, obj) {
            if (obj.lane == laneNum) {
              var output = "";
              jQuery.each(obj, function(key, value) {
                if (key != "lane") {
                  if (jQuery('#qheaders th[id=phasing]').length == 0) {
                    jQuery('#qheaders').append("<th id='phasing'>Phasing / Prephasing</th>");
                  }

                  if (key == "Index") {
                    output += "Index: " + value.phasing+" / "+value.prephasing;
                  }
                  else {
                    output += "Read "+key+": "+value.phasing+" / "+value.prephasing;
                  }
                  output += "</br>";
                }
              });
              jQuery('#metricsTable tr:last').append("<td>"+output+"</td>");
            }
          });

          //error metrics
          var em = json.errorMetrics.rates;
          jQuery.each(em, function(index, obj) {
            jQuery.each(obj, function(key, value) {
              if (obj.lane == laneNum) {
                if (key == "meanError") {
                  if (jQuery('#qheaders th[id='+key+']').length == 0) {
                    jQuery('#qheaders').append("<th id='"+key+"'>Mean Error</th>");
                  }
                  jQuery('#metricsTable tr:last').append("<td>"+value+" &plusmn; "+obj.errorSD+"</td>");
                }
              }
            });
          });

          Stats.qualityQ30ByCyclePlot(json.qualityMetrics.perCycleQualityScores);
          Stats.densityBoxPlot(json.tileMetrics.clusterDensities, json.summary.numLanes, laneNum);
          Stats.qualityScorePlot(json.qualityMetrics.combinedReadQualityScores.raw, json.qualityMetrics.perLaneQualityScores, json.summary.numLanes, laneNum);
        },
        'doOnError':
        function(json) {
          jQuery('#metrixchartsdiv').html(json.error);
        }
      }
    );
  },

  getInterOpMetrics : function(runAlias, platformType) {
    jQuery('#metrixdiv').html("<img src='/styles/images/ajax-loader.gif'/>");

    Fluxion.doAjax(
      'statsControllerHelperService',
      'getInterOpMetrics',
      {'runAlias':runAlias, 'platformType':platformType, 'url':ajaxurl},
      {'doOnSuccess':
        function(json) {
          jQuery('#metrixdiv').html("");
          //quality metrics
          jQuery('#metrixdiv').append("<h2>Summary</h2>");
          jQuery('#metrixdiv').append("<table class='list' id='summaryTable'><thead><tr id='sheaders'><th>Flowcell ID</th><th>Run Type</th><th>Cycles</th><th>Bases Mask</th></tr></thead><tbody></tbody></table>");

          jQuery('#summaryTable > tbody:last').append(jQuery('<tr>').append("<td>"+json.summary.flowcellId+"</td><td>"+json.summary.runType+"</td><td>"+json.summary.currentCycle+"/"+json.summary.totalCycles+"</td><td>"+json.summary.demultiplexIndex+"</td>"));

          jQuery('#metrixdiv').append("<div id='metrixchartsdiv'><table id='metricsPlotTable'><tbody><tr><td style='width:45%' id='qualityQ30ByCyclePlot'></td><td id='qualityScorePlot'></td></tr><tr><td id='densityBoxPlot'></td><td id='intensityByCyclePlot'></td></tr></tbody></table></div>");

          Stats.qualityQ30ByCyclePlot(json.qualityMetrics.perCycleQualityScores);
          Stats.intensityByCyclePlot(json.intensityMetrics.averages, json.summary.currentCycle, json.summary.numLanes);
          Stats.densityBoxPlot(json.tileMetrics.clusterDensities, json.summary.numLanes);
          Stats.qualityScorePlot(json.qualityMetrics.combinedReadQualityScores.raw, json.qualityMetrics.perLaneQualityScores, json.summary.numLanes);

          jQuery('#metrixdiv').append("<h2>Metrics</h2>");
          jQuery('#metrixdiv').append("<table class='list' id='metricsTable'><thead><tr id='qheaders'><th>Lane No.</th></tr></thead><tbody></tbody></table>");

          for (var lane = 1; lane < json.summary.numLanes+1; lane++) {
            jQuery('#metricsTable > tbody').append('<tr id="lane'+lane+'"><td>Lane '+lane+'</td></tr>');
          }

          //tile metrics
          //{"clusterDensities":[{"lane":1,"density":"834","densitySD":"92","densityPassingFilter":"722","densityPassingFilterSD":"50","densityPercentPassed":"86.6","units":"K/mm2"}]
          var cd = json.tileMetrics.clusterDensities;
          jQuery.each(cd, function(index, obj) {
            jQuery.each(obj, function(key, value) {
              if (key == "density") {
                if (jQuery('#qheaders th[id=density]').length == 0) {
                  jQuery('#qheaders').append("<th id='density'>Density "+obj.units+"</th>");
                }
                jQuery('#metricsTable tr:eq('+obj.lane+'):last').append('<td>'+obj.density+' &plusmn; '+obj.densitySD+'</td>');
              }

              if (key == "densityPassingFilter") {
                if (jQuery('#qheaders th[id=densityPassingFilter]').length == 0) {
                  jQuery('#qheaders').append("<th id='densityPassingFilter'>Density PF "+obj.units+"</th>");
                }
                jQuery('#metricsTable tr:eq('+obj.lane+'):last').append('<td>'+obj.densityPassingFilter+' &plusmn; '+obj.densityPassingFilterSD+'</td>');
              }

              if (key == "densityPercentPassed") {
                if (jQuery('#qheaders th[id=densityPercentPassed]').length == 0) {
                  jQuery('#qheaders').append("<th id='densityPercentPassed'>Density Passed %</th>");
                }
                jQuery('#metricsTable tr:eq('+obj.lane+'):last').append('<td>'+obj.densityPercentPassed+'</td>');
              }
            });
          });

          if (jQuery(json.qualityMetrics).length > 0) {
            var cm = json.qualityMetrics.combinedReadQualityScores;
            jQuery.each(cm, function(key, value) {
              if (key != "raw") {
                //add combined qscores to summary
                //"combinedReadQualityScores":{">Q20":96.72566652362707,">Q30":89.7416606943109}
                jQuery('#sheaders').append("<th>Quality "+key.replace(">", "&gt;")+"</th>");
                jQuery('#summaryTable tr:gt(0):last').append('<td>'+value.toFixed(2)+'%</td>');
              }
            });

            var qm = json.qualityMetrics.perLaneQualityScores;
            //"perLaneQualityScores":[{"lane":1,">Q20":96.64105035802294,">Q30":88.9216603526773,">Q40":20.68262353622945,"raw":[{"5":"46492415"},{"6":"20471476"},{"7":"143103000"},{"8":"55734604"},{"9":"12254404"},{"10":"67201395"},{"11":"22793269"},{"12":"22546252"},{"13":"23825791"},{"14":"12566510"},{"15":"65735037"},{"17":"52210617"},{"16":"26216691"},{"19":"67752596"},{"18":"85193076"},{"21":"34161879"},{"20":"132913093"},{"23":"139165499"},{"22":"48120848"},{"25":"193049152"},{"24":"163817129"},{"27":"273124858"},{"26":"186226529"},{"29":"368992273"},{"28":"124516853"},{"31":"779584637"},{"30":"455167706"},{"34":"2119454108"},{"35":"4050550629"},{"32":"572390834"},{"33":"1281578553"},{"38":"1008199209"},{"39":"1631854913"},{"36":"867398267"},{"37":"1944280122"},{"40":"1723703686"},{"41":"2734900938"}]}
            jQuery.each(qm, function(index, obj) {
              jQuery.each(obj, function(key, value) {
                if (key != "lane" && key != "raw") {
                  if (jQuery('#qheaders th[id='+key.replace(">", "")+']').length == 0) {
                    jQuery('#qheaders').append("<th id='"+key.replace(">", "")+"'>Quality "+key.replace(">", "&gt;")+"</th>");
                    jQuery('#metricsTable tr:eq('+obj.lane+'):last').append('<td>'+value+'</td>');
                  }
                  else {
                    jQuery('#metricsTable tr:eq('+obj.lane+'):last').append('<td>'+value+'</td>');
                  }
                }
              });
            });
          }

          //phasing metrics
          //"phasingMetrics":[{"lane":1,"1":{"phasing":"0.2","prephasing":"0.254"},"Index":{"phasing":"0","prephasing":"0"}}]
          var pm = json.tileMetrics.phasingMetrics;
          jQuery.each(pm, function(index, obj) {
            var output = "";
            jQuery.each(obj, function(key, value) {
              if (key != "lane") {
                if (jQuery('#qheaders th[id=phasing]').length == 0) {
                  jQuery('#qheaders').append("<th id='phasing'>Phasing / Prephasing</th>");
                }

                if (key == "Index") {
                  output += "Index: " + value.phasing+" / "+value.prephasing;
                }
                else {
                  output += "Read "+key+": "+value.phasing+" / "+value.prephasing;
                }
                output += "</br>";
              }
            });
            jQuery('#metricsTable tr:eq('+obj.lane+'):last').append("<td>"+output+"</td>");
          });

          //error metrics
          var em = json.errorMetrics.rates;
          jQuery.each(em, function(index, obj) {
            jQuery.each(obj, function(key, value) {
              if (key == "meanError") {
                if (jQuery('#qheaders th[id='+key+']').length == 0) {
                  jQuery('#qheaders').append("<th id='"+key+"'>Mean Error</th>");
                }
                jQuery('#metricsTable tr:eq('+obj.lane+'):last').append("<td>"+value+" &plusmn; "+obj.errorSD+"</td>");
              }
            });
          });
        },
        'doOnError':
        function(json) {
          jQuery('#metrixdiv').html(json.error);
        }
      }
    );
  },

  qualityQ30ByCyclePlot : function(json) {
    var q30Data = [];

    jQuery.each(json, function(index, obj) {
      var dd = [];
      //the smallest observation (sample minimum)
      dd[0] = obj.qMin;
      //lower quartile (Q1)
      dd[1] = obj.qQ1;
      //median (Q2)
      dd[2] = obj.qMedian;
      //upper quartile (Q3)
      dd[3] = obj.qQ3;
      //largest observation (sample maximum)
      dd[4] = obj.qMax;
      q30Data.push(dd);
    });

    var chart3 = new Highcharts.Chart({
      chart: {
        type: 'boxplot',
        renderTo: 'qualityQ30ByCyclePlot',
        zoomType: 'x',
        spacingRight: 20
      },
      title: {
        text: '%>Q30'
      },
      legend: {
        enabled: false
      },
      xAxis: {
      },
      yAxis: {
        title: {
          text: '% Bases >Q30'
        }
      },
      plotOptions: {
        boxplot: {
          medianColor: 'red'
        }
      },
      series: [{
        name: 'Q30',
        data: q30Data,
        tooltip: {
          headerFormat: '<em>Cycle {point.key}</em><br/>'
        }
      }]
    });
  },

  intensityByCyclePlot : function(json, cycleNum, numLanes, laneNum) {
    var allLanesA = [];
    var allLanesC = [];
    var allLanesT = [];
    var allLanesG = [];

    if (typeof laneNum !== "undefined") {
      jQuery.each(json, function(lane, obj) {
        if (obj.lane == laneNum) {
          for (var j = 0; j < obj.intA.length; j++) {
            allLanesA.push(obj.intA[j]);
            allLanesC.push(obj.intC[j]);
            allLanesT.push(obj.intT[j]);
            allLanesG.push(obj.intG[j]);
          }
        }
      });
    }
    else {
      jQuery.each(json, function(lane, obj) {
        if (obj.lane == 1) {
          for (var j = 0; j < obj.intA.length; j++) {
            allLanesA.push(obj.intA[j]);
            allLanesC.push(obj.intC[j]);
            allLanesT.push(obj.intT[j]);
            allLanesG.push(obj.intG[j]);
          }
        }
        else {
          for (var i = 0; i < obj.intA.length; i++) {
            allLanesA[i] += obj.intA[i];
            allLanesC[i] += obj.intC[i];
            allLanesT[i] += obj.intT[i];
            allLanesG[i] += obj.intG[i];
          }
        }
      });

      for (var i = 0; i < allLanesA.length; i++) {
        allLanesA[i] = allLanesA[i]/numLanes;
        allLanesC[i] = allLanesC[i]/numLanes;
        allLanesT[i] = allLanesT[i]/numLanes;
        allLanesG[i] = allLanesG[i]/numLanes;
      }
    }

    var chart1 = new Highcharts.Chart({
      chart: {
        type: 'line',
        renderTo: 'intensityByCyclePlot',
        zoomType: 'x',
        spacingRight: 20
      },
      title: {
        text: 'Average Intensity by Cycle',
        x: -20 //center
      },
      xAxis: {
        //tickInterval: 1
      },
      yAxis: {
        title: {
          text: 'All Bases Intensity'
        },
        plotLines: [{
          value: 0,
          width: 1,
          color: '#808080'
        }]
      },
      legend: {
        layout: 'vertical',
        align: 'right',
        verticalAlign: 'middle',
        borderWidth: 0
      },
      series: [{
        name: 'A',
        data: allLanesA
      }, {
        name: 'C',
        data: allLanesC
      }, {
        name: 'T',
        data: allLanesT
      }, {
        name: 'G',
        data: allLanesG
      }],
      plotOptions: {
        line: {
          lineWidth: 1,
          marker: {
            enabled: false
          },
          shadow: false
        }
      }
    });
  },

  qualityScorePlot : function(combined, perLane, numLanes, laneNum) {
    var cq10 = 0;
    var cq20 = 0;
    var cq30 = 0;
    var cq40 = 0;
    var cq50 = 0;
    var cqdata = [];

    var seriesData = [];
    var yAxis = [];
    var legend = {layout: 'vertical',align: 'right',verticalAlign: 'middle',borderWidth: 0};

    if (typeof laneNum !== "undefined") {
      jQuery.each(perLane, function(index, obj) {
        if (obj.lane == laneNum) {
          var rawdata = obj.raw;

          var q10 = 0;
          var q20 = 0;
          var q30 = 0;
          var q40 = 0;
          var q50 = 0;
          var qdata = [];

          for (var i = 0; i < rawdata.length; i++) {
            if (i >= 0 && i < 10) {
              q10 += rawdata[i];
            }

            if (i >= 10 && i < 20) {
              q20 += rawdata[i];
            }

            if (i >= 20 && i < 30) {
              q30 += rawdata[i];
            }

            if (i >= 30 && i < 40) {
              q40 += rawdata[i];
            }

            if (i >= 40) {
              q50 += rawdata[i];
            }
          }

          qdata.push(q10);
          qdata.push(q20);
          qdata.push(q30);
          qdata.push(q40);
          qdata.push(q50);

          var lreadtotal = q10+q20+q30+q40+q50;

          seriesData.push({name:'Lane '+(index+1), yAxis:0, data:qdata, type: 'column', tooltip: {
            formatter: function() {
              return (this.y/1000000).toFixed(0) + 'm reads >Q'+this.x+'<br/><em>' + ((this.y/lreadtotal)*100).toFixed(2) + '%</em><br/>';
            }
          }});

          yAxis.push({title: {text: 'Reads Per Lane'}, plotLines: [{ value: 0, width: 1, color: '#808080' }]});
          legend.enabled = false;
        }
      });
    }
    else {
      for (var i = 0; i < combined.length; i++) {
        if (i >= 0 && i < 10) {
          cq10 += combined[i];
        }

        if (i >= 10 && i < 20) {
          cq20 += combined[i];
        }

        if (i >= 20 && i < 30) {
          cq30 += combined[i];
        }

        if (i >= 30 && i < 40) {
          cq40 += combined[i];
        }

        if (i >= 40) {
          cq50 += combined[i];
        }
      }

      var readtotal = cq10+cq20+cq30+cq40+cq50;
      cqdata.push(cq10);
      cqdata.push(cq20);
      cqdata.push(cq30);
      cqdata.push(cq40);
      cqdata.push(cq50);

      seriesData = [{
        name:'Combined',
        data: cqdata,
        type: 'column',
        tooltip: {
          formatter: function() {
            return (this.y/1000000).toFixed(0) + 'm reads >Q'+this.x+'<br/><em>' + ((this.y/readtotal)*100).toFixed(2) + '%</em><br/>';
          }
        }
      }];

      if (numLanes > 1) {
        jQuery.each(perLane, function(index, obj) {
          var rawdata = obj.raw;

          var q10 = 0;
          var q20 = 0;
          var q30 = 0;
          var q40 = 0;
          var q50 = 0;
          var qdata = [];

          for (var i = 0; i < rawdata.length; i++) {
            if (i >= 0 && i < 10) {
              q10 += rawdata[i];
            }

            if (i >= 10 && i < 20) {
              q20 += rawdata[i];
            }

            if (i >= 20 && i < 30) {
              q30 += rawdata[i];
            }

            if (i >= 30 && i < 40) {
              q40 += rawdata[i];
            }

            if (i >= 40) {
              q50 += rawdata[i];
            }
          }

          qdata.push(q10);
          qdata.push(q20);
          qdata.push(q30);
          qdata.push(q40);
          qdata.push(q50);

          var lreadtotal = cq10+cq20+cq30+cq40+cq50;

          seriesData.push({name:'Lane '+(index+1), yAxis:1, data:qdata, type: 'column', tooltip: {
            formatter: function() {
              return (this.y/1000000).toFixed(0) + 'm reads >Q'+this.x+'<br/><em>' + ((this.y/lreadtotal)*100).toFixed(2) + '%</em><br/>';
            }
          }});
        });
      }

      yAxis.push({title: {text: 'Total Reads'},plotLines: [{value: 0,width: 1,color: '#808080'}]});
      yAxis.push({title: {text: 'Reads Per Lane'},opposite:true,plotLines: [{value: 0,width: 1,color: '#808080'}]});
    }

    var chart2 = new Highcharts.Chart({
      chart: {
        type: 'column',
        renderTo: 'qualityScorePlot'
      },
      title: {
        text: 'QScores: all lanes, all reads, all cycles',
        x: -20 //center
      },
      xAxis: {
        categories: [0,10,20,30,40]
      },
      yAxis: yAxis,
      legend: legend,
      series: seriesData
    });
  },

  densityBoxPlot : function(json, numLanes, laneNum) {
    var cats = [];
    var densityData = [];
    var densityDataPF = [];

    if (typeof laneNum !== "undefined") {
      cats.push(laneNum);

      jQuery.each(json, function(index, obj) {
        if (obj.lane == laneNum) {
          var dd = [];
          //the smallest observation (sample minimum)
          dd[0] = obj.densityMin;
          //lower quartile (Q1)
          dd[1] = obj.densityQ1;
          //median (Q2)
          dd[2] = obj.density;
          //upper quartile (Q3)
          dd[3] = obj.densityQ3;
          //largest observation (sample maximum)
          dd[4] = obj.densityMax;
          densityData.push(dd);

          var ddPF = [];
          ddPF[0] = obj.densityPassingFilterMin;
          ddPF[1] = obj.densityPassingFilterQ1;
          ddPF[2] = obj.densityPassingFilter;
          ddPF[3] = obj.densityPassingFilterQ3;
          ddPF[4] = obj.densityPassingFilterMax;
          densityDataPF.push(ddPF);
        }
      });
    }
    else {
      for (var i = 1; i < numLanes+1; i++) {
        cats.push(i);
      }

      jQuery.each(json, function(index, obj) {
        var dd = [];
        //the smallest observation (sample minimum)
        dd[0] = obj.densityMin;
        //lower quartile (Q1)
        dd[1] = obj.densityQ1;
        //median (Q2)
        dd[2] = obj.density;
        //upper quartile (Q3)
        dd[3] = obj.densityQ3;
        //largest observation (sample maximum)
        dd[4] = obj.densityMax;
        densityData.push(dd);

        var ddPF = [];
        ddPF[0] = obj.densityPassingFilterMin;
        ddPF[1] = obj.densityPassingFilterQ1;
        ddPF[2] = obj.densityPassingFilter;
        ddPF[3] = obj.densityPassingFilterQ3;
        ddPF[4] = obj.densityPassingFilterMax;
        densityDataPF.push(ddPF);
      });
    }

    var chart3 = new Highcharts.Chart({
      chart: {
          type: 'boxplot',
          renderTo: 'densityBoxPlot'
      },
      title: {
          text: 'Cluster Density'
      },
      legend: {
          enabled: false
      },
      xAxis: {
        categories: cats,
        title: {
          text: 'Lane'
        }
      },
      yAxis: {
        title: {
          text: 'Density (K/mm2)'
        }
      },
      plotOptions: {
        boxplot: {
          medianColor: 'red'
        }
      },
      series: [{
        name: 'Density',
        data: densityData,
        tooltip: {
          headerFormat: '<em>Lane {point.key}</em><br/>'
        }
      },
      {
        name: 'Density Passing Filter',
        data: densityDataPF,
        color: '#8bbc21',
        tooltip: {
          headerFormat: '<em>Lane {point.key}</em><br/>'
        }
      }]
    });
  }
};