package ru.task.slovo.servlet;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.task.slovo.model.RequestDto;
import ru.task.slovo.service.RequestService;

import java.io.IOException;

public class RequestServlet extends HttpServlet {

    private final RequestService processor = new RequestService();
    private final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        RequestDto request = gson.fromJson(req.getReader(), RequestDto.class);
        processor.submit(request);
        resp.getWriter().write("Request received: " + request);
    }

    @Override
    public void destroy() {
        processor.shutdown();
        super.destroy();
    }

}