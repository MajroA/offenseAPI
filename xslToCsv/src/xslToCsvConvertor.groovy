import groovy.text.GStringTemplateEngine

/**
 * User: Miro
 * Date: 14.3.15
 * Time: 14:27
 */

class Metadata {
    def vehicleRegistrationPlate
    def vehicleBrand
}

class CaseType {
    def category
    def type
    def subtype
    def detail
    Metadata metadata
}

class Fine {
    Integer amount
    def currency
}

class Solution {
    def form
    Fine fine
}

class Time {
    Date caseStart
    Date caseEnd
    def processed
}

class Place {
    def city
    def streetName
    def partOfStreet
    def latitude
    def longitude
}

class PoliceOfficer {
    def id
    def solutionRole
}

class Offender {
    def name
    Integer yearOfBirth
    Integer age
}

class Crime {
    def caseId
    CaseType caseType
    Solution solution
    Time time
    Place place
    PoliceOfficer policeOfficer
    Offender offender
}

def formatValue(value) {
    return value != null && value != "" ?
        value instanceof Integer ?
            value : value instanceof Date ?
            "\"${value.format('yyyy-MM-dd\'T\'HH:mm:ss')}\"" : "\"${value}\"" : null
}

def crimes = []

new File("pardubice_mestska_policie_komplet 2014.csv").withReader("UTF-8") {reader ->
    def index = 0
    reader.splitEachLine(';'){ splitLine ->
        if(index++ > 0 && splitLine.size() > 0) {
            Crime crime = new Crime()
            crime.caseId = splitLine[0]
            crime.caseType = new CaseType()
            crime.caseType.category = splitLine[1]
            crime.caseType.type = splitLine[2]
            crime.caseType.subtype = splitLine[3]
            crime.caseType.detail = splitLine[4]
            crime.caseType.metadata = new Metadata()
            crime.caseType.metadata.vehicleRegistrationPlate = splitLine[12]
            crime.caseType.metadata.vehicleBrand = splitLine[13]
            crime.solution = new Solution()
            crime.solution.form = splitLine[5]
            crime.solution.fine = new Fine()
            crime.solution.fine.amount = splitLine[6] ==~ /[\d]+/ ? Integer.parseInt(splitLine[6]) : null
            crime.solution.fine.currency = "CZK"
            crime.time = new Time()
            crime.time.caseStart = splitLine[8] != null && splitLine[8] != "" ? Date.parse("yyyy-MM-dd hh:mm:ss.sss" ,splitLine[8]) : null
            crime.time.caseEnd = splitLine[9] != null && splitLine[9] != "" ? Date.parse("yyyy-MM-dd hh:mm:ss.sss" ,splitLine[9]) : null
            crime.time.processed = ""
            crime.place = new Place()
            crime.place.city = splitLine[10]
            crime.place.streetName = splitLine[11]
            crime.place.partOfStreet = ""
            crime.place.latitude = splitLine[21]
            crime.place.longitude = splitLine[22]
            crime.policeOfficer = new PoliceOfficer()
            crime.policeOfficer.id = splitLine[16]
            crime.policeOfficer.solutionRole = splitLine[17]
            crime.offender = new Offender()
            crime.offender.name = splitLine[18]
            crime.offender.yearOfBirth = crime.offender.yearOfBirth ==~ /[\d]+/ ? Integer.parseInt(splitLine[24]) : null
            crime.offender.age = crime.offender.yearOfBirth != null ? (Calendar.getInstance().get(Calendar.YEAR) - 1900 - crime.offender.yearOfBirth) : null

            crimes << crime
        }
    }
}


new File("export.json").withWriter("UTF-8") {writer ->
    def templatePath = "crimeJson.template"
    GStringTemplateEngine engine = new GStringTemplateEngine()
    templateFile = new File(templatePath)
    def template = engine.createTemplate(templateFile.newReader("UTF-8"))

//    writer << "[\n"

    crimes.eachWithIndex { crime, index ->
        println index
        def binding = [
                "caseId": crime.caseId,
                "category": crime.caseType.category,
                "type": crime.caseType.type,
                "subtype": crime.caseType.subtype,
                "detail": crime.caseType.detail,
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
                "id": formatValue(crime.policeOfficer.id),
                "solutionRole": formatValue(crime.policeOfficer.solutionRole),
                "name": formatValue(crime.offender.name),
                "yearOfBirth": formatValue(crime.offender.yearOfBirth),
                "age": formatValue(crime.offender.age)
        ]

        def result = template.make(binding)
        writer << "{ \"create\": { \"_index\": \"pardubice\", \"_type\": \"prestupek\"}}\n"
        writer << result.toString().replaceAll(/^[\s]+/, '').replace('\n', '') + "\n"
    }
//    writer << "]"
}


println crimes.size()