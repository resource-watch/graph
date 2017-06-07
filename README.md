# Resource Watch Knowledge Graph

![screen shot 2017-06-07 at 3 06 04 pm](https://user-images.githubusercontent.com/545342/26879898-fb6500e6-4b92-11e7-8bdc-8dc469b1cf2b.png)

## Online Data browser

http://104.131.87.76:7474/browser/

## Data model

### Current node types

* CONCEPT
* DATASET

### Current relationship types

* RELATED_TO _This is a generic relationship used whenever it's not possible to use one of the more specific types_
* TYPE_OF
* PART_OF
* LOCATED_AT
* TAGGED_WITH
* TRIGGERED_BY
* IS_A
* GENERATED_BY
* IS_INVOLVED_IN

## Database creation

The knowledge graph of concepts is generated from the information stated in [this Google Spreadsheet](https://docs.google.com/a/vizzuality.com/spreadsheets/d/1awsO5aPEOv_OEFTakIhn-Ej7RFw46UP-jUWXnskPRqk/edit?usp=sharing).
 
### Step by step guide to import the Graph

1. Download the aforementioned spreadsheet as a CSV file
2. Upload it to the server using scp
3. Copy it to the folder called **import** that is located inside the Neo4j installation
4. Download the JSON file resulting from this request to the WRI API https://api.resourcewatch.org/dataset/?app=rw&includes=vocabulary
5. Execute the JAVA program: [src/main/java/com/vizzuality/CreateDatasetTagCSV.java]
6. Upload the resulting file to the server using scp
7. Copy it to the folder called **import** that is located inside the Neo4j installation
8. Execute the various Cypher statements included in the file: [ImportDBCypher.txt](ImportDBCypher.txt)
