# java-regex-cultivator
Regex generator which use genetic programming to evolve grok and automatically discover regex given a set of texts having similar structure.


# Install

Add the following dependency to your POM file:

```xml
<dependency>
  <groupId>com.github.chen0040</groupId>
  <artifactId>java-regex-cultivator</artifactId>
  <version>1.0.1</version>
</dependency>
```

# Usage

The sample code below shows how the gp regex cultivator discover the regex for the message "":

```java
GpCultivator generator = new GpCultivator();
      generator.setDisplayEvery(2);
      generator.setPopulationSize(1000);
      generator.setMaxGenerations(50);

List<String> trainingData = new ArrayList<>();
trainingData.add("user root login at 127.0.0.1");
Grok generated_grok = generator.fit(trainingData); // this is the grok interpreter generated

System.out.println("user root login at 127.0.0.1");
System.out.println(generator.getRegex()); // this is the regex generated


Match matched = generated_grok.match("user root login at 127.0.0.1");
matched.captures();
System.out.println(matched.toJson());
```

Below is the print out from the sample code above:

```bash
...
Generation: 4 (Pop: 1000), elapsed: 3 seconds
Global Cost: 0.2	Current Cost: 0.2
...
Global Cost: 0.14285714285714285	Current Cost: 0.16666666666666666
user root login at 127.0.0.1
%{LOGLEVEL} %{USER} %{URIPROTO} %{URIHOST} %{IPV4}
{"IPORHOST":"at","IPV4":"127.0.0.1","LOGLEVEL":"er","URIHOST":"at","URIPROTO":"login","USER":"root"}
```


