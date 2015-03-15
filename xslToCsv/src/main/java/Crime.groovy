/**
 * User: Miro
 * Date: 15.3.15
 * Time: 9:20
 */
class Crime {
    /** for index purpose */
    def crimeType

    def caseId
    CaseType caseType
    Solution solution
    Time time
    PoliceOfficer policeOfficer
    Offender offender
    Place place

    def convert(splitLine) {
        return null
    }
}

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

    /** area from http://crimehackathon.azurewebsites.net/ */
    def areas
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

