import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
	
	public static Connection dbConnection;
	
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
	
	
	// Code from https://www.tutorialspoint.com/sqlite/sqlite_java.htm
	public static Connection SQL_init() {
		Statement stmt = null;
		try {
			Class.forName("org.sqlite.JDBC");
			dbConnection = DriverManager.getConnection("jdbc:sqlite:test.db");
			dbConnection.setAutoCommit(false);
			System.out.println("Opened database successfully");

			stmt = dbConnection.createStatement();
			String classSQL = "CREATE TABLE IF NOT EXISTS CLASS( " +
					"COURSEID	TEXT 	NOT NULL,"  +
					"TITLE		TEXT	NOT NULL," +
					"SESSION	TEXT	NOT NULL," +
					"DAY		CHAR	NOT NULL," +
					"START_TIME	TIME	NOT NULL," +
					"END_TIME	TIME	NOT NULL," +
					"LOCATION	TEXT	NOT NULL," +
					"INSTRUCTOR	TEXT,"
					+ "PRIMARY KEY(COURSEID, SESSION, DAY)"
					+ ");";
			
			stmt.execute(classSQL);
			
			String coordinatesSQL = "CREATE TABLE IF NOT EXISTS COORDINATES(" +
						"BUILDING	TEXT	NOT NULL," +
						"LATTITUDE	REAL," +
						"LONGITUDE	REAL," +
						"PRIMARY KEY(BUILDING)"
						+ ");";
			stmt.execute(coordinatesSQL);
			stmt.close();
			return dbConnection;	

		} catch (Exception e) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			System.exit(0);
		}
		return null;
	}
	
