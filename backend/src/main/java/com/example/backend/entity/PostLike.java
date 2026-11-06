package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

// Un like = une ligne. La clé (user_id, post_id) empêche de liker deux fois le même post.
// Seuls les posts du forum se likent : la relation pointe vers PostForum.
@Entity
@Table(name = "post_like")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class PostLike {

    @EmbeddedId
    private PostLikeId id = new PostLikeId();

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("postId")
    @JoinColumn(name = "post_id")
    private PostForum post;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public PostLike(User user, PostForum post) {
        this.user = user;
        this.post = post;
    }
}
