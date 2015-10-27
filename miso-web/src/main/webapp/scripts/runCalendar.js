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

var width;
var date = new Date;
//Default Selection
var cellwidth;
var tempcolour = ["#1f77b4", "#aec7e8", "#ff7f0e", "#ffbb78", "#2ca02c", "#98df8a", "#d62728", "#ff9896", "#9467bd", "#c5b0d5", "#8c564b", "#c49c94", "#e377c2", "#f7b6d2", "#7f7f7f", "#c7c7c7", "#bcbd22", "#dbdb8d", "#17becf", "#9edae5" ];
var day = d3.time.format("%w"), week = d3.time.format("%U"), percent = d3.format(".1%"), format = d3.time.format("%Y-%m-%d");

var months = ["Jan", "Feb", "Mar", "April", "May", "June", "July", "Aug", "Sept", "Oct", "Nov", "Dec"];
var dates = ["1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31"];

//Default Year rage
var temp_range_start;
var temp_range_stop;
var range_start;
var range_stop;
var readFile;
var noofmachine;
var m, w, h, z;

pw = 50;
n = 17;
ph = n >> 1;
var flowcell;
var day = d3.time.format("%w"), week = d3.time.format("%U"), percent = d3.format(".1%"), format = d3.time.format("%Y-%m-%d");

var vis, data, instrument;

//Inint function to set filters
function init(selectedYear, machine, numberofmachine) {
  readFile = "/miso/d3graph/run";
  flowcell = 1;

  d3.json(readFile, function (json) {
    datanew = json.filter(function (d) {
      if (d.Health == "Running" && d.Stop == "null") {
        d.Stop = (getyear(d.Start)) + "-" + (getmonth(d.Start)) + "-" + (getdate(d.Start));
      }
      if (d.Health == "Failed" || d.Health == "Unknown") {
        d.Stop = (getyear(d.Start)) + "-" + (getmonth(d.Start)) + "-" + (getdate(d.Start));
      }
      if ((d.Health == "Completed" || d.Health == "Stopped" ) && d.Stop == "null") {
        d.Stop = (getyear(d.Start)) + "-" + (getmonth(d.Start)) + "-" + (getdate(d.Start));
      }
      if (getyear(d.Stop) < getyear(d.Start) || (getyear(d.Stop) == getyear(d.Start) && getmonth(d.Stop) < getmonth(d.Start)) || (getyear(d.Stop) == getyear(d.Start) && getmonth(d.Stop) == getmonth(d.Start) && getdate(d.Stop) < getdate(d.Start))) {
        d.Stop = (getyear(d.Start)) + "-" + (getmonth(d.Start)) + "-" + (getdate(d.Start));
      }
      return d;
    });
    data = datanew;
    drawCalendar(selectedYear, machine, numberofmachine)
  });
}


