import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;


public class CoutrierTest {

    private final String url = "https://qa-scooter.praktikum-services.ru";

    CourierObject courier = new CourierObject("goldy3", "goldy3", "goldy3");


    @Before
    public void setUp() {
        RestAssured.baseURI = url;

    }

    @Test
    @DisplayName("Создание курьера с ожидание ответа 201 и ответа ок теле ответа ")
    public void testCreateCourier() {
        Response response = createCourier(courier);
        compareStatusCode(response, 201);
        compareResponseData(response);
        deleteAccount();

    }

    @Step("Запрос на создание курьера")
    public Response createCourier(CourierObject courier) {
        return given()
                .header("Content-type", "application/json")
                .and()
                .body(courier)
                .when()
                .post("/api/v1/courier");
    }

    @Step("Сравнить статус код")
    public void compareStatusCode(Response response, int code) {
        response.then().statusCode(code);
    }

    @Step("Проверка на тело ответа ok:true")
    public void compareResponseData(Response response) {
        response.then().assertThat().body("ok", equalTo(true));
    }


    @Test
    @DisplayName("Повторная отправка запроса регистрации")
    public void testCreateDoubleCourier() {
        createCourier(courier);
        Response response = createCourier(courier);
        compareStatusCode(response, 409);
        deleteAccount();
    }


    @Test
    @DisplayName("Регистрация без полей")
    public void testWithoutRequiredField() {
        Response response = sendJsonRequest("{'текст'}");
        compareStatusCode(response, 400);
    }

    @Step("Проверка на заполняемость полей, отправка пустой строки")
    public Response sendJsonRequest(String json) {
        return given()
                .header("Content-type", "application/json")
                .and()
                .body(json)
                .when()
                .post("/api/v1/courier");
    }

    @Step("Удаление учетки после регистрации")
    public void deleteAccount() {
        int id = given()
                .header("Content-type", "application/json")
                .and()
                .body(new CourierLogInObject(courier.getLogin(), courier.getPassword()))
                .when()
                .post("/api/v1/courier/login").then().extract().body().path("id");

        given()
                .header("Content-type", "application/json")
                .when()
                .delete("/api/v1/courier/" + id);
    }





}
