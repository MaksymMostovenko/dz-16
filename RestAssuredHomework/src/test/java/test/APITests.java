package test;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;


public class APITests {
    private TestSteps apiTestSteps;
    @BeforeTest
    public void setup() {
        apiTestSteps = new TestSteps();
        apiTestSteps.authenticate();
    }

    @Test
    public void addBookingTest(){
        apiTestSteps.addNewBooking();
    }

    @Test
    public void getAllBookingTest(){
        apiTestSteps.getAllBookingIds();
    }

    @Test
    public void partialUpdateBookingTest(){
        apiTestSteps.patchBookingPrice();
    }

    @Test
    public void changeNameNeedsTest(){
        apiTestSteps.putNewData();
    }

    @Test
    public void deleteBookingTest(){
        apiTestSteps.deleteBooking();
    }
}
