package com.example.demo.Mapper

import com.example.demo.Dto.ChannelDto
import com.example.demo.Dto.PostDto
import com.example.demo.Model.Channel
import com.example.demo.Model.Post

fun Channel.toDto() = ChannelDto(
    id = this.id,
    name = this.name,
    description = this.description,
    ownerEmail = this.owner.email,
    subscribersCount = this.subscribers.size
)

fun Post.toDto() = PostDto(
    id = this.id,
    content = this.content,
    createdAt = this.created_at.toString(),
    channelName = this.channelName.name
)