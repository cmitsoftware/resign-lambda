curl -X PUT "https://search-resign-test-es-bkh4z7fqh2avchcg57ekp2tmt4.eu-west-3.es.amazonaws.com/resources/res/cerri3" -H 'Content-Type: application/json' -d'
{
	"userId":"cerri",
	"ts":"2018-12-08T08:20:30Z",
	"type":3,
	"name":"name 3",
	"surname":"surname 3",
	"desc":"desc 3",
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
		{"uuid":"1","name":"tag 1"},
		{"uuid":"2","name":"tag 2"},
		{"uuid":"3","name":"tag 3"}
	],
	"visibleFrom":"2018-12-12T08:20:30Z",
	"visibleTo":"2018-12-14T08:20:30Z"
}
'
