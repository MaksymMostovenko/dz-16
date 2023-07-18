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
    private final int SUCCESS_CODE = 200;
    private static final String AUTH_ENDPOINT = "/auth/";
    private static final String BOOKING_ENDPOINT = "/booking/";
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
        apiResponce.then()
                .assertThat()
                .statusCode(200)
                .body("token", Matchers.notNullValue())
                .body("token.length()", Matchers.is(15))
                .body("token", Matchers.matchesRegex("^[a-z0-9]+$"));


        String token = apiResponce.getCookie("token");
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

        String bookingId = response.jsonPath().get("bookingid").toString();

        Response responce =  RestAssured.given()
                .baseUri(HTTPS_RESTFUL_BOOKER+bookingId)
                .accept("application/json")
                .get();

        responce.then()
                .assertThat()
                .statusCode(SUCCESS_CODE)
                .body("firstname", Matchers.equalTo("Ivan"))
                .body("lastname", Matchers.equalTo("Piddubnyy"))
                .body("totalprice", Matchers.equalTo(42))
                .body("depositpaid", Matchers.equalTo(true))
                .body("additionalneeds", Matchers.equalTo("None"));

        return this;
    }

    public void assertBookingAdd(){

    }

    public TestSteps getAllBookingIds(){
        Response responce =  RestAssured
                .given()
                .baseUri(HTTPS_RESTFUL_BOOKER)
                .get();

        responce.then()
                .assertThat()
                    .statusCode(SUCCESS_CODE);

        return this;
    }

    private String getFirstId(){
        Response apiResponce = RestAssured.get(BOOKING_ENDPOINT);
        apiResponce.then().statusCode(SUCCESS_CODE);
        return apiResponce.jsonPath().get("[0].bookingid").toString();
    }

    private String getToken(){
        Response apiResponce = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(authRequestBody)
                .post(AUTH_ENDPOINT);

        apiResponce.then().statusCode(SUCCESS_CODE);
        String token = apiResponce.getBody().asString();

        JSONObject str = new JSONObject(token);
        return str.getString("token");
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public TestSteps patchBookingPrice(){

        String requestBody="{\"totalprice\" : 45}";
        String token = this.getToken();
        String patchedId = this.getFirstId();

        Response responce = RestAssured
                        .given()
                            .baseUri(HTTPS_RESTFUL_BOOKER+patchedId)
                            .header("Accept","application/json")
                            .contentType(ContentType.JSON)
                            .cookie("token", token)
                            .body(requestBody)
                        .patch();
        responce.then()
                .assertThat()
                .statusCode(SUCCESS_CODE)
                .body("totalprice",Matchers.equalTo(45));

        return this;

    }

    public void putNewData(){
        String patchedId = this.getFirstId();
        String token = this.getToken();

        Response responce =  RestAssured.given()
                .baseUri(HTTPS_RESTFUL_BOOKER+patchedId)
                .accept("application/json")
                .get();

        String data =  responce.getBody().asString();
        JSONObject jData = new JSONObject(data);
        jData.put("firstname", "Maksym");
        jData.put("additionalneeds", "black-jack");
        String requestBody = jData.toString();


        Response newResponce = RestAssured
                .given()
                .baseUri(HTTPS_RESTFUL_BOOKER+patchedId)
                .header("Accept","application/json")
                .contentType(ContentType.JSON)
                .cookie("token", token)
                .body(requestBody)
                .put();

    }

    public void deleteBooking(){
        String deleteId = this.getFirstId();
        String token = this.getToken();
        final int SUCCESSFUL_DELETE = 201;
        final int URL_FORBIDEN = 404;

        RestAssured
                .given()
                .baseUri(HTTPS_RESTFUL_BOOKER+deleteId)
                .cookie("token", token)
                .when()
                .delete()
                .then()
                .assertThat()
                .statusCode(SUCCESSFUL_DELETE);

        RestAssured
                .given()
                .baseUri(HTTPS_RESTFUL_BOOKER+deleteId)
                .when()
                .get()
                .then()
                .statusCode(URL_FORBIDEN);
    }
}
