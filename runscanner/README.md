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
      "timeZone": "America/Toronto"
    }

The JSON file then contains a list of instruments:

    [
      {
        "path": "/srv/sequencer/hiseq2500_1",
        "platformType": "ILLUMINA",
        "name": "default",
        "timeZone": "America/Toronto"
      },
      {
        "path": "/srv/sequencer/hiseq2500_2",
        "platformType": "ILLUMINA",
        "name": "default",
        "timeZone": "America/Toronto"
      }
    ]

The name/platform-type combination decide what scanner is used to interpret the sequencer's results. A list of supported scanners can be found on the status page.

## Debugging
For troublesome runs, you can see the output for a particular run directory using:

    CLASSPATH=runscanner.war java uk.ac.bbsrc.tgac.miso.runscanner.Main

It will display instructions on how to use it.
