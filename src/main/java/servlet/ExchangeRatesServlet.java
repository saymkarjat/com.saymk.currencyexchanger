package servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import dto.CurrencyDTO;
import dto.ExchangeRateDTO;
import dto.MessageDTO;
import exception.CurrencyNotFoundException;
import exception.DatabaseException;
import exception.ExchangeRateAlreadyExistException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.CurrencyService;
import service.ExchangeRateService;
import util.Util;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@WebServlet("/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {
    ExchangeRateService service = new ExchangeRateService();
    CurrencyService serviceCurrency = new CurrencyService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        try {
            List<ExchangeRateDTO> allExchangeRates = service.findAllExchangeRates();
            resp.getWriter().write(mapper.writeValueAsString(allExchangeRates));
            resp.setStatus(HttpServletResponse.SC_OK);
        }
        catch (DatabaseException e){
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(mapper.writeValueAsString(new MessageDTO("База данных недоступна")));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        String baseCurrency = req.getParameter("baseCurrencyCode");
        String targetCurrency = req.getParameter("targetCurrencyCode");
        String rate = req.getParameter("rate");

        if (!Util.isExchangeRateValid(baseCurrency, targetCurrency, rate) || !Util.isStringDouble(rate)) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(mapper.writeValueAsString(new MessageDTO("Некорректные данные")));
            return;
        }
        try {
            CurrencyDTO baseDTO = serviceCurrency.getCurrencyByCode(baseCurrency);
            CurrencyDTO targetDTO = serviceCurrency.getCurrencyByCode(targetCurrency);
            BigDecimal rateDecimal = BigDecimal.valueOf(Double.parseDouble(rate));
            ExchangeRateDTO exchangeRateDTO = new ExchangeRateDTO();
            exchangeRateDTO.setBaseCurrency(baseDTO);
            exchangeRateDTO.setTargetCurrency(targetDTO);
            exchangeRateDTO.setRate(rateDecimal);
            service.addExchangeRate(exchangeRateDTO);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            ExchangeRateDTO responseRate = service.findExchangeRateByCodes(baseCurrency, targetCurrency);
            resp.getWriter().write(mapper.writeValueAsString(responseRate));
        }
        catch (CurrencyNotFoundException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write(mapper.writeValueAsString(new MessageDTO("Одна (или обе) валюта из валютной пары не существует в БД")));
        }
        catch (ExchangeRateAlreadyExistException e) {
            resp.setStatus(HttpServletResponse.SC_CONFLICT);
            resp.getWriter().write(mapper.writeValueAsString(new MessageDTO("Курс уже существует")));
        }
        catch (DatabaseException e){
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(mapper.writeValueAsString(new MessageDTO("База данных недоступна")));
        }

    }

}
