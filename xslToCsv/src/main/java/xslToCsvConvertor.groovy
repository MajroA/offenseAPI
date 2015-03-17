import groovy.json.JsonBuilder
import groovy.text.GStringTemplateEngine

import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * User: Miro
 * Date: 14.3.15
 * Time: 14:27
 */

//def importFileName = getClass().getResource("prestupky_pardubice_mestska_policie_komplet_2014.csv")
def importFileName = getClass().getResource("prestupky_praha_6_2013_5_2014.csv")

//def exportFileName = "prestupky-pardubice.json"
def exportFileName = "prestupky-praha.json"

def jsonTemplateName = getClass().getResource("crimeJson.template")

//def exportFirstLine = ["index" :"prestupky", "type": "prestupek-pardubice"]
def exportFirstLine = ["index" :"prestupky", "type": "prestupek-praha"]

//Crime crimeInstance = new PardubiceCrime()
Crime crimeInstance = new PrahaCrime()

def formatValue(value) {
    return value != null && value != "" ?
        value instanceof Integer ?
            value : value instanceof Date ?
            "\"${value.format('yyyy-MM-dd\'T\'HH:mm:ss')}\"" : "\"${value.replace("\"",'')}\"" : null
}

def crimes = []

/**
 * read CSV
 */
new File(importFileName.path).withReader("UTF-8") {reader ->
    def index = 0
    reader.splitEachLine(';'){ splitLine ->
        if(index++ > 0 && splitLine.size() > 0) {
            crimes << crimeInstance.convert(splitLine)
        }
    }
}

/**
 * getting areas
 */
//AreasFromLocationDownloader.download(crimes)

/**
 * conver to JSON
 */

new File(exportFileName).withWriter("UTF-8") {writer ->
    def templatePath = jsonTemplateName.path
    GStringTemplateEngine engine = new GStringTemplateEngine()
    templateFile = new File(templatePath)
    def template = engine.createTemplate(templateFile.newReader("UTF-8"))

    crimes.eachWithIndex { crime, index ->
        println index
        def binding = [
                "crimeType": formatValue(crime.crimeType),
                "caseId": formatValue(crime.caseId),
                "category": formatValue(crime.caseType.category),
                "type": formatValue(crime.caseType.type),
                "subtype": formatValue(crime.caseType.subtype),
                "detail": formatValue(crime.caseType.detail),
                "vehicleRegistrationPlate": formatValue(crime.caseType.metadata.vehicleRegistrationPlate),
                "vehicleBrand": formatValue(crime.caseType.metadata.vehicleBrand),
                "form": formatValue(crime.solution.form),
                "amount": formatValue(crime.solution.fine.amount),
                "currency": formatValue(crime.solution.fine.currency),
                "caseStart": formatValue(crime.time.caseStart),
                "caseEnd": formatValue(crime.time.caseEnd),
                "processed": formatValue(crime.time.processed),
                "city": formatValue(crime.place.city),
                "streetName": formatValue(crime.place.streetName),
                "partOfStreet": formatValue(crime.place.partOfStreet),
                "latitude": formatValue(crime.place.latitude),
                "longitude": formatValue(crime.place.longitude),
                "areas": new JsonBuilder(crime.place.areas).toString(),
                "id": formatValue(crime.policeOfficer.id),
                "solutionRole": formatValue(crime.policeOfficer.solutionRole),
                "name": formatValue(crime.offender.name),
                "yearOfBirth": formatValue(crime.offender.yearOfBirth),
                "age": formatValue(crime.offender.age)
        ]

        def result = template.make(binding)
        writer << "{ \"create\": { \"_index\": \"${exportFirstLine['index']}\", \"_type\": \"${exportFirstLine['type']}\"}}\n"
        writer << result.toString().replaceAll(/^[\s]+/, '').replace('\n', '') + "\n"
    }

}


println crimes.size()
