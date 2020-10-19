package com.esgsubstitutionplanapp.connection;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class ConnectionClient {

    private static final OkHttpClient client = new OkHttpClient()
            .newBuilder()
            .followRedirects(false)
            .followSslRedirects(false)
            .build();
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
     * @throws IOException see OkHttpClient
     */
    public String getHtml() throws IOException {
        setSessionValues();
        login();
        return getContent();
    }

    private void setSessionValues() throws IOException {
        Request request = new Request.Builder()
                .url(new URL(url))
                .get()
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/85.0.4183.102 Safari/537.36")
                .build();
        Response response = client.newCall(request).execute();

        StringBuilder stringBuilder = new StringBuilder();
        for(String s : response.headers("set-cookie")){
            stringBuilder.append(s).append(";");
        }
        sessionCookieValue = stringBuilder.toString();

        ResponseBody responseBody = response.body();
        String html = responseBody.string();
        responseBody.close();

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
    }

    private void login() throws IOException {
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(mediaType, buildPostParams(user, password));

        Request request = new Request.Builder()
                .url(new URL(url))
                .post(body)
                .addHeader("cookie", sessionCookieValue)
                .addHeader("content-type", "application/x-www-form-urlencoded")
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/85.0.4183.102 Safari/537.36")
                .addHeader("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
                .addHeader("accept-language", "de-DE,de;q=0.9,en-US;q=0.8,en;q=0.7")
                .addHeader("cache-control", "max-age=0")
                .addHeader("sec-fetch-dest", "document")
                .addHeader("sec-fetch-mode", "navigate")
                .addHeader("sec-fetch-site", "?1")
                .addHeader("sec-fetch-user", "1")
                .addHeader("upgrade-insecure-requests", "1")
                .addHeader("referer", url)
                .addHeader("origin", "https://www.esg-landau.de")
                .build();
        Response response = client.newCall(request).execute();

        StringBuilder stringBuilder = new StringBuilder();
        for(String s : response.headers("set-cookie")){
            stringBuilder.append(s).append(";");
        }
        sessionCookieValue = stringBuilder.toString();
        if (logOutput) {
            System.out.println("StatusCode: " + response.code());
            System.out.println("Headers: " + response.headers());
            System.out.println("New Cookie: " + sessionCookieValue);
        }
    }

    private String buildPostParams(String user, String pw) throws UnsupportedEncodingException {
        Map<String, String> data = new HashMap<>();
        data.put("username", user);
        data.put("password", pw);
        data.put(token, "1");
        data.put("option", "com_users");
        data.put("task", "user.login");
        data.put("Submit", "");
        data.put("return", "aHR0cHM6Ly93d3cuZXNnLWxhbmRhdS5kZS91bnRlcnN0dWV0enVuZy9pbmZvcm1hdGlvbmVuL3ZlcnRyZXR1bmdzcGxhbg==");

        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, String> entry : data.entrySet()) {
            if (builder.length() > 0) {
                builder.append("&");
            }
            builder.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            builder.append("=");
            builder.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }
        String params = builder.toString();
        if(logOutput){
            System.out.println("Parameter " + params);
        }
        return params;
    }

    private String getContent() throws IOException {
        Request request = new Request.Builder()
                .url(new URL(url))
                .get()
                .addHeader("cookie", sessionCookieValue)
                .addHeader("referer", url)
                .addHeader("sec-fetch-dest", "document")
                .addHeader("sec-fetch-mode", "navigate")
                .addHeader("sec-fetch-site", "?1")
                .addHeader("sec-fetch-user", "1")
                .addHeader("upgrade-insecure-requests", "1")
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/85.0.4183.102 Safari/537.36")
                .addHeader("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
                .addHeader("accept-language", "de-DE,de;q=0.9,en-US;q=0.8,en;q=0.7")
                .addHeader("cache-control", "max-age=0")
                .build();
        Response response = client.newCall(request).execute();
        ResponseBody responseBody = response.body();
        String html = responseBody.string();
        responseBody.close();
        if (logOutput) {
            System.out.println("StatusCode: " + response.code());
            System.out.println("Headers: " + response.headers());
            System.out.println("Html: " + html.length());
        }
        return html;
    }
}
