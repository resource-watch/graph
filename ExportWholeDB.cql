// EXPORT WHOLE DATABASE
MATCH (a)-[r]->(b)
WITH collect(
    {
        source: a.id,
        target: b.id,
        relType: type(r),
        sourceType: labels(a),
        targetType: labels(b)
    }
) AS edges
MATCH (a)
WITH edges, COLLECT({ id: a.id, label: a.label, synonyms: a.synonyms, default_parent: a.default_parent, labels: labels(a)}) AS nodes
RETURN { edges: edges, nodes: nodes }