package com.example.demo.Controller

import com.example.demo.Dto.ChannelDto
import com.example.demo.Dto.CreateChannelRequest
import com.example.demo.Model.TokenData
import com.example.demo.Service.ChannelService
import com.example.demo.Service.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/channels")
class ChannelController(private val channelService: ChannelService) {

    @GetMapping("")
    fun getAll(): List<ChannelDto>  = channelService.getAll()
    @PostMapping("/create")
    fun createChannel(@RequestBody request: CreateChannelRequest,@AuthenticationPrincipal tokenData: TokenData): ResponseEntity<ChannelDto> {
        var result = channelService.createChannel(request , tokenData.email)
        return ResponseEntity.status(HttpStatus.CREATED).body(result)
    }
    @DeleteMapping("/{id}")
    fun deleteChannel(@PathVariable id: Long, @AuthenticationPrincipal tokenData: TokenData): ResponseEntity<Void> {
        channelService.delete(id, tokenData.email)
        return ResponseEntity.noContent().build()
    }
    @PostMapping("/subscribe/{id}")
    fun subscribe(@PathVariable id: Long, @AuthenticationPrincipal tokenData: TokenData)
    : ChannelDto = channelService.Subscribe(id , tokenData.email)
    @PostMapping("/unsubscribe/{id}")
    fun unsubscribe(@PathVariable id: Long, @AuthenticationPrincipal tokenData: TokenData)
    : ChannelDto = channelService.unSubscribe(id , tokenData.email)
}