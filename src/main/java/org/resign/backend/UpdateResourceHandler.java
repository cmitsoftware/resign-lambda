package org.resign.backend;

import org.resign.backend.domain.Resource;
import org.resign.backend.domain.Tag;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class UpdateResourceHandler implements RequestHandler<Resource, Resource> {

    @Override
    public Resource handleRequest(Resource input, Context context) {
    	
    	context.getLogger().log("\nInput: " + input.toString());
//    	ObjectMapper mapper = new ObjectMapper();
//        try {
//			Resource res = mapper.readValue(input, Resource.class);
//			return mapper.writeValueAsString(res);
        	
			
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
        return input;
    }
}
