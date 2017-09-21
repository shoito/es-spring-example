```
curl -XDELETE 'localhost:9200/male'
curl -XPUT 'localhost:9200/male?pretty' -H 'Content-Type: application/json' -d @infrastructure/elasticsearch/indices/male.json

curl -XDELETE 'localhost:9200/female'
curl -XPUT 'localhost:9200/female?pretty' -H 'Content-Type: application/json' -d @infrastructure/elasticsearch/indices/female.json
```