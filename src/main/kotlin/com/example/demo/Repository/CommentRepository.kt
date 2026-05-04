package com.example.demo.Repository

import com.example.demo.Model.Comment
import org.springframework.data.jpa.repository.JpaRepository

interface CommentRepository : JpaRepository<Comment, Long> {
    fun findAllByPost_Id(postId: Long): List<Comment>
}