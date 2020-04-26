package project.content.recommend.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import project.content.recommend.helper.RetrofitHelper;
import project.content.recommend.helper.WebHelper;
import project.content.recommend.model.SearchMovieInfo;
import project.content.recommend.model.SearchMovieInfo.MovieInfoResult.MovieInfo.*;
import project.content.recommend.model.SearchMovieList;
import project.content.recommend.model.SearchMovieList.MovieListResult.MovieList;
import project.content.recommend.service.ApiKobisService;
import project.content.recommend.service.ContentSimilarity;
import retrofit2.Call;
import retrofit2.Retrofit;

public class MovieSimilarity implements ContentSimilarity {

	@Override
	public int calcSimilarity(String name) {
		return 0;
	}

	public ArrayList<Nations> getSameNations(List<Nations> list1, List<Nations> list2, List<Nations> list3) {
		ArrayList<Nations> list = new ArrayList<Nations>();

		for (int i = 0; i < list1.size(); i++) {
			for (int j = 0; j < list2.size(); j++) {
				String nation1 = list1.get(i).getNationNm();
				String nation2 = list2.get(j).getNationNm();
				if (nation1.equals(nation2)) {
					list.add(list1.get(i));
				}
			}
		}
		for (int i = 0; i < list2.size(); i++) {
			for (int j = 0; j < list3.size(); j++) {
				String nation1 = list2.get(i).getNationNm();
				String nation2 = list3.get(j).getNationNm();
				if (nation1.equals(nation2)) {
					list.add(list2.get(i));
				}
			}
		}
		for (int i = 0; i < list3.size(); i++) {
			for (int j = 0; j < list1.size(); j++) {
				String nation1 = list3.get(i).getNationNm();
				String nation2 = list1.get(j).getNationNm();
				if (nation1.equals(nation2)) {
					list.add(list3.get(i));
				}
			}
		}

		return list;
	}

	public ArrayList<Genres> getSameGenres(List<Genres> list1, List<Genres> list2, List<Genres> list3) {
		ArrayList<Genres> list = new ArrayList<Genres>();

		for (int i = 0; i < list1.size(); i++) {
			for (int j = 0; j < list2.size(); j++) {
				String genre1 = list1.get(i).getGenreNm();
				String genre2 = list2.get(j).getGenreNm();
				if (genre1.equals(genre2)) {
					list.add(list1.get(i));
				}
			}
		}
		for (int i = 0; i < list2.size(); i++) {
			for (int j = 0; j < list3.size(); j++) {
				String genre1 = list2.get(i).getGenreNm();
				String genre2 = list3.get(j).getGenreNm();
				if (genre1.equals(genre2)) {
					list.add(list2.get(i));
				}
			}
		}
		for (int i = 0; i < list3.size(); i++) {
			for (int j = 0; j < list1.size(); j++) {
				String genre1 = list3.get(i).getGenreNm();
				String genre2 = list1.get(j).getGenreNm();
				if (genre1.equals(genre2)) {
					list.add(list3.get(i));
				}
			}
		}

		return list;
	}

	public ArrayList<Directors> getSameDirectors(List<Directors> list1, List<Directors> list2, List<Directors> list3) {
		ArrayList<Directors> list = new ArrayList<Directors>();

		for (int i = 0; i < list1.size(); i++) {
			for (int j = 0; j < list2.size(); j++) {
				if (list1.get(i).getPeopleNm() == list2.get(j).getPeopleNm()) {
					list.add(list1.get(i));
				}
			}
		}
		for (int i = 0; i < list2.size(); i++) {
			for (int j = 0; j < list3.size(); j++) {
				if (list2.get(i).getPeopleNm() == list3.get(j).getPeopleNm()) {
					list.add(list2.get(i));
				}
			}
		}
		for (int i = 0; i < list3.size(); i++) {
			for (int j = 0; j < list1.size(); j++) {
				if (list3.get(i).getPeopleNm() == list1.get(j).getPeopleNm()) {
					list.add(list3.get(i));
				}
			}
		}

		return list;
	}

	public ArrayList<Actors> getSameActors(List<Actors> list1, List<Actors> list2, List<Actors> list3) {
		ArrayList<Actors> list = new ArrayList<Actors>();

		for (int i = 0; i < list1.size(); i++) {
			for (int j = 0; j < list2.size(); j++) {
				if (list1.get(i).getPeopleNm() == list2.get(j).getPeopleNm()) {
					list.add(list1.get(i));
				}
			}
		}
		for (int i = 0; i < list2.size(); i++) {
			for (int j = 0; j < list3.size(); j++) {
				if (list2.get(i).getPeopleNm() == list3.get(j).getPeopleNm()) {
					list.add(list2.get(i));
				}
			}
		}
		for (int i = 0; i < list3.size(); i++) {
			for (int j = 0; j < list1.size(); j++) {
				if (list3.get(i).getPeopleNm() == list1.get(j).getPeopleNm()) {
					list.add(list3.get(i));
				}
			}
		}

		return list;
	}
	
	public int compNations(ArrayList<Nations> origin, List<Nations> comp) {
		int same = 0;

		for (int i = 0; i < origin.size(); i++) {
			for (int j = 0; j < comp.size(); j++) {
				String originNation = origin.get(i).getNationNm();
				String compNation = comp.get(j).getNationNm();
				if (originNation.equals(compNation)) {
					same++;
				}
			}
		}

		return same;
	}

	public int compGenres(ArrayList<Genres> origin, List<Genres> comp) {
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

	public int compDirectors(ArrayList<Directors> origin, List<Directors> comp) {
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

	public int compActors(ArrayList<Actors> origin, List<Actors> comp) {
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
