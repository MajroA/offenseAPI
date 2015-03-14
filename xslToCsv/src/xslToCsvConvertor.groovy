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
    def amount
    def currency
}

class Solution {
    def form
    Fine fine
}

class Time {
    def caseStart
    def caseEnd
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
    def yearOfBirth
    def age
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

def crimes = []

new File("pardubice_mestska_policie_komplet 2014.csv").withReader("UTF-8") {reader ->
    reader.splitEachLine(';'){ splitLine ->
        if(splitLine.size() > 0) {
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
            crime.solution.fine.amount = splitLine[6]
            crime.solution.fine.currency = "CZK"
            crime.time = new Time()
            crime.time.caseStart = splitLine[8]
            crime.time.caseEnd = splitLine[9]
            crime.time.processed = "?????"
            crime.place = new Place()
            crime.place.city = splitLine[10]
            crime.place.streetName = splitLine[11]
            crime.place.partOfStreet = "???"
            crime.place.latitude = splitLine[21]
            crime.place.longitude = splitLine[22]
            crime.policeOfficer = new PoliceOfficer()
            crime.policeOfficer.id = splitLine[16]
            crime.policeOfficer.solutionRole = splitLine[17]
            crime.offender = new Offender()
            crime.offender.name = splitLine[18]
            crime.offender.yearOfBirth = splitLine[23]
            crime.offender.age = "TODO"

            crimes << crime
        }
    }
}


new File("export.json").withWriter("UTF-8") {writer ->
    def templatePath = "crimeJson.template"
    GStringTemplateEngine engine = new GStringTemplateEngine()
    templateFile = new File(templatePath)
    def template = engine.createTemplate(templateFile.newReader("UTF-8"))

    writer << "{[\n"

    crimes.eachWithIndex { crime, index ->
        println index
        def binding = [
                "caseId": crime.caseId,
                "category": crime.caseType.category,
                "type": crime.caseType.type,
                "subtype": crime.caseType.subtype,
                "detail": crime.caseType.detail,
                "metadata": crime.caseType.metadata,
                "vehicleRegistrationPlate": crime.caseType.metadata.vehicleRegistrationPlate,
                "vehicleBrand": crime.caseType.metadata.vehicleBrand,
                "form": crime.solution.form,
                "amount": crime.solution.fine.amount,
                "currency": crime.solution.fine.currency,
                "caseStart": crime.time.caseStart,
                "caseEnd": crime.time.caseEnd,
                "processed": crime.time.processed,
                "city": crime.place.city,
                "streetName": crime.place.streetName,
                "partOfStreet": crime.place.partOfStreet,
                "latitude": crime.place.latitude,
                "longitude": crime.place.longitude,
                "id": crime.policeOfficer.id,
                "solutionRole": crime.policeOfficer.solutionRole,
                "name": crime.offender.name,
                "yearOfBirth": crime.offender.yearOfBirth,
                "age": crime.offender.age
        ]

        def result = template.make(binding)
        writer << result.toString() + "\n"
    }
    writer << "]}"
}


println crimes.size()