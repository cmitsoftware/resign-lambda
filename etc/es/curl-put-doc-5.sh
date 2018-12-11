curl -X PUT "https://search-resign-test-es-bkh4z7fqh2avchcg57ekp2tmt4.eu-west-3.es.amazonaws.com/resources/res/cerri5" -H 'Content-Type: application/json' -d'
{
	"userId":"cerri",
	"ts":"2018-12-11T07:58:30Z",
	"type":1,
	"name":"finestra di alluminio",
	"desc":"una finestra di alluminio. aspetto moderno",
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
		{"uuid":"7","name":"Alluminio"},
		{"uuid":"5","name":"Serramenti"}
	]
}
'
