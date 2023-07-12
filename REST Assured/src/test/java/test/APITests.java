package test;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;


public class APITests {

    public static void main(String[] args) {
        String baseUrl = "https://restful-booker.herokuapp.com";
        String endpoint = "/booking/{bookingId}";

        Response response = RestAssured.given()
                .pathParam("bookingId", 3191)
                .get(baseUrl + endpoint);

        int statusCode = response.getStatusCode();
        String responseBody = response.getBody().asString();

        System.out.println("Status Code: " + statusCode);
        System.out.println("Response Body: " + responseBody);
    }

    private TestSteps apiTestSteps;
    @BeforeMethod
    public void setup() {
        apiTestSteps = new TestSteps();
    }

    @Test
    public void authTest(){
        apiTestSteps.authenticate()
                .getAuthTocken()
                .getCookie()
                .assessStatusCode();
    }

    @Test
    public void addBookingTest(){
        apiTestSteps.sendBookingRequest()
                .getBookingDetails()
                .assessStatusCode()
                .assessBooking();
    }

    @Test
    public void editBookingTest(){
        apiTestSteps.editBooking(2286)
                .assessStatusCode()
                .assessEditBookingData();
    }
}
