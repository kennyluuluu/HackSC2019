import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Calendar;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Retrieve {
	public static int getYear() {
		Date today = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(today);
		return cal.get(Calendar.YEAR);
	}

	public static int getSemester() {
		Date today = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(today);
		int month = cal.get(Calendar.MONTH);
		if (month > 0 && month <= 5)
			return 1;
		else if (month > 5 && month <= 7)
			return 2;
		else return 3;	
	}

	@SuppressWarnings("serial")
	public static <T> HashSet<T> mergeSet(HashSet<T> a, HashSet<T> b)
	{
		return new HashSet<T>() {{
				addAll(a);
				addAll(b);
		}};
	}

	public static HashSet<String> findDepartmentCodes(JsonElement x) {
		HashSet<String> codes = new HashSet<String>();
		if (x.isJsonArray()) {
			for (int i = 0; i < ((JsonArray) x).size(); i++) {
				codes = mergeSet(codes, findDepartmentCodes(((JsonArray) x).get(i).getAsJsonObject()));
			}
		}
		else if (x.isJsonObject()) {
			JsonObject y = ((JsonObject) x);
			Set<Entry <String, JsonElement>> entrySet = y.entrySet();
			for(Map.Entry<String, JsonElement> entry : entrySet) {
				if(entry.getValue().isJsonObject() || entry.getValue().isJsonArray()) {
					codes = mergeSet(codes, findDepartmentCodes(entry.getValue()));
				} else if(entry.getKey().equals("code")) {
					codes.add(y.get("code").getAsString());
				}
			}
		}
		return codes;
	}

<<<<<<< HEAD
	// Code from https://www.tutorialspoint.com/sqlite/sqlite_java.htm
	public static Connection SQL_init() {
		Connection c = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:test.db");
			c.setAutoCommit(false);
			System.out.println("Opened database successfully");

			stmt = c.createStatement();
			String sql = "CREATE TABLE CLASSES " +
					"(TITLE		TEXT	NOT NULL," +
					" DAY		TEXT	NOT NULL," +
					" START_TIME	TIME	NOT NULL," +
					" END_TIME	TIME	NOT NULL," +
					" LOCATION	TEXT	NOT NULL," +
					" INSTRUCTOR	TEXT)";
			stmt.execute(sql);
			stmt.close();
			return c	

		} catch (Exception e) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			System.exit(0);
		}
		return null;
	}

	public static void SQL_insert(Connection c, String title, String day, String start_time, String end_time,
				      String location, String instructor) {
		
		Statement stmt = null;
		
		try {
			stmt = c.createStatement();
			String sql = "INSERT INTO CLASSES (TITLE,DAY,START_TIME,END_TIME,LOCATION,INSTRUCTOR) " +
					"VALUES ('" + title + "', '"
			       			    + day + "', "
						    + start_time + ", "
						    + end_time + ", '"
						    + location + "', '"
						    + instructor + "' )";
			stmt.executeUpdate(sql);
			stmt.close();
			c.commit();
		} catch (Exception e) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			System.exit(0);
		}
	}

	public static void findClasses(HashSet<String> codes, Iterator<String> iterate, int year, int semester) throws IOException {

		Connection dbConnection = SQL_init();

		Iterator<String> iterate = codes.iterator();
		while(iterate.hasNext()) {
			
			
			String departmentCode = iterate.next();
			JsonObject classJson = readJsonFromURL("https://web-app.usc.edu/web/soc/api/classes/" + departmentCode + "/" + Integer.toString(year) + Integer.toString(semester));
			if(classJson == null) {
				continue;
			}
			
			//Retrieve classes and sections from URL
			JsonObject offeredCourses = classJson.get("OfferedCourses").getAsJsonObject();
			
			JsonElement coursesElement = offeredCourses.get("course");
			
			if(coursesElement.isJsonArray()) {
				
				JsonArray courses = coursesElement.getAsJsonArray();
			
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
							JsonElement dayElement = section.get("day");
							if(dayElement != null) {
								if (dayElement.isJsonObject()){
									JsonObject dayObject = dayElement.getAsJsonObject();
									if(dayObject != null) {
										JsonElement dayWithinElement = dayObject.get("day");
										if(dayWithinElement != null) {
											String day = dayWithinElement.getAsString();
											System.out.print(day + " ");
										}
									}
								} else if (dayElement.isJsonArray()){
									JsonArray dayArray = dayElement.getAsJsonArray();
									for(int k = 0; k < dayArray.size(); k++) {
										JsonElement dayWithinElement = dayArray.get(k);
										if(!dayWithinElement.isJsonObject()) {
											String day = dayArray.get(k).getAsString();
											System.out.print(day + " ");
										}

									}
								} else {
									String day = dayElement.getAsString();
									System.out.print(day + " ");
								}
							}
	
							//Retrieve time 
							JsonElement timeElement = section.get("start_time");
							if(timeElement != null) {
								if(timeElement.isJsonObject()) {
									String time = section.get("start_time").getAsString() + "-" + section.get("end_time").getAsString();
									System.out.print(time + " ");
								} else if (timeElement.isJsonArray()){
									JsonArray timeArray = timeElement.getAsJsonArray();
									for(int k = 0; k < timeArray.size(); k++) {
										JsonElement timeWithinElement = timeArray.get(k);
										if(!timeWithinElement.isJsonObject()) {
											String time = timeArray.get(k).getAsString() + "-" + section.get("end_time").getAsJsonArray().get(k).getAsString();
											System.out.print(time+ " ");
										}
									}
								} else {
									String time = timeElement.getAsString() + "-" + section.get("end_time").getAsString();
									System.out.print(time + " ");
								}
							}
						
							//Retrieve location
							JsonElement locateElement = section.get("location");
							if(locateElement != null && !locateElement.isJsonObject()) {
								if(locateElement.isJsonObject()) {
									String location = section.get("location").getAsString();
									System.out.print(location + " ");
								} else if(locateElement.isJsonArray()) {
									JsonArray locateArray = locateElement.getAsJsonArray();
									for(int k = 0; k < locateArray.size(); k++) {
										JsonElement locateWithinElement = locateArray.get(k);
										if(!locateWithinElement.isJsonObject()) {
											String location = locateArray.get(k).getAsString();
											System.out.print(location + " ");
										}
									}
								} else {
									String location = locateElement.getAsString();
									System.out.print(location + " ");
								}
							}
						
							//Retrieve instructor
							JsonElement instructElement = section.get("instructor");
						
							//Check if instructor exists
							if(instructElement != null) {
								if(instructElement.isJsonObject()) {
								
									//Receive data for single instructor
									JsonObject instructObj = instructElement.getAsJsonObject();
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
						JsonElement dayElement = section.get("day");
						if(dayElement != null) {
							if(dayElement.isJsonObject()) {
								JsonObject dayObject = dayElement.getAsJsonObject();
								JsonElement dayWithinObject = dayObject.get("day");
								if(dayWithinObject != null) {
									String day = dayObject.get("day").getAsString();
									System.out.print(day + " ");
								}
							} else if(dayElement.isJsonArray()) {
								JsonArray dayArray = dayElement.getAsJsonArray();
								for(int k = 0; k < dayArray.size(); k++) {
									JsonElement dayWithinElement = dayArray.get(k);
									if(dayWithinElement.isJsonObject()) {
										JsonObject dayObject = dayWithinElement.getAsJsonObject();
										JsonElement dayWithinObject = dayObject.get("day");
										if(dayWithinObject != null) {
											String day = dayWithinObject.getAsString();
											System.out.print(day+ " ");
										}
									} else {
										String day = dayWithinElement.getAsString();
										System.out.print(day + " ");
									}

								}
							} else {
								String day = dayElement.getAsString();
								System.out.print(day + " ");
							}
						}
					
						//Retrieve time
						JsonElement timeElement = section.get("start_time");
						if(timeElement != null) {
							if(timeElement.isJsonObject()) {
								String time = section.get("start_time").getAsString() + "-" + section.get("end_time").getAsString();
								System.out.print(time + " ");
							} else if (timeElement.isJsonArray()){
								JsonArray timeArray = timeElement.getAsJsonArray();
								for(int k = 0; k < timeArray.size(); k++) {
									JsonElement timeWithinElement = timeArray.get(k);
									if(timeWithinElement != null) {
										if(!timeWithinElement.isJsonObject()) {
											String time = timeWithinElement.getAsString() + "-" + section.get("end_time").getAsJsonArray().get(k).getAsString();
											System.out.print(time+ " ");
										}
									}
								}
							} else {
								String time = timeElement.getAsString() + "-" + section.get("end_time").getAsString();
								System.out.print(time + " ");
							}
						}
					
						//Retrieve location
						JsonElement locateElement = section.get("location");
						if(locateElement != null) {
							if(locateElement.isJsonObject()) {
	//							JsonObject locateObject = locateElement.getAsJsonObject();
	//							String location = locateObject.get("location").getAsString();
	//							System.out.print(location + " ");
							} else if(locateElement.isJsonArray()) {
								JsonArray locateArray = locateElement.getAsJsonArray();
								for(int k = 0; k < locateArray.size(); k++) {
									JsonElement locateWithinElement = locateArray.get(k);
									if(!locateWithinElement.isJsonObject()) {
										String location = locateWithinElement.getAsString();
										System.out.print(location + " ");
									}
								}
							} else {
								String location = section.get("location").getAsString();
								System.out.print(location + " ");
							}
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
			} else {
				JsonObject courseObj = coursesElement.getAsJsonObject();
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
						JsonElement dayElement = section.get("day");
						if(dayElement != null) {
							if (dayElement.isJsonObject()){
								JsonObject dayObject = dayElement.getAsJsonObject();
								if(dayObject != null) {
									JsonElement dayWithinElement = dayObject.get("day");
									if(dayWithinElement != null) {
										String day = dayWithinElement.getAsString();
										System.out.print(day + " ");
									}
								}
							} else if (dayElement.isJsonArray()){
								JsonArray dayArray = dayElement.getAsJsonArray();
								for(int k = 0; k < dayArray.size(); k++) {
									String day = dayArray.get(k).getAsString();
									System.out.print(day + " ");
								}
							} else {
								String day = dayElement.getAsString();
								System.out.print(day + " ");
							}
						}

						//Retrieve time 
						JsonElement timeElement = section.get("start_time");
						if(timeElement != null) {
							if(timeElement.isJsonObject()) {
								String time = section.get("start_time").getAsString() + "-" + section.get("end_time").getAsString();
								System.out.print(time + " ");
							} else if (timeElement.isJsonArray()){
								JsonArray timeArray = timeElement.getAsJsonArray();
								for(int k = 0; k < timeArray.size(); k++) {
									String time = timeArray.get(k).getAsString() + "-" + timeArray.get(k).getAsString();
									System.out.print(time+ " ");
								}
							} else {
								String time = timeElement.getAsString() + "-" + section.get("end_time").getAsString();
								System.out.print(time + " ");
							}
						}
					
						//Retrieve location
						JsonElement locateElement = section.get("location");
						if(locateElement != null && !locateElement.isJsonObject()) {
							if(locateElement.isJsonObject()) {
								String location = section.get("location").getAsString();
								System.out.print(location + " ");
							} else if(locateElement.isJsonArray()) {
								JsonArray locateArray = locateElement.getAsJsonArray();
								for(int k = 0; k < locateArray.size(); k++) {
									String location = locateArray.get(k).getAsString();
									System.out.print(location + " ");
								}
							} else {
								String location = locateElement.getAsString();
								System.out.print(location + " ");
							}
						}
					
						//Retrieve instructor
						JsonElement instructElement = section.get("instructor");
					
						//Check if instructor exists
						if(instructElement != null) {
							if(instructElement.isJsonObject()) {
							
								//Receive data for single instructor
								JsonObject instructObj = instructElement.getAsJsonObject();
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
					JsonElement dayElement = section.get("day");
					if(dayElement != null) {
						if(dayElement.isJsonObject()) {
							JsonObject dayObject = dayElement.getAsJsonObject();
							JsonElement dayWithinObject = dayObject.get("day");
							if(dayWithinObject != null) {
								String day = dayObject.get("day").getAsString();
								System.out.print(day + " ");
							}
						} else if(dayElement.isJsonArray()) {
							JsonArray dayArray = dayElement.getAsJsonArray();
							for(int k = 0; k < dayArray.size(); k++) {
								JsonObject dayObject = dayArray.get(k).getAsJsonObject();
								JsonElement dayWithinObject = dayObject.get("day");
								if(dayWithinObject != null) {
									String day = dayWithinObject.getAsString();
									System.out.print(day+ " ");
								}
							}
						} else {
							String day = dayElement.getAsString();
							System.out.print(day + " ");
						}
					}
				
					//Retrieve time
					JsonElement timeElement = section.get("start_time");
					if(timeElement != null) {
						if(timeElement.isJsonObject()) {
							String time = section.get("start_time").getAsString() + "-" + section.get("end_time").getAsString();
							System.out.print(time + " ");
						} else if (timeElement.isJsonArray()){
							JsonArray timeArray = timeElement.getAsJsonArray();
							for(int k = 0; k < timeArray.size(); k++) {
								JsonElement timeWithinElement = timeArray.get(k);
								if(timeWithinElement != null) {
									if(!timeWithinElement.isJsonObject()) {
										String time = timeWithinElement.getAsString() + "-" + section.get("end_time").getAsJsonArray().get(k).getAsString();
										System.out.print(time+ " ");
									}
								}
							}
						} else {
							String time = timeElement.getAsString() + "-" + section.get("end_time").getAsString();
							System.out.print(time + " ");
						}
					}
				
					//Retrieve location
					JsonElement locateElement = section.get("location");
					if(locateElement != null) {
						if(locateElement.isJsonObject()) {
//							JsonObject locateObject = locateElement.getAsJsonObject();
//							String location = locateObject.get("location").getAsString();
//							System.out.print(location + " ");
						} else if(locateElement.isJsonArray()) {
							JsonArray locateArray = locateElement.getAsJsonArray();
							for(int k = 0; k < locateArray.size(); k++) {
								JsonElement locateWithinElement = locateArray.get(k);
								if(!locateWithinElement.isJsonObject()) {
									String location = locateWithinElement.getAsString();
									System.out.print(location + " ");
								}
							}
						} else {
							String location = section.get("location").getAsString();
							System.out.print(location + " ");
						}
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
		dbConnection.close();	
		}
	}
	
	public static JsonObject readJsonFromURL(String stringURL) throws IOException {
		try {
			URL url = new URL(stringURL);
			URLConnection request = url.openConnection();
			request.connect();
			JsonParser jp = new JsonParser();
			JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent()));
			return root.getAsJsonObject();
		} catch(Exception e) {
		}
		return null;
		
	}
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		JsonObject rootObj = readJsonFromURL("https://web-app.usc.edu/web/soc/api/departments/20143");
		HashSet<String> codes = new HashSet<String>();
		codes = findDepartmentCodes(rootObj);
		Iterator<String> iterate = codes.iterator();
		int year = getYear();
		// 1 is sprint, 2 is summer, 3 is fall
		int semester = getSemester();
		
		findClasses(codes, iterate, year, semester);

	}
	


}
