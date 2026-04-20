package com.example.demo

class JwtRefreshException(
    message: String,
    cause: Throwable? = null
) : RuntimeException(message, cause)