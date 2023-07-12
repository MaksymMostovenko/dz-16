package test;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.http.Cookie;
import io.restassured.http.Cookies;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.testng.Assert;

public class TestSteps {
    private Response response;
    private String token;
    private int bookingId;
    private final int SUCCESS_CODE = 200;
    private final String baseUrl = "https://restful-booker.herokuapp.com";
    public TestSteps(){
        RestAssured.baseURI = "https://restful-booker.herokuapp.com/";
        RestAssured.requestSpecification = new RequestSpecBuilder()
                .addHeader("Content-Type", "application/json")
                .build();
    }
    private final String authRequestBody = "{\"username\": \"admin\", \"password\": \"password123\"}";
    public TestSteps authenticate(){
        response = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(authRequestBody)
                .post("/auth");
        return this;
    }

    public TestSteps getAuthTocken(){
        token = response.jsonPath().getString("token");
        return this;
    }

    public TestSteps getCookie(){
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

    public TestSteps assessStatusCode(){
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
    public TestSteps sendBookingRequest() {
        response = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(PLACE_BOOKING_DATA)
                .post("/booking");

        int statusCode = response.getStatusCode();
        String responseBody = response.getBody().asString();
        JsonPath jsonPath = new JsonPath(responseBody);
        bookingId = jsonPath.getInt("bookingid");
        return this;
    }

    public TestSteps getBookingDetails() {
        response = RestAssured.given()
                .pathParam("bookingId", bookingId)
                .get("/booking/{bookingId}");
        return this;
    }

    public TestSteps assessBooking(){
        String responseBody = response.getBody().asString();
        JsonPath jsonPath = new JsonPath(responseBody);

        // Perform assertions on the booking details
        JsonPath expectedBooingData = new JsonPath(PLACE_BOOKING_DATA);
        String actualFirstName = jsonPath.getString("firstname");
        String expectedFirstName = expectedBooingData.getString("firstname");
        Assert.assertEquals(actualFirstName, expectedFirstName);

        String actualLastName = jsonPath.getString("lastname");
        String expectedLastName = expectedBooingData.getString("lastname");
        Assert.assertEquals(actualLastName, expectedLastName);

        String actualTotalPrice = jsonPath.getString("totalprice");
        String expectedTotalPrice = expectedBooingData.getString("totalprice");
        Assert.assertEquals(actualTotalPrice, expectedTotalPrice);

        String actualDeposite = jsonPath.getString("depositpaid");
        String expectedDeposite = expectedBooingData.getString("depositpaid");
        Assert.assertEquals(actualDeposite, expectedDeposite);

        String actualBookingDatesCheckIn = jsonPath.getString("bookingdates.checkin");
        String expectedBookingDatesCheckIn = expectedBooingData.getString("bookingdates.checkin");
        Assert.assertEquals(actualBookingDatesCheckIn, expectedBookingDatesCheckIn);

        String actualAdditionalNeeds = jsonPath.getString("additionalneeds");
        String expectedAdditionalNeeds = expectedBooingData.getString("additionalneeds");
        Assert.assertEquals(actualAdditionalNeeds, expectedAdditionalNeeds);

        return this;
    }


    private final String EDITED_BOOKING_DATA = "{\"firstname\": \"editedName\", " +
                                                "\"lastname\": \"editedLastName\", " +
                                                "\"additionalneeds\": \"Black Jack and Hookers\"}";
    public TestSteps editBooking(int editBookingId){
        response = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(EDITED_BOOKING_DATA)
                .pathParam("bookingId", editBookingId)
                .put("/booking/{bookingId}");

        return this;
    }

    public TestSteps assessEditBookingData() {
        String responseBody = response.getBody().asString();
        JsonPath jsonPath = new JsonPath(responseBody);
        JsonPath expectedBooingData = new JsonPath(EDITED_BOOKING_DATA);

        String actualFirstName = jsonPath.getString("firstname");
        String expectedFirstName = expectedBooingData.getString("firstname");
        Assert.assertEquals(actualFirstName, expectedFirstName);

        String actualLastName = jsonPath.getString("lastname");
        String expectedLastName = expectedBooingData.getString("lastname");
        Assert.assertEquals(actualLastName, expectedLastName);

        String actualAdditionalNeeds = jsonPath.getString("additionalneeds");
        String expectedAdditionalNeeds = expectedBooingData.getString("additionalneeds");
        Assert.assertEquals(actualAdditionalNeeds, expectedAdditionalNeeds);

        return this;
    }
}
