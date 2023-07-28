package api.scripts.tg_school;

import api.pojo_classes.tg_school.CreateStudent;
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
import utils.DBUtil;

public class APIProject3 {

    Response response;
    private RequestSpecification baseSpec;
    Faker faker = new Faker();


    @BeforeMethod
    public void setTest(){
        baseSpec = new RequestSpecBuilder().log(LogDetail.ALL)
                .setBaseUri(ConfigReader.getProperty("TGSchoolBaseURI"))
                .setContentType(ContentType.JSON)
                .build();

        DBUtil.createDBConnection();
    }

    @Test
    public void TGAPIProject3(){

        CreateStudent createStudent = CreateStudent.builder().firstName("Jehad").lastName(faker.name().lastName()).email(faker.internet().emailAddress()).dob("2000-01-01").build();

        response = RestAssured.given()
                .spec(baseSpec)
                .body(createStudent)
                .when().post("/students")
                .then().log().all()
                .assertThat().statusCode(200)
                .assertThat().time(Matchers.lessThan(5000L))
                .extract().response();

        int userID = response.jsonPath().getInt("id");


        response = RestAssured.given()
                .spec(baseSpec)
                .when().get("/students/" + userID)
                .then().log().all()
                .assertThat().statusCode(200)
                .assertThat().time(Matchers.lessThan(5000L))
                .extract().response();

        Assert.assertEquals(createStudent.getFirstName(), "Jehad");

        createStudent = CreateStudent.builder().firstName("Jehad").lastName("Mohammad").email(faker.internet().emailAddress()).dob("2000-01-01").build();


        response = RestAssured.given()
                .spec(baseSpec)
                .body(createStudent)
                .when().put("/students/" + userID)
                .then().log().all()
                .assertThat().statusCode(200)
                .assertThat().time(Matchers.lessThan(5000L))
                .extract().response();



        response = RestAssured.given()
                .spec(baseSpec)
                .when().get("/students/" + userID)
                .then().log().all()
                .assertThat().statusCode(200)
                .assertThat().time(Matchers.lessThan(5000L))
                .extract().response();

        Assert.assertEquals(createStudent.getFirstName(), "Jehad");
        Assert.assertEquals(createStudent.getLastName(), "Mohammad");
        Assert.assertEquals(createStudent.getDob(), "2000-01-01");


        response = RestAssured.given()
                .spec(baseSpec)
                .when().delete("/students/" + userID)
                .then().log().all()
                .assertThat().statusCode(200)
                .assertThat().time(Matchers.lessThan(5000L))
                .extract().response();



    }


}
