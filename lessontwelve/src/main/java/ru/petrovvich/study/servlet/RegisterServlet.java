package ru.petrovvich.study.servlet;

import ru.petrovvich.study.processor.TemplateProcessor;
import ru.petrovvich.study.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RegisterServlet extends AbstractUserServlet {

    public RegisterServlet(TemplateProcessor templateProcessor, UserService userService) {
        super(templateProcessor, userService);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Map<String, Object> parameters = new HashMap<>();
        resp.setContentType(TEXT_HTML_CHARSET_UTF_8);
        resp.getWriter().println(templateProcessor.getPage("register.html", parameters));
        resp.setStatus(200);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType(TEXT_HTML_CHARSET_UTF_8);

        boolean saved = userService.saveNew(req.getParameter(LOGIN), req.getParameter("password"),
                req.getParameter("phone"), req.getParameter("city"), req.getParameter("street"),
                req.getParameter("house"));

        if (saved) {
            req.getSession().setAttribute("user", req.getParameter(LOGIN));
            resp.sendRedirect("home");
            resp.setStatus(200);
        }
    }
}
