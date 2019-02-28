const aws = require('aws-sdk');
module.exports = CreateRecord => {
  CreateRecord.controllers.createSignature = (event, context, callback) => {
    
    console.log(JSON.stringify(event, null, 2));
    
    const s3 = new aws.S3({
      signatureVersion: 'v4',
    });
    var objectKey = event.objectKey;
    const params = {
      Bucket: 'resign-test-resource-images',
      //Key: 'cerri/20181104102840',
      Key: objectKey,
      Expires: 100
    };

    s3.getSignedUrl('putObject', params, function(err, signedUrl) {
      let response;
      if (err) {
        response = {
          statusCode: 500,
          headers: {
            'Access-Control-Allow-Origin': '*',
          },
          body: JSON.stringify({
            error: 'Did not receive signed url'
          }),
        };
      } else {
        response = {
          statusCode: 200,
          headers: {
            'Access-Control-Allow-Origin': '*', // Required for CORS support to work
          },
          body: JSON.stringify({
            message: `Url successfully created`,
            signedUrl,
          })
        };
      }
      callback(null, response);
    });
  };
};