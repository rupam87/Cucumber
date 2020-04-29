package Api;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import org.json.JSONArray;
import org.json.JSONObject;
import java.nio.file.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class AppTest
{
    
	//@Test
	public void ParseFlightStatus() throws IOException
	{
		String currentDirectory = System.getProperty("user.dir");
		System.out.println(currentDirectory);
		File file = new File(currentDirectory + "\\src\\test\\java\\com\\api\\UA\\Api\\Templates\\FlightStatus.json");
		String contents = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
		System.out.println("Contents is : " + contents);
		
		try (FileReader reader = new FileReader(file))
		{
			//Read JSON file
            JSONObject obj = new JSONObject(contents);
            JSONArray flightLegs =  (JSONArray) obj.get("flightLegs");
            System.out.println(flightLegs.toString());
            
            JSONArray operationalFlightSegments = flightLegs.getJSONObject(0).getJSONArray("operationalFlightSegments");            		 
            System.out.println(operationalFlightSegments.toString());
            
            JSONArray flightStatuses = operationalFlightSegments.getJSONObject(0).getJSONArray("flightStatuses");
            flightStatuses.forEach((o) -> System.out.println(o.toString()));
            
            // Use Stream to fetch the Status Type for the node whose code = ERL
            List<JSONObject> flightStatus = StreamSupport.stream(flightStatuses.spliterator(), false)
                    .map(val -> (JSONObject) val)
                    .filter(val -> val.getString("code").equals("ERL"))
                    .collect(Collectors.toList());
            System.out.println("statusType for code 'ERL' :" + flightStatus.get(0).getString("statusType"));
            
            // Get Airport details
            JSONObject departureAirport = operationalFlightSegments.getJSONObject(0).getJSONObject("departureAirport");
            System.out.println("Departure Airport Code: " + departureAirport.getString("iataCode"));
            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } 
		
	}
   
}
