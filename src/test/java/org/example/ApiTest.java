package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.RestAssuredConfig;
import io.restassured.config.SSLConfig;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.Is.is;

/**
 * API tests
 */
public class ApiTest
{
    public static RequestSpecification requestSpec = new RequestSpecBuilder().setBaseUri("https://reqres.in")
            .setBasePath("/api").build().config(new RestAssuredConfig()
                    .sslConfig(new SSLConfig().relaxedHTTPSValidation()));
    class User {
        public String name;
        public String job;

        public User(String name, String job) {
            this.name = name;
            this.job = job;
        }
    }
    @JsonIgnoreProperties (ignoreUnknown = true)
    public class UserResponsePOJO {

        public Data data;
        public Support support;
    }
    public class Data {

        public Long id;
        public String email;
        public String first_name;
        public String last_name;
        public String avatar;
    }

    public class Support {
        public String url;
        public String text;
    }

   @Test(description = "Test Get Users")
    public void testGetUsers() {

      given()
              .log().all()
              .spec(requestSpec)
              .contentType(ContentType.JSON)
      .when()
              .get("users?page=2")
      .then()
              .log().all()
              .statusCode(200)
              .body("total", equalTo (12));

   }

    @Test(description = "Test Single User")
    public void testSingleUser() {

        Response res = given()
                .log().all()
                .spec(requestSpec)
                .contentType(ContentType.JSON)
        .when()
                .get("users/2")
        .then()
                .log().all()
                .statusCode(200)
                .body("data.first_name", equalTo("Janet"))
                .extract().response();
        /* JsonObject jo = new Gson().fromJson(res.asPrettyString(), JsonObject.class);
        assertThat("First name is not Janet", jo.get("data").getAsJsonObject().get("first_name").getAsString(), is("Janet"));
        assertThat("Last name is not Janet", jo.get("data").getAsJsonObject().get("last_name").getAsString(), is("Weaver"));
        assertThat("Last name is not Janet", jo.get("data").getAsJsonObject().get("email").getAsString(), is("janet.weaver@reqres.in"));*/
        UserResponsePOJO serializedResponse = res.as(UserResponsePOJO.class);
        Data data = serializedResponse.data;
        assertThat("First name is not Janet", data.first_name, is("Janet"));


    }

    @Test(description = "Test Single User Not Found")
    public void testSingleUserNotFound() {

        given()
                .log().all()
                .spec(requestSpec)
                .contentType(ContentType.JSON)
        .when()
                .get("users/23")
        .then()
                .log().all()
                .statusCode(404)
                .extract().response();

    }
    @Test(description = "Test Get Resources")
    public void testGetResources() {

        Response res = given()
                .log().all()
                .spec(requestSpec)
                .contentType(ContentType.JSON)
        .when()
                .get("unknown")
        .then()
                .log().all()
                .statusCode(200)
                //.body("total", equalTo(12))
                .extract().response();
        Map<String, Object> map = res.as(Map.class);
        assertThat("Total is 12", map.get("total").equals(12));
        assertThat("There are 6 resources in response", ((List)map.get("data")).size(), is(6));

    }

    @Test(description = "Test Single Resource")
    public void testSingleResource() {

        given()
                .log().all()
                .spec(requestSpec)
                .contentType(ContentType.JSON)
        .when()
                .get("unknown/2")
        .then()
                .log().all()
                .statusCode(200)
                .body("data.name", equalTo("fuchsia rose"));

    }

    @Test(description = "Test Single Resource Not Found")
    public void testSingleResourceNotFound() {

        given()
                .log().all()
                .spec(requestSpec)
                .contentType(ContentType.JSON)
        .when()
                .get("unknown/23")
        .then()
                .log().all()
                .statusCode(404)
                .body(is("{}"));

    }

    
   @Test(description = "Test Post User")
    public void testPostUser() {

       User user = new User("Ivan", "programmer");

       given()
               .log().all()
               .spec(requestSpec)
               .contentType(ContentType.JSON)
               .body(user)
               .post("users")
       .then()
               .log().all()
               .statusCode(201)
               .body("id", notNullValue())                            ;

   }

    @Test(description = "Test Update User")
    public void testUpdateUser() {

        User user = new User("morpheus", "zion resident");

        given()
                .log().all()
                .spec(requestSpec)
                .contentType(ContentType.JSON)
                .body(user)
                .patch("users/2")
        .then()
                .log().all()
                .statusCode(200)
                .body("name", equalTo("morpheus"));

    }

    @Test(description = "Test Delete User")
    public void testDeleteUser() {

        given()
                .log().all()
                .spec(requestSpec)
                .delete("users/2")
        .then()
                .log().all()
                .statusCode(204);

    }

    @Test(description = "Test Register User")
    public void testRegisterUser() {

        Map<String, String> requestBody = new HashMap<>() {{
            put ("email", "eve.holt@reqres.in");
            put ("password", "pistol");
        }};

        given()
                .log().all()
                .spec(requestSpec)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .post("register")
        .then()
                .log().all()
                .statusCode(200)
                .body("id", notNullValue());

    }

    @Test(description = "Test Register User without password")
    public void testRegisterUserError() {
        String requestBody = """
        {
            "email": "eve.holt@reqres.in"            
        }""";

        given()
                .log().all()
                .spec(requestSpec)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .post("register")
        .then()
                .log().all()
                .statusCode(400)
                .body("error", is("Missing password"));
    }

    @Test(description = "Test Login User")
    public void testLoginUser() {

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("email", "eve.holt@reqres.in");
        jsonObject.addProperty("password", "cityslicka");

        given()
                .log().all()
                .spec(requestSpec)
                .contentType(ContentType.JSON)
                .body(jsonObject.toString())
                .post("login")
        .then()
                .log().all()
                .statusCode(200)
                .body("token", notNullValue());

    }

    @Test(description = "Test Login User without password")
    public void testLoginUserError() {

        String requestBody = """
        {
            "email": "peter@klaven"            
        }""";

        given()
                .log().all()
                .spec(requestSpec)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .post("register")
        .then()
                .log().all()
                .statusCode(400)
                .body("error", is("Missing password"));
    }
    @Test(description = "Test Delayed Response")
    public void testDelayedResponse() {

        Response res = given()
                .log().all()
                .spec(requestSpec)
                .contentType(ContentType.JSON)
        .when()
                .get("users?delay=2")
        .then()
                .log().all()
                .statusCode(200)
                .extract().response();
        int total = res.jsonPath().getInt("total");
        Assert.assertEquals(total, 12, "Total is not 12");

    }
}
