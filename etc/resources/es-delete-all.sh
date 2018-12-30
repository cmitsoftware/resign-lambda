curl -X POST "https://search-resign-test-es-bkh4z7fqh2avchcg57ekp2tmt4.eu-west-3.es.amazonaws.com/resources/_delete_by_query?conflicts=proceed" -H 'Content-Type: application/json' -d'
{
  "query": {
    "match_all": {}
  }
}'