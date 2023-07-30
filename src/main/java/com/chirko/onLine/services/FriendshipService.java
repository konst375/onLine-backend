package com.chirko.onLine.services;

import com.chirko.onLine.dto.response.FriendshipsDto;
import com.chirko.onLine.entities.Friendship;
import com.chirko.onLine.entities.User;
import com.chirko.onLine.entities.enums.FriendshipStatus;
import com.chirko.onLine.entities.enums.NotificationType;
import com.chirko.onLine.exceptions.ErrorCause;
import com.chirko.onLine.exceptions.OnLineException;
import com.chirko.onLine.repos.FriendshipRepo;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class FriendshipService {
    private final UserService userService;
    private final NotificationService notificationService;
    private final FriendshipRepo friendshipRepo;

    public void sendFriendRequest(UUID recipientId, User sender) {
        User recipient = userService.findById(recipientId);
        if (friendshipRepo.existsByUsers(Set.of(sender, recipient))) {
            throw new OnLineException(ErrorCause.ALREADY_FRIENDS, HttpStatus.FORBIDDEN);
        }
        Friendship friendship = Friendship.builder()
                .sender(sender)
                .recipient(recipient)
                .status(FriendshipStatus.REQUEST)
                .build();
        friendshipRepo.save(friendship);
        notificationService.saveFriendshipNotification(recipient, NotificationType.FRIEND_REQUEST, sender.getId());
    }

    @Transactional
    public void approveFriendship(UUID senderId, User recipient) {
        User sender = userService.findById(senderId);
        Friendship friendship = friendshipRepo.findByRecipientAndSender(recipient, sender)
                .orElseThrow(() -> new OnLineException(ErrorCause.FRIENDSHIP_NOT_FOUND, HttpStatus.NOT_FOUND));
        friendship.setStatus(FriendshipStatus.MUTUAL);
        notificationService.saveFriendshipNotification(sender, NotificationType.FRIENDSHIP_APPROVED, recipient.getId());
    }

    @Transactional
    public void denyFriendship(UUID senderId, User recipient) {
        User sender = userService.findById(senderId);
        Friendship friendship = friendshipRepo.findByRecipientAndSender(recipient, sender)
                .orElseThrow(() -> new OnLineException(ErrorCause.FRIENDSHIP_NOT_FOUND, HttpStatus.NOT_FOUND));
        friendship.setStatus(FriendshipStatus.DENIED);
        notificationService.saveFriendshipNotification(sender, NotificationType.FRIENDSHIP_DENIED, recipient.getId());
    }

    public FriendshipsDto getFriendshipsDto(User user) {
        Set<Friendship> friendships = getFriendships(user);
        return new FriendshipsDto(
                userService.toBaseUsersDto(getFriends(user, friendships)),
                userService.toBaseUsersDto(getFollowers(friendships).stream()
                        .filter(sender -> !sender.equals(user))
                        .map(friend -> userService.findByIdWithImages(friend.getId()))
                        .collect(Collectors.toSet())),
                userService.toBaseUsersDto(getFollowers(friendships).stream()
                        .filter(sender -> sender.equals(user))
                        .map(friend -> userService.findByIdWithImages(friend.getId()))
                        .collect(Collectors.toSet())));
    }

    public Set<User> getFriends(User user) {
        return getFriends(user, getFriendships(user));
    }

    @Transactional
    public void deleteFriend(UUID friendId, User user) {
        User friend = userService.findById(friendId);
        Friendship friendship = friendshipRepo.findByUsers(Set.of(friend, user)).orElseThrow(() -> new OnLineException(
                ErrorCause.FRIENDSHIP_NOT_FOUND,
                HttpStatus.NOT_FOUND));
        friendship.setStatus(FriendshipStatus.DENIED);
    }

    private Set<Friendship> getFriendships(User user) {
        return friendshipRepo.findByUser(user).orElseThrow(() -> new OnLineException(
                ErrorCause.FRIENDSHIP_NOT_FOUND,
                HttpStatus.NOT_FOUND));
    }

    private Set<User> getFriends(User user, Set<Friendship> friendships) {
        return friendships.stream()
                .filter(friendship -> friendship.getStatus().equals(FriendshipStatus.MUTUAL))
                .map(friendship -> friendship.getFriend(user))
                .map(friend -> userService.findByIdWithImages(friend.getId()))
                .collect(Collectors.toSet());
    }

    private Set<User> getFollowers(Set<Friendship> friendships) {
        return friendships.stream()
                .filter(friendship -> !friendship.getStatus().equals(FriendshipStatus.MUTUAL))
                .map(Friendship::getSender)
                .collect(Collectors.toSet());
    }

    @Transactional
    public void unsubscribe(UUID userId, User user) {
        User follow = userService.findById(userId);
        Friendship friendship = friendshipRepo.findByUsers(Set.of(follow, user)).orElseThrow(() -> new OnLineException(
                ErrorCause.FRIENDSHIP_NOT_FOUND,
                HttpStatus.NOT_FOUND));
        friendshipRepo.delete(friendship);
    }
}
