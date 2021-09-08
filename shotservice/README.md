MMSI - Shot Service
===================

MMSI-v1 allows segmenting a video asset into different shots. The segmentation can be
performed manually or automatically. In the case of manual shot segmentation, the Shot
Service provides functions to add, edit and delete shots. The automatic shot detection is
performed by TUW-CVL. The Shot Service provides API endpoints for TUW-CVL to store
the results of the automatic shot detection.

Setup of Development Environment
--------------------------------

### Mapstruct in Eclipse

  * Java Compiler -> Annotation Processing
    * Enable everything 
    * Generated source directory: ``target/eclipse-generated-sources/annotations``
    * Generated test source directory: ``target/eclipse-generated-test-sources/annotations``
    * Processor options: ``mapstruct.defaultComponentModel=spring``

  * Java Compiler -> Annotation Processing -> Factory Path
    * Add External Jars...
    * The mapstruct jar can be found here: ``~/.m2/repository/org/mapstruct/mapstruct-processor/1.3.1.Final/mapstruct-processor-1.3.1.Final.jar``
    * At the time of writing, version 1.3.1 is used. See ``pom.xml`` for the currently used version

  * Java Build Path -> Source -> Add Folder
     * ``target/eclipse-generated-sources/annotations``
     * ``target/eclipse-generated-test-sources/annotations``

### Lombok in Eclipse

  * Follow the installation instructions on <https://projectlombok.org/setup/eclipse>
    * The lombok jar must be downloaded or can be found here: ``~/.m2/repository/org/projectlombok/lombok/1.18.10/lombok-1.18.10.jar``
    * At the time of writing, version 1.18.10 is used. See ``pom.xml`` for the currently used version


Building the shot service
--------------------

Use the maven wrapper ``mvnw`` to build the application. https://github.com/takari/maven-wrapper

    ./mvnw clean install

On the first usage, the correct maven version will be downloaded if it is not available on the 
system.

Running the shot service
-------------------

### Starting the shot service

1) The shot service needs a running PostgreSQL database on ``localhost``, start it with

    ./bin/db/start-local-postgres.sh

  * The database and user are created in the above script
  * The schema is loaded via flyway when running the application

2a) Run the application from the command line

    ./mvnw spring-boot:run

2b) Run the application within Eclipse

  * Navigate to ``eu.vhhproject.mmsi.shotservice.ShotServiceApplication``
  * Right click on the class name ``ShotServiceApplication``
  * Run As -> Java Application

3) The shot service is then available on

    http://localhost:8080

4) Optionally load some test data

    ./bin/test/shotservice-load-test-data.sh

5) The database can be stopped with

    ./bin/db/stop-local-postgres.sh


Comments for Structuring the Source Code
----------------------------------------

For member variables:

    /* Properties */
    /* Services */
    /* Repositories */
    /* Mappers */
    /* Controllers */
    /* Misc */

For REST Controllers:

    /* ***** REST endpoints ***** */
    /* ***** Private Methods ***** */
