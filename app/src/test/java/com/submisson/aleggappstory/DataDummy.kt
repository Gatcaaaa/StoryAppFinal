package com.submisson.aleggappstory

import com.submisson.aleggappstory.data.response.ListStoryItem

object DataDummy {
    fun generateDummyStoryResponse(): List<ListStoryItem>{
        val item: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100){
            val story = ListStoryItem(
                id = "story-Tdwmy4wBZD8iwLUM",
                name = "dna",
                photoUrl = "ini ga pake loc",
                createdAt = "2023-11-05T15:09:13.102Z",
                lat = 0.51443751,
                lon = 117.08472006
            )
            item.add(story)
        }
        return item
    }
}