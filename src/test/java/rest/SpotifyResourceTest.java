package rest;

import io.restassured.RestAssured;
import io.restassured.parsing.Parser;
import io.restassured.response.Response;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;

public class SpotifyResourceTest {
    private static final int SERVER_PORT = 7777;
    private static final String SERVER_URL = "http://localhost/api";


    static final URI BASE_URI = UriBuilder.fromUri(SERVER_URL).port(SERVER_PORT).build();
    private static HttpServer httpServer;

    static HttpServer startServer() {
        ResourceConfig rc = ResourceConfig.forApplication(new ApplicationConfig());
        return GrizzlyHttpServerFactory.createHttpServer(BASE_URI, rc);
    }


    @BeforeAll
    public static void setUpClass() {
        httpServer = startServer();
        //Setup RestAssured
        RestAssured.baseURI = SERVER_URL;
        RestAssured.port = SERVER_PORT;
        RestAssured.defaultParser = Parser.JSON;
    }

    @AfterAll
    public static void closeTestServer() {
        httpServer.shutdownNow();
    }

    @Test
    public void testServerIsUp() {
        given().when().get("/spotify/browse").then().statusCode(200);
    }

    @Test
    public void testSizeOfBrowseResponse() {
       Response response = given()
                .contentType("application/json")
                .when()
                .get("/spotify/browse").then()
                .extract().response();

        List<String> JsonResponse = response.jsonPath().getList("$");
        Assertions.assertEquals(20, JsonResponse.size());


    }

    @Test
    public void testIsArrayOfBrowseWithGenreResponse() {
       Response response = given()
                .contentType("application/json")
                .when()
                .get("/spotify/browse/gaming").then()
                .extract().response();

        List<String> JsonResponse = response.jsonPath().getList("$");
        Assertions.assertEquals(ArrayList.class, JsonResponse.getClass());

    }

    @Test
    public void testIsArrayOfTracks() {
       Response response = given()
                .contentType("application/json")
                .when()
                .get("/spotify/playlist/37i9dQZF1DXdfOcg1fm0VG").then()
                .extract().response();

        List<String> JsonResponse = response.jsonPath().getList("$");
        Assertions.assertEquals(ArrayList.class, JsonResponse.getClass());

    }
}
