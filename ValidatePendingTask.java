package com.abi.saviyntusercreation.com.api.leaverjml;

import java.util.List;
import java.util.Map;
import java.util.Properties;


import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class ValidatePendingTask {
    public static boolean validatePendingTasks( String accessToken,Properties properties, Map<String, List<String>> userPendingTaskIds) {
        
        boolean runTrigger = true;

      
        // final String validatePendingTasksEndpoint = properties.getProperty("Validate_Pending_Tasks_Url");
        // Loop over the keys and values
        for (Map.Entry<String, List<String>> entry : userPendingTaskIds.entrySet()) {
            String user_name = entry.getKey();
            List<String> taskIds = entry.getValue();

            System.out.println("User: " + user_name);
            System.out.println("Tasks: " + taskIds);


            // If you want to loop over the values in the list
            for (String task : taskIds) {
                
                Boolean continueFetching = true;
                int x=0;
                do{

                    Response response = RestAssured.given()
                    .contentType(ContentType.MULTIPART)
                    .header("Authorization", "Bearer " + accessToken)
                    .multiPart("taskid", task)
                    .post(properties.getProperty("Validate_Pending_Tasks_Url"));

                 
                    
                    if (response.getStatusCode() == 200) {

                        String status = response.jsonPath().getString("Status");
                        
                        String updateDate = response.jsonPath().getString("UpdateDate");

                        if (status.equals(null) || status.equals("")) {
                            runTrigger = false; 
                            continueFetching=false;
                        }
                    } else {
                        runTrigger = false; 
                        continueFetching=false;
                    }

                    x++;
                }while(continueFetching);
            }
        }
        return runTrigger;
    }
}
