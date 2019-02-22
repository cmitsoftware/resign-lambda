package org.resign.backend.util;

import java.io.UnsupportedEncodingException;

import org.json.JSONObject;

import com.amazonaws.util.Base64;

public class CognitoUtil {

	public static String readSubFromAuthorization(String authorizationHeader) throws UnsupportedEncodingException {
		
		String payload;
		payload = new String(Base64.decode(authorizationHeader.split("\\.")[1]), "UTF-8");
//			context.getLogger().log("Payload: " + payload);
		JSONObject json = new JSONObject(payload);
		String sub = json.getString("sub");
		return sub;
			
	}
}
