package com.goit.cubot.pbapi;

import com.goit.cubot.Bank;
import com.goit.cubot.CurrencyInfo;
import com.goit.cubot.exceptions.BadServerResponceException;
import com.goit.cubot.exceptions.NetworkProblemException;
import com.goit.cubot.exceptions.UnsupportedCurrencyException;
import com.goit.cubot.Currency;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class PrivatBankService implements Bank {
    //https://api.privatbank.ua/#p24/exchange
    private static final String URL = "https://api.privatbank.ua/p24api/pubinfo?exchange&json&coursid=11";

    public CurrencyInfo getCurrencyByCode(Currency valute) {
        List<CurrencyInfo> exchangeList = getCurrencyList();

        Optional<CurrencyInfo> exchange = exchangeList.stream()
                .filter(ex -> ex.getCode().equalsIgnoreCase(valute.name()))
                .findFirst();

        return exchange.orElseThrow(() -> new UnsupportedCurrencyException("Don`t supported currency for PrivatBank: "
                + valute.name(), valute.name()));
    }

    @Override
    public List<CurrencyInfo> getCurrencyList() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL))
                .GET()
                .build();
        HttpResponse<String> response;
        try {
            response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new NetworkProblemException();
        }
        if (response.statusCode() == 200) {
            List<Exchange> exchanges = new Gson().fromJson(response.body(), new TypeToken<List<Exchange>>() {
            }.getType());

            return exchanges.stream().map(s -> (CurrencyInfo) s).collect(Collectors.toList());
        } else {
            throw new BadServerResponceException("Error for getting data from: " + System.lineSeparator()
                    + response.uri()
                    , String.valueOf(response.statusCode()));
        }

        @Override
        public String toString(){
            return "Privat Bank";
        }


    }

        @Override
        public String toString(){
            return "Privat Bank";
        }

}

