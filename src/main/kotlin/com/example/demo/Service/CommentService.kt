package com.example.demo.Service

import com.example.demo.Dto.CommentDto
import com.example.demo.Dto.CreateCommentRequest
import com.example.demo.Mapper.toDto
import com.example.demo.Model.Comment
import com.example.demo.Repository.CommentRepository
import com.example.demo.Repository.PostRepository
import com.example.demo.Repository.UserRepository
import jakarta.persistence.Id
import jakarta.transaction.Transactional
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class CommentService(private val commentRepository: CommentRepository ,
    private val postRepository: PostRepository , private val userRepository: UserRepository
)  {

    fun findAllCommentsByPostId(postId: Long): List<CommentDto>  = commentRepository.findAllByPost_Id(postId).map { it.toDto() }

        @Transactional
    fun createComment(postId: Long, request: CreateCommentRequest, userEmail: String): CommentDto {
        val post = postRepository.findById(postId)
            .orElseThrow { RuntimeException("Post not found") }
        val user = userRepository.findByEmail(userEmail)
            ?: throw RuntimeException("User not found")

        val comment = Comment(
            content = request.content,
            created_at = Instant.now(),
            userId = user.id,
            post = post
        )
        return commentRepository.save(comment).toDto()
    }
    @Transactional
    fun deleteComment(commentId: Long, userEmail: String) {
        val comment = commentRepository.findById(commentId)
            .orElseThrow { RuntimeException("Comment not found") }
        val user = userRepository.findByEmail(userEmail)
            ?: throw RuntimeException("User not found")

        if (comment.userId != user.id)
            throw RuntimeException("Нету прав для удаления комментария")

        commentRepository.delete(comment)
    }
        @Transactional
    fun updateComment(userEmail: String, request: CreateCommentRequest, commentId: Long, postId: Long): CommentDto {
        val post = postRepository.findById(postId)
            .orElseThrow { RuntimeException("Post not found") }
        val comment = commentRepository.findById(commentId)
            .orElseThrow { RuntimeException("Comment not found") }
        val user = userRepository.findByEmail(userEmail)
            ?: throw RuntimeException("User not found")

        if (comment.userId != user.id)
            throw RuntimeException("Нету прав для обновления комментария")

        comment.content = request.content
        comment.updatedAt = Instant.now()
        return commentRepository.save(comment).toDto()
    }
}