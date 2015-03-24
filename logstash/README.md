# Logstash
We have used parsing tool logstash (http://logstash.net/docs/1.4.2/) for processing of several simple structured CSV files and tranferring results to the Elasticsearch (ES) DB.

##### Configuration
* ES index - target index, must exist in ES before running the script
* ES type - target type, must has defined correct mapping before running the script

In according to the differences in data formats between available cities there have been prepared three separated configurations
* brno.conf
* pardubice.conf
* praha.conf

##### Input files
The input files has to be defined by absolute path. The current set path is _/var/data/prestupky_ which can be changed directly in the configuration files.

Actual full source data in CSV format ready to be used by the logstash are available at
https://drive.google.com/folderview?id=0B3sZN1XOQdkefklVaGNQMi00UDRIYVZZb1JxLTBsR2FnVmdESFZ5anVkcDBKYWVnUHRkWXM&usp=sharing

The first line of each file contains header, this row begins with # character which defines comment rows.

##### Running
For running logstash processing and exporting data to ES run the command

```
logstash -f city_file.conf
```
