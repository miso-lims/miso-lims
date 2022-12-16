MISO has been updated to use Java 17. If you use Docker to run MISO, you
don't need to worry about this as the images have been updated as required.
If you build MISO from source, run the Flyway command-line tool, or run MISO
on your own Tomcat installation, you will need to have JDK 17 or higher
installed and ensure that Tomcat is configured to use the newly installed
JDK. We are also discontinuing support for Tomcat 8, and recommend running
MISO on Tomcat 9
