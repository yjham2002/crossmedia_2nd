package com.ccmheaven.tube.pub;

import com.RKclassichaeven.tube.models.CategoryBox;

public class CategoryListInfo {
	private String name;
	private String cgid;
	private String imageUrl;

	public String getName() {
		return name;
	}

	public void setName(String str) {
		name = str;
	}

	public void setCgid(String str) {
		cgid = str;
	}

	public String getCgid() {
		return cgid;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageurl) {
		imageUrl = imageurl;
	}

	public CategoryBox toCategoryBox(){
		CategoryBox categoryBox = new CategoryBox();
		categoryBox.setCg_name(name);
		categoryBox.setAlias(name);
		categoryBox.setCg_id(Integer.parseInt(cgid));
		categoryBox.setCg_image_url(imageUrl);

		return categoryBox;
	}
	
}
