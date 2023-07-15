package com.seftian.storyapp.ui.activities.home

import com.seftian.storyapp.data.model.StoryResponse

object Dummy {
    fun generateDataDummy(): List<StoryResponse>{
        val dummyList = mutableListOf<StoryResponse>()

        for (i in 1..10) {
            dummyList.add(
                StoryResponse(
                    id = "$i",
                    name = "Story $i",
                    description = "This is the first story",
                    photoUrl = "https://example.com/photo$i.jpg",
                    createdAt = "2023-07-13",
                    lat = 123.456f,
                    lon = 789.012f
                )
            )
        }

        return dummyList
    }
}