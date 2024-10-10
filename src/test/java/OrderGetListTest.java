import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

public class OrderGetListTest {

    @Before
    public void setUp() {
        RestAssured.baseURI = "http://qa-scooter.praktikum-services.ru/";
    }

    @Step("Получение списка заказов")
    public Response GetListOrders(){
        return given()
                .header("Content-type", "application/json")
                .and()
                .get("/api/v1/orders");
    }

    @Step("Смотрит чтобы поля не были пустыми")
    public void compareBodyOrderField( Response response){
        response
                .then()
                .assertThat()
                .body("orders", notNullValue());
    }

    @Test
    @DisplayName("Создание заказа и проверка есть ли в теле ответа поле заказы")
    public void testCreateOrder() {
        Response response = GetListOrders();
        compareBodyOrderField(response);
    }
}