package com.example.demo.Dto

import com.example.demo.Model.Post
import java.time.Instant

data class CommentDto(
    val id : Long,
    var content : String,
    val userId : Long,
    val created_at : Instant = Instant.now(),
    val updatedAt: Instant = Instant.now()
)
