package com.example.demo.Service

import com.example.demo.Dto.CreatePostRequest
import com.example.demo.Dto.PostDto
import com.example.demo.Mapper.toDto
import com.example.demo.Model.Post
import com.example.demo.Repository.ChannelRepository
import com.example.demo.Repository.PostRepository
import jakarta.persistence.EntityNotFoundException
import jakarta.persistence.Id
import org.springframework.stereotype.Service

@Service
class PostService (private val postRepository: PostRepository,
                   private val channelRepository: ChannelRepository,) {

    fun getByChannel(channelId: Long): List<PostDto>  = postRepository.findByChannelId(channelId).map { it.toDto() }

    fun createPost(channelId: Long, request: CreatePostRequest): PostDto {
        val channel = channelRepository.findById(channelId).orElseThrow { RuntimeException("Channel not found") }
        val post = Post(
            id =0,
            content = request.content,
            channelName = channel
        )
        return postRepository.save(post).toDto()
    }
    fun deletePost(postId: Long) {
        if (!postRepository.existsById(postId)) {
            throw RuntimeException("Post not found")
        }
        postRepository.deleteById(postId)
    }

    fun updatePost(postId: Long , request: CreatePostRequest): PostDto {
        val post = postRepository.findById(postId).orElseThrow { RuntimeException("Post not found") }
        post.content = request.content
        return postRepository.save(post).toDto()
    }
}