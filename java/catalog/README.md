## Using the z/OS Catalog Search Interface from Java ##

IBM JZOS provides the class **com.ibm.jzos.CatalogSearch** to provide an interface to IGGCSI00 from Java.

These sample programs demonstrate its use. The programs can be run under the JZOS batch launcher using the JCL procedures included in this repository.

### ListCatalog ###

List entries matching a search pattern.

#### JCL ####

```
//JAVAG    EXEC PROC=JAVAG,                                     
// CLASPATH='./java/catalog*',                                  
// JAVACLS='com/blackhillsoftware/catalog/sample/ListCatalog'
//G.MAINARGS DD *                                               
SYS1.**                                                                                                   
```

[ListCatalog.java](./src/main/java/com/blackhillsoftware/catalog/sample/ListCatalog.java)

### CompareHLQ ###

Compare entries found under 2 different high level qualifiers (or multiple qualifiers) and show the differences, e.g. for a product upgrade.

#### JCL ####

```
//JAVAG    EXEC PROC=JAVAG,                                     
// CLASPATH='./java/catalog*',                                  
// JAVACLS='com/blackhillsoftware/catalog/sample/CompareHLQ'
//G.MAINARGS DD *                                               
DFH540
DFH550                                                  
```

[CompareHLQ.java](./src/main/java/com/blackhillsoftware/catalog/sample/CompareHLQ.java)

### CompareCatalog ###

Compare the entries in 2 different catalogs and show the differences, e.g. if you need to switch to a new master catalog as part of a z/OS upgrade.

#### JCL ####

```
//JAVAG    EXEC PROC=JAVAG,                                     
// CLASPATH='./java/catalog*',                                  
// JAVACLS='com/blackhillsoftware/catalog/sample/CompareCatalog'
//G.MAINARGS DD *                                               
CATALOG.MASTER                                                 
CATALOG.MASTER.NEW                                                  
```

[CompareCatalog.java](./src/main/java/com/blackhillsoftware/catalog/sample/CompareCatalog.java)




