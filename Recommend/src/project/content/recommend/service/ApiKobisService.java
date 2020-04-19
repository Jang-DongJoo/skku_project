package project.content.recommend.service;

import project.content.recommend.model.*;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiKobisService {
	public static final String BASE_URL = "http://www.kobis.or.kr";

	@GET("/kobisopenapi/webservice/rest/movie/searchMovieList.json?key=4617f1d10ee8084d9469cf54fa5017bb")
	Call<SearchMovieList> getSearchMovieList(@Query("movieNm") String movieNm);

	@GET("/kobisopenapi/webservice/rest/movie/searchMovieList.json?key=4617f1d10ee8084d9469cf54fa5017bb")
	Call<SearchMovieList> getMovieList(@Query("curPage") int curPage);

	@GET("/kobisopenapi/webservice/rest/movie/searchMovieInfo.json?key=4617f1d10ee8084d9469cf54fa5017bb")
	Call<SearchMovieInfo> getSearchMovieInfo(@Query("movieCd") String movieCd);
}
