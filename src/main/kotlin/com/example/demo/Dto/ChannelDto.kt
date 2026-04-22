package com.example.demo.Dto

data class ChannelDto(
    val id: Long,
    val name: String,
    val description: String,
    val ownerEmail : String,
    val subscribersCount: Int
)
