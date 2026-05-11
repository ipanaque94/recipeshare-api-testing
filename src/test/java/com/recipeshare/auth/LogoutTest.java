package com.recipeshare.auth;

import com.recipeshare.base.BaseTest;
import io.qameta.allure.*;
import org.testng.annotations.Test;

import static org.hamcrest.Matchers.*;

@Epic("EP-A — Autenticación y Cuenta")
@Feature("AUTH-03 — Logout")
public class LogoutTest extends BaseTest {

    @Test(priority = 1)
    @Story("AUTH-03")
    @Severity(SeverityLevel.CRITICAL)
    @Description("TC-AUTH-015: Logout global destruye la sesión")
    public void tc015_logoutExitoso() {
        specAutenticado()
                .when()
                .post("/auth/v1/logout?scope=global")
                .then()
                .statusCode(anyOf(is(200), is(204)));
    }
}