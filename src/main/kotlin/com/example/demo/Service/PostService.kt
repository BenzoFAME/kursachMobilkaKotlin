package com.example.demo.Service

import com.example.demo.Dto.CreatePostRequest
import com.example.demo.Dto.PostDto
import com.example.demo.Mapper.toDto
import com.example.demo.Model.Post
import com.example.demo.Repository.ChannelRepository
import com.example.demo.Repository.PostRepository
import jakarta.persistence.EntityNotFoundException
import jakarta.persistence.Id
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class PostService (private val postRepository: PostRepository,
                   private val channelRepository: ChannelRepository,) {
    @Transactional
    fun getByChannel(channelId: Long): List<PostDto>  = postRepository.findByChannelName_Id(channelId).map { it.toDto() }
    @Transactional
    fun createPost(channelId: Long, request: CreatePostRequest , ownerEmail : String): PostDto {
        val channel = channelRepository.findById(channelId).orElseThrow { RuntimeException("Channel not found") }

        if (channel.owner.email != ownerEmail){
            throw RuntimeException("Owner email does not match owner")
        }
        val post = Post(
            id =0,
            content = request.content,
            channelName = channel
        )
        return postRepository.save(post).toDto()
    }
    @Transactional
    fun deletePost(postId: Long, ownerEmail: String) {
        val post = postRepository.findById(postId)
            .orElseThrow { RuntimeException("Post not found") }
        if (post.channelName.owner.email != ownerEmail)
            throw RuntimeException("Только владелец канала может удалять посты")
        postRepository.deleteById(postId)
    }
    @Transactional
    fun updatePost(postId: Long, request: CreatePostRequest, ownerEmail: String): PostDto {
        val post = postRepository.findById(postId)
            .orElseThrow { RuntimeException("Post not found") }
        if (post.channelName.owner.email != ownerEmail)
            throw RuntimeException("Только владелец канала может редактировать посты")
        post.content = request.content
        return postRepository.save(post).toDto()
    }
}