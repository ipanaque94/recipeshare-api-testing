package com.recipeshare.base;

import com.recipeshare.config.SupabaseConfig;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeSuite;
import static io.restassured.RestAssured.given;

public class BaseTest {
    protected static String accessToken;
    protected static String userId;
    protected static String recipeIdCreado;

    @BeforeSuite(alwaysRun = true)
    public void setup() {
        RestAssured.baseURI = SupabaseConfig.getBaseUrl();
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.filters(new AllureRestAssured());
        loginYGuardarToken();
    }

    private void loginYGuardarToken() {
        String body = "{"
                + "\"email\":\"" + SupabaseConfig.getEmailValido() + "\","
                + "\"password\":\"" + SupabaseConfig.getPasswordValido() + "\""
                + "}";

        Response response = given()
                .contentType(ContentType.JSON)
                .header("apikey", SupabaseConfig.getApiKey())
                .body(body)
                .when()
                .post("/auth/v1/token?grant_type=password")
                .then()
                .statusCode(200)
                .extract().response();

        accessToken = response.jsonPath()
                .getString("access_token");
        userId = response.jsonPath()
                .getString("user.id");

        System.out.println("✅ Login OK — userId: " + userId);
    }

    // Headers Supabase sin auth — para signup y login
    protected RequestSpecification specPublico() {
        return given()
                .contentType(ContentType.JSON)
                .header("apikey", SupabaseConfig.getApiKey());
    }

    // Headers Supabase con auth — para lectura
    protected RequestSpecification specAutenticado() {
        return given()
                .contentType(ContentType.JSON)
                .header("apikey", SupabaseConfig.getApiKey())
                .header("Authorization", "Bearer " + accessToken);
    }

    // Headers Supabase con auth + Prefer — para escritura
    // Prefer: return=representation hace que Supabase
    // devuelva el objeto creado/modificado en la respuesta
    protected RequestSpecification specEscritura() {
        return given()
                .contentType(ContentType.JSON)
                .header("apikey", SupabaseConfig.getApiKey())
                .header("Authorization", "Bearer " + accessToken)
                .header("Prefer", "return=representation");
    }
}
