#include <ctime>
#include <iomanip>
#include <iostream>
#include <sstream>
#include <string>
#include <vector>
#include <interop/interop.h>
#include <json/json.h>

int length(const illumina::interop::model::run::cycle_range &range) {

  return (range.last_cycle() >= range.first_cycle())
             ? range.last_cycle() - range.first_cycle() + 1
             : 0;
}

/**
 * Convert a "candlestick" or "line" point into a JSON object.
 *
 * This logic is taken from the gnuplot rendering code provided by Illumina.
 * Frustratingly, the Illumina interop library stuff all the line data into the
 * candlestick objects for no clear reason.
 */
Json::Value convert_point(
    bool is_candlestick,
    const illumina::interop::model::plot::candle_stick_point &input_point) {
  Json::Value output_point(Json::objectValue);
  output_point["x"] = input_point.x();
  if (is_candlestick) {
    output_point["low"] = input_point.lower();
    output_point["q1"] = input_point.p25();
    output_point["median"] = input_point.p50();
    output_point["q3"] = input_point.p75();
    output_point["high"] = input_point.upper();
  } else {
    output_point["y"] = input_point.y();
  }
  return output_point;
}

/**
 * Convert a "bar" point into a JSON object.
 *
 * This logic is taken from the gnuplot rendering code provided by Illumina.
 */
Json::Value
convert_point(bool is_candlestick,
              const illumina::interop::model::plot::bar_point &input_point) {
  Json::Value output_point(Json::objectValue);
  output_point["x"] = input_point.x();
  output_point["y"] = input_point.y();
  output_point["width"] = input_point.width();
  return output_point;
}

/**
 * For a plot output with points of type T, convert all the series into JSON
 * objects with the appropriate data.
 */
template <typename T>
void render(
    const std::string &type,
    const std::vector<std::pair<
        std::string, illumina::interop::model::plot::plot_data<T>>> &dataset,
    Json::Value &output) {
  if (dataset.size() == 0)
    return;

  Json::Value series(Json::arrayValue);
  for (const auto &data : dataset) {
    for (const auto &input_series : data.second) {
      bool is_candlestick =
          input_series.series_type() ==
          illumina::interop::model::plot::series<T>::Candlestick;
      Json::Value output_series(Json::objectValue);
      std::stringstream name;
      if (input_series.title().length() == 0) {
        name << data.first;
      } else {
        name << input_series.title() << " (" << data.first << ")";
      }
      output_series["name"] = name.str();
      Json::Value output_points(Json::arrayValue);
      for (const auto &point : input_series)
        output_points.append(::convert_point(is_candlestick, point));
      output_series["data"] = std::move(output_points);
      series.append(std::move(output_series));
    }
  }
  if (series.size() == 0) {
    return;
  }
  Json::Value result(Json::objectValue);
  result["type"] = type;
  result["series"] = std::move(series);
  output.append(std::move(result));
}

void plot_by_lane_wrapper(
    illumina::interop::model::metrics::run_metrics &run,
    const illumina::interop::constants::metric_type metrics_name,
    const illumina::interop::model::plot::filter_options &options,
    illumina::interop::model::plot::plot_data<
        illumina::interop::model::plot::candle_stick_point> &data,
    bool skip_empty) {
  illumina::interop::logic::plot::plot_by_lane(run, metrics_name, options, data,
                                               skip_empty);
}

/**
 * For a particular Illumina metric, construct a per-cycle candlestick plot.
 */
template <void (*X)(illumina::interop::model::metrics::run_metrics &,
                    const illumina::interop::constants::metric_type,
                    const illumina::interop::model::plot::filter_options &,
                    illumina::interop::model::plot::plot_data<
                        illumina::interop::model::plot::candle_stick_point> &,
                    bool)>
void add_plot(const illumina::interop::constants::metric_type metric_name,
              const std::string &type_name, bool include_combined,
              bool include_lanes,
              illumina::interop::model::metrics::run_metrics &run,
              Json::Value &output) {
  std::vector<std::pair<
      std::string, illumina::interop::model::plot::plot_data<
                       illumina::interop::model::plot::candle_stick_point>>>
      dataset;
  try {
    for (auto lane = include_combined ? 0 : 1;
         lane <= include_lanes ? run.run_info().flowcell().lane_count() : 0;
         lane++) {
      illumina::interop::model::plot::plot_data<
          illumina::interop::model::plot::candle_stick_point> data;
      illumina::interop::model::plot::filter_options options(
          run.run_info().flowcell().naming_method());
      options.lane(lane);
      X(run, metric_name, options, data, true);
      std::stringstream name;
      if (lane == 0) {
        name << "Combined";
      } else {
        name << "Lane " << lane;
      }
      dataset.push_back(std::make_pair(name.str(), std::move(data)));
    }
  } catch (const std::exception &ex) {
    return;
  }
  render(type_name, dataset, output);
}

