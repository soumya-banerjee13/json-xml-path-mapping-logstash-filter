# json-xml-path-mapping-logstash-filter
A logstash filter plugin, which parse input events from both json and xml files and modifies the events based on the configuration specified for the plugin

## What it does?
1. It takes both xml and json type documents from logstash input events.
2. Extract fields from the documents based on some xml and json specific configuration values in configurations files.
3. Sets the extracted value as the fields of the output events, based on the configuration values in configurations files.

## How the Configuration Files look like?

## How to Build an Install it in logstash?

### Clone the plugin repo
Clone this filter plugin repository or download zip

### Obtain a local copy of logstash codebase
Obtain a copy of the Logstash codebase with the following git command:

. git clone --branch <branch_name> --single-branch https://github.com/elastic/logstash.git <target_folder>

where, branch_name = Major version of logstash where you want to install the plugin
target_folder = Location of logstash codebase in your local system(Call this LS_HOME)

### Generate Logstash-Core jar files
Run ./gradlew assemble in terminal from LS_HOME. This should produce the $LS_HOME/logstash-core/build/libs/logstash-core-x.y.z.jar where x, y, and z refer to the version of Logstash.

### Create gradle.properties file 
Create gradle.properties file in the root folder of this cloned plugin project.

### Run the Gradle packaging task
Run ./gradlew gem from the root folder of this cloned plugin project. This task will produce a gem file in the root directory of your plugin’s codebase with the name logstash-filter-json_xml_path_filter-1.0.0-SNAPSHOT.gem

### Install it in your logstash deployment
1. Go to your logstash deployment folder.
2. Run the command: bin/logstash-plugin install --no-verify --local /path/to/logstash-filter-json_xml_path_filter-1.0.0-SNAPSHOT.gem

## How to Run the Plugin?
