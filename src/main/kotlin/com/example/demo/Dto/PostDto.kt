package com.example.demo.Dto

data class PostDto(
    val id: Long,
    val content: String,
    val createdAt: String,
    val channelName: String,
    val commentDto : MutableList<CommentDto> = mutableListOf()
)