//width parametes
function drawCalendar(selectedYear, machine, numberofmachine) {
  width = 0.8 * getWindowWidth();
  cellwidth = width / 31;
  m = [19, 20, 20, 19], // top right bottom left margin
          w = width, // width
          h = 700 - m[0] - m[2], // height
          z = cellwidth; // cell size

  var currentTime = new Date();
  range_start = Number(currentTime.getFullYear());
  range_stop = parseInt(range_start) + 1;

  if (selectedYear == "lweek") {
    var month_filter_start = parseInt(currentTime.getMonth()) + 1;
    var month_filter_stop = parseInt(currentTime.getMonth()) + 1;
    var date_filter_start = currentTime.getDate() - 7;
    if (date_filter_start < 1) {
      month_filter_start -= 1;
      date_filter_start += daysInMonth(month_filter_start, currentTime.getFullYear());
    }
    if (month_filter_start < 1) {
      range_start -= 1;
    }
    var date_filter_stop = currentTime.getDate();
  }
  else if (selectedYear == "lmonth") {
    var month_filter_start = parseInt(currentTime.getMonth());
    var month_filter_stop = parseInt(currentTime.getMonth()) + 1;
    var date_filter_start = currentTime.getDate();

    var date_filter_stop = currentTime.getDate();
    range_start = Number(currentTime.getFullYear());
    range_stop = parseInt(range_start) + 1;
    if (month_filter_start < 1) {
      range_start -= 1;
    }
  }
  else if (selectedYear == "l3month") {
    var month_filter_start = currentTime.getMonth() - 2; // +1 -3
    var month_filter_stop = parseInt(currentTime.getMonth()) + 1;
    range_start = Number(currentTime.getFullYear());
    range_stop = parseInt(range_start) + 1;

    if (parseInt(month_filter_start < 1)) {
      range_start -= 1;
      month_filter_start = 12 + month_filter_start;
    }
    var date_filter_start = currentTime.getDate();
    var date_filter_stop = currentTime.getDate();
  }
  else if (selectedYear == "l6month") {
    var month_filter_start = currentTime.getMonth() - 5; // +1 -3
    var month_filter_stop = parseInt(currentTime.getMonth()) + 1;
    range_start = Number(currentTime.getFullYear());
    range_stop = parseInt(range_start) + 1;
    if (month_filter_start < 1) {
      range_start -= 1;
      month_filter_start = 12 + month_filter_start;
    }
    var date_filter_start = currentTime.getDate();
    var date_filter_stop = currentTime.getDate();
  }
  else if (selectedYear == "cyear") {
    var month_filter_start = "01";
    var month_filter_stop = parseInt(currentTime.getMonth()) + 1;
    var date_filter_start = "01"
    var date_filter_stop = currentTime.getDate();
    range_start = Number(currentTime.getFullYear());
    range_stop = parseInt(range_start) + 1;
  }
  else if (selectedYear == "lyear") {
    var month_filter_start = "01";
    var month_filter_stop = "12";
    var date_filter_start = "01"
    var date_filter_stop = "31";
    range_start = Number(currentTime.getFullYear()) - 1;
    range_stop = parseInt(range_start) + 1;
  }
  else if (selectedYear == "custom") {
    if (jQuery("#calendarfrom").val() != "" && jQuery("#calendarto").val()) {
      var calendarfrom = jQuery("#calendarfrom").val().split("-");
      var calendarto = jQuery("#calendarto").val().split("-");
      var month_filter_start = calendarfrom[1];
      var month_filter_stop = calendarto[1];
      var date_filter_start = calendarfrom[0];
      var date_filter_stop = calendarto[0];
      range_start = calendarfrom[2];
      range_stop = Number(calendarto[2]) + 1;
    }
  }
  var years;
  if ((range_stop - range_start) > 1) {
    years = "multi";
  }
  else {
    years = "single";
  }

  instrument = machine;
  if (machine == 0) {
    noofmachine = Number(numberofmachine) + 1;
  }
  else {
    noofmachine = 1;
  }

  jQuery("#daterange").html("From " + date_filter_start + "-" + month_filter_start + "-" + range_start + " to " + date_filter_stop + "-" + month_filter_stop + "-" + (range_stop - 1));
  document.getElementById("chartD3Calendar").innerHTML = "";

  for (var j = 0; j < (range_stop - range_start); j++) {
    eachCalendar((Number(range_start) + j), Number(range_start) + j + 1, data);
  }

  function eachCalendar(start, stop, data) {
    var temp_data = data
    temp_range_start = start;
    temp_range_stop = stop;

    jQuery("#chartD3Calendar").append("<div id=chartD3Calendar" + start + "></div>");

    vis = d3.select("#chartD3Calendar" + temp_range_start)
            .selectAll("svg")
            .data(d3.range(temp_range_start, temp_range_stop))
            .enter().append("svg:svg")
            .attr("width", w * 1.1)
            .attr("height", h)
            .attr("class", "RdYlGn")
            .append("svg:g")
            .attr("transform", "translate(" + pw + "," + ph + ")");


    vis.selectAll("text.day")
            .data(months)
            .enter().append("svg:text")
            .attr("transform", function (d, i) {
                    return"translate(20," + (i + 0.50) * z + ")";
                  })
            .attr("text-anchor", "end")
            .text(function (d) {
                    return d;
                  });

    vis.selectAll("text.day")
            .data(dates)
            .enter().append("svg:text")
            .attr("x", function (d, i) {
                    return (i * z) + (z * 1.5);
                  })
            .attr("y", 0)
            .attr("text-anchor", "middle")
            .text(function (d) {
                    return d;
                  });
    vis.selectAll("text.day")
            .data(d3.range(temp_range_start, temp_range_stop))
            .enter().append("svg:text")
            .attr("transform", function (d, i) {
                    return"translate(-16," + (6.25) * z + ")rotate(-90)";
                  })
            .attr("text-anchor", "end")
            .text(function (d) {
                    return d;
                  });

    vis.selectAll("text.day")
            .data(dates)
            .enter().append("svg:text")
            .attr("x", function (d, i) {
                    return (i * z) + (z * 1.5);
                  })
            .attr("y", z * 12 + 10)
            .attr("text-anchor", "middle")
            .text(function (d) {
                    return d;
                  });

    if (noofmachine == 1) {
      flowcell = 1;
      jQuery("#legendD3Calendar").fadeOut();

      vis.selectAll("rect.day")
              .data(temp_data.filter(function (d) {
                if (d.Instrument == machine && d.InstrumentName == "Illumina HiSeq 2000") {
                  flowcell = 2;
                }


                if (years == "multi") {
                  return (d.Instrument == machine && (
                    // last year
                          ((getyear(d.Start) < temp_range_start && getyear(d.Stop) == temp_range_start && getmonth(d.Stop) < month_filter_stop && temp_range_start != range_start) || (getyear(d.Start) < range_start && getyear(d.Start) == temp_range_start && getmonth(d.Start) == month_filter_stop && getdate(d.Stop) <= date_filter_stop))
                            // next year
                                  || ((getyear(d.Stop) > temp_range_start && getyear(d.Start) == temp_range_start && getmonth(d.Start) > month_filter_start && temp_range_stop != range_stop) || (getyear(d.Stop) > temp_range_start && getyear(d.Start) == temp_range_start && getmonth(d.Start) == month_filter_start && getdate(d.Start) >= date_filter_start))
                            //(getyear(d.Start) < temp_range_start && getyear(d.Stop) == temp_range_start && getmonth(d.Stop) < month_filter_stop && getmonth(d.Stop) > month_filter_start && temp_range_start == range_start)
                            // present
                                  || ((getyear(d.Start) == temp_range_start && getyear(d.Stop) == temp_range_start && getmonth(d.Start) > month_filter_start && getmonth(d.Stop) < month_filter_stop && temp_range_start == range_start && temp_range_stop == range_stop))
                                  || ((getyear(d.Start) == temp_range_start && getyear(d.Stop) == temp_range_start && (getmonth(d.Start) > month_filter_start || (getmonth(d.Start) == month_filter_start && getdate(d.Start) >= date_filter_start)) && temp_range_start < range_stop && temp_range_stop != range_stop && temp_range_start == range_start))
                                  || // last year after month filter
                          ((getyear(d.Start) == temp_range_start && getyear(d.Stop) == temp_range_start && (temp_range_start != range_start)))
                                  || //present same month
                          ((getyear(d.Start) == temp_range_start && getyear(d.Stop) == temp_range_start && getmonth(d.Start) == month_filter_start && getmonth(d.Stop) == month_filter_stop && getdate(d.Start) >= date_filter_start && getdate(d.Stop) <= date_filter_stop))
                          ));
                }
                else {
                  return (d.Instrument == machine && ((getyear(d.Start) < temp_range_start && getyear(d.Stop) == temp_range_start && getmonth(d.Stop) < month_filter_stop) && (getmonth(d.Stop) > month_filter_start || (getmonth(d.Stop) == month_filter_start && getdate(d.Stop) > date_filter_start)) || (getyear(d.Start) == temp_range_start && getyear(d.Stop) > temp_range_start && getmonth(d.Start) > month_filter_start) ||
                                                      ((getyear(d.Start) == range_start && getyear(d.Stop) == range_start) &&
                                                       ((getmonth(d.Start) > month_filter_start && getmonth(d.Stop) < month_filter_stop) ||
                                                        ((getmonth(d.Start) == month_filter_start && getdate(d.Start) >= date_filter_start && getdate(d.Start) <= date_filter_stop) || (getmonth(d.Stop) == month_filter_stop && getdate(d.Stop) <= date_filter_stop && ((getdate(d.Stop) >= date_filter_start && getmonth(d.Stop) == month_filter_start) || month_filter_start != month_filter_stop)))
                                                               ))));
                }
              }))
              .enter().append('svg:rect')
              .attr('fill', function (d) {
                      var reg = /^B+.*$/;
                      var temp = 1;
                      if (reg.test(d.Description)) {
                        temp = 2;
                      }

                      if (flowcell == 2 && temp == 2) {
                        return ("yellow");
                      }
                      else {
                        return ("green");
                      }
                    })
              .attr("width", findWidth)
              .attr("height", function (d) {
                      return((z / (noofmachine * flowcell)) - 1);
                    })
              .attr("x", function (d) {
                      if (getyear(d.Start) == temp_range_start) {
                        return(getdate(d.Start) * z + 1);
                      }
                      else {
                        return (z + 1);
                      }
                    })
              .attr("y", function (d) {
                      var reg = /^B+.*$/;
                      var temp = 1;
                      if (reg.test(d.Description)) {
                        temp = 2;
                      }
                      if (flowcell == 2 && temp == 2) {
                        if (getyear(d.Start) == temp_range_start) {
                          return(((getmonth(d.Start) - 1) * z) + (z / (noofmachine * flowcell)) + 1 );
                        }
                        else {
                          return(((getmonth(d.Stop) - 1) * z) + (z / (noofmachine * flowcell)) + 1);
                        }
                      }
                      else {
                        if (getyear(d.Start) == temp_range_start) {
                          return(((getmonth(d.Start) - 1) * z) + 1);
                        }
                        else {
                          return(((getmonth(d.Stop) - 1) * z) + 1);
                        }
                      }
                    })
              .on("click", over)
              .attr("opacity", 0.7)
              .style("cursor", "pointer")
              .append("svg:title")
              .text(function (d) {
                      return(d.Start + ":" + d.Stop + "," + d.Name);
                    });
    }
    else {
      flowcell = 1;
      jQuery("#legendD3Calendar").fadeIn();

      vis.selectAll("rect.day")
              .data(temp_data.filter(function (d) {

                if (years == "multi") {
                  return (

//middle year
// last year
                          ((getyear(d.Start) < temp_range_start && getyear(d.Stop) == temp_range_start && getmonth(d.Stop) < month_filter_stop && temp_range_start != range_start) || (getyear(d.Start) < range_start && getyear(d.Start) == temp_range_start && getmonth(d.Start) == month_filter_stop && getdate(d.Stop) <= date_filter_stop))
// next year

                          || ((getyear(d.Stop) > temp_range_start && getyear(d.Start) == temp_range_start && getmonth(d.Start) > month_filter_start && temp_range_stop != range_stop) || (getyear(d.Stop) > temp_range_start && getyear(d.Start) == temp_range_start && getmonth(d.Start) == month_filter_start && getdate(d.Start) >= date_filter_start))

//(getyear(d.Start) < temp_range_start && getyear(d.Stop) == temp_range_start && getmonth(d.Stop) < month_filter_stop && getmonth(d.Stop) > month_filter_start && temp_range_start == range_start)

// present 

                          || ((getyear(d.Start) == temp_range_start && getyear(d.Stop) == temp_range_start && getmonth(d.Start) > month_filter_start && getmonth(d.Stop) < month_filter_stop && temp_range_start == range_start && temp_range_stop == range_stop))


                          || ((getyear(d.Start) == temp_range_start && getyear(d.Stop) == temp_range_start && (getmonth(d.Start) > month_filter_start || (getmonth(d.Start) == month_filter_start && getdate(d.Start) >= date_filter_start)) && temp_range_start < range_stop && temp_range_stop != range_stop && temp_range_start == range_start))

                          || // last year after month filter

                          ((getyear(d.Start) == temp_range_start && getyear(d.Stop) == temp_range_start && (temp_range_start != range_start)))

                          || //present same month

                          ((getyear(d.Start) == temp_range_start && getyear(d.Stop) == temp_range_start && getmonth(d.Start) == month_filter_start && getmonth(d.Stop) == month_filter_stop && getdate(d.Start) >= date_filter_start && getdate(d.Stop) <= date_filter_stop))
                          );
                }
                else {
                  return ((getyear(d.Start) < temp_range_start && getyear(d.Stop) == temp_range_start && getmonth(d.Stop) < month_filter_stop) && (getmonth(d.Stop) > month_filter_start || (getmonth(d.Stop) == month_filter_start && getdate(d.Stop) > date_filter_start)) || (getyear(d.Start) == temp_range_start && getyear(d.Stop) > temp_range_start && getmonth(d.Start) > month_filter_start) ||
                          ((getyear(d.Start) == range_start && getyear(d.Stop) == range_start) &&
                           ((getmonth(d.Start) > month_filter_start && getmonth(d.Stop) < month_filter_stop) ||
                            ((getmonth(d.Start) == month_filter_start && getdate(d.Start) >= date_filter_start && ((getdate(d.Start) <= date_filter_stop && getmonth(d.Start) == month_filter_stop) || month_filter_start != month_filter_stop))
                                    || (getmonth(d.Stop) == month_filter_stop && getdate(d.Stop) <= date_filter_stop && ((getdate(d.Stop) >= date_filter_start && getmonth(d.Stop) == month_filter_start) || month_filter_start != month_filter_stop)))
                                   )));
                }
              }))
              .enter().append('svg:rect')
              .attr('fill', function (d) {
                      return(tempcolour[d.Instrument]);
                    })

              .attr("opacity", 0.7)
              .attr("height", function (d) {
                      return((z / noofmachine) - 1);
                    })
              .attr("x", function (d) {

                      if (getyear(d.Start) == temp_range_start) {
                        return(getdate(d.Start) * z);
                      }
                      else {
                        return z;
                      }
                    })
              .attr("y", function (d) {


                      if (getyear(d.Start) == temp_range_start) {
                        return(((getmonth(d.Start) - 1) * z) + (d.Instrument) * z / noofmachine + 1);
                      }
                      else {
                        return(((getmonth(d.Stop) - 1) * z) + (d.Instrument) * z / noofmachine + 1);
                      }
                    })
              .attr("width", findWidth)
              .on("click", over)
              .style("cursor", "pointer")
              .append("svg:title")
              .text(function (d) {
                      return(d.Start + ":" + d.Stop + "," + d.Name);
                    });
    }


    vis.selectAll("path.month")
            .data(function (d) {
                    return d3.time.months(new Date(d, 0, 1), new Date(d + 1, 0, 1));
                  })
            .enter().append("svg:path")
            .attr("class", "month")
            .attr("d", monthPath);

    function monthoutline() {
    }

    monthoutline();
    return vis;
  }

  if (Number(jQuery("#chartD3Calendar").height()) > 100) {
    jQuery("#tabResource").height(Number(jQuery("#chartD3Calendar").height()) + 200);
  }
}


