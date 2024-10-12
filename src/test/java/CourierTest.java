import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

public class CourierTest {

    private final String url = "https://qa-scooter.praktikum-services.ru";
    private CourierObject courier;
    private int courierId;

    @Before
    public void setUp() {
        RestAssured.baseURI = url;
        courier = new CourierObject("goldy3", "goldy3", "goldy3");
        Response response = createCourier(courier);
        compareStatusCode(response, 201);
        compareResponseData(response);
        courierId = getCourierId(courier); // Сохраняем ID курьера для удаления после теста
    }

    @Test
    @DisplayName("Проверка регистрации курьера")
    public void testCreateCourier() {
        // Сам тест, например, можно проверять авторизацию курьера
        Response response = loginCourier(courier);
        compareStatusCode(response, 200);
    }

    @Test
    @DisplayName("Повторная регистрация курьера")
    public void testCreateDoubleCourier() {
        Response response = createCourier(courier);
        compareStatusCode(response, 409);
    }

    @Test
    @DisplayName("Регистрация без полей")
    public void testWithoutRequiredField() {
        Response response = sendJsonRequest("{}");
        compareStatusCode(response, 400);
    }

    @Step("Запрос на создание курьера")
    public Response createCourier(CourierObject courier) {
        return given()
                .header("Content-type", "application/json")
                .body(courier)
                .when()
                .post("/api/v1/courier");
    }

    @Step("Запрос на логин курьера")
    public Response loginCourier(CourierObject courier) {
        return given()
                .header("Content-type", "application/json")
                .body(new CourierLogInObject(courier.getLogin(), courier.getPassword()))
                .when()
                .post("/api/v1/courier/login");
    }

    @Step("Получить ID курьера после логина")
    public int getCourierId(CourierObject courier) {
        return loginCourier(courier)
                .then()
                .extract()
                .path("id");
    }

    @Step("Сравнить статус код")
    public void compareStatusCode(Response response, int code) {
        response.then().statusCode(code);
    }

    @Step("Проверка на тело ответа ok:true")
    public void compareResponseData(Response response) {
        response.then().body("ok", equalTo(true));
    }

    @Step("Проверка на заполняемость полей, отправка пустой строки")
    public Response sendJsonRequest(String json) {
        return given()
                .header("Content-type", "application/json")
                .body(json)
                .when()
                .post("/api/v1/courier");
    }

    @After
    @DisplayName("Происходит удаление если id будет больше нуля что гарантирует что юзер авторизовался и несломает After")
    public void tearDown() {
        if (courierId > 0) {
            deleteCourier(courierId);
        }
    }

    @Step("Удаление курьера")
    public void deleteCourier(int id) {
        given()
                .header("Content-type", "application/json")
                .when()
                .delete("/api/v1/courier/" + id);
    }
}
