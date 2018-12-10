curl -X PUT "https://search-resign-test-es-bkh4z7fqh2avchcg57ekp2tmt4.eu-west-3.es.amazonaws.com/resources/res/cerri2" -H 'Content-Type: application/json' -d'
{
	"userId":"cerri",
	"ts":"2018-12-08T09:20:30Z",
	"type":2,
	"name":"name 2",
	"surname":"surname 2",
	"desc":"desc 2",
	"resStatus":2,
	"location":{
		"administrative_area_1":"Lombardia",
		"administrative_area_2":"Varese",
		"country":"Italia"
	},
	"geoLocation":{
		"lat":45.8413278,
		"lon":-8.7927409
	},
	"tags":[
		{"uuid":"2","name":"tag 2"},
		{"uuid":"3","name":"tag 3"}
	]
}
'
