import groovy.json.JsonBuilder
import groovy.json.StringEscapeUtils
import groovy.text.GStringTemplateEngine

/**
 * User: Miro
 * Date: 14.3.15
 * Time: 14:27
 */

def importFileName = getClass().getResource(ParserConf.INSTANCE.importFileName)
def exportFileName = ParserConf.INSTANCE.exportFileName
def jsonTemplateName = getClass().getResource(ParserConf.INSTANCE.jsonTemplateName)
def exportFirstLine = ParserConf.INSTANCE.exportFirstLine
Crime crimeInstance = ParserConf.INSTANCE.crimeInstance

def formatValue(value) {
    if (value != null) {
        if (value instanceof Integer)
            return value
        else if (value instanceof Date)
            return "\"${value.format('yyyy-MM-dd\'T\'HH:mm:ss')}\""
        else if (value != "")
            return "\"${value.replace("\"",'')}\""
        else
            return null
    }
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
if (ParserConf.INSTANCE.downloadAreas)
    AreasFromLocationDownloader.download(crimes)

/**
 * write as JSON
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
                "areas": crime.place.areas != null ? StringEscapeUtils.unescapeJavaScript(new JsonBuilder(crime.place.areas).toString()) : null,
                "id": formatValue(crime.policeOfficer.id),
                "solutionRole": formatValue(crime.policeOfficer.solutionRole),
                "name": formatValue(crime.offender.name),
                "yearOfBirth": formatValue(crime.offender.yearOfBirth),
                "age": formatValue(crime.offender.age)
        ]

        def result = template.make(binding)
        writer.println("{ \"create\": { \"_index\": \"${exportFirstLine['index']}\", \"_type\": \"${exportFirstLine['type']}\"}}")
        writer.println(result.toString().replaceAll(/^[\s]+/, '').replace('\n', ''))
        writer.flush()
    }

    writer.close()
}


println crimes.size()