//functions


//fill if months are diff
function findWidth(g) {

  if (g.Stop == "null") {
    if (g.Health == "Running") {
      var date = new Date;
      g.Stop = date.getFullYear() + "-" + (date.getMonth() + 1) + "-" + date.getDate();
      findWidth(g);
    }
    else {
      return (z - 1);
    }
  }
  else if (g.Stop == "null") {
    return (z - 1);
  }
  else if (getmonth(g.Stop) == getmonth(g.Start) && getyear(g.Stop) == getyear(g.Start) && getyear(g.Start) == temp_range_start) {

    return((getdate(g.Stop) - getdate(g.Start) + 1) * z - 1);

  }
  else if (getyear(g.Stop) == getyear(g.Start) && getyear(g.Stop) == temp_range_start) {
    var i = getmonth(g.Start);
    var j = getmonth(g.Stop);

    for (i; i < j; i++) {
      if ((j - i) >= 2) {
        fillTheRest((daysInMonth((Number(i) + 1), getyear(g.Start))), g.Instrument, g, g.Description);
      }
      else {
        fillTheRest(getdate(g.Stop), (Number(i) + 1), g.Instrument, g, g.Description);
      }
    }

    return ((daysInMonth(getmonth(g.Start), getyear(g.Start)) - getdate(g.Start) + 1) * z - 1);
  }
  else if (getyear(g.Stop) > temp_range_start) {
    var i = getmonth(g.Start);
    var j = 12;
    for (i; i < j; i++) {
      if ((j - i) >= 2) {
        fillTheRest((daysInMonth((Number(i) + 1), getyear(g.Start))), g.Instrument, g, g.Description);
      }
      else {
        fillTheRest(daysInMonth(12), (Number(i) + 1), g.Instrument, g, g.Description);
      }
    }

    return ((daysInMonth(getmonth(g.Start), getyear(g.Start)) - getdate(g.Start) + 1) * z - 1);
  }
  else {
    var i = getmonth(g.Stop) - 1;
    var j = 1;
    for (i; i >= j; i--) {
      if ((i - j) >= 1) {
        fillTheBegin((daysInMonth((Number(i)), getyear(g.Start))), g.Instrument, g, g.Description);
      }
      else {
        fillTheBegin(daysInMonth(1), (Number(1)), g.Instrument, g, g.Description);
      }
    }


    return (getdate(g.Stop) * z - 1);
  }
}


