package com.abi.saviyntusercreation.com.api.leaverjml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.json.JSONArray;
import org.json.JSONObject;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class FetchPendingTask {
    public static Map<String, List<String>> pendingTask(String accessToken, Properties properties, List<Map<String, String>> userData){
        Map<String, List<String>> userPendingTaskIds = new HashMap<>();
        for (Map<String, String> user : userData) {
            String userName = user.get("username");
            System.out.println(userName);
            JSONObject jsonObject=new JSONObject();
            jsonObject.put("username", userName);
            jsonObject.put("TASKSTATUS", "PENDING");
            System.out.println(jsonObject.toString());
            Boolean continueFetching = true;
            int x=0;
            do{
                Response response=RestAssured.given().
                header("Authorization", "Bearer "+accessToken).
                body(jsonObject.toString()).when().
                post(properties.getProperty("Fetch_Tasks_Url"));

                System.out.println(response.getStatusCode());
                
                if(response.getStatusCode()==200){
                    JSONObject jsonResponse = new JSONObject(response.asString());

                    String errorCode = jsonResponse.optString("errorCode");

                    String msg = jsonResponse.optString("msg");

                    String totaltasksString = jsonResponse.optString("totaltasks");
                    Integer totaltasks = totaltasksString != null && totaltasksString != ""
                            ? Integer.parseInt(totaltasksString)
                            : 0;
                    
                    if (errorCode.equals("0") && totaltasks > 0) {
                        continueFetching = false;

                        JSONArray tasksArray = jsonResponse.optJSONArray("tasks");

                        List<String> taskIds = new ArrayList<>(); 

                        for (int i = 0; i < tasksArray.length(); i++) {
                            JSONObject task = tasksArray.getJSONObject(i);

                            String taskId = task.optString("TASKID");

                            taskIds.add(taskId);
                        }
                        userPendingTaskIds.put(userName, taskIds);
                    } else {
                        // getPendingTaskTest.log(Status.FAIL, "Response Message: " + msg);
                        if(x == 0) {
                            // getPendingTaskTest.log(Status.INFO, "Please Wait Under Process !!" );
                        }
                    }    
                } else{

                } 
                x++;
            }while(continueFetching);
        }
        return userPendingTaskIds;

    }
}
