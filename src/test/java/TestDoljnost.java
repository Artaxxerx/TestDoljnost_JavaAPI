import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;


public class TestDoljnost {

    public String idValue;
    public String getIdValued;

    @Test
    public void createSotrudnik() {    //Создание сотрудника

        List<Map<String, String>> attributes = new ArrayList<>();

        Map<String, String> attribute1 = new HashMap<>();
        attribute1.put("key", "FIO");
        attribute1.put("value", "Иванов Иван Иванович");
        attributes.add(attribute1);

        Map<String, String> attribute2 = new HashMap<>();
        attribute2.put("key", "appointment");
        attribute2.put("value", "Менеджер на телефоне");
        attributes.add(attribute2);

        Map<String, String> attribute3 = new HashMap<>();
        attribute3.put("key", "Filial_name");
        attribute3.put("value", "Рога и копыта");
        attributes.add(attribute3);

        Map<String, Object> jsonBody = new HashMap<>();
        jsonBody.put("Filial_ID", "Test_1");
        jsonBody.put("attributes1", attributes);


        Response response = given()
                .baseUri(BASE_URL)
                .basePath(ENDPOINT)
                .contentType("application/json")
                .body(jsonBody)
                .when()
                .post();
        response.then().assertThat().body("returnCode", equalTo("EA.200"))    //Проверка, что в ответе есть EA.200
                .assertThat().body("attributes1[2].value", notNullValue());           //Проверка, что в ответе есть должность созданного сотрудника
        idValue = response.then().contentType(ContentType.JSON).extract().path("attributes1[1].value");        //Сохранения ID сотрудника для последующего использования

    }


    @Test
    public void findlByCriteria() {       //Поиск созданного сотрудника
        List<Map<String, String>> criterionBody = new ArrayList<>();

        Map<String, String> findCriterion1 = new HashMap<>();
        findCriterion1.put("attributes_name", "appointment");
        findCriterion1.put("operation", "contains");
        findCriterion1.put("value", "Менеджер на телефоне");
        criterionBody.add(findCriterion1);

        Map<String, String> findCriterion2 = new HashMap<>();
        findCriterion2.put("attributes_name", "Filial_name");
        findCriterion2.put("operation", "contains");
        findCriterion2.put("value", "Рога и копыта");
        criterionBody.add(findCriterion2);

        Map<String, Object> jsonFindBody = new HashMap<>();
        jsonFindBody.put("find_criterion", criterionBody);


        Response response = given()
                .baseUri(BASE_URL)
                .basePath(ENDPOINT)
                .contentType("application/json")
                .body(jsonFindBody)
                .when()
                .post();
        response.then().statusCode(200)     //Проверка на статус код ответа
                .body("returnCode", equalTo("EA.200"));     //В ответе метода EA.200
        response.then().assertThat().body("attributes[5].value", is("false"));  //Проверка, что у искомой записи созданного сотрудника признак удален = false
        getIdValued = response.then().contentType(ContentType.JSON).extract().path("attributes[2].value");
        Assertions.assertEquals(idValue, getIdValued, "Id созданного сотрудника равен id найденного сотрдника");  //Проверка, что id искомого сотрудника = id созданного сотрудника
    }

    @Test
    public void deleteSotrudnik() {   //Удаление сотрудника
        Map<String, String> deleteSotrudnik = new HashMap<>();
        deleteSotrudnik.put("Filial_ID", "Test_1");
        deleteSotrudnik.put("object_id", idValue);

        Response response = given()
                .baseUri(BASE_URL)
                .basePath(ENDPOINT)
                .contentType("application/json")
                .body(deleteSotrudnik)
                .when()
                .post();
        response.then().assertThat().body("returnCode", equalTo("EA.201"));   //Проверка, что код ответа EA.201
        response.then().assertThat().body("attributes1[0].is_deleted", equalTo("true"));   //Проверка, что для удаляемого объекта признак is_deleted = true в ответе
        response.then().assertThat().body("returnMessage", equalTo("Запись успешно удалена"));  //Сообщение содержит: "Запись успешно удалена"
    }
}
