package com.example.demo.Repository

import com.example.demo.Model.Channel
import org.springframework.data.jpa.repository.JpaRepository

interface ChannelRepository : JpaRepository<Channel, Long> {
    fun findByOwnerId(ownerId: Long) : List<Channel>
}