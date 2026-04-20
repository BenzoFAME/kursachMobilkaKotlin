package com.example.demo.Model

data class TokenData(
    val id: Long,
    val email: String,
    var roles: Set<Role> = setOf(Role.ROLE_USER),
    var isEnabled: Boolean = true,
    var isAccountNonLocked: Boolean = true
)