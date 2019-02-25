// Load the SDK for JavaScript
var AWS = require('aws-sdk');
// Set the region 
AWS.config.update({region: 'eu-west-3'});

exports.handler = (event, context, callback) => {
    //console.log(event);
    //console.log("-------");
    var s3path = event['Records'][0]['s3']['object']['key'];
    //console.log(s3path);
    var path = s3path.split("/");
    var user = path[1];
    var ts = path[2];
    var s3Image = path[3];
    //console.log(user);
    //console.log(ts);
    //console.log(s3Image);
    // Create the DynamoDB service object
    var ddb = new AWS.DynamoDB({apiVersion: '2012-08-10'});
    
    var params = {
      TableName: 'post',
      Key: {
        "userId": {S:user},
        "ts": {S:ts}
      }
    };
    
    // Call DynamoDB to read the item from the table
    ddb.getItem(params, function(err, data) {
      
      if (err) {
          console.log(err, err.stack);
          callback(null, {
              statusCode: '500',
              body: err
          });
      } else {
          //console.log("Data item - title: " + data.Item.title.S);
          //console.log("Data item - image: " + data.Item.image.S);
          var dataItemImagePath = user + "/" + ts + "/" + s3Image;
          var images = [];
          if(data.Item.images == undefined) {
            images = [{"S": dataItemImagePath}];
            data.Item.images = {"L": images};
          } else {
            //console.log(data.Item.images);
            data.Item.images.L.push({"S": dataItemImagePath});
          }
          ddb.putItem(
            {
              TableName: "post",
              Item: data.Item
            },
            function(err, data) {
              if (err) {
                  console.log(err, err.stack);
                  callback(null, {
                      statusCode: '500',
                      body: err
                  });
              } else {
                  callback(null, {
                      statusCode: '200',
                      body: 'Hello ' + event.userName + '!'
                  });
              }
          }); 
          /*
          callback(null, {
              statusCode: '200',
              body: 'ok'
          });
          */
      }
    });
};
