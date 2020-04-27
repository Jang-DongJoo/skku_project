package project.content.recommend.helper;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WebHelper {
    /** 기본 인코딩 타입 설정 */
    private String encType = "UTF-8";

    /** 쿠키에서 사용할 도메인 */
    private String domain = "localhost";

    /** JSP의 request 내장 객체 */
    private HttpServletRequest request;

    /** JSP의 response 내장 객체 */
    private HttpServletResponse response;

    /** JSP의 session 내장 객체 */
    private HttpSession session;

    // ----------- 싱글톤 객체 생성 시작 ----------
    private static WebHelper current;

    // JSP의 내장 객체는 일반 JAVA 클래스가 생성할 수 없기 때문에
    // JSP 페이지로부터 request와 response 객체를 전달받아야 한다.
    // --> import javax.servlet.http.HttpServletRequest;
    // --> import javax.servlet.http.HttpServletResponse;
    public static WebHelper getInstance(HttpServletRequest request, HttpServletResponse response) {
        if (current == null) {
            current = new WebHelper();
        }

        // JSP 내장객체를 연결하기 위한 메서드를 호출한다.
        current.init(request, response);
        return current;
    }

    public static void freeInstance() {
        current = null;
    }

    private WebHelper() {
        super();
    }

    public String getEncType() {
        return encType;
    }

    public void setEncType(String encType) {
        this.encType = encType;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    public void setResponse(HttpServletResponse response) {
        this.response = response;
    }

    public HttpSession getSession() {
        return session;
    }

    public void setSession(HttpSession session) {
        this.session = session;
    }

    /**
     * JSP의 주요 내장객체를 멤버변수에 연결한다.
     *
     * @param request
     * @param response
     */
    public void init(HttpServletRequest request, HttpServletResponse response) {
        this.request = request; // JSP 내장객체 참조하기
        this.response = response; // JSP 내장객체 참조하기

        this.session = this.request.getSession(); // request 객체를 사용하여 세션 생성하기

        String methodName = request.getMethod(); // GET방식인지, POST방식인지 조회한다.
        String url = request.getRequestURL().toString(); // 현재 URL을 획득한다.
        String queryString = request.getQueryString(); // URL에서 "?"이후의 GET파라미터 문자열을 모두 가져온다.

        if (queryString != null) { // 가져온 값이 있다면 URL과 결합하여 완전한 URL을 구성한다.
            url = url + "?" + queryString;
        }

        log.debug(String.format("[%s] %s", methodName, url)); // 획득한 정보를 로그로 표시한다.

        /** 내장객체 초기화 -> utf-8 설정 */
        // --> import java.io.UnsupportedEncodingException;
        try {
            this.request.setCharacterEncoding(this.encType);
            this.response.setCharacterEncoding(this.encType);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * 메시지 표시 후, 페이지를 지정된 곳으로 이동한다.
     *
     * @param url - 이동할 페이지의 URL, Null일 경우 이전페이지로 이동
     * @param msg - 화면에 표시할 메시지. Null일 경우 표시 안함
     */
    public void redirect(String url, String msg) {

        // 획득한 정보를 로그로 표시한다.
        log.debug(String.format(" --> [redirect] %s >> %s", url, msg));

        // 가상의 View로 만들기 위한 HTML 태그 구성
        String html = "<!doctype html>";
        html += "<html>";
        html += "<head>";
        html += "<meta charset='" + this.encType + "'>";

        // 메시지 표시
        if (msg != null) {
            html += "<script type='text/javascript'>alert('" + msg + "');</script>";
        }

        // 페이지 이동
        if (url != null) {
            html += "<meta http-equiv='refresh' content='0; url=" + url + "' />";
        } else {
            html += "<script type='text/javascript'>history.back();</script>";
        }

        html += "</head>";
        html += "<body></body>";
        html += "</html>";

        // 구성된 HTML을 출력한다.
        try {
            // 브라우저에 결과값을 출력하기 위한 out객체를 생성한다. (예외처리 필요.)
            PrintWriter out = this.response.getWriter();

            // 준비된 html 태그를 출력한다.
            out.print(html);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 파라미터를 전달받아서 리턴한다.
     *
     * @param fieldName    - 파라미터 이름
     * @param defaultValue - 값이 없을 경우 사용될 기본값
     * @return String
     */
    public String getString(String fieldName, String defaultValue) {
        // 리턴을 위한 값을 두 번째 파라미터(기본값)로 설정해 둔다.
        String result = defaultValue;

        // GET,POST 파라미터를 받는다.
        String param = this.request.getParameter(fieldName);

        if (param != null) { // 값이 null이 아니라면?
            param = param.trim(); // 앞뒤 공백을 제거한다.
            if (!param.equals("")) { // 공백제거 결과가 빈 문자열이 아니라면?
                result = param; // 리턴을 위해서 준비한 변수에 수신한 값을 복사한다.
            }
        }

        // 파라미터 내용을 로그로 기록한다.
        log.debug(String.format("(p) <-- %s = %s", fieldName, result));

        // 값을 리턴. param값이 존재하지 않을 경우 미리 준비한 기본값이 그대로 리턴된다.
        return result;
    }

    /**
     * 파라미터를 전달받아서 int로 형변환 하여 리턴한다.
     *
     * @param fieldName    - 파라미터 이름
     * @param defaultValue - 값이 없을 경우 사용될 기본값
     * @return int
     */
    public int getInt(String fieldName, int defaultValue) {
        // 리턴을 위한 값을 두 번째 파라미터(기본값)로 설정해 둔다.
        int result = defaultValue;

        // getString()메서드를 통해서 파라미터를 문자열 형태로 받는다.
        // 파라미터가 존재하지 않는다면 두 번째로 전달한 값이 리턴된다.
        String param = this.getString(fieldName, null);

        // 숫자형인 경우 숫자값으로 변환한다.
        try {
            result = Integer.parseInt(param);
        } catch (Exception e) { }

        return result;
    }

    /**
     * 배열 형태의 파라미터를 리턴한다. 체크박스 전용 기능
     *
     * @param fieldName    - 파라미터 이름
     * @param defaultValue - 값이 없거나 배열의 길이가 0인 경우 사용될 기본값
     * @return String[]
     */
    public String[] getStringArray(String fieldName, String[] defaultValue) {
        // 리턴을 위한 값을 두 번째 파라미터(기본값)로 설정해 둔다.
        String[] result = defaultValue;
        // 배열 형태의 GET,POST 파라미터를 받는다.
        String[] param = this.request.getParameterValues(fieldName);

        if (param != null) {        // 수신된 파라미터가 존재한다면?
            if (param.length > 0) { // 배열의 길이가 0보다 크다면?
                result = param;     // 리턴을 위해서 준비한 변수에 수신한 값을 복사한다.
            }
        }

        // 수신한 값을 로그로 기록한다.
        if (result != null) {
            log.debug(String.format("(p) <-- %s = %s", fieldName, String.join(", ", result)));
        } else {
            log.debug(String.format("(p) <-- %s = null", fieldName));
        }

        // 값을 리턴. param값이 존재하지 않을 경우 미리 준비한 기본값이 그대로 리턴된다.
        return result;
    }

    /**
     * 파라미터를 전달받아서 리턴한다. 값이 없을 경우 null을 리턴한다.
     *
     * @param fieldName - 파라미터 이름
     * @return String
     */
    public String getString(String fieldName) {
        return this.getString(fieldName, null);
    }

    /**
     * 파라미터를 전달받아서 int로 형변환 하여 리턴한다. 값이 없을 경우 0을 리턴한다.
     *
     * @param fieldName - 파라미터 이름
     * @return int
     */
    public int getInt(String fieldName) {
        return this.getInt(fieldName, 0);
    }

    /**
     * 배열 형태의 파라미터를 리턴한다. 값이 없을 경우 null을 리턴한다.
     *
     * @param fieldName - 파라미터 이름
     * @return String[]
     */
    public String[] getStringArray(String fieldName) {
        return this.getStringArray(fieldName, null);
    }

}
