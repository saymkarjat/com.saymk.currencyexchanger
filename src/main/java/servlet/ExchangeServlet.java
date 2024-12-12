package servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import dto.ExchangeDTO;
import dto.MessageDTO;
import exception.CurrencyNotFoundException;
import exception.DatabaseException;
import exception.ExchangeRateNotFoundException;
import exception.InvalidExchangeRateArgsException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.ExchangeService;

import java.io.IOException;

@WebServlet("/exchange")
public class ExchangeServlet extends HttpServlet {
    ExchangeService service = new ExchangeService();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        try {
            ExchangeDTO exchange = service.getExchange(req);
            resp.getWriter().write(mapper.writeValueAsString(exchange));
            resp.setStatus(HttpServletResponse.SC_OK);
        }
        catch (InvalidExchangeRateArgsException e){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(mapper.writeValueAsString(new MessageDTO("Некорректные данные")));
        }
        catch (CurrencyNotFoundException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write(mapper.writeValueAsString(new MessageDTO("Валюта не найдена")));
        }
        catch (ExchangeRateNotFoundException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write(mapper.writeValueAsString(new MessageDTO("Курс не найден")));
        }
        catch (DatabaseException e){
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(mapper.writeValueAsString(new MessageDTO("База данных недоступна")));
        }


    }
}
