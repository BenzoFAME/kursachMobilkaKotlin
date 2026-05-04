package com.example.demo.Service

import com.example.demo.Dto.JwtAuthenticationDto
import com.example.demo.Dto.LoginUser
import com.example.demo.Dto.RefreshTokenDto
import com.example.demo.Dto.RegisterUser
import com.example.demo.Security.JwtService
import com.example.demo.mapper.toTokenData
import com.example.demo.mapper.toUser
import jakarta.transaction.Transactional
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.DisabledException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService(private val passwordEncoder: PasswordEncoder ,
    private val userService: UserService ,
    private val jwtService: JwtService
) {
        @Transactional
    fun createUser(registerUser: RegisterUser) : JwtAuthenticationDto {
        if (userService.exitstByEmail(registerUser.email)) {
            throw IllegalArgumentException("User already exists")
        }
        if (registerUser.password.length < 8) {
            throw IllegalArgumentException("User password must be 8 characters long")
        }

        val user = userService.save(registerUser.toUser(
            passwordEncoder.encode(registerUser.password)))
        return jwtService.generatePairToken(user.toTokenData())
    }
        @Transactional
    fun loginUser(loginUser: LoginUser): JwtAuthenticationDto {
        var user = userService.findByEmail(loginUser.email)
        if (!passwordEncoder.matches(loginUser.password, user.password)) {
            throw BadCredentialsException("неверный пароль")
        }
        if (!user.isEnabled) {
            throw DisabledException("аккаунт заблокирован")
        }
        return jwtService.generatePairToken(user.toTokenData())
    }
        @Transactional
    fun refreshToken(refreshToken: RefreshTokenDto): JwtAuthenticationDto {
        val email = jwtService.extractEmail(refreshToken.refreshToken)
        val user = userService.findByEmail(email)
        jwtService.validateRefreshToken(refreshToken, email)
        return jwtService.refreshTokens(user.toTokenData() , refreshToken)
    }
}

