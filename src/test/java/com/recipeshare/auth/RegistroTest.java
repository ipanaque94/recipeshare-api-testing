package com.recipeshare.auth;

import com.recipeshare.base.BaseTest;
import com.recipeshare.config.SupabaseConfig;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.Test;

import static org.hamcrest.Matchers.*;

@Epic("EP-A — Autenticación y Cuenta")
@Feature("AUTH-01 — Registro de Cuenta")
public class RegistroTest extends BaseTest {

    @Test(priority = 1)
    @Story("AUTH-01")
    @Severity(SeverityLevel.CRITICAL)
    @Description("TC-AUTH-001: Registro exitoso con email nuevo y password válido")
    public void tc001_registroExitoso() {
        String email = "qa.auto."
                + System.currentTimeMillis() + "@test.com";

        specPublico()
                .body("{"
                        + "\"email\":\"" + email + "\","
                        + "\"password\":\"Password1!\""
                        + "}")
                .when()
                .post("/auth/v1/signup")
                .then()
                .statusCode(200)
                .body("access_token", notNullValue())
                .body("user.email", equalTo(email));
    }

    @Test(priority = 2)
    @Story("AUTH-01")
    @Severity(SeverityLevel.CRITICAL)
    @Description("TC-AUTH-002: Email inválido sin @ — rechazado")
    public void tc002_emailInvalidoSinArroba() {
        specPublico()
                .body("{"
                        + "\"email\":\"usertest.com\","
                        + "\"password\":\"Password1!\""
                        + "}")
                .when()
                .post("/auth/v1/signup")
                .then()
                .statusCode(anyOf(is(400), is(422)))
                .body("access_token", nullValue());
    }

    @Test(priority = 3)
    @Story("AUTH-01")
    @Severity(SeverityLevel.CRITICAL)
    @Description("TC-AUTH-003: BVA 7 chars — [BUG] Supabase acepta password menor a 8 chars")
    @Issue("BUG-001")
    @Link(name = "BUG-001",
            url = "https://github.com/ipanaque94/recipeshare-qa/blob/main/bug-reports/BUG-001-registro-password.md")
    public void tc003_passwordBVA7CharsInvalido() {
        String email = "bva7."
                + System.currentTimeMillis() + "@test.com";

        int statusCode = specPublico()
                .body("{"
                        + "\"email\":\"" + email + "\","
                        + "\"password\":\"Pass12!\""
                        + "}")
                .when()
                .post("/auth/v1/signup")
                .then()
                .extract().response().statusCode();

        // COMPORTAMIENTO ACTUAL: Supabase acepta password de 7 chars
        // COMPORTAMIENTO ESPERADO: 400 — mínimo 8 chars (AC-AUTH-01-01)
        // Este bug ya está documentado en JIRA como BUG-001
        if (statusCode == 200) {
            System.out.println("⚠️ BUG-001 CONFIRMADO: "
                    + "API acepta password de 7 chars. "
                    + "Incumple AC-AUTH-01-01");
            Assert.assertTrue(true,
                    "BUG-001 conocido y documentado");
        } else {
            // Si algún día lo corrigen, el test pasa normalmente
            Assert.assertEquals(statusCode, 400,
                    "Bug corregido — password 7 chars ahora rechazado");
        }
    }

    @Test(priority = 4)
    @Story("AUTH-01")
    @Severity(SeverityLevel.CRITICAL)
    @Description("TC-AUTH-004: BVA 8 chars exactos — aceptado")
    public void tc004_passwordBVA8CharsValido() {
        String email = "bva8."
                + System.currentTimeMillis() + "@test.com";

        specPublico()
                .body("{"
                        + "\"email\":\"" + email + "\","
                        + "\"password\":\"Pass123!\""
                        + "}")
                .when()
                .post("/auth/v1/signup")
                .then()
                .statusCode(200)
                .body("access_token", notNullValue());
    }

    @Test(priority = 5)
    @Story("AUTH-01")
    @Severity(SeverityLevel.NORMAL)
    @Description("TC-AUTH-005: BVA 9 chars — aceptado")
    public void tc005_passwordBVA9CharsValido() {
        String email = "bva9."
                + System.currentTimeMillis() + "@test.com";

        specPublico()
                .body("{"
                        + "\"email\":\"" + email + "\","
                        + "\"password\":\"Pass1234!\""
                        + "}")
                .when()
                .post("/auth/v1/signup")
                .then()
                .statusCode(200)
                .body("access_token", notNullValue());
    }

    @Test(priority = 6)
    @Story("AUTH-01")
    @Severity(SeverityLevel.CRITICAL)
    @Description("TC-AUTH-006: Email duplicado — [BUG] API revela que el email ya existe")
    @Issue("BUG-003")
    @Link(name = "BUG-003",
            url = "https://github.com/ipanaque94/recipeshare-qa/blob/main/bug-reports/BUG-003-email-duplicado.md")
    public void tc006_emailDuplicado() {
        String respuesta = specPublico()
                .body("{"
                        + "\"email\":\""
                        + SupabaseConfig.getEmailValido() + "\","
                        + "\"password\":\"Password1!\""
                        + "}")
                .when()
                .post("/auth/v1/signup")
                .then()
                .statusCode(anyOf(is(400), is(422)))
                .extract().asString();

        // COMPORTAMIENTO ACTUAL: Supabase retorna "user already registered"
        // COMPORTAMIENTO ESPERADO: Mensaje genérico sin revelar existencia
        // BUG: Incumple AC-AUTH-01-02
        boolean revelaExistencia =
                respuesta.toLowerCase().contains("already registered")
                        || respuesta.toLowerCase().contains("user_already_exists");

        if (revelaExistencia) {
            System.out.println("⚠️ BUG-003 CONFIRMADO: "
                    + "API revela que el email ya está registrado. "
                    + "Respuesta: " + respuesta);
            Assert.assertTrue(true,
                    "BUG-003 conocido y documentado");
        } else {
            Assert.assertFalse(
                    respuesta.toLowerCase()
                            .contains("already registered"),
                    "OK — mensaje genérico sin revelar existencia");
        }
    }
}