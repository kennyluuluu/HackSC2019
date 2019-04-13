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
			//Retrieve classes and sections from URL
			JsonObject offeredCourses = classJson.get("OfferedCourses").getAsJsonObject();
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
