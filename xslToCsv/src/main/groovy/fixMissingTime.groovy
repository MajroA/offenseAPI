if (args.length == 0) {
	println "Run with 'filepath' argument"
	return;
}
def filepath = args[0]

def previousTime = ""
new File("${filepath}.fix").withWriter("UTF-8") {writer ->

	new File("${filepath}").eachLine("UTF-8") {line ->
		def splits = line.split(";")
		if (splits.size() == 0) {
			return;
		}

        if(splits[0].equals("")) {
    		writer << previousTime + line << "\n"
        } else {
        	previousTime = splits[0]
        	writer << line << "\n"
        }
	}
}
