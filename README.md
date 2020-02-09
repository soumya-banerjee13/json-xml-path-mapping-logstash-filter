# json-xml-path-mapping-logstash-filter
A logstash filter plugin, which parse input events from both json and xml files and modifies the events based on the configuration specified for the plugin

## Filter Configuration Options:
| <b>Setting</b> | <b>Input Type</b> | <b>Required</b> | <b>Default Value</b> |
|----------------|-------------------|-----------------|----------------------|
| document       | String            | No              | message              |
| type           | String            | No              | type                 |
| mainProp       | String(Uri)       | Yes             | -                    |
| cacheSize      | Long              | No              | -                    |

### document:
Configuration to set the field of the event from where we will get the document.
Default value is “message”.

### type:
Configuration to set the field of the event from where we will get the type of the document.
If the type is json/xml it will be processed. Default value is “type”.

### mainProp:
Configuration setting for the filter containing path of the main properties file.

This is a required field, should be a valid file path. Inside the file four properties and their values must be present. 
These are: identifier.attribute.path.xml (xpath of identifier field), identifier.attribute.path.json (jsonpath of identifier field), config.location.xml (folder path where document type specific configuration files will be present for xml documents), config.location.json (folder path where document type specific configuration files will be present for json documents).

### cacheSize:
Configuration setting for the filter, which says maximum how many configurations files can be stored in cache memory. This is being done to avoid reading document type specific configuration files to be read multiple times from disk, which may cause performance degradation during event filtering.

If not specified cache size will be infinite, which may cause memory overflow.


## What it does?
1. It takes both xml and json type documents from logstash input events.
2. Extract fields from the documents based on some xml and json specific configuration values in configurations files.
3. Sets the extracted value as the fields of the output events, based on the configuration values in configurations files.

## How do the Configuration Files look like?
1. <b>main-config.properties</b> contains all configuration path and path of identifier attribute. Sample <b>main-config.properties</b> is given below:
``` properties
identifier.attribute.path.xml = parent/child/grandchild/id
identifier.attribute.path.json = $.parent.child.grandchild.id
config.location.xml = <path to xml config folder>
config.location.json = <path to json config folder>
```
2. Suppose, <b>value at one document at identifier path is id1</b>. Then one <b>id1.conf</b> should be present in both xml and json config folder.
Sample id1.conf for xml will look like:
```
parent/child/grandchild/field1 => field1
parent/child/grandchild/field2 => field2
```
Sample id1.conf for json will look like:
```
$.parent.child.grandchild.field1 => field1
$.parent.child.grandchild.field2 => field2
```
This configuration will add <b>field1 and field2</b> fields with the value in their respective path in <b>output event</b> of logstash, for all document <b>having the value id1 at identifier attribute path, in the document field of the input event</b>.  

## How to Build and Install it in logstash?

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
Create a configuration file which will look like below:
```
input {
	file {
		path => "<some_folder_path>/*.json"
		start_position => "beginning"
		sincedb_path => "/dev/jsonDb"
		exclude => "*.gz"
		codec => json
	}
	file {
		path => "<some_folder_path>/*.xml"
		start_position => "beginning"
		sincedb_path => "/dev/xmlDb"
		exclude => "*.gz"
		codec => json
	}
}
filter {
  	json_xml_path_filter {
		mainProp => "<some_folder_path>/testProp.properties"
		cacheSize => 10
  	}
}
output {
  stdout { codec => rubydebug }
}
```
Let's name the configuration file test-config.conf and place it inside config folder of logstash deployment. Run the plugin using the command from logstash deployment home: 
```bash 
bin/logstash -f config/test-config.conf
```

## Sample configuration files and detailed documents
Available at: https://drive.google.com/drive/folders/1U9Xi62tcozdczyvy79H00hoF9_sfIAT8?usp=sharing