//fill next months and year
function fillTheRest(date, month, Instrument, g, Description) {


  vis.selectAll("rect.day")
          .data(d3.range(temp_range_start, temp_range_stop))
          .enter().append('svg:rect')
          .attr('fill', function () {
                  if (noofmachine == 1) {
                    var reg = /^B+.*$/;
                    var temp = 1;

                    if (reg.test(Description)) {
                      temp = 2;
                    }

                    if (flowcell == 2 && temp == 2) {
                      return ("yellow");
                    }
                    else {
                      return ("green");
                    }
                  }
                  else {
                    return tempcolour[Instrument];
                  }
                })
          .on("click", function () {
                over(g);
              })
          .style("cursor", "pointer")
          .attr("width", date * z - 1)
          .attr("opacity", 0.7)
          .attr("height", function () {
                  return((z / (noofmachine * flowcell)) - 1);
                })
          .attr("x", function () {
                  return(z);
                })
          .attr("y", function (g) {
                  if (noofmachine == 1) {
                    var reg = /^B+.*$/;
                    var temp = 1;
                    if (reg.test(Description)) {
                      temp = 2;
                    }
                    if (flowcell == 2 && temp == 2) {
                      return(((month - 1) * z) + (z / (noofmachine * flowcell)) + 1 );
                    }
                    else {
                      return(((month - 1) * z) + 1);
                    }
                  }
                  else {
                    return((month - 1) * z + (Instrument) * z / noofmachine + 1 )
                  }
                })
          .append("svg:title")
          .text(function (d) {
                  return(g.Start + ":" + g.Stop + "," + g.Name);
                });
}


