package com.esgsubstitutionplanapp.connection;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class ConnectionClient {

    private static final HttpClient client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NEVER).build();
    private static final String ID_FORM = "login-form";

    private final String url;
    private final String user;
    private final String password;
    private final boolean logOutput;

    private String sessionCookieValue;
    private String token;

    public ConnectionClient(String url, String user, String password, boolean logOutput){
        this.url = url;
        this.user = user;
        this.password = password;
        this.logOutput = logOutput;
    }

    /**
     * connects to given URL, logs in using provided credentials and returns HTML
     * @return full and untouched HTML of the requested page
     * @throws IOException see Java 11 HttpClient.send
     * @throws InterruptedException see Java 11 HttpClient.send
     */
    public String getHtml() throws Exception{
        setSessionValues();
        login();
        return getContent();
    }

    private void setSessionValues() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/85.0.4183.102 Safari/537.36")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        StringBuilder stringBuilder = new StringBuilder();
        for(String s : response.headers().allValues("set-cookie")){
            stringBuilder.append(";").append(s);
        }
        sessionCookieValue = stringBuilder.toString();

        String html = response.body();
        Document document = Jsoup.parse(html);
        Element form = document.getElementById(ID_FORM);
        for(Element element : form.child(0).children()){
            if(element.tagName().equals("input")){
                token =  element.attr("name");
                if(logOutput){
                    System.out.println("Token: " + token);
                    System.out.println("Cookie: " + sessionCookieValue);
                }
                return;
            }
        }
        throw new Exception("Kein Security-Token gefunden");
    }

    private void login() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .POST(buildPostParams(user, password))
                .setHeader("cookie", sessionCookieValue)
                .setHeader("content-type", "application/x-www-form-urlencoded")
                .setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/85.0.4183.102 Safari/537.36")
                .setHeader("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
                .setHeader("accept-language", "de-DE,de;q=0.9,en-US;q=0.8,en;q=0.7")
                .setHeader("cache-control", "max-age=0")
                .setHeader("sec-fetch-dest", "document")
                .setHeader("sec-fetch-mode", "navigate")
                .setHeader("sec-fetch-site", "?1")
                .setHeader("sec-fetch-user", "1")
                .setHeader("upgrade-insecure-requests", "1")
                .setHeader("referer", url)
                .setHeader("origin", "https://www.esg-landau.de")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        StringBuilder stringBuilder = new StringBuilder();
        for(String s : response.headers().allValues("set-cookie")){
            stringBuilder.append(";").append(s);
        }
        sessionCookieValue = stringBuilder.toString();
        if (logOutput) {
            System.out.println("StatusCode: " + response.statusCode());
            System.out.println("Headers: " + response.headers());
            System.out.println("Redirect: " + response.uri());
            System.out.println("New Cookie: " + sessionCookieValue);
        }
    }

    private HttpRequest.BodyPublisher buildPostParams(String user, String pw) {
        Map<String, String> data = new HashMap<>();
        data.put("username", user);
        data.put("password", pw);
        data.put(token, "1");
        data.put("option", "com_users");
        data.put("task", "user.login");
        data.put("Submit", "");
        data.put("return", "aHR0cHM6Ly93d3cuZXNnLWxhbmRhdS5kZS91bnRlcnN0dWV0enVuZy9pbmZvcm1hdGlvbmVuL3ZlcnRyZXR1bmdzcGxhbg==");

        var builder = new StringBuilder();
        for (Map.Entry<String, String> entry : data.entrySet()) {
            if (builder.length() > 0) {
                builder.append("&");
            }
            builder.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8));
            builder.append("=");
            builder.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
        }
        String params = builder.toString();
        if(logOutput){
            System.out.println("Parameter " + params);
        }
        return HttpRequest.BodyPublishers.ofString(params);
    }

    private String getContent() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .setHeader("cookie", sessionCookieValue)
                .setHeader("referer", url)
                .setHeader("sec-fetch-dest", "document")
                .setHeader("sec-fetch-mode", "navigate")
                .setHeader("sec-fetch-site", "?1")
                .setHeader("sec-fetch-user", "1")
                .setHeader("upgrade-insecure-requests", "1")
                .setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/85.0.4183.102 Safari/537.36")
                .setHeader("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
                .setHeader("accept-language", "de-DE,de;q=0.9,en-US;q=0.8,en;q=0.7")
                .setHeader("cache-control", "max-age=0")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (logOutput) {
            System.out.println("StatusCode: " + response.statusCode());
            System.out.println("Headers: " + response.headers());
            System.out.println("Redirect: " + response.uri());
            System.out.println("Html: " + response.body().length());
        }
        return response.body();
    }
}
