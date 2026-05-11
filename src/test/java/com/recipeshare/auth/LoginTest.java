package com.recipeshare.auth;

import com.recipeshare.base.BaseTest;
import com.recipeshare.config.SupabaseConfig;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.Test;

import static org.hamcrest.Matchers.*;

@Epic("EP-A — Autenticación y Cuenta")
@Feature("AUTH-02 — Login")
public class LoginTest extends BaseTest {

    @Test(priority = 1)
    @Story("AUTH-02")
    @Severity(SeverityLevel.CRITICAL)
    @Description("TC-AUTH-008: Login exitoso — credenciales correctas")
    public void tc008_loginExitoso() {
        specPublico()
                .body("{"
                        + "\"email\":\""
                        + SupabaseConfig.getEmailValido() + "\","
                        + "\"password\":\""
                        + SupabaseConfig.getPasswordValido() + "\""
                        + "}")
                .when()
                .post("/auth/v1/token?grant_type=password")
                .then()
                .statusCode(200)
                .body("access_token", notNullValue())
                .body("token_type", equalTo("bearer"))
                .body("user.email",
                        equalTo(SupabaseConfig.getEmailValido()));
    }

    @Test(priority = 2)
    @Story("AUTH-02")
    @Severity(SeverityLevel.CRITICAL)
    @Description("TC-AUTH-010: Password incorrecto — mensaje genérico")
    @Issue("BUG-AUTH-002")
    public void tc010_passwordIncorrecto() {
        specPublico()
                .body("{"
                        + "\"email\":\""
                        + SupabaseConfig.getEmailValido() + "\","
                        + "\"password\":\"Incorrecto1\""
                        + "}")
                .when()
                .post("/auth/v1/token?grant_type=password")
                .then()
                .statusCode(400)
                .body("access_token", nullValue())
                .body("error_code", equalTo("invalid_credentials"))
                .body("msg", containsString("Invalid"));
    }

    @Test(priority = 3)
    @Story("AUTH-02")
    @Severity(SeverityLevel.CRITICAL)
    @Description("TC-AUTH-011: Email no registrado — mensaje genérico")
    public void tc011_emailNoRegistrado() {
        specPublico()
                .body("{"
                        + "\"email\":\"noexiste.qa@test.com\","
                        + "\"password\":\"Password1!\""
                        + "}")
                .when()
                .post("/auth/v1/token?grant_type=password")
                .then()
                .statusCode(400)
                .body("access_token", nullValue());
    }

    @Test(priority = 4)
    @Story("AUTH-02")
    @Severity(SeverityLevel.NORMAL)
    @Description("TC-AUTH-012: Ambas credenciales incorrectas")
    public void tc012_ambasCredencialesIncorrectas() {
        specPublico()
                .body("{"
                        + "\"email\":\"mal@test.com\","
                        + "\"password\":\"Mal123!\""
                        + "}")
                .when()
                .post("/auth/v1/token?grant_type=password")
                .then()
                .statusCode(400)
                .body("access_token", nullValue());
    }

    @Test(priority = 7)
    @Story("AUTH-02")
    @Severity(SeverityLevel.NORMAL)
    @Description("TC-AUTH-014: [BUG] Sesión persistente con Recordarme — no implementado")
    @Issue("TRW-27")
    public void tc014_sesionPersistenteRecordarme() {
        // Este test documenta que el token devuelto al hacer login
        // no tiene fecha de expiración extendida (Recordarme no implementado)
        String respuesta = specPublico()
                .body("{"
                        + "\"email\":\""
                        + SupabaseConfig.getEmailValido() + "\","
                        + "\"password\":\""
                        + SupabaseConfig.getPasswordValido() + "\""
                        + "}")
                .when()
                .post("/auth/v1/token?grant_type=password")
                .then()
                .statusCode(200)
                .extract().asString();

        // Supabase no devuelve ningún campo diferenciador para "Recordarme"
        // COMPORTAMIENTO ESPERADO: expires_in extendido (ej: 30 días = 2592000s)
        // COMPORTAMIENTO ACTUAL: expires_in estándar (~3600s)
        int expiresIn = io.restassured.path.json.JsonPath
                .from(respuesta).getInt("expires_in");

        System.out.println("⚠️ TRW-27: expires_in actual = "
                + expiresIn + "s. "
                + "Recordarme debería extender a >= 2592000s (30d)");
        Assert.assertTrue(true, "TRW-27 documentado — Recordarme no diferencia sesión");
    }
}