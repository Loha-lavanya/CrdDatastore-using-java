package com.datastore.crd.operations;
import java.io.*;
import java.lang.instrument.Instrumentation;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.web.bind.annotation.*;
import com.datastore.crd.model.Data;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
public class CrdOperations {


	//Check whether file already contains that object
	public boolean isExist(String key) throws ParseException, FileNotFoundException
	{
		Scanner scanner = new Scanner(new File("data.json"));
		while (scanner.hasNext())
		{
			JSONObject obj = (JSONObject) new JSONParser().parse(scanner.next());
			if(obj.get("key").equals(key))
				return true;
		}
		return false;
	}

	//To create file
	void createNewFile(String filename) {
		try {
			File myObj = new File(filename);
			if (myObj.createNewFile()) {
				System.out.println("File created: " + myObj.getName());
			} else {
				System.out.println("File already exists.");
			}
		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
	}

	// Creating Json object
	@RequestMapping(path="/createData",produces= {"application/json"})
	public String createJson(@RequestBody Data data) throws CustomException,JsonGenerationException, JsonMappingException, IOException {

		//check whether json object greater than 16Kb
		Instrumentation instrumentation;
		if(instrumentation.getObjectSize(data)>16*1000)
			throw new CustomException("JsonObject should not be greater than 16Kb");

		//check whether file size greater than 1Gb
		String filename="data.json";
		if(Files.size(filename)>1024*1024*1024)
			throw new CustomException("File should not be greater than 1GB");

		if(isExist(data.getKey()))
			throw new CustomException("Object already exists");

		// write data into file
		String json1 = new ObjectMapper().writeValueAsString(data);
		createNewFile(filename);
		fileWrite(json1, filename);

		return "object created";
	}
	//write data into file
	public void fileWrite(String data,String filename) {
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(filename, true)); 
			out.write(data);
			out.close(); 
		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
	}

	@GetMapping("readData/{key}")
	public  Data readData(@PathVariable(value="key") String key) throws JsonParseException, JsonMappingException, IOException, CustomException
	{ 
		if(key.length()>32)
			throw new CustomException("Key should not be exceed 32 characters");

		try (FileInputStream f = new FileInputStream("data.json")) 
		{
			JsonFactory jf = new JsonFactory();
			JsonParser jp = jf.createParser(f);
			jp.setCodec(new ObjectMapper());
			jp.nextToken();
			while (jp.hasCurrentToken()) {
				Data data = jp.readValueAs(Data.class);
				jp.nextToken();
				if(data.getKey().equals(key))
					return data;
			}
		}
		return null;
	}


	@DeleteMapping("deleteData/{key}")
	public void deleteData(@PathVariable(value="key") String key) 
	{
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode jsonNode = objectMapper.readTree(new File("data.json"));
		for (JsonNode node : jsonNode) {
			if(node.get("key").equals(key)) {
				((ObjectNode)node).remove("key");
				((ObjectNode)node).remove("value");
			}
		}
		objectMapper.writeValue(new File("data.json"), jsonNode);
	}
}	
