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
<%@ page import="project.content.recommend.model.*"%>
<%@ page
    import="project.content.recommend.model.SearchMovieInfo.MovieInfoResult.MovieInfo.*"%>
<%@ page
    import="project.content.recommend.model.SearchMovieList.MovieListResult"%>
<%@ page
    import="project.content.recommend.model.SearchMovieList.MovieListResult.MovieList"%>
<%@ page import="project.content.recommend.model.SearchVideoList.Items"%>
<%@ page import="project.content.recommend.service.*"%>
<%
    /** 1) 필요한 객체 생성 부분 */
    // Helper 객체 생성
    WebHelper webHelper = WebHelper.getInstance(request, response);
    RetrofitHelper retrofitHelper = RetrofitHelper.getInstance();
    // Retrofit 객체 생성
    Retrofit retrofit1 = retrofitHelper.getRetrofit(ApiKobisService.BASE_URL);
    Retrofit retrofit2 = retrofitHelper.getRetrofit(ApiYoutubeService.BASE_URL);
    // Service 객체를 생성한다. 구현체는 Retrofit이 자동으로 생성해 준다.
    ApiKobisService apiKobisService = retrofit1.create(ApiKobisService.class);
    ApiYoutubeService apiYoutubeService = retrofit2.create(ApiYoutubeService.class);

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
    double similarity = 0;
    MovieSimilarity movieSimilarity = new MovieSimilarity();

    // 영화의 추출
    int code1 = 0, code2 = 0, code3 = 0;
    for (int i = 0; i < 1; i++) {
        code1 = list1.get(i).getMovieCd();
        code2 = list2.get(i).getMovieCd();
        code3 = list3.get(i).getMovieCd();
    }

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
    Map<String, Double> map = new HashMap<String, Double>();
    int curPage = 1;
    int itemPerPage = 100;
    for (int i = curPage; i <= 10; i++) {
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

            similarity += 0.25 * movieSimilarity.compNations(orginNations, compNations);
            similarity += 0.25 * movieSimilarity.compGenres(orginGenres, compGenres);
            similarity += 0.25 * movieSimilarity.compDirectors(orginDirectors, compDirectors);
            similarity += 0.25 * movieSimilarity.compActors(orginActors, compActors);

            if (similarity >= 1) {
                map.put(movieList.get(j).getMovieNm(), similarity);
            }

            similarity = 0;
        }
    }

    Iterator it = movieSimilarity.sortMap(map).iterator();

    /** 5) 관련 영상 검색 */
    // YoutubeAPI를 통해 검색결과 받아오기
    String q = movieNm1 + " " + movieNm2 + " " + movieNm3;
    Call<SearchVideoList> callVideo = apiYoutubeService.getSearchVideoList(q);

    // API 키를 잘못 설정한 경우등의 이유로 연동에 실패 할 수 있기 때문에 예외처리를 준비한다.
    SearchVideoList searchVideoList = null;
    try {
        searchVideoList = callVideo.execute().body();
    } catch (Exception e) {
        e.printStackTrace();
    }

    List<Items> items = null;
    // 검색 결과가 있다면 list만 추출한다.
    if (searchVideoList != null) {
        items = searchVideoList.getItems();
    }
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8" />
<title>Recommend System</title>
<style type="text/css">
* {
    margin: 0;
    padding: 0;
}

html, body {
    width: 100%;
}

#container {
    width: 90%;
    margin: auto;
    margin-top: 40px;
}

form {
    width: 100%;
    margin-top: 20px;
}

form div {
    width: 100%;
}

form div label {
    width: 30%;
    float: left;
}

form div input {
    width: 65%;
    float: right;
}

.clearfix:after {
    content: '';
    display: block;
    float: none;
    clear: both;
}

form div:last-child {
    margin-top: 5px;
    text-align: center;
}

form button {
    width: 20%;
    color: #fff;
    background-color: #298367;
    border: 1px solid #298367;
    border-radius: 2px;
}

table {
    width: 100%;
    margin-top: 30px;
    border: 1px solid #298367;
}

thead, tbody, tr, th, td {
    border: 1px solid #298367;
    text-align: center;
}

table thead tr th:first-child {
    width: 70%;
}

table tbody tr td:first-child {
    width: 70%;
}
</style>
</head>
<body>
    <div id="container">
        <h3>좋아하는 영화 제목을 알려주세요.</h3>
        <!-- 1) 검색폼 구성 -->
        <form name="form1" method="get"
            action="<%=request.getContextPath()%>/main.jsp">
            <div class="clearfix">
                <label for="movieNm1">영화제목1</label> <input type="text" id="movieNm1"
                    name="movieNm1" value="<%=movieNm1%>" />
            </div>
            <div class="clearfix">
                <label for="movieNm2">영화제목2</label> <input type="text" id="movieNm2"
                    name="movieNm2" value="<%=movieNm2%>" />
            </div>
            <div class="clearfix">
                <label for="movieNm3">영화제목3</label> <input type="text" id="movieNm3"
                    name="movieNm3" value="<%=movieNm3%>" />
            </div>
            <div>
                <button type="submit">검색</button>
                <button type="reset">초기화</button>
            </div>
        </form>

        <!-- 2) 검색 결과가 존재할 경우 목록을 표 형식으로 출력 -->
        <%
            if (movieNm1 != null && movieNm2 != null && movieNm3 != null && map != null && map.size() > 0) {
        %>
        <table>
            <thead>
                <tr>
                    <th>영화 제목</th>
                    <th>유사도</th>
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
        <%
            if (movieNm1 != null && movieNm2 != null && movieNm3 != null && items != null && items.size() > 0) {
        %>
        <table>
            <thead>
                <tr>
                    <th>영상 제목</th>
                    <th>영상 내용</th>
                </tr>
            </thead>
            <tbody>
                <!-- 영상리스트 출력 -->
                <%
                    for (Items video : items) {
                %>
                <tr>
                    <td><%=video.getSnippet().getTitle()%></td>
                    <td><%=video.getSnippet().getDescription()%></td>
                </tr>
                <%
                    }
                %>
            </tbody>
        </table>
        <%
            }
        %>
    </div>
</body>
</html>