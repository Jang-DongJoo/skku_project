package project.recommend.content.helper;

import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.AbstractView;

import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WebHelper {
	/** 기본 인코딩 타입 설정 */
	private String encType;

	/** 쿠키에서 사용할 도메인 */
	// 상용화시에 수정해야 함.
	// 사이트 주소가 "http://www.itpaper.co.kr"인 경우 -> ".itpaper.co.kr"
	private String domain;

	/** JSP의 request 내장 객체 */
	// --> import javax.servlet.http.HttpServletRequest;
	private HttpServletRequest request;

	/** JSP의 response 내장 객체 */
	// --> import javax.servlet.http.HttpServletResponse;
	private HttpServletResponse response;

	/** JSP의 session 내장 객체 */
	// --> import javax.servlet.http.HttpSession;
	private HttpSession session;

	/** Multipart 전송시 텍스트 데이터를 저장하기 위한 컬렉션 */
	private Map<String, String> paramMap;

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

	public Map<String, String> getParamMap() {
		return paramMap;
	}

	public void setParamMap(Map<String, String> paramMap) {
		this.paramMap = paramMap;
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
	public ModelAndView redirect(String url, String msg) {

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
		// try {
		// 브라우저에 결과값을 출력하기 위한 out객체를 생성한다. (예외처리 필요.)
		// PrintWriter out = this.response.getWriter();

		// 준비된 html 태그를 출력한다.
		// out.print(html);
		// } catch (IOException e) {
		// e.printStackTrace();
		// }

		return this.virtualView(html);
	}

	/**
	 * 파라미터를 전달받아서 리턴한다.
	 *
	 * @param fieldName    - 파라미터 이름
	 * @param defaultValue - 값이 없을 경우 사용될 기본값
	 * @return String
	 */
	@Deprecated
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
	@Deprecated
	public int getInt(String fieldName, int defaultValue) {
		// 리턴을 위한 값을 두 번째 파라미터(기본값)로 설정해 둔다.
		int result = defaultValue;

		// getString()메서드를 통해서 파라미터를 문자열 형태로 받는다.
		// 파라미터가 존재하지 않는다면 두 번째로 전달한 값이 리턴된다.
		String param = this.getString(fieldName, null);

		// 숫자형인 경우 숫자값으로 변환한다.
		try {
			result = Integer.parseInt(param);
		} catch (Exception e) {
		}

		return result;
	}

	/**
	 * 배열 형태의 파라미터를 리턴한다. 체크박스 전용 기능
	 *
	 * @param fieldName    - 파라미터 이름
	 * @param defaultValue - 값이 없거나 배열의 길이가 0인 경우 사용될 기본값
	 * @return String[]
	 */
	@Deprecated
	public String[] getStringArray(String fieldName, String[] defaultValue) {
		// 리턴을 위한 값을 두 번째 파라미터(기본값)로 설정해 둔다.
		String[] result = defaultValue;
		// 배열 형태의 GET,POST 파라미터를 받는다.
		String[] param = this.request.getParameterValues(fieldName);

		if (param != null) { // 수신된 파라미터가 존재한다면?
			if (param.length > 0) { // 배열의 길이가 0보다 크다면?
				result = param; // 리턴을 위해서 준비한 변수에 수신한 값을 복사한다.
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
	@Deprecated
	public String getString(String fieldName) {
		return this.getString(fieldName, null);
	}

	/**
	 * 파라미터를 전달받아서 int로 형변환 하여 리턴한다. 값이 없을 경우 0을 리턴한다.
	 *
	 * @param fieldName - 파라미터 이름
	 * @return int
	 */
	@Deprecated
	public int getInt(String fieldName) {
		return this.getInt(fieldName, 0);
	}

	/**
	 * 배열 형태의 파라미터를 리턴한다. 값이 없을 경우 null을 리턴한다.
	 *
	 * @param fieldName - 파라미터 이름
	 * @return String[]
	 */
	@Deprecated
	public String[] getStringArray(String fieldName) {
		return this.getStringArray(fieldName, null);
	}

	/**
	 * 세션값을 저장한다.
	 *
	 * @param key   - 세션이름
	 * @param value - 저장할 데이터
	 */
	public void setSession(String key, Object value) {
		// 세션의 기록 정보를 로그로 남긴다.
		log.debug(String.format("(s) <-- %s = %s", key, value.toString()));

		// 세션을 저장한다.
		this.session.setAttribute(key, value);
	}

	/**
	 * 세션값을 조회한다.
	 *
	 * @param key          - 조회할 세션의 이름
	 * @param defaultValue - 값이 없을 경우 대체할 기본값
	 * @return Object이므로 명시적 형변환 필요함
	 */
	public Object getSession(String key, Object defaultValue) {
		// 세션값 가져오기
		Object value = this.session.getAttribute(key);

		if (value == null) {
			value = defaultValue;
		}

		// 조회된 세션 정보를 로그로 남긴다.
		if (value == null) {
			log.debug(String.format("(s) --> %s = null", key));
		} else {
			log.debug(String.format("(s) --> %s = %s", key, value.toString()));
		}

		return value;
	}

	/**
	 * 세션값을 조회한다. 값이 없을 경우에 대한 기본값을 null로 설정
	 *
	 * @param key - 세션 이름
	 * @return Object이므로 명시적 형변환 필요함
	 */
	public Object getSession(String key) {
		return this.getSession(key, null);
	}

	/**
	 * 특정 세션값을 삭제한다.
	 *
	 * @param key - 세션 이름
	 */
	public void removeSession(String key) {
		log.debug(String.format("(s) <-- %s delete", key));
		this.session.removeAttribute(key);
	}

	/**
	 * 현재 사용자에 대한 모든 세션값을 일괄 삭제한다.
	 */
	public void removeAllSession() {
		log.debug(String.format("(s) <-- delete all"));
		this.session.invalidate();
	}

	/**
	 * 쿠키값을 저장한다.
	 *
	 * @param key     - 쿠키이름
	 * @param value   - 값
	 * @param timeout - 설정시간. 즉시 삭제할 경우 0, 브라우저를 닫기 전까지 유효할 경우 -1, 그 외에는 지정된 시간동안
	 *                유지.
	 */
	public void setCookie(String key, String value, int timeout) {
		/** 쿠키의 기록 정보를 로그로 남긴다. */
		if (timeout == 0) {
			log.debug(String.format("(c) <-- %s delete", key));
		} else {
			log.debug(String.format("(c) <-- %s = %s (%dsec)", key, value, timeout));
		}

		/** 전달된 값을 URLEncoding 처리 한다. */
		if (value != null) {
			try {
				// import java.net.URLEncoder
				value = URLEncoder.encode(value, this.encType);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}

		/** 쿠키 객체 생성 및 기본 설정 */
		Cookie cookie = new Cookie(key, value);
		cookie.setPath("/");
		cookie.setDomain(this.domain);

		/** 유효시간 설정 */
		// 시간값이 0보다 작은 경우는 이 메서드를 설정하지 않도록 한다. (브라우저를 닫으면 삭제)
		// 0으로 설정할 경우 setMaxAge(0)이라고 설정되므로 즉시 삭제된다.
		if (timeout > -1) {
			cookie.setMaxAge(timeout);
		}

		/** 쿠키 저장하기 */
		this.response.addCookie(cookie);
	}

	/**
	 * 쿠키값을 조회한다.
	 *
	 * @param key          - 쿠키이름
	 * @param defaultValue - 값이 없을 경우 사용될 기본값
	 * @return String
	 */
	public String getCookie(String key, String defaultValue) {
		/** 리턴할 값을 설정 */
		String result = defaultValue;

		/** 쿠키 배열 가져오기 */
		// import javax.servlet.http.Cookie
		Cookie[] cookies = this.request.getCookies();

		/** 쿠키가 있다면? 추출된 배열의 항목 수 만큼 반복하면서 원하는 이름의 값을 검색 */
		if (cookies != null) {
			for (int i = 0; i < cookies.length; i++) {
				String cookieName = cookies[i].getName(); // 쿠키의 이름 얻기

				if (cookieName.equals(key)) { // 원하는 이름이라면?
					result = cookies[i].getValue(); // 값을 추출 --> 이 값이 리턴된다.
					try { // 인코딩 된 값이므로 디코딩 처리 수행
							// import java.net.URLDecoder
						result = URLDecoder.decode(result, this.encType);
					} catch (UnsupportedEncodingException e) {
					}

					// 배열 안에서 원하는 값을 찾았으므로 나머지 원소는 탐색할 필요가 없기 때문에 반복 중단.
					break;
				} // end if
			} // end for
		} // end if

		/** 조회된 쿠키 정보를 로그로 남긴다. */
		if (result == null) {
			log.debug(String.format("(c) --> %s = null", key));
		} else {
			log.debug(String.format("(c) --> %s = %s", key, result));
		}

		return result;
	}

	/**
	 * 쿠키값을 조회한다. 값이 없을 경우 Null을 리턴한다.
	 *
	 * @param key - 쿠키이름
	 * @return String
	 */
	public String getCookie(String key) {
		return this.getCookie(key, null);
	}

	/**
	 * 지정된 키에 대한 쿠키를 삭제한다.
	 *
	 * @param key
	 */
	public void removeCookie(String key) {
		this.setCookie(key, null, 0);
	}

	/**
	 * JSON 형식으로 결과 메시지를 리턴한다.
	 *
	 * @param statusCode HTTP 결과코드 (200, 404, 500 등)
	 * @param rt         결과메시지 (성공일 경우 OK, 그 밖의 경우 에러메시지)
	 * @param data       JSON으로 변환할 데이터 컬렉션
	 *
	 * @return String
	 */
	public String getJsonData(int statusCode, String rt, Map<String, Object> data) {
		/** 1) JSON 형식 출력을 위한 HTTP 헤더 설정 */
		// JSON 형식임을 명시함
		this.response.setContentType("application/json");

		// HTTP 상태 코드 설정 (200, 404, 500 등)
		this.response.setStatus(statusCode);

		// CrossDomain에 의한 접근 허용 (보안에 좋지 않기 때문에 이 옵션을 허용할 경우 인증키 등의 보안장치가 필요함)
		// 여기서는 실습을 위해 허용함
		this.response.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, DELETE");
		this.response.setHeader("Access-Control-Max-Age", "3600");
		this.response.setHeader("Access-Control-Allow-Headers", "x-requested-with");
		this.response.setHeader("Access-Control-Allow-Origin", "*");

		/** 2) 문자열 생성하기 */
		// JSON 생성일시를 위한 현재 시각 문자열 만들기
		Calendar c = Calendar.getInstance();
		String pubDate = String.format("%04d-%02d-%02d %02d:%02d:%02d", c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1,
				c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE),
				c.get(Calendar.SECOND));

		// JSON 구성을 위한 컬렉션 객체 생성
		Map<String, Object> map = new HashMap<String, Object>();

		// 결과코드 추가
		map.put("rt", rt);

		// 생성일시 추가
		map.put("pubDate", pubDate);

		// data가 전달되었다면 map에 병합한다.
		if (data != null) {
			map.putAll(data);
		}

		// 문자열로 변환 후 리턴함
		Gson gson = new Gson();
		return gson.toJson(map);
	}

	/**
	 * JSON 형식으로 결과 메시지를 리턴하기 위한 메서드 오버로드 HTTP 상태코드는 200으로 설정하고, 결과 메시지는 "OK"라는 값을
	 * 설정하여 getJsonData(int statusCode, String rt, Map<String, Object> data) 를 호출한다.
	 *
	 * JSON으로 표현하고자 하는 컬렉션이 있을 때 사용하는 가장 일반적인 메서드이다.
	 *
	 * @param data JSON으로 변환할 데이터 컬렉션
	 *
	 * @return String
	 */
	public String getJsonData(Map<String, Object> data) {
		return this.getJsonData(200, "OK", data);
	}

	/**
	 * JSON 형식으로 결과 메시지를 리턴하기 위한 메서드 오버로드 HTTP 상태코드는 200으로 설정하고, 결과 메시지는 "OK"라는 값을
	 * 설정하여 getJsonData(int statusCode, String rt, Map<String, Object> data) 를 호출한다.
	 *
	 * JSON으로 표현하고자 하는 컬렉션이 없지만 처리 결과는 성공인 경우 사용한다. ex) 데이터 입력, 수정, 삭제 등을 수행하고 특별히
	 * 조회 결과를 반환하지 않을 경우
	 *
	 * @return String
	 */
	public String getJsonData() {
		return this.getJsonData(200, "OK", null);
	}

	/**
	 * JSON 형식으로 에러 메시지를 리턴한다. HTTP 상태코드는 500으로 설정하고, 결과 메시지는 파라미터로 전달되는 값을 설정하여
	 * JSON 문자열을 리턴한다.
	 *
	 * Service 구현체에서 예외 발생시 전달하는 에러 메시지 등을 JSON으로 표현할 때 사용.
	 *
	 * @param rt
	 *
	 * @return String
	 */
	public String getJsonError(String rt) {
		return this.getJsonData(500, rt, null);
	}

	/**
	 * JSON 형식으로 잘못된 접근에 대한 경고 메시지를 리턴한다. HTTP 상태코드는 400으로 설정하고, 결과 메시지는 파라미터로 전달되는
	 * 값을 설정하여 JSON 문자열을 리턴한다.
	 *
	 * 파라미터 점검 과정에서 유효성 검증에 통과하지 못한 경우 사용.
	 *
	 * @param rt
	 *
	 * @return String
	 */
	public String getJsonWarning(String rt) {
		return this.getJsonData(400, rt, null);
	}

	/**
	 * 파라미터로 받은 내용을 가상의 View로 생성후 리턴한다. 브라우저에게 전달할 HTML,CSS,JS 조합을 출력하기 위해 사용한다.
	 * 
	 * @param body - 브라우저에게 전달할 HTML,CSS,JS 조합 문자열
	 * @return ModelAndView
	 */
	public ModelAndView virtualView(final String body) {
		/** 가상의 View를 익명 클래스 방식으로 생성하여 리턴 */
		// --> import org.springframework.web.servlet.View;
		// --> import org.springframework.web.servlet.view.AbstractView;
		View view = new AbstractView() {
			@Override
			protected void renderMergedOutputModel(Map<String, Object> map, HttpServletRequest request,
					HttpServletResponse response) throws Exception {
				PrintWriter out = response.getWriter();
				out.println(body);
				out.flush();
			}
		};

		// 가상의 뷰를 리턴한다.
		return new ModelAndView(view);
	}
}
