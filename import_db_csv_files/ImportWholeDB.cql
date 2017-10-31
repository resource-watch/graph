// CONCEPTS
LOAD CSV FROM 'file:///concepts.csv'
AS line
WITH line
MERGE (c:CONCEPT { id: line[0], label: line[1], synonyms: CASE line[2] WHEN null THEN '' ELSE split(line[2],'|') END, default_parent: CASE line[3] WHEN null THEN '' else line[3] END});

LOAD CSV FROM 'file:///concepts.csv'
AS line
WITH line
MATCH (c:CONCEPT { id: line[0]})
FOREACH(ignoreMe IN CASE WHEN line[4] = 'TOPIC' THEN [1] ELSE [] END | SET c:TOPIC)
FOREACH(ignoreMe IN CASE WHEN line[4] = 'GEOGRAPHY' THEN [1] ELSE [] END | SET c:GEOGRAPHY)
FOREACH(ignoreMe IN CASE WHEN line[4] = 'DATA_TYPE' THEN [1] ELSE [] END | SET c:DATA_TYPE)
WITH count(c.id) as updated_nodes
RETURN updated_nodes

// CONCEPT EDGES
LOAD CSV FROM 'file:///conceptEdges.csv'
AS line
WITH line
MATCH (c1:CONCEPT { id: line[0]}), (c2:CONCEPT { id: line[2]})
FOREACH(ignoreMe IN CASE WHEN line[1] = 'IS_A' THEN [1] ELSE [] END | MERGE (c1)-[:IS_A]->(c2))
FOREACH(ignoreMe IN CASE WHEN line[1] = 'PART_OF' THEN [1] ELSE [] END | MERGE (c1)-[:PART_OF]->(c2))
FOREACH(ignoreMe IN CASE WHEN line[1] = 'TYPE_OF' THEN [1] ELSE [] END | MERGE (c1)-[:TYPE_OF]->(c2))
FOREACH(ignoreMe IN CASE WHEN line[1] = 'QUALITY_OF' THEN [1] ELSE [] END | MERGE (c1)-[:QUALITY_OF]->(c2))
FOREACH(ignoreMe IN CASE WHEN line[1] = 'AFFECTS' THEN [1] ELSE [] END | MERGE (c1)-[:AFFECTS]->(c2))
FOREACH(ignoreMe IN CASE WHEN line[1] = 'RELATED_TO' THEN [1] ELSE [] END | MERGE (c1)-[:RELATED_TO]->(c2))
FOREACH(ignoreMe IN CASE WHEN line[1] = 'CAN_AFFECT' THEN [1] ELSE [] END | MERGE (c1)-[:CAN_AFFECT]->(c2))
FOREACH(ignoreMe IN CASE WHEN line[1] = 'IS_SIMILAR_TO' THEN [1] ELSE [] END | MERGE (c1)-[:IS_SIMILAR_TO]->(c2))
FOREACH(ignoreMe IN CASE WHEN line[1] = 'IS_INVOLVED_IN' THEN [1] ELSE [] END | MERGE (c1)-[:IS_INVOLVED_IN]->(c2))
FOREACH(ignoreMe IN CASE WHEN line[1] = 'LOCATED_AT' THEN [1] ELSE [] END | MERGE (c1)-[:LOCATED_AT]->(c2))
WITH count(c1.id) as edges_created
RETURN edges_created

// DATASETS
LOAD CSV FROM 'file:///datasets.csv'
AS line
WITH line
MERGE (c:DATASET { id: line[0] });

// DATASET TAGS
LOAD CSV FROM 'file:///datasetTags.csv'
AS line
WITH line
MATCH (d:DATASET { id: line[0] }), (c:CONCEPT { id: line[1] })
MERGE (d)-[:TAGGED_WITH]-(c);

// WIDGETS
LOAD CSV FROM 'file:///widgets.csv'
AS line
WITH line
MERGE (c:WIDGET { id: line[0] });

// WIDGET EDGES
LOAD CSV FROM 'file:///widgetEdges.csv'
AS line
WITH line
MERGE (c:WIDGET { id: line[0] })-[:BELONGS_TO]->(d:DATASET { id: line[1] });

// LAYERS
LOAD CSV FROM 'file:///layers.csv'
AS line
WITH line
MERGE (c:LAYER { id: line[0] });

// LAYER EDGES
LOAD CSV FROM 'file:///layerEdges.csv'
AS line
WITH line
MERGE (c:LAYER { id: line[0] })-[:BELONGS_TO]->(d:DATASET { id: line[1] });

// USERS
LOAD CSV FROM 'file:///users.csv'
AS line
WITH line
MERGE (c:USER { id: line[0] });

// User favorites
LOAD CSV FROM 'file:///favoritesEdges.csv'
AS line
WITH line
MATCH (u:USER { id: line[0]})
FOREACH(ignoreMe IN CASE WHEN line[1] = 'DATASET' THEN [1] ELSE [] END | MERGE (u)-[:FAVOURITE]->(d:DATASET { id: line[2] }))
FOREACH(ignoreMe IN CASE WHEN line[1] = 'WIDGET' THEN [1] ELSE [] END | MERGE (u)-[:FAVOURITE]->(d:WIDGET { id: line[2] }))
WITH count(u.id) as edges_created
RETURN edges_created

