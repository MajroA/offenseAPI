/**
 * Created with IntelliJ IDEA.
 * User: Miro
 * Date: 15.3.15
 * Time: 9:18
 */

class PardubiceCrime extends Crime {
    def crimeType = "prestupek-pardubice"

    def convert(splitLine) {
        PardubiceCrime crime = new PardubiceCrime()
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
        crime.place.latitude = splitLine[22]
        crime.place.longitude = splitLine[21]
        crime.policeOfficer = new PoliceOfficer()
        crime.policeOfficer.id = splitLine[16]
        crime.policeOfficer.solutionRole = splitLine[17]
        crime.offender = new Offender()
        crime.offender.name = splitLine[18]
        crime.offender.yearOfBirth = splitLine[24] ==~ /[\d]+/ ? Integer.parseInt(splitLine[24]) : null
        crime.offender.age = crime.offender.yearOfBirth != null ? (Calendar.getInstance().get(Calendar.YEAR) - 1900 - crime.offender.yearOfBirth) : null

        return crime
    }
}

