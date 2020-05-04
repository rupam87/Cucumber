package Api.Helpers;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.json.JSONArray;
import org.json.JSONObject;

import Utils.DIContext;

public class JsonHelper {
	JSONArray universalJArray;
	JSONObject universalJObject;
	List<JSONObject> universalList;
	boolean isFilter = false;
	DIContext scenarioContext;

	public JsonHelper(DIContext ctx) {
		scenarioContext = ctx;
	}

	/*
	 * Parse a JSON object using org.JSON as per the path provided.
	 */
	public String ParseJsonFromResponseToGetValue(String path, JSONObject jObj) {
		String actual_val = "Not Found";
		if (jObj == null)
			universalJObject = new JSONObject(scenarioContext.GetResponse().body().asString());
		else
			universalJObject = jObj;

		String[] paths = path.split("\\.+");
		for (int i = 0; i < paths.length; i++) {
			// The Last item is a property not a node
			if (i < paths.length - 1) {
				Matcher match = Pattern.compile("(\\[[0-9]+\\])").matcher(paths[i]);
				if (match.find()) {// if we have an index then treat as
									// JSONArray
					String matchedGrp = match.group(0);
					String index = matchedGrp.replace("[", "").replace("]", "");
					String field = paths[i].split("(\\[[0-9]+\\])")[0];
					// This is an Array
					universalJArray = universalJObject.getJSONArray(field);
					universalJObject = universalJArray.getJSONObject(Integer.parseInt(index));
				} else {
					universalJObject = universalJObject.getJSONObject(paths[i]);
				}
			} else {
				actual_val = universalJObject.getString(paths[i]);
			}
		}
		return actual_val;
	}

	/*
	 * Parse the JSON response to filter a value and reach a JSON Node.
	 * Thereafter use the pathToStore on the Node to extract specific value.
	 */
	public String ParseJsonFromResponseToFilterValue(String path, Object filterVal, String pathToStore) {
		String actual_val = "Not Found";
		universalJObject = new JSONObject(scenarioContext.GetResponse().body().asString());
		String[] paths = path.split("\\.+");
		for (int i = 0; i < paths.length; i++) {
			// The Last item is a property not a node
			if (i < paths.length - 1) {
				Matcher match = Pattern.compile("(\\[[0-9]+\\])|(\\[\\?\\])").matcher(paths[i]);
				if (match.find()) {
					// if we have an index then treat as JSONArray
					String matchedGrp = match.group();
					String index = matchedGrp.replace("[", "").replace("]", "");
					String field = paths[i].split("(\\[[0-9]+\\])|(\\[\\?\\])")[0];
					// This is an Array
					if (index.contains("?")) {
						universalJArray = universalJObject.getJSONArray(field);
						isFilter = true;
					} else {
						universalJArray = universalJObject.getJSONArray(field);
						universalJObject = universalJArray.getJSONObject(Integer.parseInt(index));
					}
				} else {
					universalJObject = universalJObject.getJSONObject(paths[i]);
				}
			} else {
				// Do the Filter if its set in previous pass of the for loop
				if (isFilter) {
					assertThat(filterVal).isNotEqualTo(null);
					String filterField = paths[i];

					// Check the DataType of the value to be filtered on and act
					// accordingly
					if (filterVal.getClass().equals(String.class)) {
						universalList = StreamSupport.stream(universalJArray.spliterator(), false)
								.map(ja -> (JSONObject) ja).filter(f -> f.getString(filterField).equals(filterVal))
								.collect(Collectors.toList());
					}

					if (filterVal.getClass().equals(Integer.class)) {
						universalList = StreamSupport.stream(universalJArray.spliterator(), false)
								.map(ja -> (JSONObject) ja).filter(f -> f.getInt(filterField) == (Integer) filterVal)
								.collect(Collectors.toList());
					}
					// Filtered JSON Object on which we need to retrieve value
					universalJObject = universalList.get(0);

					// get the path to store, iterate over the path to reach
					// final node and set actual value
					actual_val = ParseJsonFromResponseToGetValue(pathToStore, universalJObject);
				} else
					actual_val = universalJObject.getString(paths[i]);
			}
		}
		return actual_val;
	}
}
