# RecipeShare — API Testing con Rest Assured

> Automatización de pruebas de API sobre plataforma
> RecipeShare (Supabase) aplicando técnicas ISTQB.
> Modelo de datos normalizado probado por capas.

**Stack:** Java 17 · Rest Assured 5.4 · TestNG · Allure  
**API:** Supabase REST API + Auth v1  
**QA:** Enoc Isaac Ipanaque Rodas

---

## Lo que aprendí haciendo este proyecto

Antes de automatizar exploré la API con Postman
inspeccionando el tráfico real con F12 → Network.
Descubrí que RecipeShare usa un modelo normalizado:
una receta no es un solo endpoint — son 4 tablas
separadas que hay que orquestar en orden.

Eso me enseñó algo importante:
**automatizar sin entender el modelo de datos
produce tests que pasan pero no prueban nada real.**

---

## Lo que analicé antes de escribir código

1. Capturé todos los endpoints reales del sitio
2. Identifiqué el modelo normalizado de Supabase:
   `recipes → recipe_ingredients → recipe_steps → recipe_images`
3. Analicé qué TC de mi plan manual eran validables
   en API y cuáles eran responsabilidad de la UI
4. Separé los errores del código de los bugs reales
   del sistema — son cosas distintas

## Decisiones técnicas profesionales

**¿Por qué dependsOnMethods en crear receta?**
Porque el modelo es normalizado. Si la receta base
no se crea, no tiene sentido intentar agregar
ingredientes o pasos. Los tests dependen del orden
real del negocio.

**¿Por qué no fallo el build en bugs conocidos?**
Porque un bug conocido y documentado no debe
bloquear el pipeline. Lo registro con `@Issue`,
imprimo el comportamiento actual vs esperado,
y el test pasa informando el bug — no ocultándolo.

**¿Por qué BaseTest centraliza el login?**
Porque 30 tests necesitan token. Si el endpoint
de login cambia, cambio un solo método.

---

## Bugs encontrados y confirmados en API

| Bug | Historia | Descripción | Severidad |
|---|---|---|---|
| BUG-001 | AUTH-01 | API acepta password < 8 chars | Alta |
| BUG-002 | AUTH-02 | Token no persiste 30 días | Alta |
| BUG-003 | AUTH-01 | Revela que email ya existe | Alta |
| BUG-004 | PROF-02 | Acepta nombre > 60 chars | Media |
| BUG-005 | PROF-02 | Acepta bio > 300 chars | Media |
| BUG-007 | REC-01 | Acepta título > 100 chars | Alta |
| BUG-008 | REC-01 | Acepta descripción > 500 chars | Alta |

---

## Resultados

| Métrica | Valor |
|---|---|
| Total tests automatizados | 30 |
| ✅ Pass | 23 |
| ⚠️ Bugs documentados | 7 |
| 🔵 Blocked | 1 |
| Tiempo promedio de ejecución | ~47 segundos |

---

## Ejecutar

```bash
# Ejecutar todos los tests
mvn clean test

# Ver reporte visual Allure
mvn allure:serve
```

---

## Ver Automatización en UI

TC automatizados en la capa UI con
Selenium + Cucumber BDD.

[Ver proyecto UI →](https://github.com/ipanaque94/recipeshare-ui-testing)

---

[LinkedIn](https://linkedin.com/in/enoc-isaac-ipanaque-rodas-b3729a283)
| [GitHub](https://github.com/ipanaque94)
