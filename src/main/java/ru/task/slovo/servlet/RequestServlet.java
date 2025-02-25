package ru.task.slovo.servlet;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.task.slovo.model.RequestDto;
import ru.task.slovo.service.RequestService;
import ru.task.slovo.service.RequestServiceImpl;

import java.io.IOException;

@WebServlet("/request")
public class RequestServlet extends HttpServlet {

    private final Logger logger = LogManager.getLogger(RequestServlet.class);

    private final RequestService requestService;
    private final Gson gson = new Gson();

    public RequestServlet() {
        requestService = new RequestServiceImpl();
    }

    public RequestServlet(RequestService requestService) {
        this.requestService = requestService;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        try {
            RequestDto request = gson.fromJson(req.getReader(), RequestDto.class);

            if (!isValid(request)) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("Неверное тело запроса.");
                return;
            }

            requestService.submit(request);
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write("Запрос получен.");
        } catch (JsonSyntaxException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("Неверный формат JSON.");
        } catch (Exception e) {
            logger.error("Ошибка обработки запроса", e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("Внутренняя ошибка сервера.");
        }
    }

    private boolean isValid(RequestDto dto) {
        return dto != null && dto.getType() != null;
    }

    @Override
    public void destroy() {
        requestService.shutdown();
        super.destroy();
    }

}