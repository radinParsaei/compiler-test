all: jar INF native-image
jar:
	javac *.java
	echo Manifest-Version: 1.0 > manifest.txt
	echo Main-Class: Main >> manifest.txt
	jar cvfm output.jar manifest.txt *.class
	rm manifest.txt
native-image:
	native-image 2>&1 >/dev/null && echo native-image installed! || (echo native-image not found && exit 1)
	native-image -jar output.jar --no-fallback -H:ReflectionConfigurationFiles=META-INF/native-image/reflect-config.json
INF:
	mkdir -p META-INF/native-image
	echo 'print "OK" &' > samplecode
	java -agentlib:native-image-agent=config-output-dir=META-INF/native-image Main samplecode
	rm samplecode
clean:
	rm *.class *.jar output ; true
