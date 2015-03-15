/**
 * Created with IntelliJ IDEA.
 * User: Miro
 * Date: 15.3.15
 * Time: 9:23
 */

class PrahaCrime extends Crime {
    def crimeType = "prestupek-praha"

    def convert(splitLine) {
        PrahaCrime crime = new PrahaCrime()
        crime.caseType = new CaseType()
        crime.caseType.type = splitLine[4]
        crime.caseType.metadata = new Metadata()
        crime.solution = new Solution()
        crime.solution.fine = new Fine()
        crime.time = new Time()
        crime.time.caseStart = splitLine[0] ==~ /[\d]{1,2}\.[\d]{1,2}\.[\d]{4} [\d]{1,2}\:[\d]{1,2}/ ? Date.parse("dd.MM.yyyy HH:mm" ,splitLine[0]) : null
        if(!crime.time.caseStart)
            crime.time.caseStart = splitLine[0] ==~ /[\d]{1,2}\.[\d]{1,2}\.[\d]{4}/ ? Date.parse("dd.MM.yyyy" ,splitLine[0]) : null
        crime.place = new Place()
        crime.place.city = splitLine[1]
        crime.place.streetName = splitLine[2]
        crime.place.partOfStreet = splitLine[3]
        crime.policeOfficer = new PoliceOfficer()
        crime.offender = new Offender()

        return crime
    }
}
