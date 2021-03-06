import groovyx.gpars.GParsPool
import groovyx.net.http.HTTPBuilder

/**
 * User: Miro
 * Date: 15.3.15
 * Time: 11:30
 */
class AreasFromLocationDownloader {

    static download(List<Crime> crimes) {
        def start = System.currentTimeMillis()

        int cores = Runtime.getRuntime().availableProcessors();
        GParsPool.withPool(cores) {
            crimes.eachWithIndexParallel { crime, index ->
                try {
                def http = new HTTPBuilder('http://crimehackathon.azurewebsites.net/api/')
                    http.get(path: 'areas', query: [lat: "${crime.place.latitude}", lng: "${crime.place.longitude}"]) { resp, json ->

                        if (resp.status == 200)
                            crime.place.areas = json.area

                        println "${index++} DONE"
                    }
                } catch (Exception e)  {
                    println e.message
                }
            }
        }

        println(System.currentTimeMillis() - start)
    }
}
