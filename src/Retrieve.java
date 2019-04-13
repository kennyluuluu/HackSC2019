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
		
		JsonObject rootObj = readJsonFromURL("https://web-app.usc.edu/web/soc/api/departments/20191");
		JsonArray schools = rootObj.get("department").getAsJsonArray();
		String jsonString = schools.toString();
		System.out.println(jsonString);
		System.out.println(jsonString.matches("(.*)\"code\":\"([A-Z]*)\"(.*)"));
		
		//Retrieve classes and sections from URL
		rootObj = readJsonFromURL("https://web-app.usc.edu/web/soc/api/classes/csci/20191");
		JsonObject offeredCourses = rootObj.get("OfferedCourses").getAsJsonObject();
		JsonArray courses = offeredCourses.get("course").getAsJsonArray();
		
		//for loop to increment through each course in the specific department
		for(int i = 0; i < courses.size(); i++) {
			
			//Retrieve course ID
			JsonObject courseObj = courses.get(i).getAsJsonObject();
			String courseID = courseObj.get("PublishedCourseID").getAsString();
			System.out.print(courseID + " ");
			
			//Retrieve course title
			JsonObject courseData = courseObj.get("CourseData").getAsJsonObject();
			String title = courseData.get("title").getAsString();
			System.out.print(title + " ");
			
			//Retrieve section data
			JsonElement sectElement = courseData.get("SectionData");
			System.out.println();
			
			//Check if multiple sections
			if(sectElement.isJsonArray()) {
				JsonArray sections = courseData.get("SectionData").getAsJsonArray();
				
				//Loop through each section
				for(int j = 0; j < sections.size(); j++) {
					
					//Retrieve section number
					JsonObject section = sections.get(j).getAsJsonObject();
					String session = section.get("session").getAsString();
					System.out.print(session + " ");

					//Retrieve day
					JsonElement dayObj = section.get("day");
					if(dayObj != null && !dayObj.isJsonObject() ) {
						String day = dayObj.getAsString();
						System.out.print(day + " ");
					}
					
					//Retrieve time 
					JsonElement timeObj = section.get("start_time");
					if(timeObj != null) {
						String time = section.get("start_time").getAsString() + "-" + section.get("end_time").getAsString();
						System.out.print(time + " ");
					}
					
					//Retrieve location
					JsonElement locateObj = section.get("location");
					if(locateObj != null && !locateObj.isJsonObject()) {
						String location = section.get("location").getAsString();
						System.out.print(location + " ");
					}
					
					//Retrieve instructor
					JsonElement instructElement = section.get("instructor");
					
					//Check if instructor exists
					if(instructElement != null) {
						if(instructElement.isJsonObject()) {
							
							//Receive data for single instructor
							JsonObject instructObj = instructElement.getAsJsonObject();
							instructObj = instructObj.getAsJsonObject();
							String instructorName = instructObj.get("first_name").getAsString() + " " + instructObj.get("last_name").getAsString();
							System.out.print(instructorName + " ");
							
						} else {
							
							//Receive data for multiple instructors
							JsonArray instructArray = instructElement.getAsJsonArray();
							for(int k = 0; k < instructArray.size(); k++) {
								JsonObject instructObj = instructArray.get(k).getAsJsonObject();
								String instructorName = instructObj.get("first_name").getAsString() + " " + instructObj.get("last_name").getAsString();
								System.out.print(instructorName+ " ");
							}
						}
					}
					System.out.println();
				}
			} else {
				
				//Retrieve section
				JsonObject section = sectElement.getAsJsonObject();
				String session = section.get("session").getAsString();
				System.out.print(session + " ");

				//Retrieve day
				JsonElement dayObj = section.get("day");
				if(dayObj != null && !dayObj.isJsonObject() ) {
					String day = dayObj.getAsString();
					System.out.print(day + " ");
				}
				
				//Retrieve time
				JsonElement timeObj = section.get("start_time");
				if(timeObj != null) {
					String time = section.get("start_time").getAsString() + "-" + section.get("end_time").getAsString();
					System.out.print(time + " ");
				}
				
				//Retrieve location
				JsonElement locateObj = section.get("location");
				if(locateObj != null && !locateObj.isJsonObject()) {
					String location = section.get("location").getAsString();
					System.out.print(location + " ");
				}
				
				//Retrieve instructor
				JsonElement instructElement = section.get("instructor");
				if(instructElement != null) {
					if(instructElement.isJsonObject()) {
						JsonObject instructObj = instructElement.getAsJsonObject();
						instructObj = instructObj.getAsJsonObject();
						String instructorName = instructObj.get("first_name").getAsString() + " " + instructObj.get("last_name").getAsString();
						System.out.print(instructorName + " ");
					} else {
						JsonArray instructArray = instructElement.getAsJsonArray();
						for(int k = 0; k < instructArray.size(); k++) {
							JsonObject instructObj = instructArray.get(k).getAsJsonObject();
							String instructorName = instructObj.get("first_name").getAsString() + " " + instructObj.get("last_name").getAsString();
							System.out.print(instructorName+ " ");
						}
					}
				}
			}
			System.out.println();
		}
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
