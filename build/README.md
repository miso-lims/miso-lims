# MISO Web Server
Client-side application to MISO lims
### Prerequisites
For the purposes of this setup, it is assumed that the host machine has the following installed and configured:
  1. MySQL  
  Configurations have been tested against v5.6. Install using [MySQL APT repository](https://dev.mysql.com/doc/mysql-apt-repo-quick-guide/en/).
  2. MySQL Development Headers  
  Required to set login paths used by script to create/update **miso db**. Issue the following to install and configure:  
  ```bash
  ~$ sudo apt-get update
  ~$ sudo apt-get install libmysqlcliend-dev
  ~$ mysql_config_editor set --login-path=client --host=<mysql-db-host> --user=root --password
  ```
  In the above instructions, replace `<mysql-db-host>` with appropriate db host name, _e.g. localhost_. When prompted, enter the **MySQL** db root/admin password. Repeat the last command, replacing `client` with `miso-lims` and `root` with **miso-lims** db username, and when prompted db user password, as set in **context.properties**
  3. Tomcat Server (as a system service)  
  At the time of testing, only v7 of Apache Tomcat was available as an Ubuntu system service (_via apt repository_). For later versions, there are documentations at the [Apache Tomcat](http://tomcat.apache.org/) website on daemonizing tomcat server for other versions. After installation, edit `<catalina-home>/conf/tomcat-user.xml` and add the following tags:
  ```xml
  <role rolename="manager-script"/>
  <user username="<tomcat-user>" password="<tomcat-pass>" roles="manager-script"/>
  ```
  replacing `<tomcat-user>` and `<tomcat-pass>` with Tomcat manager-script credentials set in **context.properties**  

### Pre-Setup
The setup scripts require the following to be installed, but if not, will stop execution and ask that they are installed and configured:
  1. Java Development Kit (version 7 or greater required, _tested against OpenJDK v7_)
  2. Apache Maven (version 2.2.1 or greater required, _tested against v3.0.5_)
