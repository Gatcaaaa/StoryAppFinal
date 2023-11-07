package com.submisson.aleggappstory.data.response

import com.google.gson.annotations.SerializedName

data class UploadStoriesResponse(

	@field:SerializedName("error")
	val error: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)
