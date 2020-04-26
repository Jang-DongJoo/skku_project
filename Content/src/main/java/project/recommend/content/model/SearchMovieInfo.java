package project.recommend.content.model;

import java.util.List;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

/**
 * 영화 정보를 저장하기 위한 클래스
 */
@Data
public class SearchMovieInfo {
	@SerializedName("movieInfoResult")
	public MovieInfoResult movieInfoResult;

	@Data
	public class MovieInfoResult {
		@SerializedName("movieInfo")
		public MovieInfo movieInfo;

		@Data
		public class MovieInfo {
			@SerializedName("movieNm")
			public String movieNm;
			@SerializedName("movieNmEn")
			public String movieNmEn;
			@SerializedName("nations")
			public List<Nations> nations;
			@SerializedName("genres")
			public List<Genres> genres;
			@SerializedName("directors")
			public List<Directors> directors;
			@SerializedName("actors")
			public List<Actors> actors;

			@Data
			public class Nations {
				@SerializedName("nationNm")
				public String nationNm;
			}

			@Data
			public class Genres {
				@SerializedName("genreNm")
				public String genreNm;
			}

			@Data
			public class Directors {
				@SerializedName("peopleNm")
				public String peopleNm;
				@SerializedName("peopleNmEn")
				public String peopleNmEn;
			}

			@Data
			public class Actors {
				@SerializedName("peopleNm")
				public String peopleNm;
				@SerializedName("peopleNmEn")
				public String peopleNmEn;
			}
		}
	}
}