///to show runs from previous year
function fillTheBegin(date, month, Instrument, g, Description) {

  vis.selectAll("rect.day")
          .data(d3.range(temp_range_start, temp_range_stop))
          .enter().append('svg:rect')
          .attr('fill', function () {
                  if (noofmachine == 1) {
                    var reg = /^B+.*$/;
                    var temp = 1;

                    if (reg.test(Description)) {
                      temp = 2;
                    }

                    if (flowcell == 2 && temp == 2) {
                      return ("yellow");
                    }
                    else {
                      return ("green");
                    }
                  }
                  else {
                    return tempcolour[Instrument];
                  }
                })
          .attr("width", date * z - 1)
          .attr("opacity", 1)
          .attr("height", function () {
                  return((z / (noofmachine * flowcell)) - 1);
                })
          .attr("x", function () {
                  return(z);
                })
          .on("click", function () {
                over(g);
              })
          .style("cursor", "pointer")
          .attr("y", function (g) {
                  if (noofmachine == 1) {
                    var reg = /^B+.*$/;
                    var temp = 1;
                    if (reg.test(Description)) {
                      temp = 2;
                    }
                    if (flowcell == 2 && temp == 2) {
                      return(((month - 1) * z) + (z / (noofmachine * flowcell)) + 1 );
                    }
                    else {
                      return(((month - 1) * z) + 1);
                    }
                  }
                  else {
                    return((month - 1) * z + (Instrument) * z / noofmachine + 1)
                  }

                })
          .append("svg:title")
          .text(function (d) {
                  return(g.Start + ":" + g.Stop + "," + g.Name);
                });

}


