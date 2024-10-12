import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class CourierLogInTest {

    CourierObject courier = new CourierObject("goldy", "goldy", "goldy");
    CourierLogInObject courierLogInObject = new CourierLogInObject(courier.getLogin(), courier.getPassword());
    private final String url = "https://qa-scooter.praktikum-services.ru";


    @Before
    public void setUp() {
        RestAssured.baseURI = url;

    }

    @Test
    @DisplayName("Логин курьера и проверка есть ли id в теле ответа")
    public void testLoginCourierBody() {
        CourierLogInObject courierLogInObject = new CourierLogInObject(courier.getLogin(), courier.getPassword());
        Response response = sendPostRequestLoginCourier(courierLogInObject);
        compareBodyLoginAccept(response);
    }

    @Step("Send POST request to /api/v1/courier/login")
    public Response sendPostRequestLoginCourier(CourierLogInObject courierLogInObject) {
        return given()
                .header("Content-type", "application/json")
                .and()
                .body(courierLogInObject)
                .when()
                .post("/api/v1/courier/login");
    }

    @Step("Compare body login have id field")
    public void compareBodyLoginAccept(Response response) {
        response
                .then()
                .assertThat()
                .body("id", notNullValue());
    }


    @Test
    @DisplayName("Авторизация с неверным паролем")
    public void testEmptyParamLoginCourierStatusCode() {
        //  Авторизация с неверным паролем;
        CourierLogInObject courierLogInObject1 = new CourierLogInObject(courier.getLogin(), "4321");
        Response response = sendPostRequestLoginCourier(courierLogInObject1);
        compareStatusCode(response, 404);
    }

    @Test
    @DisplayName("авторизация без одного поля")
    public void testMissParamLoginCourier() {
        CourierWrongPassword courierWrongPassword = new CourierWrongPassword("test");
        Response response = sendPostRequestLoginCourierMissField(courierWrongPassword);
        compareStatusCode(response, 400);
    }

    @Step("Send POST request to /api/v1/courier/login from JSON")
    public Response sendPostRequestLoginCourierMissField(CourierWrongPassword courierWrongPassword) {
        return given()
                .header("Content-type", "application/json")
                .and()
                .body(courierWrongPassword)
                .when()
                .post("/api/v1/courier/login");
    }


    @Test
    @DisplayName("Авторизация и  проверка стауса ответа 200")
    public void testLoginCourier() {
        Response response = sendPostRequestLoginCourier(courierLogInObject);
        compareStatusCode(response, 200);
    }

    @Test
    @DisplayName("Ошибка, юзера нет")
    public void testFailLoginCourierMessage() {
        CourierLogInObject courierLogInObject1 = new CourierLogInObject("gold", "2134");
        Response response = sendPostRequestLoginCourier(courierLogInObject1);
        compareBodyLoginMessage(response, "Учетная запись не найдена");
    }

    @Step("Compare body message")
    public void compareBodyLoginMessage(Response response, String message) {
        response
                .then()
                .assertThat()
                .body("message", equalTo(message));
    }

    @Step("Compare status code")
    public void compareStatusCode(Response response, int code) {
        response.then().statusCode(code);
    }

}
