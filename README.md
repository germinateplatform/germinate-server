# Germinate

## Code generation

Linux:
```shell script
java -cp ./lib/lib-dev/*:./lib/*:./germinate.jar org.jooq.codegen.GenerationTool ./res/jooq.xml
```

Windows:
```
java -cp ./lib/lib-dev/*;./lib/*;./germinate.jar org.jooq.codegen.GenerationTool ./res/jooq.xml
```