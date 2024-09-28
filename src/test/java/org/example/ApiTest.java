package org.example;

import io.restassured.RestAssured;

import io.restassured.config.SSLConfig;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.config;
import static io.restassured.RestAssured.given;
import static org.hamcrest.core.Is.is;

/**
 * API tests
 */
public class ApiTest

{
   @BeforeClass
    public void setBaseUri() {
       RestAssured.baseURI = "https://reqres.in/api";
   }

   @Test(description = "Test Get Users")
    public void testGetUsers() {
      RestAssured.config = config().sslConfig(new SSLConfig().relaxedHTTPSValidation());

      Response res = given()
                    .log().all()
                    .contentType(ContentType.JSON)
               .when()
                    .get("users?page=2")
               .then()
                    .log().all()
                    .statusCode(200)
                    .extract().response();
      int total = res.jsonPath().getInt("total");
      Assert.assertEquals(total, 12, "Total is not 12");

   }

    @Test(description = "Test Single User")
    public void testSingleUser() {
        RestAssured.config = config().sslConfig(new SSLConfig().relaxedHTTPSValidation());

        Response res = given()
                .log().all()
                .contentType(ContentType.JSON)
        .when()
                .get("users/2")
        .then()
                .log().all()
                .statusCode(200)
                .extract().response();
        String firstName = res.jsonPath().getString("data.first_name");
        Assert.assertEquals(firstName, "Janet", "First name is not Janet");

    }

    @Test(description = "Test Single User Not Found")
    public void testSingleUserNotFound() {
        RestAssured.config = config().sslConfig(new SSLConfig().relaxedHTTPSValidation());

        given()
                .log().all()
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
        RestAssured.config = config().sslConfig(new SSLConfig().relaxedHTTPSValidation());

        Response res = given()
                .log().all()
                .contentType(ContentType.JSON)
        .when()
                .get("unknown")
        .then()
                .log().all()
                .statusCode(200)
                .extract().response();
        int total = res.jsonPath().getInt("total");
        Assert.assertEquals(total, 12, "Total is not 12");

    }

    @Test(description = "Test Single Resource")
    public void testSingleResource() {
        RestAssured.config = config().sslConfig(new SSLConfig().relaxedHTTPSValidation());

        Response res = given()
                .log().all()
                .contentType(ContentType.JSON)
        .when()
                .get("unknown/2")
        .then()
                .log().all()
                .statusCode(200)
                .extract().response();
        String name = res.jsonPath().getString("data.name");
        Assert.assertEquals(name, "fuchsia rose", "Name is not fuchsia rose");

    }

    @Test(description = "Test Single Resource Not Found")
    public void testSingleResourceNotFound() {
        RestAssured.config = config().sslConfig(new SSLConfig().relaxedHTTPSValidation());

        Response res = given()
                .log().all()
                .contentType(ContentType.JSON)
        .when()
                .get("unknown/23")
        .then()
                .log().all()
                .statusCode(404)
                .extract().response();
        Assert.assertEquals(res.body().asString(), "{}", "Body is not empty");

    }

    
   @Test(description = "Test Post User")
    public void testPostUser() {
       RestAssured.config = config().sslConfig(new SSLConfig().relaxedHTTPSValidation());

       String requestBody = "{" +
               "\"name\": \"Ivan\"," +
               "\"job\": \"programmer\"" +
               "}";

       Response res = given()
                            .log().all()
                            .contentType(ContentType.JSON)
                            .body(requestBody)
                            .post("users")
                .then()
                            .log().all()
                            .statusCode(201)
                            .extract().response();
       String id = res.jsonPath().getString("id");
       Assert.assertFalse(id.isEmpty(), "Id should not be empty");

   }

    @Test(description = "Test Update User")
    public void testUpdateUser() {
        RestAssured.config = config().sslConfig(new SSLConfig().relaxedHTTPSValidation());

        String requestBody = "{" +
                "\"name\": \"morpheus\"," +
                "\"job\": \"zion resident\"" +
                "}";

        Response res = given()
                .log().all()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .patch("users/2")
        .then()
                .log().all()
                .statusCode(200)
                .extract().response();
        String name = res.jsonPath().getString("name");
        Assert.assertEquals(name, "morpheus", "Name is not morpheus");

    }

    @Test(description = "Test Delete User")
    public void testDeleteUser() {
        RestAssured.config = config().sslConfig(new SSLConfig().relaxedHTTPSValidation());

        given()
                .log().all()
                .delete("users/2")
        .then()
                .log().all()
                .statusCode(204);

    }

    @Test(description = "Test Register User")
    public void testRegisterUser() {
        RestAssured.config = config().sslConfig(new SSLConfig().relaxedHTTPSValidation());

        String requestBody = "{" +
                "\"email\": \"eve.holt@reqres.in\"," +
                "\"password\": \"pistol\"" +
                "}";

        Response res = given()
                .log().all()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .post("register")
        .then()
                .log().all()
                .statusCode(200)
                .extract().response();
        String id = res.jsonPath().getString("id");
        Assert.assertFalse(id.isEmpty(), "Id should not be empty");

    }

    @Test(description = "Test Register User without password")
    public void testRegisterUserError() {
        RestAssured.config = config().sslConfig(new SSLConfig().relaxedHTTPSValidation());

        String requestBody = "{" +
                "\"email\": \"eve.holt@reqres.in\"" +
                "}";

        given()
                .log().all()
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
        RestAssured.config = config().sslConfig(new SSLConfig().relaxedHTTPSValidation());

        String requestBody = "{" +
                "\"email\": \"eve.holt@reqres.in\"," +
                "\"password\": \"cityslicka\"" +
                "}";

        Response res = given()
                .log().all()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .post("login")
        .then()
                .log().all()
                .statusCode(200)
                .extract().response();
        String token = res.jsonPath().getString("token");
        Assert.assertFalse(token.isEmpty(), "Token should not be empty");

    }

    @Test(description = "Test Login User without password")
    public void testLoginUserError() {
        RestAssured.config = config().sslConfig(new SSLConfig().relaxedHTTPSValidation());

        String requestBody = "{" +
                "\"email\": \"peter@klaven\"" +
                "}";

        given()
                .log().all()
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
        RestAssured.config = config().sslConfig(new SSLConfig().relaxedHTTPSValidation());

        Response res = given()
                .log().all()
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
