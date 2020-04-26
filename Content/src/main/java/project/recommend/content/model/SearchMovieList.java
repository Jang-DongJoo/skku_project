package project.recommend.content.model;

import java.util.List;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

/**
 * 영화 목록을 저장하기 위한 클래스
 */
@Data
public class SearchMovieList {
	@SerializedName("movieListResult")
	public MovieListResult movieListResult;

	@Data
	public class MovieListResult {
		@SerializedName("movieList")
		public List<MovieList> movieList;

		@Data
		public class MovieList {
			@SerializedName("movieCd")
			public int movieCd;
			@SerializedName("movieNm")
			public String movieNm;
			@SerializedName("repNationNm")
			public String repNationNm;
			@SerializedName("repGenreNm")
			public String repGenreNm;
		}
	}
}