// month outline
function monthPath(t0) {
  var t1 = new Date(t0.getYear(), t0.getMonth() + 1, 0);

  return"M" + z + "," + (t1.getMonth()) * z
                + "H" + (daysInMonth(t1.getMonth() + 1, t1.getYear()) + 1) * z + "V" + (t1.getMonth() + 1) * z
                + "H" + z + "V" + z
                + 1 + "Z";
}


//count days in each month
function daysInMonth(month, year) {
  var m = [31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31];
  if (month != 2)return m[month - 1];
  if (year % 4 != 0)return m[1];
  if (year % 100 == 0 && year % 400 != 0)return m[1];
  return m[1] + 1;
}


//windows width
function getWindowWidth() {
  return window.innerWidth || document.body.clientWidth;
}


//mouse click function
function over(g) {
  var list = "<ul class=\"bullets\">";
  list += "<li><a href='run/" + g.ID + "'>" + g.Name + "</a>  started on the Date:" + g.Start + " and was " + g.Health + " on " + g.Stop + " on the machine " + g.InstrumentName + "</li>";
  jQuery.colorbox({
                    width: "90%",
                    html: list + "</ul>"});

  return;
}

// return date
function getdate(date) {
  var temp = date.split("-");
  return temp[2];
}


//return month
function getmonth(date) {
  var temp = date.split("-");
  return temp[1];
}


//return year
function getyear(date) {
  var temp = date.split("-");
  return temp[0];
}

function addRestrictedDatePicker(id) {
  var year = jQuery('input:radio[name=calendarYear]:checked').val();

  jQuery("#" + id).datepicker({dateFormat: 'dd/mm/yy', showButtonPanel: true,
                                minDate: new Date(year, 0, 1),
                                maxDate: new Date(year, 11, 31)

                              });


}

function editRestrictedDatePicker(id) {
  var year = jQuery('input:radio[name=calendarYear]:checked').val();

  jQuery("#" + id).datepicker('change', {dateFormat: 'dd/mm/yy', showButtonPanel: true,
    minDate: new Date(year, 0, 1),
    maxDate: new Date(year, 11, 31)

  });


}
