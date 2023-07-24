package api.scripts.tg_school;

import api.pojo_classes.tg_school.CreateStudent;
import com.github.javafaker.Faker;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.Builder;
import org.hamcrest.Matchers;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import utils.ConfigReader;
import utils.DBUtil;

import java.util.List;

import static org.hamcrest.Matchers.*;

public class APIProject2 {

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
    public void TGAPIProject(){



        response = RestAssured.given()
                .spec(baseSpec)
                .when().get("/students")
                .then().log().all()
                .assertThat().statusCode(200)
                .assertThat().time(Matchers.lessThan(5000L))
                .extract().response();

        List<String> firstName = response.jsonPath().getList("firstName");
        List<String> lastName = response.jsonPath().getList("lastName");

        Assert.assertTrue(firstName.size() >= 2);
        Assert.assertEquals(firstName.get(1), "John");
        Assert.assertEquals(lastName.get(1), "Doe");

        CreateStudent createStudent = CreateStudent.builder().firstName(faker.name().firstName()).lastName(faker.name().lastName()).email(faker.internet().emailAddress()).dob("2000-01-01").build();

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

        response = RestAssured.given()
                .spec(baseSpec)
                .body(createStudent)
                .when().put("/students/" + userID)
                .then().log().all()
                .assertThat().statusCode(200)
                .assertThat().time(Matchers.lessThan(5000L))
                .extract().response();


        createStudent = CreateStudent.builder().firstName(faker.name().firstName()).lastName(faker.name().lastName()).email("123abc@gmail.com").dob("1999-01-01").build();
        response = RestAssured.given()
                .spec(baseSpec)
                .body(createStudent)
                .when().patch("/students/" + userID)
                .then().log().all()
                .assertThat().statusCode(200)
                .assertThat().time(Matchers.lessThan(5000L))
                .extract().response();




    }
}
