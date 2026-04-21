package com.example.demo.Controller

import com.example.demo.Dto.JwtAuthenticationDto
import com.example.demo.Dto.LoginUser
import com.example.demo.Dto.RefreshTokenDto
import com.example.demo.Dto.RegisterUser
import com.example.demo.Model.User
import com.example.demo.Service.AuthService
import org.apache.catalina.connector.Response
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthController(private val authService: AuthService) {
        @PostMapping("/register")
    fun register(@RequestBody request: RegisterUser): ResponseEntity<JwtAuthenticationDto> =
        ResponseEntity.status(HttpStatus.CREATED).body(authService.createUser(request))
        @PostMapping("/login")
    fun login(@RequestBody request: LoginUser): ResponseEntity<JwtAuthenticationDto>  =
        ResponseEntity.ok(authService.loginUser(request))
        @PostMapping("/refreshToken")
    fun refresh(@RequestBody request: RefreshTokenDto) : ResponseEntity<JwtAuthenticationDto> =
        ResponseEntity.ok(authService.refreshToken(request))
        @DeleteMapping("/logout")
    fun logout() :ResponseEntity<String>{
        return ResponseEntity.ok("выход")
    }
}