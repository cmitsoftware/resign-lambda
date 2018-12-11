package org.resign.backend;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpHost;
import org.apache.lucene.search.BooleanQuery;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.ExistsQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.resign.backend.domain.Resource;
import org.resign.backend.gateway.ApiGatewayProxyResponse;
import org.resign.backend.gateway.ApiGatewayRequest;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.util.StringUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SearchResourceHandler implements RequestHandler<ApiGatewayRequest, ApiGatewayProxyResponse> {

    @Override
    public ApiGatewayProxyResponse handleRequest(ApiGatewayRequest request, Context context) {

    	context.getLogger().log("Request: " + request.toString());
    	
    	String search = null;
    	Integer type = null;
    	String[] tags = null;
    	String area1 = null;
    	String area2 = null;
    	Integer start = 0;
    	Integer length = 10;
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
    		area1 = request.getQueryStringParameters().get("area1");
    		area2 = request.getQueryStringParameters().get("area2");
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
    	
    	/*
    	 * TODO
    	 * Make parametric
    	 */
    	RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(new HttpHost("search-resign-test-es-bkh4z7fqh2avchcg57ekp2tmt4.eu-west-3.es.amazonaws.com", 443, "https")));
            	
//    	{
//  		  "query": {
//  		    "bool": {
//  		      "must": [
//  		        {
//  		          "query_string" : {
//  		            "query" : "Varese",
//  		            "fields": ["name", "surname","desc","location.administrative_area_2","tags.name"]
//  		          }
//  		        },
//  		        {
//  		          "term" : {
//  		          	"tags.uuid" : 2
//  		          }
//  		        },
//  				{
//			          "term" : {
//			          	"location.administrative_area_1" : "lombardia"
//			          }
//			        },
//  		        {
//  		          "term" : {
//  		          	"location.administrative_area_2" : "varese"
//  		          }
//  		        },
//					{
//  		          "term" : {
//  		          	"resStatus" : 2
//  		          }
//  		        },
//                  {
//                    "bool": {
//                    	"should": [
//                        {
//                           "range" : {
//                             "visibleFrom" : {
//                               "lte": "now"
//                            }
//                          }
//                        },
//                        {
//                        	"bool": {
//                            "must_not": {
//                              "exists": {
//                                "field": "visibleFrom"
//                              }
//                            }
//                          }
//                        }
//                      ]
//                    }
//                  },
//					{
//                    "bool": {
//                    	"should": [
//                        {
//                           "range" : {
//                             "visibleTo" : {
//                               "gte": "now"
//                            }
//                          }
//                        },
//                        {
//                        	"bool": {
//                            "must_not": {
//                              "exists": {
//                                "field": "visibleTo"
//                              }
//                            }
//                          }
//                        }
//                      ]
//                    }
//                  }
//  		      ]
//  		    }
//  		  }
//  		}
    	
    	BoolQueryBuilder mainQuery = QueryBuilders.boolQuery();
    	if(!StringUtils.isNullOrEmpty(search)) {
    		/*
    		 * Full text search on text fields
    		 */
    		QueryStringQueryBuilder fullTextQuery = QueryBuilders.queryStringQuery(search + "*");
    		fullTextQuery.field(Resource.NAME, (float)2.0);
    		fullTextQuery.field(Resource.SURNAME, (float)2.0);
    		fullTextQuery.field(Resource.DESC);
    		fullTextQuery.field(Resource.LOCATION + "." + Resource.ADMINISTRATIVE_AREA_1);
    		fullTextQuery.field(Resource.LOCATION + "." + Resource.ADMINISTRATIVE_AREA_2);
    		fullTextQuery.field(Resource.TAGS + "." + Resource.TAG_NAME);
    		mainQuery.must(fullTextQuery);
    	}
    	if(type != null) {
    		
    		/*
    		 * Exact filter on type
    		 */
    		TermQueryBuilder typeQuery = QueryBuilders.termQuery(Resource.TYPE, type);
    		mainQuery.must(typeQuery);
    	}
    	if(tags != null) {
    		
    		/*
    		 * Exact filter on each tag
    		 */
    		for(String t: tags) {
    			TermQueryBuilder tagQuery = QueryBuilders.termQuery(Resource.TAGS + "." + Resource.TAG_UUID, t);
    			mainQuery.must(tagQuery);
    		}
    	}
		if(!StringUtils.isNullOrEmpty(area1)) {
		    		
			/*
			 * Exact filter on administrative area 1
			 */
			TermQueryBuilder area1Query = QueryBuilders.termQuery(Resource.LOCATION + "." + Resource.ADMINISTRATIVE_AREA_1, area1);
			mainQuery.must(area1Query);
		}
		if(!StringUtils.isNullOrEmpty(area2)) {
			
			/*
			 * Exact filter on administrative area 2
			 */
			TermQueryBuilder area2Query = QueryBuilders.termQuery(Resource.LOCATION + "." + Resource.ADMINISTRATIVE_AREA_2, area2);
			mainQuery.must(area2Query);
		}
		
		/*
		 * Only resources confirmed 
		 */
		mainQuery.must(QueryBuilders.termQuery(Resource.RES_STATUS, Resource.STATUS_CONFIRMED));
		
		/*
		 * Only resources visible
		 */
		BoolQueryBuilder visibleFromQuery = QueryBuilders.boolQuery();
		visibleFromQuery.should(new RangeQueryBuilder(Resource.VISIBLE_FROM).lte("now"));
		visibleFromQuery.should(QueryBuilders.boolQuery().mustNot(new ExistsQueryBuilder(Resource.VISIBLE_FROM)));
		mainQuery.must(visibleFromQuery);
		
		BoolQueryBuilder visibleToQuery = QueryBuilders.boolQuery();
		visibleToQuery.should(new RangeQueryBuilder(Resource.VISIBLE_TO).gte("now"));
		visibleToQuery.should(QueryBuilders.boolQuery().mustNot(new ExistsQueryBuilder(Resource.VISIBLE_TO)));
		mainQuery.must(visibleToQuery);
    	
    	SearchRequest searchRequest = new SearchRequest(); 
    	SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder(); 
    	searchSourceBuilder.query(mainQuery);
    	if(!StringUtils.isNullOrEmpty(order)) {
    		searchSourceBuilder.sort(order, SortOrder.fromString(dir));
    	}
    	searchSourceBuilder.from(start);
    	searchSourceBuilder.size(length);
    	searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
    	searchRequest.source(searchSourceBuilder);
    	context.getLogger().log(mainQuery.toString());
    	String reply = null;
    	List<Resource> ret = new ArrayList<Resource>();
    	try {
			SearchResponse searchResponse = client.search(searchRequest);
			SearchHits hits = searchResponse.getHits();
			for(SearchHit h: hits.getHits()) {
//				String sourceAsString = h.getSourceAsString();
//				context.getLogger().log("source as string: " + sourceAsString);
				try {
					ret.add(Resource.buildFromMap(h.getSourceAsMap()));
				} catch(Exception ex) {
					context.getLogger().log("Resource ignored: " + ex.getMessage());
				}
			}
    	
	        ObjectMapper objectMapper = new ObjectMapper();
	    	
			reply = objectMapper.writeValueAsString(ret);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
        ApiGatewayProxyResponse response = new ApiGatewayProxyResponse(200, null, reply);
        return response;
    	
    }

}
