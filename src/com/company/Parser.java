package com.company;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {
    private URL url;
    private Map<String, Map<String, String>> result;
    private String resultJsonString;
    private JsonObject resultJson;

    public Parser(URL url){
        this.url = url;
        resultJson = new JsonObject();
        resultJsonString = null;
    }

    public void setUrl(URL url){
        this.url = url;
    }

    public String getResultJsonString(){
        return resultJsonString;
    }

    public Map<String, Map<String, String>> getResult(){
        if(result.size() == 0)
            return null;
        else
            return result;
    }

    public void setResultJson(){
        Pattern regexCountryNumber = Pattern.compile("[0-9]{1,4}$");
        JsonParser jsonParser = new JsonParser();

        JsonObject parsedJSON = jsonParser.parse(parseURL()).getAsJsonObject();
        JsonObject countries = parsedJSON.get("text").getAsJsonObject();
        JsonObject prices = parsedJSON.get("list").getAsJsonObject();

        for (String s : countries.keySet()){
            Matcher matcher = regexCountryNumber.matcher(s);
            if(matcher.find()) {
                resultJson.add(countries.get(s).toString(), prices.get(s.substring(matcher.start(), matcher.end())));
            }
        }
    }

    public void writeResultJsonToFile(){
        StringBuilder stringBuilder = new StringBuilder();

        for (String s: resultJson.keySet()) {
            stringBuilder.append("\"" + s + "\" : {\n");
            if(!resultJson.get(s).isJsonNull()) {
                for (String ss : resultJson.get(s).getAsJsonObject().keySet()) {
                    stringBuilder.append("\t\"" + ss + "\" : " + resultJson.get(s).getAsJsonObject().get(ss) + "\n");
                }
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
    }

    public void initResultJsonString(){
        resultJsonString = resultJson.toString();
    }

    public void initResult(){
        Gson gson = new Gson();
        result = (Map<String, Map<String, String>>) gson.fromJson(resultJson, Map.class);
    }

    private String parseURL(){
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
