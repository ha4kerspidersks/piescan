package com.abi.saviyntusercreation.com.api.leaverjml;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONObject;

public class RunJobTrigger {
    private static Map<String, Object> runAllAnalyticsJobMap;
  private static String dateAndTime;

  public static String startrunjob(String accessToken, Properties properties) {
    RestAssured.baseURI = properties.getProperty("Base_Url");

    final JSONObject requestBody = new JSONObject();
    requestBody.put("triggername", "Test_Automation_wsretry");
    requestBody.put("jobname", "WSRETRYJOB");
    requestBody.put("jobgroup", "Utility");
    Boolean continueFetching = true;
    do {
      final Response response = RestAssured
        .given()
        .contentType(ContentType.JSON)
        .header("Authorization", "Bearer " + accessToken)
        .body(requestBody.toString())
        .post(properties.getProperty("Run_Job_Trigger_Url"))
        .then()
        .log()
        .all()
        .assertThat()
        .statusCode(200)
        .extract()
        .response();

      JsonPath jsonPath = response.jsonPath();
      String actualErrorCode = jsonPath.getString("errorCode");
      dateAndTime = jsonPath.getString("timestamp");
      if (response.getStatusCode() == 200) {
        if ("0".equals(actualErrorCode)) {
          System.out.println("Job Started Running");
          continueFetching = false;
        } else {
          System.out.println("Failed to start the Job");
          return "false";
        }
        try {
          Thread.sleep(10000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    } while (continueFetching);
    try {
      Thread.sleep(10000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    return "true";
  }

  public static String fetchJobMetadata(
    String accessToken,
    Properties properties
  ) {
    boolean continueFetching = true;

    do {
      RestAssured.baseURI = properties.getProperty("Base_Url");
      final JSONObject requestBody = new JSONObject();
      requestBody.put("triggername", "Test_Automation_wsretry");
      requestBody.put("jobname", "WSRETRYJOB");
      requestBody.put("jobgroup", "Utility");

      final Response response = RestAssured
        .given()
        .contentType(ContentType.JSON)
        .header("Authorization", "Bearer " + accessToken)
        .body(requestBody.toString())
        .post(properties.getProperty("Fetch_Job_MetaData_Url"))
        .then()
        .log()
        .all()
        .assertThat()
        .statusCode(200)
        .extract()
        .response();

      JsonPath jsonPath = response.jsonPath();
      dateAndTime = jsonPath.getString("timestamp");
      String errorCode = jsonPath.getString("errorCode");

      if ("0".equals(errorCode)) {
        String msg = jsonPath.getString("msg");

        if (msg.equals("Success")) {
          System.out.println("Job Metadata Fetched Successfully.");
          continueFetching = false;
        } else {
          System.out.println("Job is still running.....");

          // Sleep before the next iteration
          try {
            Thread.sleep(60000);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
        // Check if "RunAllAnalyticsJob" exists under the "result" object
        // Object runAllAnalyticsJob = jsonPath.get("result.RunAllAnalyticsJob");
        // if (runAllAnalyticsJob != null && runAllAnalyticsJob instanceof Map) {
        // Map<String, Object> runAllAnalyticsJobMap = (Map<String, Object>)
        // runAllAnalyticsJob;

        // if (runAllAnalyticsJobMap.size() > 1) {
        // System.out.println("Job Metadata Fetched Successfully.");
        // continueFetching = false;
        // Onboardingrunjob.runAllAnalyticsJobMap = runAllAnalyticsJobMap;

        // } else {
        // System.out.println("Job is still running.....");
        // try {
        // Thread.sleep(60000);
        // } catch (InterruptedException e) {
        // e.printStackTrace();
        // }
        // }
        // } else {
        // System.out.println("Job is still running.....");

        // // Sleep before the next iteration
        // try {
        // Thread.sleep(60000);
        // } catch (InterruptedException e) {
        // e.printStackTrace();
        // }
        // }
      } else {
        // Log failure and print an error message
        System.out.println("Failed to fetch Job Metadata.");

        // Sleep before the next iteration
        try {
          Thread.sleep(60000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        return "false";
      }
    } while (continueFetching);
    // If 'RunAllAnalyticsJob' is still missing or it has only 'Job_ID', call
    // fetchJobMetadata() again
    if (continueFetching) {
      fetchJobMetadata(accessToken, properties);
    }
    return "true";
  }

//   public static String summaryReport() {
//     StringBuilder summary = new StringBuilder();

//     Map<String, Object> analyticsJobMap = getRunAllAnalyticsJobMap();
//     if (analyticsJobMap != null) {
//       for (Map.Entry<String, Object> entry : analyticsJobMap.entrySet()) {
//         String key = entry.getKey();
//         Object value = entry.getValue();

//         // Check if the key starts with "AnalyticES"
//         if (key.startsWith("AnalyticES")) {
//           // Extract the suffix from the key
//           String suffix = key.substring("AnalyticES".length());

//           // Extract the status using the extractStatus method
//           String status = extractStatus(value.toString());

//           // Create a new key in the desired format (e.g., TEST_REPORT)
//           String newKey = suffix + ": " + status;

//           // Append the newKey to the summary
//           summary.append(newKey).append("<br>");
//         }
//       }
//     }

//     // Return the generated summary
//     return summary.toString();
//   }

  // private static String extractStatus(String value) {
  //   // Remove anything inside square brackets and the square brackets themselves
  //   value = value.replaceAll("\\[.*?\\]", "");

  //   String regex = ".*:(\\s*\\w+).*";
  //   Pattern pattern = Pattern.compile(regex);
  //   Matcher matcher = pattern.matcher(value);
  //   return matcher.matches() ? matcher.group(1) : value.trim();
  // }

//   public static Map<String, Object> getRunAllAnalyticsJobMap() {
//     return runAllAnalyticsJobMap;
//   }

  public static String checkJobStatus(
    String accessToken,
    Properties properties
  ) {
    String paramName1 = "jobname";
    String paramValue1 = "WSRETRYJOB";
    String paramName2 = "jobgroup";
    String paramValue2 = "Utility";
    String paramName3 = "triggername";
        String paramValue3 = "Test_Automation_wsretry";


    RestAssured.baseURI = properties.getProperty("Base_Url");
    Boolean continueFetching = true;
    String status;
    do {
      Response response3 = RestAssured
        .given()
        .log()
        .all()
        .header("Authorization", "Bearer " + accessToken)
        .multiPart(paramName1, paramValue1)
        .multiPart(paramName2, paramValue2)        
        .multiPart(paramName2, paramValue2)
        .when()
        .post(properties.getProperty("check_job_status"))
        .then()
        .log()
        .all()
        .assertThat()
        .statusCode(200)
        .extract()
        .response();

      JsonPath jsonPath = response3.jsonPath();
      String actual_Errorcode = jsonPath.getString("errorCode");
      if ("0".equals(actual_Errorcode)) {
        String msg = jsonPath.getString("msg");
        
                if (msg.equals("COMPLETED")) {
                  System.out.println("Check Job Status Successfully.");
                  continueFetching = false;
                  status = "true";
                } else {
                  System.out.println("Check Job is still running.....");
status = "false";
                }
      } else {
        System.out.println("Job Failed.\n\n");
        status = "false";
      }
    } while (continueFetching);
    return status;
  }
}
