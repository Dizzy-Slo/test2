package com.company;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {
    private URL url;
    private Map<String, Map<String, String>> result;
    private String resultJSON;

    public Parser(URL url){
        this.url = url;
        result = new LinkedHashMap<>();
        resultJSON = null;
    }

    public void setUrl(URL url){
        this.url = url;
    }

    public String getResultJSON(){
        return resultJSON;
    }

    public Map<String, Map<String, String>> getResult(){
        if(result.size() == 0)
            return null;
        else
            return result;
    }

    public void ParseJSON(){
        StringBuilder stringBuilder = new StringBuilder();
        Pattern regexCountryNumber = Pattern.compile("[0-9]{1,4}$");

        JSONObject parsedJSON = (JSONObject) JSONValue.parse(ParseURL());
        Map<String, String> countries = (Map<String, String>) parsedJSON.get("text");
        Map<String, Map<String, String>> prices = (Map<String, Map<String, String>>) parsedJSON.get("list");

        for (String s : countries.keySet()){
            Matcher matcher = regexCountryNumber.matcher(s);
            if(matcher.find())
                result.put(countries.get(s), prices.get(s.substring(matcher.start(), matcher.end())));
        }

        for (String s: result.keySet()) {
            stringBuilder.append("\"" + s + "\" : {\n");
            if(result.get(s) != null)
                for (String ss : result.get(s).keySet()) {
                    stringBuilder.append("\t\"" + ss + "\" : " + result.get(s).get(ss) + "\n");
                }
            else {
                stringBuilder.append("Нет опций\n");
            }
            stringBuilder.append("},\n");
        }

        try(FileWriter fileWriter = new FileWriter("Result.txt")){
            fileWriter.write(stringBuilder.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        resultJSON = JSONValue.toJSONString(result);
    }

    private String ParseURL(){
        StringBuilder stringBuilder = new StringBuilder();
        try(BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()))){
            String inputLine;
            while((inputLine = in.readLine()) != null){
                stringBuilder.append(inputLine);
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }
}
