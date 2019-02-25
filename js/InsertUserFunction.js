const AWS = require('aws-sdk');
AWS.config.update({region: 'eu-west-3'});
const dynamodb = new AWS.DynamoDB({apiVersion: '2012-08-10'});
 
exports.handler = (event, context, callback) => {
    console.log(event);
	var d = new Date();
    dynamodb.putItem({
        TableName: "user",
        Item: {
				"uuid": { "S": event.request.userAttributes.sub },
				"email": { "S": event.request.userAttributes.email },
				"ts": { "S": d.toISOString()}
        }
    }, function(err, data) {
        if (err) {
            console.log(err, err.stack);
            context.fail(event);
        } else {
            context.succeed(event);
        }
    })
};