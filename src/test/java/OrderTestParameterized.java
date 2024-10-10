import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

@RunWith(Parameterized.class)
public class OrderTestParameterized {
    private final String colors;

    public OrderTestParameterized(final String colors) {
        this.colors = colors;
    }

    @Before
    public void setUp() {
        RestAssured.baseURI = "http://qa-scooter.praktikum-services.ru/";
    }

    @Parameterized.Parameters
    public static Object[][] getCredentials() {
        return new Object[][]{
                {"GREY"},
                {"BLACK"},
                {"BLACK, GREY"},
                {""},
        };
    }


    @Test
    @DisplayName("Создание заказа с разными расцветками самокатов")
    public void testCreateOrder() {
        Order order = new Order(
                "goldy",
                "goldy",
                "Улица пушкина дом колотушкино",
                "Метро Люблино",
                "87089214128",
                1,
                "07.10.2024",
                "Метро люблино",
                colors.split(",")
        );
        Response response = CreateOrder(order);
        compareStatusCode(response, 201);
        compareBodyCreateOrder(response);
    }


    @Step("создание заказа")
    public Response CreateOrder(Order order) {
        return given()
                .header("Content-type", "application/json")
                .and()
                .body(order)
                .when()
                .post("/api/v1/orders");
    }

    @Step("поле трэк не пустой")
    public void compareBodyCreateOrder(Response response) {
        response
                .then()
                .assertThat()
                .body("track", notNullValue());
    }

    @Step("проверка статуса кода")
    public void compareStatusCode(Response response, int code) {
        response
                .then()
                .statusCode(code);
    }




}