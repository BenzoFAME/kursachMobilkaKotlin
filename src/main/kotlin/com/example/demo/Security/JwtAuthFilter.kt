package com.example.demo.Security
import com.example.demo.Security.JwtService
import com.example.demo.Service.UserService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthFilter(
    private val jwtService: JwtService,
    private val userService: UserService
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authHeader = request.getHeader("Authorization")

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response)
            return
        }

        val token = authHeader.substring(7)

        if (!jwtService.validateToken(token)) {
            filterChain.doFilter(request, response)
            return
        }

        val tokenData = jwtService.extractTokenData(token)
        val authorities = tokenData.roles
            .map { SimpleGrantedAuthority(it.name) }

        val auth = UsernamePasswordAuthenticationToken(
            tokenData, null, authorities
        )
        auth.details = WebAuthenticationDetailsSource().buildDetails(request)
        SecurityContextHolder.getContext().authentication = auth

        filterChain.doFilter(request, response)
    }
}