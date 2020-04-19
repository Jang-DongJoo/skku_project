package project.content.recommend.service.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import project.content.recommend.helper.RetrofitHelper;
import project.content.recommend.helper.WebHelper;
import project.content.recommend.model.SearchMovieInfo;
import project.content.recommend.model.SearchMovieInfo.MovieInfoResult.MovieInfo.Genres;
import project.content.recommend.model.SearchMovieList;
import project.content.recommend.model.SearchMovieList.MovieListResult.MovieList;
import project.content.recommend.service.ApiKobisService;
import project.content.recommend.service.ContentSimilarity;
import retrofit2.Call;
import retrofit2.Retrofit;

public class MovieSimilarity implements ContentSimilarity {

	@Override
	public int calcSimilarity(String name) {
		int similarity = 0;

		RetrofitHelper retrofitHelper = RetrofitHelper.getInstance();
		Retrofit retrofit = retrofitHelper.getRetrofit(ApiKobisService.BASE_URL);
		ApiKobisService apiKobisService = retrofit.create(ApiKobisService.class);

		// 영화진흥원 OpenAPI를 통해 검색결과 받아오기
		Call<SearchMovieList> call = apiKobisService.getSearchMovieList(name);

		SearchMovieList searchMovieList = null;
		try {
			searchMovieList = call.execute().body();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		List<MovieList> list = null;

		// 검색 결과가 있다면 list만 추출한다.
		if (searchMovieList != null) {
			list = searchMovieList.getMovieListResult().getMovieList();
		}

		// 영화의 국적,장르 추출
		String nation = "", genre = "";
		for (int i = 0; i < list.size(); i++) {
			nation = list.get(i).getRepNationNm();
			genre = list.get(i).getRepGenreNm();
		}

		// 관련점수가 높은 영화들을 저장할 Map
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		int curPage = 1;
		int itemPerPage = 100;
		for (int i = curPage; i <= 10; i++) {
			Call<SearchMovieList> callList = apiKobisService.getMovieList(i);
			SearchMovieList checkMovieList = null;
			try {
				checkMovieList = callList.execute().body();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			List<MovieList> movieList = null;

			// 검색 결과가 있다면 list만 추출한다.
			if (checkMovieList != null) {
				movieList = checkMovieList.getMovieListResult().getMovieList();
			}

			if (movieList.get(i).getRepNationNm().equals(nation)) {
				similarity += 10;
				map.put(movieList.get(i).getMovieNm(), similarity);
			}
			if (movieList.get(i).getRepGenreNm().equals(genre)) {
				similarity += 20;
				map.put(movieList.get(i).getMovieNm(), similarity);
			}
		}

		return similarity;
	}

}
