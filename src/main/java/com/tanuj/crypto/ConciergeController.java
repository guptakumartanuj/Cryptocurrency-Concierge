package com.tanuj.crypto;

import java.util.Arrays;
import java.util.Map;

import javax.ws.rs.GET;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

@Controller
public class ConciergeController {

    // inject via application.properties
    @Value("${welcome.message:test}")
    private String message = "Hello World";

    @RequestMapping("/")
    @GET
    public String welcome(Map<String, Object> model) {
        model.put("message", this.message);
        // final String uri = "https://cex.io/api/tickers/USD/EUR/RUB/BTC";
        //
        // RestTemplate restTemplate = new RestTemplate();
        // String result = restTemplate.getForObject(uri, String.class);
        //
        // System.out.println(result);
        final String koinexUri = "https://koinex.in/api/ticker";
        RestTemplate restTemplate = new RestTemplate();
        String koinex = restTemplate.getForObject(koinexUri, String.class);
        System.out.println(koinex);

        JSONObject koinexJson = new JSONObject(koinex);
        final String cexUri = "https://cex.io/api/ticker/ETH/USD";
        //
        // String cex = restTemplate.getForObject(cexUri, String.class);
        // System.out.println(cex);

        RestTemplate rt = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.add("user-agent",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");
        HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
        ResponseEntity<String> res = rt.exchange(cexUri, HttpMethod.GET, entity, String.class);
        System.out.println(res.getBody());

        JSONObject jsonChildObject = (JSONObject) koinexJson.get("prices");

        double finalKoinexEthr = Double.parseDouble(jsonChildObject.get("ETH").toString());
        System.out.println("Koinex ETH ::" + finalKoinexEthr);

        JSONObject cexJson = new JSONObject(res.getBody());
        System.out.println("CEX ETH :: " + cexJson.get("last"));

        final String exchangeRate = "http://api.fixer.io/latest?base=USD";
        RestTemplate restTemplate1 = new RestTemplate();
        String rate = restTemplate1.getForObject(exchangeRate, String.class);
        JSONObject rateJson = new JSONObject(rate);
        JSONObject rates = (JSONObject) rateJson.get("rates");
        System.out.println(rates.get("INR"));
        double ethrate = Double.parseDouble(cexJson.get("last").toString()) * Double.parseDouble(rates.get("INR").toString());
        System.out.println("CEX ETH in INR:: " + ethrate);
        double finalCexEthr = ethrate + getPercentage(7, ethrate) + getPercentage(.01, ethrate);
        finalCexEthr += getPercentage(8, finalCexEthr);
        finalCexEthr += 5 * (Double.parseDouble(rates.get("INR").toString()));
        System.out.println("CEX ETH in INR after deduction" + (finalCexEthr));

        if (finalKoinexEthr > finalCexEthr) {
            System.out.println("Profit is " + (finalKoinexEthr - finalCexEthr));
            message = "Profit :: " + Double.toString(finalKoinexEthr - finalCexEthr);
        } else {
            System.out.println("Profit is " + (finalCexEthr - finalKoinexEthr));
            message = "Loss :: " + Double.toString((finalCexEthr - finalKoinexEthr));
        }

        model.put("message", this.message);
        return "welcome";
    }

    public static double getPercentage(double n, double total) {
        double proportion = ((double) n) * ((double) total);
        return proportion / 100;
    }

}