package com.example.demo.Model

import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.time.Instant
import java.util.concurrent.Flow

@Entity
@Table(name = "channels")
data class Channel(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id : Long = 0,
    val name : String,
    val description : String,
    val created_at : Instant = Instant.now(),
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    val owner : User,
    @OneToMany(mappedBy = "channelName", cascade = [CascadeType.ALL], orphanRemoval = true)
    val posts: MutableList<Post> = mutableListOf(),

    @ManyToMany
    @JoinTable(
        name = "channels_posts",
        joinColumns = [JoinColumn(name = "channel_id")],
        inverseJoinColumns = [JoinColumn(name = "user_id")]
    )
    val subscribers : MutableSet<User> = mutableSetOf()
)
