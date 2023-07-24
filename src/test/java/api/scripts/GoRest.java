package api.scripts;

import api.pojo_classes.go_rest.UpdateGoRestUser;
import api.pojo_classes.go_rest.CreateGoRestUser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.hamcrest.Matchers;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import utils.ConfigReader;

public class GoRest {


    Response response;
    Faker faker = new Faker();

    String updatedUserName;
    String updatedUserEmail;
    String goRestStatus;
    String goRestGender;

    RequestSpecification baseSpec;








    /**
     * ObjectMapper is a class that is coming from faster.xml Jackson library
     * It is helping us to do Serialization. It converts Java objects to JSON object
     * So we can use it inside the Request body for POST or PUT
     */
    ObjectMapper objectMapper = new ObjectMapper();


    @BeforeMethod
    public void setAPI(){
        RestAssured.baseURI = ConfigReader.getProperty("GoRestBaseURI");

        baseSpec = new RequestSpecBuilder().log(LogDetail.ALL)
                .setBaseUri(ConfigReader.getProperty("GoRestBaseURI"))
                .setContentType(ContentType.JSON)
                .addHeader("Authorization", ConfigReader.getProperty("GoRestToken"))
                .build();






    }
    @Test
    public void GoRestCRUD() throws JsonProcessingException {

        CreateGoRestUser createGoRestUser = new CreateGoRestUser();

        createGoRestUser.setName("Tech Global");
        createGoRestUser.setGender("male");
        createGoRestUser.setEmail(faker.internet().emailAddress());
        createGoRestUser.setStatus("active");


        response = RestAssured.given().log().all()
                .spec(baseSpec)
                .body(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(createGoRestUser))
                .when().post( "/public/v2/users")
                .then().log().all()
                .assertThat().statusCode(201).time(Matchers.lessThan(4000L))
                .extract().response();


        String actualName = response.jsonPath().getString("name");
        String requestName = createGoRestUser.getName();
        int goRest_id = response.jsonPath().getInt("id");

        Assert.assertEquals(actualName, requestName);

        response = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", ConfigReader.getProperty("GoRestToken"))
                .when().get("/public/v2/users/" + goRest_id)
                .then().log().all().extract().response();

        UpdateGoRestUser updateGoRestUser =  new UpdateGoRestUser();

        updateGoRestUser.setName(faker.funnyName().name());
        updateGoRestUser.setEmail(faker.internet().emailAddress());

        String updatedGoRestUserJSON = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(updateGoRestUser);

        response = RestAssured.given().log().all()
                .spec(baseSpec)
                .body(updatedGoRestUserJSON)
                .when().put("/public/v2/users/" + goRest_id)
                .then().log().all().extract().response();


        // fetching the values
        updatedUserName = updateGoRestUser.getName();
        updatedUserEmail = updateGoRestUser.getEmail();
        goRestGender = createGoRestUser.getGender();
        goRestStatus = createGoRestUser.getStatus();


        //de-serialization
        String actualNameShown = response.jsonPath().getString("name");
        String actualEmail = response.jsonPath().getString("email");
        String actualGender = response.jsonPath().getString("gender");
        String actualStatus = response.jsonPath().getString("status");

        String[] requestValues = {updatedUserName, updatedUserEmail, goRestGender, goRestStatus};

        String[] actualValues = {actualNameShown, actualEmail, actualGender, actualStatus};

        for (int i = 0; i < requestValues.length; i++) {
            Assert.assertEquals(actualValues[i], requestValues[i]);
        }


    }
}