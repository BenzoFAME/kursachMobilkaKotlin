package com.example.demo.Dto

import java.time.Instant

data class CreateCommentRequest( var content: String , val createdAt : Instant = Instant.now() , val updatedAt : Instant = Instant.now())
