package org.example;


import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Optional;

public class Request {
    String typeRquest;
    String url;
    String body;

    Request(String typeRquest, String url, String body) {
        this.typeRquest = typeRquest;
        this.url = url;
        this.body = body;
    }

    public Optional<NameValuePair> getQueryParam(String name) throws URISyntaxException,
            UnsupportedEncodingException {
        var params = getQueryParams();
        Optional<NameValuePair> param = params.stream()
                .filter(e -> e.getName().equals(name))
                .findFirst();
        return param;
    }

    public List<NameValuePair> getQueryParams() throws URISyntaxException,
            UnsupportedEncodingException {
        return URLEncodedUtils.parse(new URI(this.url), Charset.forName("UTF-8"));
    }

    public String getUrl() throws UnsupportedEncodingException, URISyntaxException {
        var params = getQueryParams();
        if (params.size() == 0)
            return url;
        String tempUrl = new URI(this.url).getPath();
        return tempUrl;
    }
}
