package com.example.demo.Service

import com.example.demo.Model.User
import com.example.demo.Repository.UserRepository
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService(
    private val repository: UserRepository) {
    fun findByEmail(email: String): User =
        repository.findByEmail(email) ?: throw UsernameNotFoundException("User not found")

    fun exitstByEmail(email: String) : Boolean = repository.existsByEmail(email)

    fun save(user: User) : User = repository.save(user)
}