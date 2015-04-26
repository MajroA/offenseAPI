/*
Performs transformation of elastic data with array areas to areas object

 [
    {"AreaLevel":1,"Code":"1700","Name":"KŘP PARDUBICKÉHO KRAJE"},
    {"AreaLevel":0,"Code":"0","Name":"Česká republika"},
    {"AreaLevel":3,"Code":"170613","Name":"OOP PARDUBICE 1"},
    {"AreaLevel":2,"Code":"1706","Name":"ÚO PARDUBICE"}
]

to

{
  "country": {
    "AreaLevel": 0,
    "Code": "0",
    "Name": "Česká republika"
  },
  "city": {
    "AreaLevel": 2,
    "Code": "1706",
    "Name": "ÚO PARDUBICE"
  },
  "district": {
    "AreaLevel": 3,
    "Code": "170613",
    "Name": "OOP PARDUBICE 1"
  },
  "region": {
    "AreaLevel": 1,
    "Code": "1700",
    "Name": "KŘP PARDUBICKÉHO KRAJE"
  }
}
 */

import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import groovy.json.StringEscapeUtils

import java.util.regex.Matcher
import java.util.regex.Pattern

if (args.length != 2) {
    println "Run as 'inputFilePath' argument"
    return;
}

def inputFilePath = "/Users/lukovsky/Projects/krimihackathon2015/prestupky-pardubice.json"
def exportFilePath = inputFilePath + ".fix"

Pattern pattern = Pattern.compile(".*\"areas\": ([^\\]]*]).*");
slurper = new JsonSlurper()

areaTranslation = [
        "AreaLevel0": "country",
        "AreaLevel1": "region",
        "AreaLevel2": "city",
        "AreaLevel3": "district",
]
new File(exportFilePath).withWriter("UTF-8") { writer ->

    new File(inputFilePath).withReader("UTF-8") { reader ->
        reader.eachLine { String line, int lineNumber ->
            if (lineNumber % 2 == 0) {
                Matcher matcher = pattern.matcher(line);

                Map<String, Object> areasMap = new HashMap<>()
                if (matcher.find()) {
                    String areasArrayString = line.substring(matcher.start(1), matcher.end(1))
                    areasArrayString = areasArrayString.replaceAll("AreaLevel", "areaLevel")
                    areasArrayString = areasArrayString.replaceAll("Code", "code")
                    areasArrayString = areasArrayString.replaceAll("Name", "name")
                    def areasArrayJson = slurper.parseText(areasArrayString)
                    areasArrayJson.each { area ->
                        areasMap.put(areaTranslation["AreaLevel" + area.areaLevel], area)
                    }
                    line = line.substring(0, matcher.start(1)) + StringEscapeUtils.unescapeJavaScript(JsonOutput.toJson(areasMap)) + line.substring(matcher.end(1))
                } else {
                    System.err.println("Error line " + line)
                }
            }
            writer.println(line)
        }
    }

    writer.flush()
}

