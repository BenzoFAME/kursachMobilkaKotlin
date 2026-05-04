package com.example.demo.Controller

import com.example.demo.Dto.CommentDto
import com.example.demo.Dto.CreateCommentRequest
import com.example.demo.Model.TokenData
import com.example.demo.Repository.UserRepository
import com.example.demo.Service.CommentService
import com.example.demo.Service.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/posts/{postId}/comments")
class CommentController(private val commentService: CommentService) {

    @GetMapping
    fun getAll(@PathVariable postId: Long): List<CommentDto> =
        commentService.findAllCommentsByPostId(postId)

    @PostMapping
    fun create(
        @PathVariable postId: Long,
        @RequestBody request: CreateCommentRequest,
        @AuthenticationPrincipal tokenData: TokenData
    ): ResponseEntity<CommentDto> {
        val result = commentService.createComment(postId, request, tokenData.email)
        return ResponseEntity.status(HttpStatus.CREATED).body(result)
    }

    @DeleteMapping("/{commentId}")
    fun delete(
        @PathVariable commentId: Long,
        @AuthenticationPrincipal tokenData: TokenData
    ): ResponseEntity<Void> {
        commentService.deleteComment(commentId, tokenData.email)
        return ResponseEntity.noContent().build()
    }

    @PutMapping("/{commentId}")
    fun update(
        @PathVariable postId: Long,
        @PathVariable commentId: Long,
        @RequestBody request: CreateCommentRequest,
        @AuthenticationPrincipal tokenData: TokenData
    ): ResponseEntity<CommentDto> {
        val result = commentService.updateComment(tokenData.email, request, commentId, postId)
        return ResponseEntity.ok(result)
    }
}