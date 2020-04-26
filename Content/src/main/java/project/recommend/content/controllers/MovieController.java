package project.recommend.content.controllers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import project.recommend.content.helper.RetrofitHelper;
import project.recommend.content.helper.WebHelper;
import project.recommend.content.model.SearchMovieInfo;
import project.recommend.content.model.SearchMovieInfo.MovieInfoResult.MovieInfo.Actors;
import project.recommend.content.model.SearchMovieInfo.MovieInfoResult.MovieInfo.Directors;
import project.recommend.content.model.SearchMovieInfo.MovieInfoResult.MovieInfo.Genres;
import project.recommend.content.model.SearchMovieList;
import project.recommend.content.model.SearchMovieList.MovieListResult.MovieList;
import project.recommend.content.service.ApiKobisService;
import project.recommend.content.service.impl.MovieSimilarity;
import retrofit2.Call;
import retrofit2.Retrofit;

@Controller
public class MovieController {
	@Autowired
	WebHelper webHelper;
	@Autowired
	RetrofitHelper retrofitHelper;

	@RequestMapping(value = "/movie/movie_info.do", method = RequestMethod.GET)
	public String movieList(Model model, @RequestParam(required = false) String movieNm) {
		/** 1) 필요한 객체 생성 부분 */
		// Retrofit 객체 생성
		Retrofit retrofit = retrofitHelper.getRetrofit(ApiKobisService.BASE_URL);
		// Service 객체를 생성한다. 구현체는 Retrofit이 자동으로 생성해 준다.
		ApiKobisService apiKobisService = retrofit.create(ApiKobisService.class);

		/** 2) 검색일 파라마터 처리 */
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
		MovieSimilarity movieSimilarity = new MovieSimilarity();

		// 영화의 국적,장르,코드 추출
		String nation = "", genre = "", name = "";
		int code = 20124079;
		for (int i = 0; i < 1; i++) {
			name = list.get(i).getMovieNm();
			code = list.get(i).getMovieCd();
			nation = list.get(i).getRepNationNm();
			genre = list.get(i).getRepGenreNm();
		}
		System.out.println(name + " / " + code + " / " + nation + " / " + genre);

		// 장르,감독,배우 리스트 추출
		Call<SearchMovieInfo> callInfo = apiKobisService.getSearchMovieInfo(code);
		SearchMovieInfo movieInfo = null;
		try {
			movieInfo = callInfo.execute().body();
		} catch (Exception e) {
			e.printStackTrace();
		}
		List<Genres> genres = movieInfo.getMovieInfoResult().getMovieInfo().getGenres();
		List<Directors> directors = movieInfo.getMovieInfoResult().getMovieInfo().getDirectors();
		List<Actors> actors = movieInfo.getMovieInfoResult().getMovieInfo().getActors();

		// 관련점수가 높은 영화들을 저장할 Map
		Map<String, Integer> map = new HashMap<String, Integer>();
		int curPage = 1;
		int itemPerPage = 10;
		for (int i = curPage; i <= 10; i++) {
			Call<SearchMovieList> callList = apiKobisService.getMovieList(i, itemPerPage);
			SearchMovieList checkMovieList = null;
			try {
				checkMovieList = callList.execute().body();
			} catch (Exception e) {
				e.printStackTrace();
			}

			List<MovieList> movieList = null;

			// 검색 결과가 있다면 list만 추출한다.
			if (checkMovieList != null) {
				movieList = checkMovieList.getMovieListResult().getMovieList();
			}

			for (int j = 0; j < movieList.size(); j++) {
				int compCode = movieList.get(j).getMovieCd();
				String compNation = movieList.get(j).getRepNationNm();
				String compGenre = movieList.get(j).getRepGenreNm();

				Call<SearchMovieInfo> compMovie = apiKobisService.getSearchMovieInfo(compCode);
				SearchMovieInfo compMovieInfo = null;
				try {
					compMovieInfo = compMovie.execute().body();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				List<Genres> compGenres = compMovieInfo.getMovieInfoResult().getMovieInfo().getGenres();
				List<Directors> compDirectors = compMovieInfo.getMovieInfoResult().getMovieInfo().getDirectors();
				List<Actors> compActors = compMovieInfo.getMovieInfoResult().getMovieInfo().getActors();

				if (compNation.equals(nation)) {
					similarity += 10;
				}

				similarity += 10 * movieSimilarity.compGenres(genres, compGenres);
				similarity += 5 * movieSimilarity.compDirectors(directors, compDirectors);
				similarity += 5 * movieSimilarity.compActors(actors, compActors);

				if (similarity > 10) {
					map.put(movieList.get(j).getMovieNm(), similarity);
				}

				similarity = 0;
			}
		}
		
		Iterator it = movieSimilarity.sortMap(map).iterator();

		return "movie/movie_info";
	}

}
