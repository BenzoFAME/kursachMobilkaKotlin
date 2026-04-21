package com.example.demo.mapper

import com.example.demo.Dto.RegisterUser
import com.example.demo.Model.TokenData
import com.example.demo.Model.User


fun RegisterUser.toUser(encodedPassword: String) = User(
    username = this.username,
    firstName = this.firstName,
    lastName = this.lastName,
    email = this.email,
    password = encodedPassword
)

fun User.toTokenData() = TokenData(
    id = this.id,
    email = this.email,
    roles = this.roles,
    isEnabled = true,
    isAccountNonLocked = true
)

