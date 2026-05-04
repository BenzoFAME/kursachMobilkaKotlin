package com.example.demo.Mapper
import com.example.demo.Dto.CommentDto
import com.example.demo.Model.Comment

fun Comment.toDto() = CommentDto(
    id = this.id,
    content = this.content,
    userId = this.userId,
    created_at = this.created_at,
    updatedAt = this.updatedAt
)