package project.content.recommend.model;

import java.util.List;

import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * 영상 목록을 저장하기 위한 클래스
 */
@Data
public class SearchVideoList {
	@SerializedName("items")
	public List<Items> items;

	@Data
	public class Items {
		@SerializedName("snippet")
		public Snippet snippet;

		@Data
		public class Snippet {
			@SerializedName("title")
			public String title;
			@SerializedName("description")
			public String description;
			@SerializedName("channelTitle")
			public String channelTitle;
		}
	}
}