//	public static void populateBuilding() throws SQLException {
//		Statement stmt = null;
//		String selectSQL = "SELECT DISTINCT LOCATION FROM CLASS";
//		String insertSQL = null;
//		
//		stmt = dbConnection.createStatement();
//		
//		ResultSet rs = stmt.executeQuery(selectSQL);
//		while(rs.next()) {
//			String currentBuilding = rs.getString("LOCATION");
//			System.out.println(currentBuilding);
//			insertSQL = "INSERT OR REPLACE INTO COORDINATES(BUILDING) " +
//						"VALUES('" + currentBuilding + "');";
//			stmt.executeUpdate(insertSQL);
//		}
//		stmt.close();
//		
//	}
	
	public static void SQL_insert(Connection c, String courseID, String title, String session, String day,
								String start_time, String end_time, String location, String instructor) {
		Statement stmt = null;
		
		try {
			stmt = c.createStatement();
			String sql = "INSERT OR REPLACE INTO CLASS (COURSEID,TITLE,SESSION,DAY,START_TIME,END_TIME,LOCATION,INSTRUCTOR)" +
					"VALUES(" +
						"'" + courseID + "'," +
						"'" + title + "'," +
						"'" + session + "'," +
						"'" + day + "'," +
						"'" + start_time + "'," +
						"'" + end_time + "'," +
						"'" + location + "'," +
						"'" + instructor.replaceAll("'", "''") + "');";
			stmt.executeUpdate(sql);
			stmt.close();
			c.commit();
		} catch (Exception e) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			System.exit(0);
		}
	}
	
	public static String getBuilding(String classroom) {
		return classroom.replaceAll("\\d", "");
	}

	public static void findClasses(HashSet<String> codes, int year, int semester) throws IOException, SQLException {
		
		Iterator<String> iterate = codes.iterator();
		
		while(iterate.hasNext()) {
			
			String title = "";
			String courseID = "";
			String session = "";
			String day = "";
			String start_time = "";
			String end_time = "";
			String location = "";
			String instructor = "";
			
			
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
					courseID = courseObj.get("PublishedCourseID").getAsString();
				
					//Retrieve course title
					JsonObject courseData = courseObj.get("CourseData").getAsJsonObject();
					title = courseData.get("title").getAsString();
				
					//Retrieve section data
					JsonElement sectElement = courseData.get("SectionData");
				
					//Check if multiple sections
					if(sectElement.isJsonArray()) {
						JsonArray sections = courseData.get("SectionData").getAsJsonArray();
					
						//Loop through each section
						for(int j = 0; j < sections.size(); j++) {
								
							//Retrieve section number
							JsonObject section = sections.get(j).getAsJsonObject();
							session = section.get("session").getAsString();
		
							//Retrieve instructor
							JsonElement instructElement = section.get("instructor");
						
							//Check if instructor exists
							if(instructElement != null) {
								if(instructElement.isJsonObject()) {
								
									//Receive data for single instructor
									JsonObject instructObj = instructElement.getAsJsonObject();
									instructor = instructObj.get("first_name").getAsString() + " " + instructObj.get("last_name").getAsString();
								
								} else {
								
									//Receive data for multiple instructors
									JsonArray instructArray = instructElement.getAsJsonArray();
									JsonObject instructObj = instructArray.get(0).getAsJsonObject();
									instructor = instructObj.get("first_name").getAsString() + " " + instructObj.get("last_name").getAsString();
								}
							}
							
							//Retrieve day
							JsonElement dayElement = section.get("day");
							
							//Retrieve start_time
							JsonElement startTimeElement = section.get("start_time");
							
							//Retrieve end_time
							JsonElement endTimeElement = section.get("end_time");
							
							//Retrieve location
							JsonElement locationElement = section.get("location");
							
							//Check if each is not null
							if(dayElement != null && startTimeElement != null && endTimeElement != null && locationElement != null) {
								
								//check if dayElement is an object
								if (dayElement.isJsonObject()){
									JsonObject dayObject = dayElement.getAsJsonObject();
									JsonObject startTimeObject = null;
									JsonObject endTimeObject = null;
									JsonObject locationObject = null;
									
									//check if start_time & end_time are objects
									if(startTimeElement.isJsonObject()) {
										startTimeObject = startTimeElement.getAsJsonObject();
										endTimeObject = endTimeElement.getAsJsonObject();
									} else {
										start_time = startTimeElement.getAsString();
										
										end_time = endTimeElement.getAsString();
									}
									
									//check locationElement
									if(locationElement.isJsonObject()) {
										locationObject = locationElement.getAsJsonObject();
									} else {
										location = locationElement.getAsString();
									}
									
									//Create strings of each
									if(startTimeObject != null) {
										JsonElement startTimeWithinObject = startTimeObject.get("start_time");
										start_time = startTimeWithinObject.getAsString();
									}
									
									if(endTimeObject != null) {
										JsonElement endTimeWithinObject = endTimeObject.get("end_time");
										end_time = endTimeWithinObject.getAsString();
									}
									
									if(locationObject != null) {
										JsonElement locationWithinObject = locationObject.get("location");
										location = locationWithinObject.getAsString();
										location = getBuilding(location);
									}
									
									if(dayObject != null) {
										JsonElement dayWithinElement = dayObject.get("day");
										if(dayWithinElement != null) {
											day = dayWithinElement.getAsString();
											char[] multipleDays = day.toCharArray();
											for(int c = 0; c < multipleDays.length; c++) {
												day = Character.toString(multipleDays[c]);
												System.out.println(courseID +" "+ title +" "+ session +" "+ instructor + " "+ day + " " +start_time + "-" + end_time + " " + location);
												SQL_insert(dbConnection, courseID, title, session, day, start_time, end_time, location, instructor);
											}
										}
									}
									
								//Check if each is an array, (if one is an array, the others are too)
								} else if (dayElement.isJsonArray()){
									JsonArray dayArray = dayElement.getAsJsonArray();
									JsonArray startTimeArray = startTimeElement.getAsJsonArray();
									JsonArray endTimeArray = endTimeElement.getAsJsonArray();
									JsonArray locationArray = locationElement.getAsJsonArray();
									
									
									//iterate through each arrays
									for(int k = 0; k < dayArray.size(); k++) {
										JsonElement dayWithinElement = dayArray.get(k);
										JsonElement startTimeWithinElement = startTimeArray.get(k);
										JsonElement endTimeWithinElement = endTimeArray.get(k);
										JsonElement locationWithinElement = locationArray.get(k);
										
										
										if(!dayWithinElement.isJsonObject()) {
											day = dayArray.get(k).getAsString();
										}
										
										if(!startTimeWithinElement.isJsonObject()) {
											start_time = startTimeArray.get(k).getAsString();
										}
										
										if(!endTimeWithinElement.isJsonObject()) {
											end_time = endTimeArray.get(k).getAsString();
										}
											
										if(!locationWithinElement.isJsonObject()) {
											location = locationArray.get(k).getAsString();
											location = getBuilding(location);
										}
										
										char[] multipleDays = day.toCharArray();
										for(int c = 0; c < multipleDays.length; c++) {
											day = Character.toString(multipleDays[c]);
											System.out.println(courseID +" "+ title +" "+ session +" "+ instructor + " "+ day + " " +start_time + "-" + end_time + " " + location);
											SQL_insert(dbConnection, courseID, title, session, day, start_time, end_time, location, instructor);	
										}									
									}
									
								//Check if each one is just a string
								} else {
									day = dayElement.getAsString();
									
									start_time = startTimeElement.getAsString();
									
									end_time = endTimeElement.getAsString();
									
									if(!locationElement.isJsonObject()) {
										location = locationElement.getAsString();
										location = getBuilding(location);
									}
									char[] multipleDays = day.toCharArray();
									for(int c = 0; c < multipleDays.length; c++) {
										day = Character.toString(multipleDays[c]);
										System.out.println(courseID +" "+ title +" "+ session +" "+ instructor + " "+ day + " " +start_time + "-" + end_time + " " + location);
										SQL_insert(dbConnection, courseID, title, session, day, start_time, end_time, location, instructor);
									}		
								}
							}
						}
					} else {
					
						//Retrieve section number
						JsonObject section = sectElement.getAsJsonObject();
						session = section.get("session").getAsString();
	
						//Retrieve instructor
						JsonElement instructElement = section.get("instructor");
					
						//Check if instructor exists
						if(instructElement != null) {
							if(instructElement.isJsonObject()) {
							
								//Receive data for single instructor
								JsonObject instructObj = instructElement.getAsJsonObject();
								instructor = instructObj.get("first_name").getAsString() + " " + instructObj.get("last_name").getAsString();
							
							} else {
							
								//Receive data for multiple instructors
								JsonArray instructArray = instructElement.getAsJsonArray();
								JsonObject instructObj = instructArray.get(0).getAsJsonObject();
								instructor  = instructObj.get("first_name").getAsString() + " " + instructObj.get("last_name").getAsString();
							}
						}
						
						//Retrieve day
						JsonElement dayElement = section.get("day");
						
						//Retrieve start_time
						JsonElement startTimeElement = section.get("start_time");
						
						//Retrieve end_time
						JsonElement endTimeElement = section.get("end_time");
						
						//Retrieve location
						JsonElement locationElement = section.get("location");
						
						//Check if each is not null
						if(dayElement != null && startTimeElement != null && endTimeElement != null && locationElement != null) {
							
							//check if dayElement is an object
							if (dayElement.isJsonObject()){
								JsonObject dayObject = dayElement.getAsJsonObject();
								JsonObject startTimeObject = null;
								JsonObject endTimeObject = null;
								JsonObject locationObject = null;
								
								//check if start_time & end_time are objects
								if(startTimeElement.isJsonObject()) {
									startTimeObject = startTimeElement.getAsJsonObject();
									endTimeObject = endTimeElement.getAsJsonObject();
								} else {
									start_time = startTimeElement.getAsString();
									
									end_time = endTimeElement.getAsString();
								}
								
								//check locationElement
								if(locationElement.isJsonObject()) {
									locationObject = locationElement.getAsJsonObject();
								} else {
									location = locationElement.getAsString();

								}
								location = getBuilding(location);
								
								//Create strings of each
								if(startTimeObject != null) {
									JsonElement startTimeWithinObject = startTimeObject.get("start_time");
									start_time = startTimeWithinObject.getAsString();

								}
								
								if(endTimeObject != null) {
									JsonElement endTimeWithinObject = endTimeObject.get("end_time");
									end_time = endTimeWithinObject.getAsString();

								}
								
								if(locationObject != null) {
									JsonElement locationWithinObject = locationObject.get("location");
									location = locationWithinObject.getAsString();
									location = getBuilding(location);

								}
								
								if(dayObject != null) {
									JsonElement dayWithinElement = dayObject.get("day");
									if(dayWithinElement != null) {
										day = dayWithinElement.getAsString();
										char[] multipleDays = day.toCharArray();
										for(int c = 0; c < multipleDays.length; c++) {
											day = Character.toString(multipleDays[c]);
											System.out.println(courseID +" "+ title +" "+ session +" "+ instructor + " "+ day + " " +start_time + "-" + end_time + " " + location);
											SQL_insert(dbConnection, courseID, title, session, day, start_time, end_time, location, instructor);
										}
									}
								}
								
							//Check if each is an array, (if one is an array, the others are too)
							} else if (dayElement.isJsonArray()){
								JsonArray dayArray = dayElement.getAsJsonArray();
								JsonArray startTimeArray = startTimeElement.getAsJsonArray();
								JsonArray endTimeArray = endTimeElement.getAsJsonArray();
								JsonArray locationArray = locationElement.getAsJsonArray();
								
								
								//iterate through each arrays
								for(int k = 0; k < dayArray.size(); k++) {
									JsonElement dayWithinElement = dayArray.get(k);
									JsonElement startTimeWithinElement = startTimeArray.get(k);
									JsonElement endTimeWithinElement = endTimeArray.get(k);
									JsonElement locationWithinElement = locationArray.get(k);
									
									
									if(!dayWithinElement.isJsonObject()) {
										day = dayArray.get(k).getAsString();
									}
									
									if(!startTimeWithinElement.isJsonObject()) {
										start_time = startTimeArray.get(k).getAsString();
									}
									
									if(!endTimeWithinElement.isJsonObject()) {
										end_time = endTimeArray.get(k).getAsString();
									}
										
									if(!locationWithinElement.isJsonObject()) {
										location = locationArray.get(k).getAsString();
										location = getBuilding(location);
									}
									char[] multipleDays = day.toCharArray();
									for(int c = 0; c < multipleDays.length; c++) {
										day = Character.toString(multipleDays[c]);
										System.out.println(courseID +" "+ title +" "+ session +" "+ instructor + " "+ day + " " +start_time + "-" + end_time + " " + location);
										SQL_insert(dbConnection, courseID, title, session, day, start_time, end_time, location, instructor);
									}		
								}
								
							//Check if each one is just a string
							} else {
								day = dayElement.getAsString();
								
								start_time = startTimeElement.getAsString();
								
								end_time = endTimeElement.getAsString();
								
								if(!locationElement.isJsonObject()) {
									location = locationElement.getAsString();
									location = getBuilding(location);
								}
								char[] multipleDays = day.toCharArray();
								for(int c = 0; c < multipleDays.length; c++) {
									day = Character.toString(multipleDays[c]);
									System.out.println(courseID +" "+ title +" "+ session +" "+ instructor + " "+ day + " " +start_time + "-" + end_time + " " + location);
									SQL_insert(dbConnection, courseID, title, session, day, start_time, end_time, location, instructor);
								}		
							}
						}
				
					}
				}
			}
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
	
	public static void main(String[] args) throws IOException, SQLException {
		// TODO Auto-generated method stub
		int year = getYear();
		
		// 1 is spring, 2 is summer, 3 is fall
		int semester = getSemester();
		
		JsonObject rootObj = readJsonFromURL("https://web-app.usc.edu/web/soc/api/departments/" + year + semester);
		HashSet<String> codes = new HashSet<String>();
		codes = findDepartmentCodes(rootObj);

		SQL_init();
		findClasses(codes, year, semester);
//		populateBuilding();
		dbConnection.close();
	}
	


}