void add_read_bin_plot(
    const illumina::interop::model::metrics::run_metrics &run,
    const illumina::interop::model::summary::run_summary &run_summary,
    Json::Value &output) {}

void add_chart_row(Json::Value &values, const std::string &name,
                   const std::string &value) {
  Json::Value result(Json::objectValue);
  result["name"] = name;
  result["value"] = value;
  values.append(std::move(result));
}
void add_global_chart(
    const illumina::interop::model::metrics::run_metrics &run,
    const illumina::interop::model::summary::run_summary &run_summary,
    Json::Value &output) {
  Json::Value result(Json::objectValue);
  result["type"] = "chart";
  Json::Value values(Json::arrayValue);
  add_chart_row(values, "Ends",
                run.run_info().is_paired_end() ? "Paired" : "Single");

  std::stringstream cycles;
  cycles << run_summary.cycle_state().extracted_cycle_range().last_cycle()
         << " / " << run.run_info().total_cycles();
  add_chart_row(values, "Cycles", cycles.str());

  std::stringstream basemask;
  auto first = true;

  for (const auto &read : run.run_info().reads()) {
    if (first) {
      first = false;
    } else {
      basemask << ",";
    }
    basemask << (read.is_index() ? "I" : "y") << length(read);
  }
  add_chart_row(values, "Base Mask", basemask.str());
  std::stringstream q_30;
  q_30 << std::fixed << std::setprecision(2)
       << run_summary.total_summary().percent_gt_q30() << " %";
  add_chart_row(values, "% > Q30", q_30.str());
  std::stringstream total_reads;
  total_reads.imbue(std::locale(""));
  total_reads << std::accumulate(
      run_summary.begin()->begin(), run_summary.begin()->end(), 0L,
      [](long acc,
         const illumina::interop::model::summary::lane_summary &summary) {
        return acc + summary.reads();
      });
  add_chart_row(values, "Total Reads", total_reads.str());
  result["values"] = std::move(values);
  output.append(std::move(result));
}

std::string format(const illumina::interop::model::summary::metric_stat &stat,
                   const float scale = 1) {
  std::stringstream output;
  output << std::setprecision(2) << stat.mean() / scale << " ± "
         << stat.stddev() / scale;
  return output.str();
}

void add_table_column(Json::Value &columns, const std::string &name,
                      const std::string &property) {
  Json::Value result(Json::objectValue);
  result["name"] = name;
  result["property"] = property;
  columns.append(std::move(result));
}

void add_lane_charts(
    const illumina::interop::model::metrics::run_metrics &run,
    const illumina::interop::model::summary::run_summary &run_summary,
    Json::Value &output) {
  Json::Value result(Json::objectValue);
  result["type"] = "table";

  Json::Value columns(Json::arrayValue);
  add_table_column(columns, "Lane", "lane");
  add_table_column(columns, "Density", "density");
  add_table_column(columns, "% > Q30", "q30");
  auto index = 0;
  for (auto read = 0; read < run_summary.size(); read++) {
    auto is_index = run.run_info().read(read + 1).is_index();
    std::stringstream buffer;
    if (is_index) {
      index++;
      buffer << " (Index " << index << ")";
    } else {
      buffer << " (Read " << read + 1 - index << ")";
    }
    auto suffix = buffer.str();
    add_table_column(columns, "Errors" + suffix,
                     "errors" + std::to_string(read));
    if (!is_index) {
      add_table_column(columns, "Aligned" + suffix,
                       "aligned" + std::to_string(read));
    }
  }

  result["columns"] = std::move(columns);

  Json::Value rows(Json::arrayValue);
  for (auto lane = 0; lane < run_summary.lane_count(); lane++) {
    Json::Value row(Json::objectValue);
    row["lane"] = lane + 1;
    row["density"] = format(run_summary[0][lane].density(), 1e3);
    row["q30"] = format(run_summary[0][lane].percent_gt_q30());
    for (auto read = 0; read < run_summary.size(); read++) {
      row["errors" + std::to_string(read)] =
          format(run_summary[read][lane].percent_gt_q30());
      if (!run.run_info().read(read + 1).is_index()) {
        row["aligned" + std::to_string(read)] =
            format(run_summary[read][lane].percent_aligned());
      }
    }
    rows.append(std::move(row));
  }
  result["rows"] = std::move(rows);

  output.append(std::move(result));
}

