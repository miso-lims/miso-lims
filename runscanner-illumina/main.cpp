#include <ctime>
#include <iomanip>
#include <iostream>
#include <sstream>
#include <string>
#include <interop/interop.h>
#include <json/json.h>

int main(int argc, const char **argv) {
  if (argc != 2) {
    return 1;
  }

  Json::Value result(Json::objectValue);

  result["class"] = "uk.ac.bbsrc.tgac.miso.dto.IlluminaNotificationDto";

  auto is_complete = true;

  illumina::interop::model::metrics::run_metrics run;
  try {
    run.read(argv[1]);
  } catch (illumina::interop::io::incomplete_file_exception e) {
    is_complete = false;
  } catch (...) {
    return 2;
  }

  std::stringstream buffer;
  buffer << "Interop: " << illumina::interop::library_version()
         << " Instrument: " << run.run_parameters().version();
  result["software"] = buffer.str();

  /* The Illumina start date associated with a run is a "yymmdd" string, not a
   * time_t. Reformat it as "YYYY-mm-dd".*/
  std::stringstream start_date;
  start_date << "20" << run.run_info().date().substr(0, 2) << "-"
             << run.run_info().date().substr(2, 2) << "-"
             << run.run_info().date().substr(4, 2);
  result["startDate"] = start_date.str();

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

  result["imgCycle"] = (Json::Value::Int)run_summary.cycle_state()
                           .extracted_cycle_range()
                           .last_cycle();
  result["scoreCycle"] = (Json::Value::Int)run_summary.cycle_state()
                             .qscored_cycle_range()
                             .last_cycle();
  result["callCycle"] = (Json::Value::Int)run_summary.cycle_state()
                            .called_cycle_range()
                            .last_cycle();

  /* If there's an extraction metric with a end date, use that, reformatted as a
   * "YYYY-mm-dd" string.*/
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

  /* We can't tell the difference between the stopped or running states, so we
   * just assume running if it isn't finished.*/
  result["healthType"] = is_complete ? "Completed" : "Running";

  Json::Value metrics_results(Json::arrayValue);
  // TODO add metrics using metrics_results::append
  result["metrics"] = metrics_results;

  std::cout << result << std::endl;
  return 0;
}
