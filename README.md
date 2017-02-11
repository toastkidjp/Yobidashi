Yobidashi
====

This tool works as ten hands (I don't say this tool works as ten hands).

# Features
1. Writing article with Confluense(TM) like markup or Markdown.
2. Generate article's word cloud.
3. Slide show.
4. JVM Language Script Runner(Groovy/Clojure/JavaScript).
5. BMI Calculator.
6. Drawing chart.
7. Cup noodle timer.
8. CSS Generator.
9. Name Generator.
10. Loto6 number generator.

# Getting start

```shell
$ gradle clean jar del
$ java -jar Yobidashi-0.0.1.jar
```

# License
This software is licensed with Eclipse Public License - v 1.0.

## Dependencies
This software contains following open source softwares. Thanks a lot. :bow:

### JavaFX

| Name | License | Comment |
|:---|:---|:---|
| [JFoenix](http://jfoenix.com/) | [Apache License Version 2.0](https://github.com/jfoenixadmin/JFoenix/blob/master/LICENSE) | Provide Material design components.
| [RichTextFX](https://github.com/TomasMikula/RichTextFX) | BSD &amp; GPLv2 Classpath Exception | provide Rich text area. It support line-number, syntax highlighting, and so on.

### Java

| Name | License | Comment |
|:---|:---|:---|
| [Reactor Core](https://projectreactor.io/) | Apache License Version 2.0 | Implementation of Reactive Extensions.
| [Eclipse Collections](https://www.eclipse.org/collections/) | Eclipse Public License ver 1.0 | Fantastic Java Collection library.
| [commons-lang3](https://commons.apache.org/proper/commons-lang/) | [The Apache Software License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.txt)
| [PDFBox](https://pdfbox.apache.org/) | Apache License 2.0 | Converting slides to PDF
| [commons-io](http://commons.apache.org/proper/commons-io/) | Apache License 2.0 | PDF-Box's dependency lib.
| [jackson-databind](https://github.com/FasterXML/jackson-databind) | Apache License Version 2.0 | convert string to POJO.
| [slf4j-api](http://www.slf4j.org/) | [MIT license](http://www.slf4j.org/license.html) | Logging
| [Groovy](http://www.groovy-lang.org/) | [The Apache Software License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.txt) | Use at ScriptRunner.

### HTML & JavaScript

| Name | License | Comment |
|:---|:---|:---|
| [jQuery](https://jquery.com/) | MIT License
| [highlight.js](https://highlightjs.org/) | BSD 3-clause | Implement syntax hilighting.

### For testing

| Name | License | Comment |
|:---|:---|:---|
| [TestFX](https://github.com/TestFX/TestFX) | EUPL | JavaFX testing library.
| [Mockito](http://mockito.org/) | MIT | Mock lib for testing.
