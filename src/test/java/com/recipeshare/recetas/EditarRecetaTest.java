package com.recipeshare.recetas;

import com.recipeshare.base.BaseTest;
import com.recipeshare.config.SupabaseConfig;
import io.qameta.allure.*;
import org.testng.annotations.Test;

import static org.hamcrest.Matchers.*;

@Epic("EP-C — Gestión de Recetas")
@Feature("REC-03 — Editar Receta")
public class EditarRecetaTest extends BaseTest {

    @Test(priority = 1)
    @Story("REC-03")
    @Severity(SeverityLevel.CRITICAL)
    @Description("TC-REC-049: Propietario edita título de su receta")
    public void tc049_propietarioEditaReceta() {
        String recipeId = SupabaseConfig.getRecipeIdExistente();
        String tituloNuevo = "Lomo Saltado Editado QA Auto";

        // 1. Editar
        specEscritura()
                .body("{\"title\":\"" + tituloNuevo + "\"}")
                .when()
                .patch("/rest/v1/recipes?id=eq." + recipeId)
                .then()
                .statusCode(anyOf(is(200), is(204)));

        // 2. Verificar que el cambio persistió
        specAutenticado()
                .when()
                .get("/rest/v1/recipes"
                        + "?id=eq." + recipeId
                        + "&select=title")
                .then()
                .statusCode(200)
                .body("[0].title", equalTo(tituloNuevo));
    }

    @Test(priority = 2)
    @Story("REC-03")
    @Severity(SeverityLevel.CRITICAL)
    @Description("TC-REC-050: Cambios reflejados inmediatamente en GET")
    public void tc050_cambiosReflejadosInmediatamente() {
        String recipeId = SupabaseConfig.getRecipeIdExistente();
        String tituloUnico = "Titulo " + System.currentTimeMillis();

        // 1. Editar
        specEscritura()
                .body("{\"title\":\"" + tituloUnico + "\"}")
                .when()
                .patch("/rest/v1/recipes?id=eq." + recipeId)
                .then()
                .statusCode(anyOf(is(200), is(204)));

        // 2. GET inmediato
        specAutenticado()
                .when()
                .get("/rest/v1/recipes"
                        + "?id=eq." + recipeId
                        + "&select=title")
                .then()
                .statusCode(200)
                .body("[0].title", equalTo(tituloUnico));
    }
}