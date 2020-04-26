package project.recommend.content.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import project.recommend.content.helper.RetrofitHelper;
import project.recommend.content.helper.WebHelper;
import project.recommend.content.model.SearchMovieInfo;
import project.recommend.content.model.SearchMovieInfo.MovieInfoResult.MovieInfo.Actors;
import project.recommend.content.model.SearchMovieInfo.MovieInfoResult.MovieInfo.Directors;
import project.recommend.content.model.SearchMovieInfo.MovieInfoResult.MovieInfo.Genres;
import project.recommend.content.model.SearchMovieList;
import project.recommend.content.model.SearchMovieList.MovieListResult.MovieList;
import project.recommend.content.service.ApiKobisService;
import project.recommend.content.service.ContentSimilarity;
import retrofit2.Call;
import retrofit2.Retrofit;

public class MovieSimilarity implements ContentSimilarity {

	@Override
	public int calcSimilarity(String name) {
		return 0;
	}

	public int compGenres(List<Genres> origin, List<Genres> comp) {
		int same = 0;

		for (int i = 0; i < origin.size(); i++) {
			for (int j = 0; j < comp.size(); j++) {
				String originGenre = origin.get(i).getGenreNm();
				String compGenre = comp.get(j).getGenreNm();
				if (originGenre.equals(compGenre)) {
					same++;
				}
			}
		}

		return same;
	}

	public int compDirectors(List<Directors> origin, List<Directors> comp) {
		int same = 0;

		for (int i = 0; i < origin.size(); i++) {
			for (int j = 0; j < comp.size(); j++) {
				String originGenre = origin.get(i).getPeopleNm();
				String compGenre = comp.get(j).getPeopleNm();
				if (originGenre.equals(compGenre)) {
					same++;
				}
			}
		}

		return same;
	}

	public int compActors(List<Actors> origin, List<Actors> comp) {
		int same = 0;

		for (int i = 0; i < origin.size(); i++) {
			for (int j = 0; j < comp.size(); j++) {
				String originGenre = origin.get(i).getPeopleNm();
				String compGenre = comp.get(j).getPeopleNm();
				if (originGenre.equals(compGenre)) {
					same++;
				}
			}
		}

		return same;
	}

	public List sortMap(Map map) {
		List<String> list = new ArrayList();
		list.addAll(map.keySet());

		Collections.sort(list, new Comparator() {
			public int compare(Object o1, Object o2) {
				Object v1 = map.get(o1);
				Object v2 = map.get(o2);

				return ((Comparable) v2).compareTo(v1);
			}
		});

		return list;
	}

}
