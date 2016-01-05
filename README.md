# jM2converter
World of Warcraft Universal Model Converter;
Works using the [jM2lib project](https://github.com/Koward/jM2lib). *Still early prototype.*

##Usage (example with Frog.m2) :
`java -jar jm2converter.jar -in "Frog.m2" -out "FrogConverted.m2" <OPTIONS>`

Type `java -jar jm2converter.jar` to get a list of all converting options ;)

* Beware, when you retroport a model, you always lose some data (the newer client features) and you won't be able to get them back if you up convert it back later.

* The Legion support is still changing. The game is still in Beta so the M2 format changes. Right now it should work for build 20810.

##Download :
http://adf.ly/1TtsYH

##What's yet to be implemented :
* BC to LK up-conversion.
* MDX to Classic up-conversion (needs MDX support in jM2lib first)

##Known issues :
* Some particles are not displayed.
* Ribbons are sometimes funky.
* Colors may go weird, like yellow instead of purple.

##Credits :
* Thanks to all the guys who test this to find bugs ;) !

