package com.example.demo.Service

import com.example.demo.Dto.ChannelDto
import com.example.demo.Dto.CreateChannelRequest
import com.example.demo.Mapper.toDto
import com.example.demo.Model.Channel
import com.example.demo.Repository.ChannelRepository
import com.example.demo.Repository.UserRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
@Service
class ChannelService(private val userRepository: UserRepository,
                     private val channelRepository: ChannelRepository,) {
    @Transactional
    fun getAll()
            : List<ChannelDto> = channelRepository.findAll().map { it.toDto() }
    fun getById(id: Long) : ChannelDto = channelRepository.findById(id).get().toDto()
        @Transactional
    fun createChannel(request: CreateChannelRequest, ownerEmail : String) : ChannelDto {
        val owner = userRepository.findByEmail(ownerEmail)?: throw RuntimeException("User not found")
        val channel = Channel(
            name = request.name,
            description = request.description,
            owner = owner,
        )
            val saved = channelRepository.save(channel)
            saved.subscribers.add(owner)
            return channelRepository.save(saved).toDto()
    }
    @Transactional
    fun delete(id: Long, ownerEmail: String) {
        val channel = channelRepository.findById(id)
            .orElseThrow { RuntimeException("Channel not found") }

        if (channel.owner.email != ownerEmail)
            throw RuntimeException("Запрещено")

        channelRepository.delete(channel)
    }
    @Transactional
    fun Subscribe(channelId : Long , userEmail : String) : ChannelDto {
        val channel = channelRepository.findById(channelId)
            .orElseThrow { RuntimeException("Channel not found") }
        val user = userRepository.findByEmail(userEmail)
            ?: throw RuntimeException("User not found")
        channel.subscribers.add(user)
        return channelRepository.save(channel).toDto()
    }
    @Transactional
    fun unSubscribe(channelId : Long, userEmail : String) : ChannelDto {
        val channel = channelRepository.findById(channelId).orElseThrow { RuntimeException("Channel not found") }
        val user = userRepository.findByEmail(userEmail)?: throw RuntimeException("User not found")
        channel.subscribers.remove(user)
        return channelRepository.save(channel).toDto()
    }
}