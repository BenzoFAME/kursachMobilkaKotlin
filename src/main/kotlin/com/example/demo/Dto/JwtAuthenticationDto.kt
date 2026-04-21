package com.example.demo.Dto

data class JwtAuthenticationDto(
    val accessToken: String,
    val refreshToken: String
)