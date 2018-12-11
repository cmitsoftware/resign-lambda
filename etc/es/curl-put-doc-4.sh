curl -X PUT "https://search-resign-test-es-bkh4z7fqh2avchcg57ekp2tmt4.eu-west-3.es.amazonaws.com/resources/res/cerri4" -H 'Content-Type: application/json' -d'
{
	"userId":"cerri",
	"ts":"2018-12-11T07:33:30Z",
	"type":1,
	"name":"finestra di legno",
	"desc":"una vecchia finestra di legno. aspetto vintage",
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
		{"uuid":"4","name":"Legno"},
		{"uuid":"5","name":"Serramenti"},
		{"uuid":"6","name":"Vintage"}
	],
	"visibleFrom":"2018-12-08T08:20:30Z",
	"visibleTo":"2018-12-14T08:20:30Z"
}
'
