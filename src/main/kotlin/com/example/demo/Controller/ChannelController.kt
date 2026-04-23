package com.example.demo.Controller

import com.example.demo.Dto.ChannelDto
import com.example.demo.Dto.CreateChannelRequest
import com.example.demo.Service.ChannelService
import com.example.demo.Service.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/channels")
class ChannelController(private val channelService: ChannelService, private val userService: UserService) {

    @GetMapping("")
    fun getAll(): List<ChannelDto>  = channelService.getAll()
    @PostMapping("/create")
    fun createChannel(@RequestBody request: CreateChannelRequest,@AuthenticationPrincipal user: UserDetails): ResponseEntity<ChannelDto> {
        var result = channelService.createChannel(request , user.username)
        return ResponseEntity.status(HttpStatus.CREATED).body(result)
    }
    @DeleteMapping("/{id}")
    fun deleteChannel(id: Long , @AuthenticationPrincipal user : UserDetails): ResponseEntity<Void> {
        channelService.delete(id, user.username)
        return ResponseEntity.noContent().build()
    }
    @PostMapping("/subscribe/{id}")
    fun subscribe(id: Long, @AuthenticationPrincipal user : UserDetails)
    : ChannelDto = channelService.Subscribe(id , user.username)
    @PostMapping("/ubsubscribe/{id}")
    fun unsubscribe(id: Long, @AuthenticationPrincipal user : UserDetails)
    : ChannelDto = channelService.unSubscribe(id , user.username)
}