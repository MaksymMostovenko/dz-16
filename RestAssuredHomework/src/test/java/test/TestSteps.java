package test;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.json.JSONObject;


public class TestSteps {
    public static final String HTTPS_RESTFUL_BOOKER = "https://restful-booker.herokuapp.com/booking/";
    private String token;
    private String bookingId;
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
        Response apiResponce = RestAssured.given()
                .header("Accept", "application/json")
                .contentType(ContentType.JSON)
                .body(authRequestBody)
                .post(AUTH_ENDPOINT);

        token = apiResponce.getCookie("token");
        return this;
    }

    public TestSteps addNewBooking() {
        NewBooking booking = NewBooking.builder()
            .firstname("Ivan")
            .lastname("Piddubnyy")
            .totalprice(42)
            .depositpaid(true)
            .bookingdates(NewBooking
                .BookingDates.builder()
                    .checkin("2023-01-01")
                    .checkout("2023-08-01")
                    .build())
            .additionalneeds("None")
            .build();
        Response response = RestAssured.given()
                            .contentType(ContentType.JSON)
                            .body(booking)
                            .when()
                            .post(BOOKING_ENDPOINT);
        response.prettyPrint();

        this.bookingId = response.jsonPath().get("bookingid").toString();

        return this;
    }

    public TestSteps getAllBookingIds(){
        RestAssured
                .given()
                    .baseUri(HTTPS_RESTFUL_BOOKER)
                    .get()
                .then()
                    .statusCode(SUCCESS_CODE);

        return this;
    }

    public void getBooking(){
        Response responce =  RestAssured.given()
                .baseUri(HTTPS_RESTFUL_BOOKER+this.bookingId)
                .accept("application/json")
                .get();
        responce.prettyPrint();

        responce.then()
                .assertThat()
                    .statusCode(SUCCESS_CODE)
                    .body("firstname", Matchers.equalTo("Ivan"))
                    .body("lastname", Matchers.equalTo("Piddubnyy"))
                    .body("totalprice", Matchers.equalTo(42))
                    .body("depositpaid", Matchers.equalTo(true))
                    .body("additionalneeds", Matchers.equalTo("None"));


    }

    private void getAllIds(){
        Response apiResponce = RestAssured.get(BOOKING_ENDPOINT);
        apiResponce.then().statusCode(SUCCESS_CODE);
        this.bookingId = apiResponce.jsonPath().get("[0].bookingid").toString();
    }

    private void getToken(){
        Response apiResponce = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(authRequestBody)
                .post(AUTH_ENDPOINT);

        apiResponce.then().statusCode(SUCCESS_CODE);
        this.token = apiResponce.getBody().asString();

        JSONObject str = new JSONObject(this.token);
        this.token = str.getString("token");
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public TestSteps patchBookingPrice(){
        JSONObject requestBody = new JSONObject();
        requestBody.put("totalprice", "42");

        this.getToken();
        this.getAllIds();


        Response response = RestAssured
                        .given()
                            .baseUri(HTTPS_RESTFUL_BOOKER+this.bookingId)
                            .contentType("application/json")
                            .accept("application/json")
                            .cookie("token", this.token)
                            .contentType(ContentType.JSON)
                            .body(requestBody)
                        .patch();

        response.then()
                .assertThat()
                .statusCode(SUCCESS_CODE)
                .body("totalprice", Matchers.comparesEqualTo(42));

//        RestAssured
//                .given()
//                    .baseUri(HTTPS_RESTFUL_BOOKER+this.bookingId)
//                    .contentType("application/json")
//                    .accept("application/json")
//                    .cookie("token", this.token)
//                    .contentType(ContentType.JSON)
//                    .body(requestBody)
//                .when()
//                    .patch()
//                .then()
//                    .assertThat()
//                    .statusCode(SUCCESS_CODE)
//                    .body("totalprice", Matchers.comparesEqualTo(42));

        return this;

    }

}
