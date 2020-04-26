<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page trimDirectiveWhitespaces="true"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.Map"%>
<%@ page import="java.util.HashMap"%>
<%@ page import="java.util.Iterator"%>
<%@ page import="retrofit2.Call"%>
<%@ page import="retrofit2.Retrofit"%>
<%@ page import="project.content.recommend.helper.*"%>
<%@ page import="project.content.recommend.service.impl.MovieSimilarity"%>
<%@ page import="project.content.recommend.model.SearchMovieInfo"%>
<%@ page
	import="project.content.recommend.model.SearchMovieInfo.MovieInfoResult.MovieInfo.*"%>
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
	String movieNm1 = webHelper.getString("movieNm1");
	String movieNm2 = webHelper.getString("movieNm2");
	String movieNm3 = webHelper.getString("movieNm3");

	// 검색어가 없다면 alert창을 띄워준다.
	if (movieNm1 == null || movieNm2 == null || movieNm3 == null) {
		webHelper.redirect(null, "좋아하는 영화 제목을 알려주세요.");
	}

	/** 3) API 연동 */
	// 영화진흥원 OpenAPI를 통해 검색결과 받아오기
	Call<SearchMovieList> call1 = apiKobisService.getSearchMovieList(movieNm1);
	Call<SearchMovieList> call2 = apiKobisService.getSearchMovieList(movieNm2);
	Call<SearchMovieList> call3 = apiKobisService.getSearchMovieList(movieNm3);

	// API 키를 잘못 설정한 경우등의 이유로 연동에 실패 할 수 있기 때문에 예외처리를 준비한다.
	SearchMovieList searchMovieList1 = null;
	SearchMovieList searchMovieList2 = null;
	SearchMovieList searchMovieList3 = null;
	try {
		searchMovieList1 = call1.execute().body();
		searchMovieList2 = call2.execute().body();
		searchMovieList3 = call3.execute().body();
	} catch (Exception e) {
		e.printStackTrace();
	}

	List<MovieList> list1 = null;
	List<MovieList> list2 = null;
	List<MovieList> list3 = null;

	// 검색 결과가 있다면 list만 추출한다.
	if (searchMovieList1 != null) {
		list1 = searchMovieList1.getMovieListResult().getMovieList();
	}
	if (searchMovieList2 != null) {
		list2 = searchMovieList2.getMovieListResult().getMovieList();
	}
	if (searchMovieList3 != null) {
		list3 = searchMovieList3.getMovieListResult().getMovieList();
	}

	/** 4) 관련 영화 검색 */
	int similarity = 0;
	MovieSimilarity movieSimilarity = new MovieSimilarity();

	// 영화의 국적,장르,코드 추출
	String nation1 = "", genre1 = "", name1 = "";
	String nation2 = "", genre2 = "", name2 = "";
	String nation3 = "", genre3 = "", name3 = "";
	int code1 = 0, code2 = 0, code3 = 0;
	for (int i = 0; i < 1; i++) {
		name1 = list1.get(i).getMovieNm();
		code1 = list1.get(i).getMovieCd();
		nation1 = list1.get(i).getRepNationNm();
		genre1 = list1.get(i).getRepGenreNm();
		name2 = list2.get(i).getMovieNm();
		code2 = list2.get(i).getMovieCd();
		nation2 = list2.get(i).getRepNationNm();
		genre2 = list2.get(i).getRepGenreNm();
		name3 = list3.get(i).getMovieNm();
		code3 = list3.get(i).getMovieCd();
		nation3 = list3.get(i).getRepNationNm();
		genre3 = list3.get(i).getRepGenreNm();
	}
	out.println(name1 + " / " + code1 + " / " + nation1 + " / " + genre1);
	out.println(name2 + " / " + code2 + " / " + nation2 + " / " + genre2);
	out.println(name3 + " / " + code3 + " / " + nation3 + " / " + genre3);

	// 장르,감독,배우 리스트 추출
	Call<SearchMovieInfo> callInfo1 = apiKobisService.getSearchMovieInfo(code1);
	Call<SearchMovieInfo> callInfo2 = apiKobisService.getSearchMovieInfo(code2);
	Call<SearchMovieInfo> callInfo3 = apiKobisService.getSearchMovieInfo(code3);
	SearchMovieInfo movieInfo1 = null;
	SearchMovieInfo movieInfo2 = null;
	SearchMovieInfo movieInfo3 = null;
	try {
		movieInfo1 = callInfo1.execute().body();
		movieInfo2 = callInfo2.execute().body();
		movieInfo3 = callInfo3.execute().body();
	} catch (Exception e) {
		e.printStackTrace();
	}

	List<Nations> nations1 = movieInfo1.getMovieInfoResult().getMovieInfo().getNations();
	List<Genres> genres1 = movieInfo1.getMovieInfoResult().getMovieInfo().getGenres();
	List<Directors> directors1 = movieInfo1.getMovieInfoResult().getMovieInfo().getDirectors();
	List<Actors> actors1 = movieInfo1.getMovieInfoResult().getMovieInfo().getActors();
	List<Nations> nations2 = movieInfo2.getMovieInfoResult().getMovieInfo().getNations();
	List<Genres> genres2 = movieInfo2.getMovieInfoResult().getMovieInfo().getGenres();
	List<Directors> directors2 = movieInfo2.getMovieInfoResult().getMovieInfo().getDirectors();
	List<Actors> actors2 = movieInfo2.getMovieInfoResult().getMovieInfo().getActors();
	List<Nations> nations3 = movieInfo3.getMovieInfoResult().getMovieInfo().getNations();
	List<Genres> genres3 = movieInfo3.getMovieInfoResult().getMovieInfo().getGenres();
	List<Directors> directors3 = movieInfo3.getMovieInfoResult().getMovieInfo().getDirectors();
	List<Actors> actors3 = movieInfo3.getMovieInfoResult().getMovieInfo().getActors();

	ArrayList<Nations> orginNations = movieSimilarity.getSameNations(nations1, nations2, nations3);
	ArrayList<Genres> orginGenres = movieSimilarity.getSameGenres(genres1, genres2, genres3);
	ArrayList<Directors> orginDirectors = movieSimilarity.getSameDirectors(directors1, directors2, directors3);
	ArrayList<Actors> orginActors = movieSimilarity.getSameActors(actors1, actors2, actors3);

	// 관련점수가 높은 영화들을 저장할 Map
	Map<String, Integer> map = new HashMap<String, Integer>();
	int curPage = 1;
	int itemPerPage = 10;
	for (int i = curPage; i <= 50; i++) {
		Call<SearchMovieList> callList = apiKobisService.getMovieList(i, itemPerPage);
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
			int compCode = movieList.get(j).getMovieCd();

			Call<SearchMovieInfo> compMovie = apiKobisService.getSearchMovieInfo(compCode);
			SearchMovieInfo compMovieInfo = compMovie.execute().body();
			List<Nations> compNations = compMovieInfo.getMovieInfoResult().getMovieInfo().getNations();
			List<Genres> compGenres = compMovieInfo.getMovieInfoResult().getMovieInfo().getGenres();
			List<Directors> compDirectors = compMovieInfo.getMovieInfoResult().getMovieInfo().getDirectors();
			List<Actors> compActors = compMovieInfo.getMovieInfoResult().getMovieInfo().getActors();

			//if (compNation.equals(nation1)) {
			//	similarity += 10;
			//}

			similarity += 10 * movieSimilarity.compNations(orginNations, compNations);
			similarity += 10 * movieSimilarity.compGenres(orginGenres, compGenres);
			similarity += 5 * movieSimilarity.compDirectors(orginDirectors, compDirectors);
			similarity += 5 * movieSimilarity.compActors(orginActors, compActors);

			if (similarity > 0) {
				map.put(movieList.get(j).getMovieNm(), similarity);
			}

			similarity = 0;
		}
	}

	Iterator it = movieSimilarity.sortMap(map).iterator();
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
		<label for="movieNm1">영화제목: </label> <input type="text" id="movieNm1"
			name="movieNm1" value="<%=movieNm1%>" /> <label for="movieNm2">영화제목:
		</label> <input type="text" id="movieNm2" name="movieNm2"
			value="<%=movieNm2%>" /> <label for="movieNm3">영화제목: </label><input
			type="text" id="movieNm3" name="movieNm3" value="<%=movieNm3%>" /> <input
			type="submit" value="검색" />
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
				while (it.hasNext()) {
						String temp = (String) it.next();
			%>
			<tr>
				<td><%=temp%></td>
				<td><%=map.get(temp)%></td>
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