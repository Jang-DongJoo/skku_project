<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page trimDirectiveWhitespaces="true"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.HashMap"%>
<%@ page import="retrofit2.Call"%>
<%@ page import="retrofit2.Retrofit"%>
<%@ page import="project.content.recommend.helper.*"%>
<%@ page import="project.content.recommend.model.SearchMovieList"%>
<%@ page
	import="project.content.recommend.model.SearchMovieList.MovieListResult"%>
<%@ page
	import="project.content.recommend.model.SearchMovieList.MovieListResult.MovieList"%>
<%@ page import="project.content.recommend.service.*"%>
<%
	/** 1) 필요한 객체 생성 부분 */
	// Helper 객체 생성
	WebHelper webHelper = WebHelper.getInstance(request, response);
	RetrofitHelper retrofitHelper = RetrofitHelper.getInstance();
	// Retrofit 객체 생성
	Retrofit retrofit = retrofitHelper.getRetrofit(ApiKobisService.BASE_URL);
	// Service 객체를 생성한다. 구현체는 Retrofit이 자동으로 생성해 준다.
	ApiKobisService apiKobisService = retrofit.create(ApiKobisService.class);

	/** 2) 검색일 파라마터 처리 */
	// 검색할 영화제목
	String movieNm = webHelper.getString("movieNm");

	// 검색어가 없다면 alert창을 띄워준다.
	if (movieNm == null) {
		webHelper.redirect(null, "좋아하는 영화 제목을 알려주세요.");
	}

	/** 3) API 연동 */
	// 영화진흥원 OpenAPI를 통해 검색결과 받아오기
	Call<SearchMovieList> call = apiKobisService.getSearchMovieList(movieNm);

	// API 키를 잘못 설정한 경우등의 이유로 연동에 실패 할 수 있기 때문에 예외처리를 준비한다.
	SearchMovieList searchMovieList = null;
	try {
		searchMovieList = call.execute().body();
	} catch (Exception e) {
		e.printStackTrace();
	}

	List<MovieList> list = null;

	// 검색 결과가 있다면 list만 추출한다.
	if (searchMovieList != null) {
		list = searchMovieList.getMovieListResult().getMovieList();
	}

	/** 4) 관련 영화 검색 */
	int similarity = 0;
	// 영화의 국적,장르 추출
	String nation = "", genre = "", name = "";
	for (int i = 0; i < list.size(); i++) {
		name = list.get(i).getMovieNm();
		nation = list.get(i).getRepNationNm();
		genre = list.get(i).getRepGenreNm();
	}
	out.println(name + " / " + nation + " / " + genre);

	// 관련점수가 높은 영화들을 저장할 Map
	HashMap<String, Integer> map = new HashMap<String, Integer>();
	int curPage = 1;
	int itemPerPage = 10;
	for (int i = curPage; i <= 10; i++) {
		Call<SearchMovieList> callList = apiKobisService.getMovieList(i);
		SearchMovieList checkMovieList = null;
		try {
			checkMovieList = callList.execute().body();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		List<MovieList> movieList = null;

		// 검색 결과가 있다면 list만 추출한다.
		if (checkMovieList != null) {
			movieList = checkMovieList.getMovieListResult().getMovieList();
		}

		for (int j = 0; j < movieList.size(); j++) {
			if (movieList.get(j).getRepNationNm().equals(nation)) {
				similarity += 10;
				map.put(movieList.get(j).getMovieNm(), similarity);
			}
			if (movieList.get(j).getRepGenreNm().equals(genre)) {
				similarity += 20;
				map.put(movieList.get(j).getMovieNm(), similarity);
			}
			similarity = 0;
		}
	}
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Recommend System</title>
</head>
<body>
	<!-- 1) 검색폼 구성 -->
	<form name="form1" method="get"
		action="<%=request.getContextPath()%>/main.jsp">
		<label for="movieNm">영화제목: </label> <input type="text" id="movieNm"
			name="movieNm" value="<%=movieNm%>" /> <input type="submit"
			value="검색" />
	</form>

	<!-- 2) 검색 결과가 존재할 경우 목록을 표 형식으로 출력 -->
	<%
		if (map != null && map.size() > 0) {
	%>
	<hr />
	<table border="1">
		<thead>
			<tr>
				<th>Movie Name</th>
				<th>Similarity</th>
			</tr>
		</thead>
		<tbody>
			<!-- map에 담긴 내용을 반복문으로 출력 -->
			<%
				for (String key : map.keySet()) {
			%>
			<tr>
				<td><%=key%></td>
				<td><%=map.get(key)%></td>
			</tr>
			<%
				}
			%>
		</tbody>
	</table>
	<%
		}
	%>
</body>
</html>