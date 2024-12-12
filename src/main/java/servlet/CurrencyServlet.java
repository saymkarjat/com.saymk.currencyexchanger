package servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import dto.CurrencyDTO;
import dto.MessageDTO;
import exception.CurrencyNotFoundException;
import exception.DatabaseException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.CurrencyService;
import util.Util;

import java.io.IOException;

@WebServlet("/currency/*")
public class CurrencyServlet extends HttpServlet {
    CurrencyService service = new CurrencyService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        String pathInfo = req.getPathInfo();
        if (!Util.isCodeValid(pathInfo)){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(mapper.writeValueAsString(new MessageDTO("Код валюты отсутствует в адресе")));
            return;
        }
        String code = Util.getFormattedCode(pathInfo);
        try {
            CurrencyDTO currencyByCode = service.getCurrencyByCode(code);
            resp.getWriter().write(mapper.writeValueAsString(currencyByCode));
            resp.setStatus(HttpServletResponse.SC_OK);
        } catch (CurrencyNotFoundException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write(mapper.writeValueAsString(new MessageDTO("Валюта не найдена")));
        }
        catch (DatabaseException e){
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(mapper.writeValueAsString(new MessageDTO("База данных недоступна")));
        }

    }
}
