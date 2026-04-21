package com.example.demo.Security

import com.example.demo.Dto.JwtAuthenticationDto
import com.example.demo.Dto.RefreshTokenDto
import com.example.demo.JwtRefreshException
import com.example.demo.Model.Role
import com.example.demo.Model.TokenData
import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.UnsupportedJwtException
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.security.SignatureException
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date
import javax.crypto.SecretKey

@Component
class JwtService{
    companion object {
       private val log : Logger = LoggerFactory.getLogger(JwtService::class.java)
        private val ACCESS_TOKEN_DURATION = Duration.ofMinutes(10)
        private val REFRESH_TOKEN_DURATION = Duration.ofDays(30)
        private const val REFRESH_RENEW_THRESHOLD_DAYS = 7L

        private const val CLAIM_USER_ID = "userId"
        private const val CLAIM_ROLES = "roles"
        private const val CLAIM_IS_ENABLED = "isEnabled"
        private const val CLAIM_IS_ACCOUNT_NON_LOCKED = "isAccountNonLocked"
    }
    @Value("\${jwt.secret}")
    private lateinit var jwtSecret : String



    fun generatePairToken(tokenData : TokenData): JwtAuthenticationDto =
        JwtAuthenticationDto(
            accessToken = buildAccessToken(tokenData),
            refreshToken = buildRefreshToken(tokenData.email)
        )




    fun refreshTokens(tokenData : TokenData,
                      refreshTokenDto: RefreshTokenDto
    ) : JwtAuthenticationDto {
        val refreshToken = refreshTokenDto.refreshToken
        val claims = parseClaimsOrThrowRefresh(refreshToken)
        val daysLeft = Duration.between(Instant.now() , claims.expiration.toInstant()).toDays()
        val newRefreshToken =
            if (daysLeft < REFRESH_RENEW_THRESHOLD_DAYS)
                buildRefreshToken(claims.subject)
            else refreshToken
        return JwtAuthenticationDto(
            accessToken = buildAccessToken(tokenData),
            refreshToken = newRefreshToken
        )
    }
    fun refreshBaseToken(
        userId: Long,
        email: String,
        roles: Set<Role>,
        isEnabled: Boolean,
        isAccountNonLocked: Boolean,
        refreshToken: String
    ): JwtAuthenticationDto = JwtAuthenticationDto(
        accessToken = generateJwtToken(userId, email, roles, isEnabled, isAccountNonLocked),
        refreshToken = refreshToken
    )
    fun validateToken(token: String): Boolean {
        return try {
            parseClaims(token)
            true
        } catch (e: ExpiredJwtException)      { log.warn("JWT истёк: {}", e.message); false }
        catch (e: UnsupportedJwtException)  { log.warn("JWT не поддерживается: {}", e.message); false }
        catch (e: MalformedJwtException)    { log.warn("JWT повреждён: {}", e.message); false }
        catch (e: SignatureException)       { log.warn("JWT неверная подпись: {}", e.message); false }
        catch (e: IllegalArgumentException) { log.warn("JWT null: {}", e.message); false }
    }

    fun extractTokenData(token: String): TokenData {
        val claims = parseClaims(token)
        return TokenData(
            id = claims.get(CLAIM_USER_ID, Long::class.java),
            email = claims.subject,
            roles = extractRoles(claims),
            isEnabled = claims.get(CLAIM_IS_ENABLED, Boolean::class.java),
            isAccountNonLocked = claims.get(CLAIM_IS_ACCOUNT_NON_LOCKED, Boolean::class.java)
        )
    }
    fun extractEmail(token: String): String = parseClaims(token).subject

    fun validateRefreshToken(refreshTokenDto: RefreshTokenDto, expectedEmail: String) {
        val claims = parseClaimsOrThrowRefresh(refreshTokenDto.refreshToken)
        if (expectedEmail != claims.subject)
            throw JwtRefreshException("Refresh токен не принадлежит пользователю: $expectedEmail")
    }
    fun extractExpiration(token: String): Date = parseClaims(token).expiration

    fun millisUntilExpiry(token: String): Long =
        parseClaims(token).expiration.toInstant().toEpochMilli() - Instant.now().toEpochMilli()

    fun isTokenExpired(token: String): Boolean = try {
        parseClaims(token).expiration.before(Date())
    } catch (e: ExpiredJwtException) { true }


    private fun buildAccessToken(tokenData: TokenData): String {
        val now = Instant.now()
        return Jwts.builder()
            .setSubject(tokenData.email)
            .setIssuedAt(Date.from(now))
            .setExpiration(Date.from(now.plus(ACCESS_TOKEN_DURATION))) // ← ACCESS не REFRESH
            .claim(CLAIM_USER_ID, tokenData.id)
            .claim(CLAIM_ROLES, tokenData.roles.map { it.name })
            .claim(CLAIM_IS_ENABLED, tokenData.isEnabled)
            .claim(CLAIM_IS_ACCOUNT_NON_LOCKED, tokenData.isAccountNonLocked)
            .signWith(signingKey())
            .compact()
    }

    private fun buildRefreshToken(email: String): String {
        val now = Instant.now()
        return Jwts.builder()
            .setSubject(email)
            .setIssuedAt(Date.from(now))
            .setExpiration(Date.from(now.plus(REFRESH_TOKEN_DURATION)))
            .signWith(signingKey())
            .compact()
    }

    private fun parseClaims(token : String) : Claims {
        return Jwts.parserBuilder()
            .setSigningKey(signingKey())
            .build()
            .parseClaimsJws(token)
        .body
    }
    private fun parseClaimsOrThrowRefresh(token: String): Claims = try {
        parseClaims(token)
    } catch (e: ExpiredJwtException) {
        throw JwtRefreshException("Refresh-токен истёк", e)
    } catch (e: JwtException) {
        throw JwtRefreshException("Refresh-токен невалиден", e)
    }


    private fun extractRoles(claims : Claims): Set<Role> {
        val rolesNames = claims.get(CLAIM_ROLES,List :: class.java) as? List<String>
        if (rolesNames.isNullOrEmpty()) {
            return emptySet()
        }
        return rolesNames.map { Role.valueOf(it) }.toSet()
    }


    private fun signingKey(): SecretKey {
        val keyBytes = Decoders.BASE64.decode(jwtSecret)
        return Keys.hmacShaKeyFor(keyBytes)
    }


    private fun generateJwtToken(
        userId: Long,
        email: String,
        roles : Set<Role>,
        isEnabled : Boolean,
        isAccountNonLocked : Boolean
    ) : String {
        val expectation = Date.from(
            LocalDateTime.now()
                .plusMinutes(15)
                .atZone(ZoneId.systemDefault()).toInstant())
        return Jwts.builder()
            .setSubject(email)
            .setExpiration(expectation)
            .claim(CLAIM_USER_ID, userId)
            .claim(CLAIM_ROLES , roles.map { it.name })
            .claim(CLAIM_IS_ENABLED , isEnabled)
            .claim(CLAIM_IS_ACCOUNT_NON_LOCKED , isAccountNonLocked)
            .signWith(signingKey()).compact()
    }

}