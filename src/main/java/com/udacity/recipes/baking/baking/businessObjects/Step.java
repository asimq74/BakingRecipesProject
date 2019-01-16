package com.udacity.recipes.baking.baking.businessObjects;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

/**
 * Represents a Recipe Step
 *
 * @author Asim Qureshi
 */
public class Step extends RealmObject implements Parcelable {

	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
		@Override
		public Step createFromParcel(Parcel in) {
			return new Step(in);
		}

		@Override
		public Step[] newArray(int size) {
			return new Step[size];
		}
	};
	public final static String PATTERN = "(^[0-9]+\\. )";
	@SerializedName(value = "description")
	private String description;
	@SerializedName(value = "id")
	private Integer id;
	@SerializedName(value = "recipeId")
	private Integer recipeId;
	@SerializedName(value = "shortDescription")
	private String shortDescription;
	@SerializedName(value = "thumbnailURL")
	private String thumbnailURL;
	@SerializedName(value = "videoURL")
	private String videoURL;

	public Step() {
	}

	public Step(Parcel in) {
		description = in.readString();
		id = in.readInt();
		shortDescription = in.readString();
		thumbnailURL = in.readString();
		videoURL = in.readString();
		recipeId = in.readInt();
	}

	@Override
	public int describeContents() {
		return this.hashCode();
	}

	public String getDescription() {
		return description;
	}

	public String getFormattedDescription() {
		return description.replaceFirst(PATTERN, "");
	}

	public Integer getId() {
		return id;
	}

	public Integer getRecipeId() {
		return recipeId;
	}

	public String getShortDescription() {
		return shortDescription;
	}

	public String getThumbnailURL() {
		return thumbnailURL;
	}

	public String getVideoURL() {
		return videoURL;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setRecipeId(Integer recipeId) {
		this.recipeId = recipeId;
	}

	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}

	public void setThumbnailURL(String thumbnailURL) {
		this.thumbnailURL = thumbnailURL;
	}

	public void setVideoURL(String videoURL) {
		this.videoURL = videoURL;
	}

	@Override
	public String toString() {
		return "Step{" +
				"id=" + id +
				", recipeId=" + recipeId +
				", description='" + description + '\'' +
				", shortDescription='" + shortDescription + '\'' +
				", thumbnailURL='" + thumbnailURL + '\'' +
				", videoURL='" + videoURL + '\'' +
				'}';
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(description);
		dest.writeInt(id);
		dest.writeString(shortDescription);
		dest.writeString(thumbnailURL);
		dest.writeString(videoURL);
		dest.writeInt(recipeId);
	}

}
