package webserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;
import util.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/*
HttpRequest의 책임은 클라이언트 요청 데이터를 읽은 후,
각 데이터를 사용하기 좋은 형태로 분리하는 역할만 한다.
이렇게 분리한 데이터를 사용하는 부분은 RequestHandler가 가지도록 한다.
-> 즉, 데이터를 파싱하는 작업(HttpRequest)와 사용하는 부분(RequestHandler)을 분리하는 것이다.
 */
public class HttpRequest {
    private static final Logger log = LoggerFactory.getLogger(HttpRequest.class);

    private String method;
    private String path;
    private Map<String, String> headers = new HashMap<>();
    private Map<String, String> params = new HashMap<>();

    /*
    HttpRequest는 InputStream을 생성자의 인자로 받은 후,
    InputStream에 담겨있는 데이터를 필요한 형태로 분리한 후 객체의 필드에 저장하는 역할만 한다.
    이렇게 저장한 값에 접근할 수 있도록 4가지 종류의 get() 메소드를 제공할 뿐이다.
     */
    public HttpRequest(InputStream in) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            String line = br.readLine();
            if (line == null) {
                return;
            }

            processRequestLine(line);

            line = br.readLine();
            while (!line.equals("")) {
                log.debug("header : {}", line);
                String[] tokens = line.split(":");
                headers.put(tokens[0].trim(), tokens[1].trim());
                line = br.readLine();
            }

            if ("POST".equals(method)) {
                String body = IOUtils.readData(br, Integer.parseInt(headers.get("Content-Length")));
                params = HttpRequestUtils.parseQueryString(body);
            }
        } catch (IOException io) {
            log.error(io.getMessage());
        }
    }

    private void processRequestLine(String requestLine) {
        log.debug("request Line : {}", requestLine);
        String[] tokens = requestLine.split(" ");
        method = tokens[0];

        if ("POST".equals(method)) {
            path = tokens[1];
            return;
        }

        int index = tokens[1].indexOf("?");
        if (index == -1) {
            path = tokens[1];
        } else {
            path = tokens[1].substring(0, index);
            params = HttpRequestUtils.parseQueryString(
                    tokens[1].substring(index+1));
        }
    }
    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getHeader(String name) {
        return headers.get(name);
    }

    public String getParameter(String name) {
        return params.get(name);
    }


}
