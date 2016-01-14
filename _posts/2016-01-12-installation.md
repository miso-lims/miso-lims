---
layout: page
title: "Installation"
category: start
date: 2016-01-12 13:53:08
order: 1
---

VERSION 0.2.0 (Neon)  

## VirtualBox

The simplest way to get MISO up and running quickly is to use our virtual machine image with everything preinstalled. A VirtualBox instance is available with a self-contained MISO installation and MySQL database server, fully configured and can be used out-of-the-box.

To use it:

1.  Download VirtualBox software from: [https://www.virtualbox.org/](https://www.virtualbox.org/)
2.  Download the MISO OVA: [https://repos.tgac.ac.uk/miso/latest/MISO_Image.ova](https://repos.tgac.ac.uk/miso/latest/MISO_Image.ova)
3.  Import this OVA file into VirtualBox via the _File, Import Appliance_ option.
    1. You may have to select **Bridged Adaptor** instead of **NAT** in the network settings
3.  Start the instance. Starting the appliance will result in an Ubuntu virtual machine complete with all MISO’s prerequisites pre-installed. The login for the virtual image is **miso / misoadmin**.
4.  Start MISO server by using ./misoStart.sh and then open the browser and go http://localhost:8090/
5.  Log in to MISO using admin / admin. Only some simple configuration for certain elements is required, such as Sequencer References, Printers and any additional users and groups.

For more information about the VirtualBox image, see the [MISO 0.2.0 User Manual]({{ site.baseurl }}{% post_url 2016-01-12-user-manual %}).

## Building from source

For more control over MISO, you can build and deploy MISO on one of your own machines. 

The latest master build is always available at: [https://repos.tgac.ac.uk/miso/latest/](https://repos.tgac.ac.uk/miso/latest/)

The develop build is always available in the most recent versioned directory at: [https://repos.tgac.ac.uk/miso/snapshots/](https://repos.tgac.ac.uk/miso/latest/)

### Setting up the MISO database

You will need to install MySQL v5 or greater. You will then need the two latest MISO database dumps. These are available from our repository here:

*   [https://repos.tgac.ac.uk/miso/latest/sql/lims-schema.sql](https://repos.tgac.ac.uk/miso/latest/sql/lims-schema.sql)  
*   [https://repos.tgac.ac.uk/miso/latest/sql/miso_type_data.sql](https://repos.tgac.ac.uk/miso/latest/sql/miso_type_data.sql)

Log in to your local MySQL install, and create a database called 'lims':

```
CREATE DATABASE lims; 
USE lims;
```

Then add a user that has all grant access on the 'lims' db:

```
GRANT ALL ON `lims`.* TO 'tgaclims'@'localhost';GRANT ALL ON `lims`.* TO 'tgaclims'@'localhost' IDENTIFIED BY 'tgaclims';
```

If your database and Tomcat install are on different machines, then you will obviously need to add a grant privilege to the MISO database from your remote machine:

```
GRANT ALL ON `lims`.* TO 'tgaclims'@'your.tomcat.install.server';GRANT ALL ON `lims`.* TO 'tgaclims'@'your.tomcat.install.server' IDENTIFIED BY 'tgaclims';
```

Then populate the database with the two dumps by running the following commands at a shell prompt:

```
mysql -u tgaclims -p -D lims < lims-schema.sql
mysql -u tgaclims -p -D lims < miso_type_data.sql
```

### Setting up the MISO web application

You will need a suitable Java web application container, such as Tomcat 7.x ([http://tomcat.apache.org/download-70.cgi](http://tomcat.apache.org/download-70.cgi)), to deploy MISO. Once Tomcat has been installed, download the latest MISO WAR file. The latest WAR file can be found here:

[https://repos.tgac.ac.uk/miso/latest/ROOT.war](https://repos.tgac.ac.uk/miso/latest/ROOT.war)

Copy this file to your <tomcat-install>/webapps/ directory. DO NOT START TOMCAT YET. Instead, follow the configuration steps below.

## Administration

MISO is configured using the Tomcat context xml system, the main miso.properties file that resides in the <tomcat-install>/webapps/ROOT/WEB-INF/classes directory, and properties files that live in the default MISO storage directory.

Make sure that these options are correct for your system at the time of container startup.

### Setting the database environment

MISO uses JNDI (Java Naming and Directory Interface) to configure the connection to the underlying MySQL database that you setup in the step 1.1\. To configure Tomcat to manage the JNDI datasource, please create a file called 'ROOT.xml' in \<tomcat-install\>/conf/Catalina/localhost and populate it with the following information:

```
<Context path="/ROOT" docBase="${catalina.home}/webapps/ROOT" debug="1">
      <Resource name="jdbc/MISODB" type="javax.sql.DataSource"
      driverClassName="com.mysql.jdbc.Driver"
      initialSize="32"
      maxIdle="10"
      maxActive="100"
      maxWait="1000"
      removeAbandoned="true"
      removeAbandonedTimeout="120"
      logAbandoned="true"
      testWhileIdle="true"
      testOnBorrow="true"
      testOnReturn="true"
      validationQuery="select 1"
      url="jdbc:mysql://localhost:3306/lims?autoReconnect=true&amp;zeroDateTimeBehavior=convertToNull<span style="color: rgb(0,0,0);">&amp;useUnicode=true&amp;characterEncoding=UTF-8</span>"
      username="tgaclims"
      password="tgaclims"/>

     <Resource name="file/CONAN_PROPERTIES" type="java.util.File"
                factory="uk.ac.ebi.fgpt.naming.factory.FileFactory"
                 path="../webapps/ROOT/WEB-INF/conan.properties"/>

     <Resource name="jdbc/STATSDB" type="javax.sql.DataSource"  
      driverClassName="com.mysql.jdbc.Driver" initialSize="32" maxIdle="32" maxActive="100" maxWait="-1"  
      url="jdbc:mysql://yourdb.hostname:3306/database?zeroDateTimeBehavior=convertToNull"  
      username="username"  
      password="password" />  
</Context>
```

If the \<tomcat-install\>/conf/Catalina/localhost directory doesn't exist (usually due to a fresh Tomcat install), either create it yourself, or cycle a Tomcat startup/shutdown and it should appear.

If your Tomcat install has the autoDeploy="true" flag set in server.xml, if you delete the webapps/ROOT directory and the ROOT.war file, Tomcat will delete the context ROOT.xml file. Either set autoDeploy to false, and manually deploy your webapp, or make the ROOT.xml file undeletable by using 'chattr +i' ('chattr -i' will undo this operation). See:

[https://issues.apache.org/bugzilla/show_bug.cgi?id=40050](https://issues.apache.org/bugzilla/show_bug.cgi?id=40050)

Again, if your database and Tomcat install are on different machines, then you will need to change the connection URL above to the name of your remote database server:

```
    ...
    url="jdbc:mysql://your.database.server:3306/lims"
    ...

```

You will need to copy the mysql connector library and the JNDI File Factory library to your Tomcat install to ensure the JNDI system can see the MISO database. Grab the jar files from:

*   [mysql-connector-java-5.1.10.jar](https://repos.tgac.ac.uk/miso/common/mysql-connector-java-5.1.10.jar)
*   [jndi-file-factory-1.0.jar](https://repos.tgac.ac.uk/miso/common/jndi-file-factory-1.0.jar)

And copy them to \<tomcat-install\>/lib/

If you cannot connect to the database make sure you are using the right connection port. By default in ROOT.xml the port is set to 3306.


```
url="jdbc:mysql://localhost:3306 
```


To check which port is being used by mySQL, run

```
mysqladmin -h localhost variables | grep port
```

and modify ROOT.xml accordingly.

Depending on your mySQL setup you might also need to substitute 'localhost' with 127.0.0.1.

See:

[http://serverfault.com/questions/295285/mysql-cannot-connect-via-localhost-only-127-0-0-1](http://serverfault.com/questions/295285/mysql-cannot-connect-via-localhost-only-127-0-0-1)

### Setting up miso.properties file

The main miso.properties file is located in the <tomcat-install>/lib/classes/ directory.

The miso.properties file specifies a number of general options. The only option that needs attention is 'miso.baseDirectory'. Please set this to the absolute path of where you would like MISO to store files on disk, i.e. logs, uploaded files, etc.

The default path is '/storage/miso'. It is recommended that you do not change this location as some things will break.

You shouldn't need to change the miso.properties file as long as the /storage/miso directory exists on your filesystem and is writeable!


### Setting up userspace properties

MISO achieves userspace configuration via extra properties files kept in the storage directory specified by the miso.baseDirectory property (see 2.2 above). Default versions of these files are available here:

[miso_userspace_properties.tar.gz](https://repos.tgac.ac.uk/miso/latest/miso_userspace_properties.tar.gz)

Unpack this file to your MISO storage directory, which is /storage/miso by default. You should see 3 files:

*   mail.properties - email settings so that MISO can send emails to users.
*   security.properties - properties to set the security environment (see 2.4 below).
*   submission.properties - properties to set the submission environment (see 2.5 below).

### Setting the security environment

MISO can use either LDAP or SQL as an authentication mechanism, and will need to know where your chosen method resides. These properties need to be set before the container is started, and relate to properties files stored on disk.

Firstly, the security options for both auth systems are specified in the security.properties file. If you are using JDBC, then it is unlikely that you will need to change any options, but the LDAP setup is far more complex. Please ask your sysadmin to match up the MISO LDAP options with your local LDAP server options.

Secondly, the 'security.method' environment property relates to the security mechanism, i.e. 'ldap' or 'jdbc', where 'ldap' auths against an LDAP server, and 'jdbc' auths against a local database (usually the MISO database itself). Please use 'jdbc' if you are unsure. There is a default admin user that is shipped with the MISO database, and the username/password login is admin/admin.

To set the 'security.method' property, use local environment variables on Tomcat startup. Please see the "Starting MISO" section below.

You are likely to see startup errors like the one below if you do not set this property, and start Tomcat as outlined in section 3!

```
20-Oct-2010 11:08:28 org.apache.catalina.core.StandardContext start

SEVERE: Error listenerStart

20-Oct-2010 11:08:28 org.apache.catalina.core.StandardContext start

SEVERE: Context [/tgac] startup failed due to previous errors
```






### Submission properties

MISO is able to submit sequence data to the major sequence archives, i.e. the SRA at the EBI, and the EMBL GenBank. Currently, only ERA submissions are supported out-of-the-box. The settings for these services are specified in the submission.properties file.

### Customising MISO

##### Replacing the logo

To replace the TGAC logo for MISO, simply replace the **<tomcat>/<miso_webapp_dir>/WEB-INF/styles/images/brand_logo.png** file in the exploded web application directory.

##### Styling

To change any styling of MISO (colour schemes etc), simply copy the **<tomcat>/<miso_webapp_dir>/WEB-INF/styles/style.css** file, make any customisations, then overwrite the original.

## Starting MISO

To set the 'security.method' property to use the local MISO database and start the Tomcat instance, run the following command:

```
cd <tomcat-install>/bin/
JAVA_OPTS="$JAVA_OPTS -Dsecurity.method=jdbc" ./startup.sh
```







All going well, you should be able to go to [http://localhost:8080/](http://localhost:8080/) and log in to MISO using the admin/admin username and password combination.

Please remember that if you need to set any properties in the userspace properties files, you will need to make sure that you stop Tomcat, make the changes and then restart it.






