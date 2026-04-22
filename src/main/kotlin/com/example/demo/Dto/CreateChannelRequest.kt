package com.example.demo.Dto

data class CreateChannelRequest(
    val name: String,
    val description: String = ""
)
