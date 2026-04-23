package com.example.demo.Controller

import com.example.demo.Dto.ChannelDto
import com.example.demo.Dto.CreatePostRequest
import com.example.demo.Dto.PostDto
import com.example.demo.Service.PostService
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
@RequestMapping("/api/channels/{channelId}/posts")
class PostContoller(private val postService: PostService) {
    @GetMapping("")
    fun getByChannel(@PathVariable channelId: Long)
    : List<PostDto> = postService.getByChannel(channelId)


    @PostMapping
    fun createPost(
        @PathVariable channelId: Long,
        @RequestBody request: CreatePostRequest,
        @AuthenticationPrincipal user: UserDetails
    ): ResponseEntity<PostDto> {
        val result = postService.createPost(channelId, request, user.username)
        return ResponseEntity.status(HttpStatus.CREATED).body(result)
    }


    @DeleteMapping("/{id}")
    fun deletePost(
        @PathVariable id: Long,
        @AuthenticationPrincipal user: UserDetails
    ): ResponseEntity<Void> {
        postService.deletePost(id, user.username)
        return ResponseEntity.noContent().build()
    }

    @PutMapping("/{id}")
    fun updatePost(
        @PathVariable id: Long,
        @RequestBody request: CreatePostRequest,
        @AuthenticationPrincipal user: UserDetails
    ): ResponseEntity<PostDto> {
        val updated = postService.updatePost(id, request, user.username)
        return ResponseEntity.ok(updated)
    }
}