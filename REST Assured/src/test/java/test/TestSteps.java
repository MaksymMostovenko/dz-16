package test;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TestSteps {
    private static final Logger logger = LogManager.getLogger(TestSteps.class);
    private String token;
    private int bookingId;
    private final int SUCCESS_CODE = 200;
    private static final String AUTH_ENDPOINT = "/auth/";
    private static final String BOOKING_ENDPOINT = "/booking/";
    private static final String BOOKING_ENDPOINT_ID = "/booking/{bookingid}";
    private final String baseUrl = "https://restful-booker.herokuapp.com";
    private final String authRequestBody = "{\"username\": \"admin\", \"password\": \"password123\"}";

    public TestSteps() {
        RestAssured.baseURI = "https://restful-booker.herokuapp.com/";
        RestAssured.requestSpecification = new RequestSpecBuilder()
                .addHeader("Content-Type", "application/json")
                .build();
    }



    public TestSteps authenticate() {
        logger.info("Authenticating...");
        Response apiResponce = RestAssured.given()
                .header("Accept", "application/json")
                .contentType(ContentType.JSON)
                .body(authRequestBody)
                .post(AUTH_ENDPOINT);

        token = apiResponce.getCookie("token");
        return this;
    }

    public TestSteps addNewBooking() {
        logger.info("Sending booking request...");
        NewBooking booking = NewBooking.builder()
                .firstname("Ivan")
                .lastname("Piddubnyy")
                .totalprice(42)
                .depositpaid(true)
                .bookingdates(NewBooking
                        .BookingDates.builder().checkin("2023-01-01").checkout("2023-08-01").build())
                .additionalneeds("None")
                .build();

        Response apiResponce = RestAssured.given()
                .contentType(ContentType.JSON)
                .cookie("token", token)
                .body(booking)
                .post(BOOKING_ENDPOINT);

        apiResponce.then().statusCode(SUCCESS_CODE);
        return this;
    }

    public TestSteps getAllBookingIds(){
        logger.info("Get all booking Ids...");
        Response apiResponce = RestAssured.get(BOOKING_ENDPOINT);
        apiResponce.then().statusCode(SUCCESS_CODE);
        apiResponce.prettyPrint();

        return this;
    }

    private void getAllIds(){
        Response apiResponce = RestAssured.get(BOOKING_ENDPOINT);
        apiResponce.then().statusCode(SUCCESS_CODE);
        this.bookingId = apiResponce.jsonPath().get("[0].bookingid");
    }

    private void getToken(){
        Response apiResponce = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(authRequestBody)
                .post(AUTH_ENDPOINT);
        apiResponce.prettyPrint();
        this.token = apiResponce.getCookie("token");
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public TestSteps patchBookingPrice(){
        this.getToken();
        this.getAllIds();
        Response response = RestAssured.given()
                .contentType("application/json")
                .accept("application/json")
                .cookie("token", this.token)
                .body("{\n" + "\"totalprice\" : 100,\n" + "}")
                .patch(BOOKING_ENDPOINT_ID,this.bookingId);

        response.prettyPrint();
        response.then().statusCode(SUCCESS_CODE);

        System.out.println("debug");

        return this;
    }

}
