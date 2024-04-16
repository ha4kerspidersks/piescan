package com.abi.saviyntusercreation.com.api.leaverjml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.json.JSONObject;
import org.springframework.web.multipart.MultipartFile;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class BulkUploadUser {
    public static Boolean bulkUploadUser(
    String accessToken,
    MultipartFile csvFilePath,
    Properties properties
  ) throws FileNotFoundException, IOException {
    Boolean continueFetching = true;
    System.err.println("User Upload Started");
    File file = File.createTempFile(csvFilePath.getOriginalFilename(), ".csv");

    try (FileOutputStream fos = new FileOutputStream(file)) {
      fos.write(csvFilePath.getBytes());
    }
    JSONObject jsonObject = new JSONObject();
    jsonObject.put("file", file);
    System.out.println(file);
    System.out.println(csvFilePath);

    do {
      final Response response = RestAssured
        .given()
        .contentType(ContentType.MULTIPART)
        .header("Authorization", "Bearer " + accessToken)
        // .multiPart("delimiter", "comma")
        .multiPart("file", file)
        .multiPart("checkrules", "YES")
        .post(properties.getProperty("Bulk_Upload_User_Url"));

      System.out.println(response.getStatusCode());
      try {
        if (response.getStatusCode() == 200) {
          String errorcode = response.jsonPath().getString("errorcode");
          String msg = response.jsonPath().getString("msg");
          if (errorcode.equals("0")) {}
          continueFetching = false;
          System.err.println("User Upload Completed");
        }
      } catch (Exception e) {
        e.printStackTrace();
        return false;
      }
    } while (continueFetching);

    return true;
  }
}
