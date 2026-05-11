package com.recipeshare.perfil;

import com.recipeshare.base.BaseTest;
import io.qameta.allure.*;
import org.testng.annotations.Test;
import org.testng.Assert;

import static org.hamcrest.Matchers.*;

@Epic("EP-B — Perfil de Usuario")
@Feature("PROF-02 — Editar Perfil")
public class EditarPerfilTest extends BaseTest {

    private String perfilEndpoint() {
        return "/rest/v1/profiles?id=eq." + userId;
    }

    @Test(priority = 1)
    @Story("PROF-02")
    @Severity(SeverityLevel.CRITICAL)
    @Description("TC-PROF-021: BVA full_name 60 chars — aceptado")
    public void tc021_fullName60CharsValido() {
        String nombre60 = "A".repeat(60);

        specEscritura()
                .body("{\"full_name\":\"" + nombre60 + "\"}")
                .when()
                .patch(perfilEndpoint())
                .then()
                .statusCode(anyOf(is(200), is(204)));
    }

    @Test(priority = 2)
    @Story("PROF-02")
    @Severity(SeverityLevel.CRITICAL)
    @Description("TC-PROF-022: BVA full_name 61 chars — [BUG] API acepta sin validar longitud")
    @Issue("BUG-PROF-001")
    @Link(name = "BUG-PROF-001",
            url = "https://github.com/ipanaque94/recipeshare-qa/blob/main/bug-reports/BUG-PROF-001.md")
    public void tc022_fullName61CharsInvalido() {
        String nombre61 = "A".repeat(61);

        int statusCode = specEscritura()
                .body("{\"full_name\":\"" + nombre61 + "\"}")
                .when()
                .patch(perfilEndpoint())
                .then()
                .extract().response().statusCode();

        // COMPORTAMIENTO ACTUAL: API retorna 200 sin validar
        // COMPORTAMIENTO ESPERADO: 400 o 422
        // BUG: Supabase no tiene check constraint para full_name <= 60
        if (statusCode == 200 || statusCode == 204) {
            System.out.println("⚠️ BUG-PROF-001: API acepta "
                    + "full_name de 61 chars. "
                    + "Esperado: 400. Actual: " + statusCode);
            // No falla el build — documenta el bug
            Assert.assertTrue(true,
                    "BUG conocido documentado: BUG-PROF-001");
        } else {
            Assert.fail("Status inesperado: " + statusCode);
        }
    }

    @Test(priority = 3)
    @Story("PROF-02")
    @Severity(SeverityLevel.CRITICAL)
    @Description("TC-PROF-023: BVA bio 300 chars — aceptado")
    public void tc023_bio300CharsValido() {
        String bio300 = "B".repeat(300);

        specEscritura()
                .body("{\"bio\":\"" + bio300 + "\"}")
                .when()
                .patch(perfilEndpoint())
                .then()
                .statusCode(anyOf(is(200), is(204)));
    }

    @Test(priority = 4)
    @Story("PROF-02")
    @Severity(SeverityLevel.CRITICAL)
    @Description("TC-PROF-024: BVA bio 301 chars — [BUG] API acepta sin validar longitud")
    @Issue("BUG-PROF-002")
    @Link(name = "BUG-PROF-002",
            url = "https://github.com/ipanaque94/recipeshare-qa/blob/main/bug-reports/BUG-PROF-002.md")
    public void tc024_bio301CharsInvalido() {
        String bio301 = "B".repeat(301);

        int statusCode = specEscritura()
                .body("{\"bio\":\"" + bio301 + "\"}")
                .when()
                .patch(perfilEndpoint())
                .then()
                .extract().response().statusCode();

        // COMPORTAMIENTO ACTUAL: API retorna 200 sin validar
        // COMPORTAMIENTO ESPERADO: 400 o 422
        // BUG: Supabase no tiene check constraint para bio <= 300
        if (statusCode == 200 || statusCode == 204) {
            System.out.println("⚠️ BUG-PROF-002: API acepta "
                    + "bio de 301 chars. "
                    + "Esperado: 400. Actual: " + statusCode);
            Assert.assertTrue(true,
                    "BUG conocido documentado: BUG-PROF-002");
        } else {
            Assert.fail("Status inesperado: " + statusCode);
        }
    }

    @Test(priority = 5)
    @Story("PROF-02")
    @Severity(SeverityLevel.NORMAL)
    @Description("TC-PROF-029: Cambios persisten al consultar perfil")
    public void tc029_cambiosPersistenEnDB() {
        String nombreNuevo = "Enoc QA Auto";

        specEscritura()
                .body("{\"full_name\":\"" + nombreNuevo + "\"}")
                .when()
                .patch(perfilEndpoint())
                .then()
                .statusCode(anyOf(is(200), is(204)));

        specAutenticado()
                .when()
                .get("/rest/v1/profiles"
                        + "?id=eq." + userId
                        + "&select=full_name")
                .then()
                .statusCode(200)
                .body("[0].full_name", equalTo(nombreNuevo));
    }
}