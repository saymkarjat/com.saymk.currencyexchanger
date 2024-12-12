package servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import dto.ExchangeRateDTO;
import dto.MessageDTO;
import exception.DatabaseException;
import exception.ExchangeRateNotFoundException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.ExchangeRateService;
import util.Util;
import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.stream.Collectors;

@WebServlet("/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet {
    ExchangeRateService service = new ExchangeRateService();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        String pathInfo = req.getPathInfo();
        if (!Util.isCurrencyPairValid(pathInfo)){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(mapper.writeValueAsString(new MessageDTO("Код валюты отсутствует в адресе")));
            return;
        }
        String currencyPair = Util.getFormattedCode(pathInfo);
        String base = currencyPair.substring(0, 3);
        String target = currencyPair.substring(3);
        try {
            ExchangeRateDTO exchangeRateDTO = service.findExchangeRateByCodes(base, target);
            resp.getWriter().write(mapper.writeValueAsString(exchangeRateDTO));
            resp.setStatus(HttpServletResponse.SC_OK);
        } catch (ExchangeRateNotFoundException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write(mapper.writeValueAsString(new MessageDTO("Курс не найден")));
        }
        catch (DatabaseException e){
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(mapper.writeValueAsString(new MessageDTO("База данных недоступна")));
        }
    }

    @Override
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        String pathInfo = req.getPathInfo();
        BufferedReader reader = req.getReader();
        String requestBody = reader.lines().collect(Collectors.joining(System.lineSeparator()));
        String[] keyValue = requestBody.split("=");
        String rate = keyValue[1];
        if (!Util.isStringDouble(rate) || !Util.isCurrencyPairValid(pathInfo)){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(mapper.writeValueAsString(new MessageDTO("Некорректные данные")));
            return;
        }
        String currencyPair = Util.getFormattedCode(pathInfo);
        String base = currencyPair.substring(0, 3);
        String target = currencyPair.substring(3);
        try {
            ExchangeRateDTO exchangeRateDTO = service.findExchangeRateByCodes(base, target);
            exchangeRateDTO.setRate(BigDecimal.valueOf(Double.parseDouble(rate)));
            service.updateExchangeRate(exchangeRateDTO);
            resp.getWriter().write(mapper.writeValueAsString(exchangeRateDTO));
            resp.setStatus(HttpServletResponse.SC_OK);
        } catch (ExchangeRateNotFoundException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write(mapper.writeValueAsString(new MessageDTO("Курс не найден")));
        }

        catch (DatabaseException e){
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(mapper.writeValueAsString(new MessageDTO("База данных недоступна")));
        }
    }

}
