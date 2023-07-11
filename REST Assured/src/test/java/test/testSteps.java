package test;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.http.Cookie;
import io.restassured.http.Cookies;
import io.restassured.response.Response;
import org.testng.Assert;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import java.io.FileReader;
import java.lang.reflect.Type;

public class testSteps {
    private Response response;
    String token;
    private final int SUCCESS_CODE = 200;
    private final String baseUrl = "https://restful-booker.herokuapp.com";
    public testSteps(){
        RestAssured.baseURI = "https://restful-booker.herokuapp.com/";
        RestAssured.requestSpecification = new RequestSpecBuilder()
                .addHeader("Content-Type", "application/json")
                .build();
    }
    private final String requestBody = "{\"username\": \"admin\", \"password\": \"password123\"}";
    public testSteps authenticate(){
        response = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .post("/auth");
        return this;
    }

    public testSteps getAuthTocken(){
        token = response.jsonPath().getString("token");
        return this;
    }

    public testSteps getCookie(){
        Cookie tokenCookie = new Cookie.Builder("token", token)
                .setDomain(baseUrl)
                .setPath("/")
                .build();

        Cookies cookies = new Cookies(tokenCookie);
        RestAssured.filters((req, res, ctx) -> {
            req.cookies(cookies);
            return ctx.next(req, res);
        });
        return this;
    }

    public testSteps accessStatusCode(){
        int statusCode = response.getStatusCode();
        Assert.assertEquals(statusCode, SUCCESS_CODE);
        return this;
    }

    private final String  PLACE_BOOKING_DATA = "{\"firstname\": \"Ivan\", " +
                                                "\"lastname\": \"Piddubnyy\", " +
                                                "\"totalprice\": 42, " +
                                                "\"depositpaid\": false, " +
                                                "\"bookingdates\": " +
                                                    "{\"checkin\": \"2023-01-01\", " +
                                                    "\"checkout\": \"2023-08-01\"}, " +
                                                "\"additionalneeds\": \"None\"}";
    public testSteps sendBookingRequest() {
        response = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(PLACE_BOOKING_DATA)
                .post("/booking");

        int statusCode = response.getStatusCode();
        String responseBody = response.getBody().asString();

        System.out.println("Status Code: " + statusCode);
        System.out.println("Response Body: " + responseBody);
        return this;
    }
}
