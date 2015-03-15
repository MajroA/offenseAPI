import groovyx.net.http.HTTPBuilder

/**
 * Created with IntelliJ IDEA.
 * User: Miro
 * Date: 15.3.15
 * Time: 11:30
 */
class AreasFromLocation implements Runnable {

    def id
    def crimes
    def doneSignal

    AreasFromLocation(crimes, doneSignal, id) {
        this.id = id
        this.crimes = crimes
        this.doneSignal = doneSignal
    }

    @Override
    void run() {
        try {
            def http = new HTTPBuilder('http://crimehackathon.azurewebsites.net/api/')

            crimes.eachWithIndex{ crime, index ->
                try {
                    http.get(path: 'areas', query: [lat: "${crime.place.latitude}", lng: "${crime.place.longitude}"]) { resp, json ->

                        if (resp.status == 200)
                            crime.place.areas = json.area

                        doneSignal.countDown()
                        println "STOP ${id} - ${index}"
                    }
                } catch (Exception e) {
                    println e.message
                }
            }
        } catch (InterruptedException ex) {} // return;
    }
}
