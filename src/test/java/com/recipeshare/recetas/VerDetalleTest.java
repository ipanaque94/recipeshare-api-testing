package com.recipeshare.recetas;

import com.recipeshare.base.BaseTest;
import com.recipeshare.config.SupabaseConfig;
import io.qameta.allure.*;
import org.testng.annotations.Test;

import static org.hamcrest.Matchers.*;

@Epic("EP-C — Gestión de Recetas")
@Feature("REC-02 — Ver Detalle de Receta")
public class VerDetalleTest extends BaseTest {

    @Test(priority = 1)
    @Story("REC-02")
    @Severity(SeverityLevel.CRITICAL)
    @Description("TC-REC-045: Detalle contiene todos los campos requeridos")
    public void tc045_detalleConTodosLosCampos() {
        specAutenticado()
                .when()
                .get("/rest/v1/recipes"
                        + "?id=eq."
                        + SupabaseConfig.getRecipeIdExistente()
                        + "&select=*"
                        + ",recipe_ingredients(*)"
                        + ",recipe_steps(*)"
                        + ",recipe_images(*)")
                .then()
                .statusCode(200)
                .body("[0].title", notNullValue())
                .body("[0].description", notNullValue())
                .body("[0].prep_time", notNullValue())
                .body("[0].cook_time", notNullValue())
                .body("[0].difficulty", notNullValue())
                .body("[0].category", notNullValue())
                .body("[0].recipe_ingredients",
                        instanceOf(java.util.List.class))
                .body("[0].recipe_steps",
                        instanceOf(java.util.List.class));
    }

    @Test(priority = 2)
    @Story("REC-02")
    @Severity(SeverityLevel.CRITICAL)
    @Description("TC-REC-046: Visitante sin token puede ver receta pública")
    public void tc046_visitanteVeRecetaPublica() {
        specPublico()
                .when()
                .get("/rest/v1/recipes"
                        + "?id=eq."
                        + SupabaseConfig.getRecipeIdExistente())
                .then()
                .statusCode(200)
                .body("size()", greaterThan(0));
    }

    @Test(priority = 3)
    @Story("REC-02")
    @Severity(SeverityLevel.NORMAL)
    @Description("TC-REC-047: Ingredientes del detalle son array no vacío")
    public void tc047_ingredientesNoVacios() {
        specAutenticado()
                .when()
                .get("/rest/v1/recipe_ingredients"
                        + "?recipe_id=eq."
                        + SupabaseConfig.getRecipeIdExistente())
                .then()
                .statusCode(200)
                .body("size()", greaterThan(0));
    }

    @Test(priority = 4)
    @Story("REC-02")
    @Severity(SeverityLevel.NORMAL)
    @Description("TC-REC-048: Pasos del detalle son array no vacío")
    public void tc048_pasosNoVacios() {
        specAutenticado()
                .when()
                .get("/rest/v1/recipe_steps"
                        + "?recipe_id=eq."
                        + SupabaseConfig.getRecipeIdExistente())
                .then()
                .statusCode(200)
                .body("size()", greaterThan(0));
    }
}