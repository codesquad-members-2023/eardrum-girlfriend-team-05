package com.codesquad.controlG.domain.chat.entity;

import com.codesquad.controlG.domain.member.entity.Member;
import java.sql.Timestamp;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
@Entity
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "chat_room_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private ChatRoom chatRoom;

    @JoinColumn(name = "sender_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Member sender;

    private String message;

    @CreationTimestamp
    private Timestamp sentAt;

    private boolean isRead;

    @Builder
    private ChatMessage(Long id, ChatRoom chatRoom, Member sender, String message, Timestamp sentAt, boolean isRead) {
        this.id = id;
        this.chatRoom = chatRoom;
        this.sender = sender;
        this.message = message;
        this.sentAt = sentAt;
        this.isRead = isRead;
    }

    public static ChatMessage of(String message, ChatRoom chatRoom, Member sender, int size) {
        return ChatMessage.builder()
                .message(message)
                .chatRoom(chatRoom)
                .sender(sender)
                .isRead(size == 2)
                .build();
    }
}
