package com.example.demo.Repository

import com.example.demo.Model.Post
import org.springframework.data.jpa.repository.JpaRepository

interface PostRepository  : JpaRepository<Post, Long> {
    fun findByChannelName_Id(channelId: Long): List<Post>
}