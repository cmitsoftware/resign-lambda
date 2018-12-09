package org.resign.backend;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.resign.backend.domain.Resource;
import org.resign.backend.gateway.ApiGatewayProxyResponse;
import org.resign.backend.gateway.ApiGatewayRequest;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.util.StringUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SearchResourceHandler implements RequestHandler<ApiGatewayRequest, ApiGatewayProxyResponse> {

    @Override
    public ApiGatewayProxyResponse handleRequest(ApiGatewayRequest request, Context context) {

    	//search: search token
    	//type: type of resource
    	//s: start index
    	//l: length
    	//o: order (default ts)
    	//d: order direction (default desc)
    	
    	context.getLogger().log("Request: " + request.toString());
    	
    	String search = null;
    	Integer type = null;
    	String[] tags = null;
//    	String address = null;
    	Integer start = null;
    	Integer length = null;
    	String order = null;
    	String dir = null;
    	if(request.getQueryStringParameters() != null) {
    		
    		search = request.getQueryStringParameters().get("search");
    		try {
    			String typeS = request.getQueryStringParameters().get("type");
    			if(!StringUtils.isNullOrEmpty(typeS)) {
    				type = Integer.parseInt(typeS);
    			}
    		} catch (Exception e) {
    			context.getLogger().log("Error: " + e.getMessage());
    		}
    		try {
    			String tagsS = request.getQueryStringParameters().get("tags");
    			if(!StringUtils.isNullOrEmpty(tagsS)) {
    				tags = tagsS.split(",");
    			}
    		} catch (Exception e) {
    			context.getLogger().log("Error: " + e.getMessage());
    		}
    		try {
    			String startS = request.getQueryStringParameters().get("s");
    			if(!StringUtils.isNullOrEmpty(startS)) {
    				start = Integer.parseInt(startS);
    			}
    		} catch (Exception e) {
    			context.getLogger().log("Error: " + e.getMessage());
    		}
    		try {
    			String lengthS = request.getQueryStringParameters().get("l");
    			if(!StringUtils.isNullOrEmpty(lengthS)) {
    				length = Integer.parseInt(lengthS);
    			}
    		} catch (Exception e) {
    			context.getLogger().log("Error: " + e.getMessage());
    		}
    		order = request.getQueryStringParameters().get("o");
    		dir = request.getQueryStringParameters().get("d");
    	}
    	
//    	AmazonDynamoDB ddb = AmazonDynamoDBClientBuilder.standard()
//    			.withRegion(Regions.EU_WEST_3)
//    			.build();
//    	DynamoDBMapper mapper = new DynamoDBMapper(ddb);
//    	
//    	Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
//    	eav.put(":status", new AttributeValue().withN(String.valueOf(Resource.STATUS_CONFIRMED)));
//    	if(!StringUtils.isNullOrEmpty(search)) {
//    		eav.put(":name", new AttributeValue().withS(search));
//    		eav.put(":surname", new AttributeValue().withS(search));
//    		eav.put(":desc", new AttributeValue().withS(search));
//    	}
//    	
//    	DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
//    	String filter = "resStatus = :status"; 
//    	if(!StringUtils.isNullOrEmpty(search)) {
//    		filter += " and (contains(name, :name) or contains(surname, :surname)) or contains(desc, :desc))";
//    	}
//    	scanExpression.withFilterExpression(filter.trim()).withExpressionAttributeValues(eav);
//        List<Resource> searchResult = mapper.scan(Resource.class, scanExpression);
//
    	RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(new HttpHost("search-resign-test-es-bkh4z7fqh2avchcg57ekp2tmt4.eu-west-3.es.amazonaws.com", 443, "https")));
            	
//    	{
//    		  "query": {
//    		    "bool": {
//    		      "must": [
//    		        {
//    		          "query_string" : {
//    		            "query" : "Varese",
//    		            "fields": ["name", "surname","desc","location.administrative_area_2","tags.name"]
//    		          }
//    		        },
//    		        {
//    		          "term" : {
//    		          	"tags.uuid" : 2
//    		          }
//    		        },
//    		        {
//    		          "term" : {
//    		          	"location.administrative_area_2" : "varese"
//    		          }
//    		        }
//    		      ]
//    		    }
//    		  }
//    		}
    	
    	
    	BoolQueryBuilder mainQuery = QueryBuilders.boolQuery();
    	if(!StringUtils.isNullOrEmpty(search)) {
    		
    		/*
    		 * Full text search on text fields
    		 */
    		QueryStringQueryBuilder fullTextQuery = QueryBuilders.queryStringQuery(search + "*");
    		fullTextQuery.field("name");
    		fullTextQuery.field("surname");
    		fullTextQuery.field("desc");
    		fullTextQuery.field("location.administrative_area_1");
    		fullTextQuery.field("location.administrative_area_2");
    		fullTextQuery.field("tags.name");
    		mainQuery.must(fullTextQuery);
    	}
    	if(type != null) {
    		
    		/*
    		 * Exact filter on type
    		 */
    		TermQueryBuilder typeQuery = QueryBuilders.termQuery("type", type);
    		mainQuery.must(typeQuery);
    	}
    	if(tags != null) {
    		
    		/*
    		 * Exact filter on each tag
    		 */
    		for(String t: tags) {
    			TermQueryBuilder tagQuery = QueryBuilders.termQuery("tags.uuid", t);
    			mainQuery.must(tagQuery);
    		}
    	}
    	
    	SearchRequest searchRequest = new SearchRequest(); 
    	SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder(); 
    	searchSourceBuilder.query(mainQuery);
    	searchSourceBuilder.sort(order, SortOrder.fromString(dir));
    	searchSourceBuilder.from(start);
    	searchSourceBuilder.size(length);
    	searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
    	searchRequest.source(searchSourceBuilder);
    	context.getLogger().log(mainQuery.toString());
    	String reply = null;
    	try {
			SearchResponse searchResponse = client.search(searchRequest);
			SearchHits hits = searchResponse.getHits();
			for(SearchHit h: hits.getHits()) {
				String sourceAsString = h.getSourceAsString();
				context.getLogger().log("source as string: " + sourceAsString);
				
				for(String f: h.getSourceAsMap().keySet()) {
					context.getLogger().log(f + " : " + h.getSourceAsMap().get(f));
				}
			}
    	
	        ObjectMapper objectMapper = new ObjectMapper();
	    	
//			reply = objectMapper.writeValueAsString(searchResult);
			reply = objectMapper.writeValueAsString(new ArrayList<Resource>());
		} catch (Exception e) {
			e.printStackTrace();
		}
        ApiGatewayProxyResponse response = new ApiGatewayProxyResponse(200, null, reply);
        return response;
    	
    }

}
