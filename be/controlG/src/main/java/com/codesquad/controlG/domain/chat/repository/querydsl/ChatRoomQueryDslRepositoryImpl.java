package com.codesquad.controlG.domain.chat.repository.querydsl;

import static com.codesquad.controlG.domain.block.entity.QBlock.block;
import static com.codesquad.controlG.domain.chat.entity.QChatMember.chatMember;
import static com.codesquad.controlG.domain.chat.entity.QChatMessage.chatMessage;
import static com.codesquad.controlG.domain.chat.entity.QChatRoom.chatRoom;
import static com.codesquad.controlG.domain.member.entity.QMember.member;

import com.codesquad.controlG.domain.chat.dto.response.ChatListMessages;
import com.codesquad.controlG.domain.chat.dto.response.ChatListPartner;
import com.codesquad.controlG.domain.chat.dto.response.ChatListResponse;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ChatRoomQueryDslRepositoryImpl implements ChatRoomQueryDslRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Long> findMemberChatroom(Long groupId, Long memberId) {
        return queryFactory.select(createPartnerExpression(memberId))
                .from(chatRoom)
                .where(
                        equalMember(memberId),
                        chatRoom.group.id.eq(groupId)
                )
                .fetch();
    }

    @Override
    public List<ChatListResponse> getChatList(Long groupId, Long memberId) {
        return queryFactory.select(Projections.fields(ChatListResponse.class,
                        chatRoom.id.as("chatRoomId"),
                        Projections.fields(ChatListPartner.class,
                                member.id,
                                member.nickname,
                                member.name,
                                member.profileImg).as("partner"),
                        Projections.fields(ChatListMessages.class,
                                chatMessage.message.as("lastMessage"),
                                chatMessage.sentAt.as("lastSendTime")).as("messages")))
                .from(chatRoom)
                .leftJoin(member)
                .on(member.id.eq(getPartner(memberId)))
                .leftJoin(chatMessage)
                .on(chatMessage.id.eq(JPAExpressions.select(chatMessage.id.max())
                        .from(chatMessage)
                        .where(chatMessage.chatRoom.id.eq(chatRoom.id))))
                .join(chatMember)
                .on(chatMember.chatRoom.eq(chatRoom))
                .where(
                        equalMember(memberId),
                        equalGroupId(groupId),
                        isExit(memberId)
                )
                .fetch();
    }

    private BooleanExpression isExit(Long memberId) {
        return chatMember.member.id.eq(memberId).and(chatMember.isExit.eq(false));
    }

    @Override
    public Map<Long, Long> countNewMessage(Long groupId, Long memberId) {
        List<Tuple> results = queryFactory
                .select(chatMessage.chatRoom.id,
                        chatMessage.chatRoom.id.count())
                .from(chatMessage)
                .join(chatMessage.chatRoom, chatRoom)
                .where(isUnread()
                        .and(isSender(memberId)))
                .groupBy(chatMessage.chatRoom.id)
                .fetch();

        return results.stream()
                .collect(Collectors.toMap(
                        tuple -> tuple.get(0, Long.class),  // ChatRoomId
                        tuple -> tuple.get(1, Long.class)   // newMessageCount
                ));
    }

    @Override
    public void updateExit(Long chatRoomId, Long memberId, Long receiverId) {
        if (!checkBlocked(memberId, receiverId)) {
            queryFactory.update(chatMember)
                    .set(chatMember.isExit, false)
                    .where(
                            chatMember.chatRoom.id.eq(chatRoomId),
                            chatMember.member.id.eq(receiverId)
                    )
                    .execute();
        }
    }

    private boolean checkBlocked(Long memberId, Long receiverId) {
        return queryFactory.selectFrom(block)
                .where(
                        block.blocker.id.eq(receiverId),
                        block.blocked.id.eq(memberId)
                )
                .fetchFirst() != null;
    }

    private NumberExpression<Long> getPartner(Long memberId) {
        return new CaseBuilder()
                .when(chatRoom.member1.id.eq(memberId))
                .then(chatRoom.member2.id)
                .otherwise(chatRoom.member1.id);
    }

    private BooleanExpression isSender(Long memberId) {
        return chatMessage.sender.id.ne(memberId);
    }

    private BooleanExpression isUnread() {
        return chatMessage.isRead.eq(false);
    }

    private BooleanExpression equalMember(Long memberId) {
        return chatRoom.member1.id.eq(memberId)
                .or(chatRoom.member2.id.eq(memberId));
    }

    private Expression<Long> createPartnerExpression(Long memberId) {
        return new CaseBuilder()
                .when(chatRoom.member1.id.eq(memberId))
                .then(chatRoom.member2.id)
                .otherwise(chatRoom.member1.id)
                .as("chatPartnerId");
    }

    private BooleanExpression equalGroupId(Long groupId) {
        return groupId == null ? null : chatRoom.group.id.eq(groupId);
    }
}