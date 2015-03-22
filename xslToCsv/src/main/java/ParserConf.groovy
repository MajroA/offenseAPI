/**
 * User: Miro
 * Date: 22/03/15
 * Time: 16:23
 */
class ParserConf {

    String importFileName;
    String exportFileName;
    String jsonTemplateName;
    def exportFirstLine;
    Crime crimeInstance;
    boolean downloadAreas;

    private ParserConf(importFileName, exportFileName, jsonTemplateName, exportFirstLine, Crime crimeInstance, downloadAreas) {
        this.importFileName = importFileName
        this.exportFileName = exportFileName
        this.jsonTemplateName = jsonTemplateName
        this.exportFirstLine = exportFirstLine
        this.crimeInstance = crimeInstance
        this.downloadAreas = downloadAreas
    }

    private static ParserConf INSTANCE_PARDUBICE = new ParserConf(
            "prestupky_pardubice_mestska_policie_komplet_2014.csv",
            "prestupky-pardubice.json",
            "crimeJson.template",
            ["index" :"prestupky", "type": "prestupek-pardubice"],
            new PardubiceCrime(),
            true
    )

    private static ParserConf INSTANCE_PRAHA = new ParserConf(
            "prestupky_praha_6_2013_5_2014_FIXED_DATETIME.csv",
            "prestupky-praha.json",
            "crimeJson.template",
            ["index" :"prestupky", "type": "prestupek-praha"],
            new PrahaCrime(),
            false
    )

    private static ParserConf INSTANCE_PRAHA_TEST = new ParserConf(
            "testPraha.csv",
            "prestupky-praha.json",
            "crimeJson.template",
            ["index" :"prestupky", "type": "prestupek-praha"],
            new PrahaCrime(),
            false
    )

    static ParserConf INSTANCE = INSTANCE_PRAHA_TEST;

}
