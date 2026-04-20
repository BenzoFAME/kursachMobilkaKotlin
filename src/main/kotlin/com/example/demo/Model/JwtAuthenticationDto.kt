package com.example.demo.Model

data class JwtAuthenticationDto(
    val accessToken: String,
    val refreshToken: String
)