import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import java.util.Date;
import java.util.Calender;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Retrieve {
	public static int getYear() {
		Date today = new Date();
		Calender cal = Calender.getInstance();
		cal.setTime(today);
		return cal.get(Calender.YEAR);
	}

	public static int getSemester() {
		Date today = new Date();
		Calender cal = Calender.getInstance();
		cal.setTime(today);
		int month = cal.get(Calender.MONTH);
		if (month > 0 && month <= 5)
			return 1;
		else if (month > 5 && month <= 7)
			return 2;
		else return 3;	
	}

	public static <T> Set<T> mergeSet(Set<T> a, Set<T> b)
	{
		return new HashSet<T>() {{
				addAll(a);
				addAll(b);
		}};
	}

	public static HashSet<String> findDepartmentCodes(JsonElement x) {
		HashSet<String> codes = new HashSet<String>()
		if (x.isJsonArray()) {
			for (int i = 0; i < x.size(); i++) {
				codes = mergeSet(codes, findDepartmentCodes(x.getJsonObject(i));
			}
		}
		else if (x.isJsonObject()) {
			for (int j = 0; j < x.size(); j++) {
				if (x.has("code") {
					codes.add(x.getJsonObject("code").getAsString());
				}
				codes = mergeSet(codes, r(x.getJsonObject(i));
			}
		}
		return codes;
	}

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		JsonObject rootObj = readJsonFromURL("https://web-app.usc.edu/web/soc/api/departments/20143");
		HashSet<String> codes = new HashSet<String>();
		codes = r(rootObj);
		int year = getYear();
		// 1 is sprint, 2 is summer, 3 is fall
		int semester = getSemester();

		for(int i = 0; i < codes.size(); i++)
			JsonObject classJson = readJsonFromURL("https://web-app.usc.edu/web/soc/api/classes/" + codes[i] + "/" + Integer.toString(year) + Integer.toString(semester));
			// RICHARD's part
		}


		//System.out.println(jsonString);
		//System.out.println(jsonString.matches("\\\"code\":\"([A-Z])\\"));
		
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
