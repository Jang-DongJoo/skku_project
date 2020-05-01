package project.content.recommend.service;

import project.content.recommend.model.SearchVideoList;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiYoutubeService {
	public static final String BASE_URL = "https://www.googleapis.com";

	@GET("/youtube/v3/search?part=snippet&key={자신의 YoutubeApi key 값}&type=video&maxResults=10")
	Call<SearchVideoList> getSearchVideoList(@Query("q") String q);
}
