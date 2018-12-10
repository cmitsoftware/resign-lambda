curl -X PUT "https://search-resign-test-es-bkh4z7fqh2avchcg57ekp2tmt4.eu-west-3.es.amazonaws.com/resources/res/cerri1" -H 'Content-Type: application/json' -d'
{
	"userId":"cerri",
	"ts":"2018-12-08T10:20:30Z",
	"type":1,
	"name":"name 1",
	"surname":"surname 1",
	"desc":"desc 1",
	"resStatus":2,
	"location":{
		"administrative_area_1":"Lombardia",
		"administrative_area_2":"Como",
		"country":"Italia"
	},
	"geoLocation":{
		"lat":45.8413278,
		"lon":-8.7927409
	},
	"tags":[
		{"uuid":"1","name":"tag 1"},
		{"uuid":"2","name":"tag 2"}
	]
}
'
