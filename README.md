# jM2converter 1.0.8a-beta (fixed !)
World of Warcraft Universal Model Converter.  
Works using the [jM2lib project](https://github.com/Koward/jM2lib). *Still early prototype.*  

##Usage (example with Frog.m2) :
This is a Java command-line tool.  
You may need to install Java : [Install Java](https://java.com/en/download/help/download_options.xml)  
And setup paths for your environment : [PATH and CLASSPATH](https://docs.oracle.com/javase/tutorial/essential/environment/paths.html)  
`java -jar jm2converter.jar -in "Frog.m2" -out "FrogConverted.m2" <OPTIONS>`

Type `java -jar jm2converter.jar` to get a list of all converting options ;)

* Beware, when you retroport a model, you always lose some data (the newer client features) and you won't be able to get them back if you up convert it back later.

* The Legion support is still changing. The game is still in Beta so the M2 format changes. Right now it should work up to build 20810.

##Download :
[MediaFire](http://adf.ly/1TtsYH)

##What's yet to be implemented :
* BC => LK up-conversion.
* LK => Cata cameras up-conversion.
* MDX <=> Classic conversion (needs MDX support in jM2lib first)

##Known issues :
* Ribbons are sometimes funky.
* Colors may go weird, like yellow instead of purple.

##Credits :
* Thanks to all the guys who test this to find bugs ;) !
