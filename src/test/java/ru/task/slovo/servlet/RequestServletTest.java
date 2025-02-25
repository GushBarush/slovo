package ru.task.slovo.servlet;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.task.slovo.model.RequestDto;
import ru.task.slovo.service.RequestService;

import java.io.*;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;

class RequestServletTest {

    private static WireMockServer wireMockServer;
    private RequestServlet requestServlet;
    private Gson gson;

    @Mock
    private RequestService requestService;

    @BeforeAll
    static void beforeAll() {
        wireMockServer = new WireMockServer(8080);
        wireMockServer.start();
        WireMock.configureFor("localhost", 8080);
    }

    @AfterAll
    static void afterAll() {
        wireMockServer.stop();
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        requestServlet = new RequestServlet(requestService);
        gson = new Gson();
    }

    static Stream<Arguments> provideRequests() {
        return Stream.of(
                Arguments.of(new RequestDto(RequestDto.Type.A, 10), HttpServletResponse.SC_OK, "Запрос получен."),
                Arguments.of(new RequestDto(null, 10), HttpServletResponse.SC_BAD_REQUEST, "Неверное тело запроса."),
                Arguments.of(null, HttpServletResponse.SC_BAD_REQUEST, "Неверный формат JSON.")
        );
    }

    @ParameterizedTest
    @MethodSource("provideRequests")
    void testDoPost(RequestDto requestDto, int expectedStatus, String expectedMessage) throws IOException {
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        HttpServletResponse mockResponse = mock(HttpServletResponse.class);

        String jsonInput = (requestDto != null) ? gson.toJson(requestDto) : "invalid json";
        Reader reader = new StringReader(jsonInput);
        when(mockRequest.getReader()).thenReturn(new BufferedReader(reader));

        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        when(mockResponse.getWriter()).thenReturn(printWriter);

        requestServlet.doPost(mockRequest, mockResponse);

        verify(mockResponse).setStatus(expectedStatus);
        printWriter.flush();
        Assertions.assertTrue(stringWriter.toString().contains(expectedMessage));
    }
}