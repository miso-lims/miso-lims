# Run Scanner
This is a web service that monitors directories on the file system containing
the output from sequencing instruments and provides them to MISO.

## Setup
Deploy the built WAR to a Tomcat instance with the following `ROOT.xml`:

    <Context>
       <Parameter name="runscanner.configFile" value="/etc/runscanner.json" override="false"/>
    </Context>

In `/etc/runscanner.json`, or another path of your choosing, put JSON data describing your instruments. You will need one record for each instrument:

    {
      "path": "/some/directory/where/sequencer/writes",
      "platformType": "ILLUMINA",
      "name": "default",
      "timeZone": "America/Toronto",
      "parameters": {}
    }

The JSON file then contains a list of instruments:

    [
      {
        "path": "/srv/sequencer/hiseq2500_1",
        "platformType": "ILLUMINA",
        "name": "default",
        "timeZone": "America/Toronto",
        "parameters": {}
      },
      {
        "path": "/srv/sequencer/hiseq2500_2",
        "platformType": "ILLUMINA",
        "name": "default",
        "timeZone": "America/Toronto",
        "parameters": {}
      }
    ]

The name/platform-type combination decide what scanner is used to interpret the sequencer's results. A list of supported scanners can be found on the status page or the debugging interface below.

The parameters are set based on the processor.

- PACBIO/default requires `address` to be set to the URL of the PacBio machine.
- ILLUMINA/default optionally allows `checkOutput`. If true, the scanner will
  try to look for BCL files to verify a run is complete if no logs are present.
  If false, it will assume the run is complete if ambiguous. The default is true.
  This can be very slow on certain network file systems.

## Debugging
For troublesome runs, you can see the output for a particular run directory using:

    java -cp $RUN_SCANNER_HOME/WEB-INF/classes:$RUN_SCANNER_HOME/WEB-INF/lib/'*' uk.ac.bbsrc.tgac.miso.runscanner.Main

It will display instructions on how to use it. You will have to set the `RUN_SCANNER_HOME` to the path containing an unpacked version of the WAR.
