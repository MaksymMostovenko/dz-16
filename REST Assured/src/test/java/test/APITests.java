package test;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import test.testSteps;

import java.nio.file.Path;
import java.nio.file.Paths;



public class APITests {

    public static void main(String[] args) {
        Path currRelativePath = Paths.get("");
        String currAbsolutePathString = currRelativePath.toAbsolutePath().toString();
        System.out.println("Current absolute path is - " + currAbsolutePathString);
    }

    private testSteps apiTestSteps;
    @BeforeMethod
    public void setup() {
        apiTestSteps = new testSteps();
    }

    @Test
    public void authTest(){
        apiTestSteps.authenticate()
                .getAuthTocken()
                .getCookie()
                .accessStatusCode();
    }

    @Test
    public void addBookingTest(){
        apiTestSteps.sendBookingRequest()
                .accessStatusCode();
    }
}