int main(int argc, const char **argv) {
  if (argc != 2) {
    return 1;
  }

  Json::Value result(Json::objectValue);

  /* Jackson expects the class to be embedded as an attribute, so we provided it
   * here. */
  result["class"] = "uk.ac.bbsrc.tgac.miso.dto.IlluminaNotificationDto";

  auto is_complete = true;

  illumina::interop::model::metrics::run_metrics run;
  try {
    run.read(argv[1]);
  } catch (illumina::interop::io::incomplete_file_exception e) {
    is_complete = false;
  } catch (...) {
    /* We are really unable to recover from any other exceptions, so just bail
     * out. */
    return 2;
  }

  std::stringstream buffer;
  buffer << "Interop: " << illumina::interop::library_version()
         << " Instrument: " << run.run_parameters().version();
  result["software"] = buffer.str();

  /* The Illumina start date associated with a run is a "yymmdd" string, not a
   * time_t. Reformat it as "YYYY-mm-dd". */
  std::stringstream start_date;
  start_date << "20" << run.run_info().date().substr(0, 2) << "-"
             << run.run_info().date().substr(2, 2) << "-"
             << run.run_info().date().substr(4, 2);
  result["startDate"] = start_date.str();

  /* Copy all the trivial values from the run information.  */
  result["containerSerialNumber"] = run.run_info().flowcell_id();
  result["numCycles"] = (Json::Value::Int)run.run_info().total_cycles();
  result["pairedEndRun"] = run.run_info().is_paired_end();
  result["runAlias"] = run.run_info().name();
  result["sequencerName"] = run.run_info().instrument_name();
  result["laneCount"] =
      (Json::Value::Int)run.run_info().flowcell().lane_count();
  illumina::interop::model::summary::run_summary run_summary;
  illumina::interop::logic::summary::summarize_run_metrics(run, run_summary,
                                                           true);

  result["imgCycle"] = (Json::Value::Int)length(
      run_summary.cycle_state().extracted_cycle_range());
  result["scoreCycle"] =
      (Json::Value::Int)length(run_summary.cycle_state().qscored_cycle_range());
  result["callCycle"] =
      (Json::Value::Int)length(run_summary.cycle_state().called_cycle_range());

  /* If there's an extraction metric with a end date, use that, reformatted as a
   * "YYYY-mm-dd" string. There can be multiple extractions, so pick on
   * basically at random. */
  auto has_extraction = false;
  for (const auto &extraction_metric :
       run.get<illumina::interop::model::metrics::extraction_metric>()) {
    std::time_t t = extraction_metric.date_time();
    std::stringstream date_buffer;
    date_buffer << std::put_time(std::localtime(&t), "%Y-%m-%d");
    result["completionDate"] = date_buffer.str();
    has_extraction = true;
  }
  is_complete &= has_extraction;

  is_complete &= run_summary.cycle_state().called_cycle_range().last_cycle() ==
                 run.run_info().total_cycles();

  /* We can't tell the difference between the stopped or running states, so we
   * just assume running if it isn't finished. */
  result["healthType"] = is_complete ? "Completed" : "Running";

  Json::Value metrics_results(Json::arrayValue);
  add_plot<illumina::interop::logic::plot::plot_by_cycle>(
      illumina::interop::constants::Q30Percent, "illumina-q30-by-cycle", true,
      true, run, metrics_results);
  add_plot<illumina::interop::logic::plot::plot_by_cycle>(
      illumina::interop::constants::CalledIntensity,
      "illumina-called-intensity-by-cycle", true, true, run, metrics_results);
  add_plot<illumina::interop::logic::plot::plot_by_cycle>(
      illumina::interop::constants::BasePercent,
      "illumina-base-percent-by-cycle", true, true, run, metrics_results);
  add_plot<plot_by_lane_wrapper>(illumina::interop::constants::Clusters,
                                 "illumina-cluster-count-by-lane", true, false,
                                 run, metrics_results);
  add_read_bin_plot(run, run_summary, metrics_results);
  add_global_chart(run, run_summary, metrics_results);
  add_lane_charts(run, run_summary, metrics_results);
  result["metrics"] = metrics_results;

  /* Write everything to standard output from consumption by Java. */
  std::cout << result << std::endl;
  return 0;
}
