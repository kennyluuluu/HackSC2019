import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Retrieve {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		JsonObject rootObj = readJsonFromURL("https://web-app.usc.edu/web/soc/api/departments/20143");
		JsonArray schools = rootObj.get("department").getAsJsonArray();
		String jsonString = schools.toString();
		System.out.println(jsonString);
		System.out.println(jsonString.matches("\\\"code\":\"([A-Z])\\"));
		
	}
	
	public static JsonObject readJsonFromURL(String stringURL) throws IOException {
		
		URL url = new URL(stringURL);
		URLConnection request = url.openConnection();
		request.connect();
		
		JsonParser jp = new JsonParser();
		JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent()));
		
		return root.getAsJsonObject();
		
	}

}
