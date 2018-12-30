curl -X POST "https://ub5i5suju2.execute-api.eu-west-3.amazonaws.com/beta/resources" -H 'Content-Type: application/json' -d'
{
	"userId":"2cee43c3-e107-4eef-93ea-f9e88da4d28b",
	"type":3,
	"name":"Letto armadio",
	"desc":"Un letto che è anche un armadio",
	"resStatus":2,
	"location":{
		"administrative_area_1":"Lombardia",
		"administrative_area_2":"Varese",
		"country":"Italia",
		"lat":45.8413278,
		"lon":-8.7927409
	},
	"tags":[
		{"uuid":"086bcc21-31b5-4d6c-a43c-f4c0078e339e","name":"Legno"},
		{"uuid":"e8f10c34-4a5f-44df-b945-37873184a21","name":"Do it yourself"}
	],
	"images":[
		{
			"url":"2cee43c3-e107-4eef-93ea-f9e88da4d28b/20181229043335/letto-armadio.jpg",
			"desc": "Foto del letto"
		}
	]
}'