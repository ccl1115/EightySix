package com.utree.eightysix.dao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table CONVERSATION.
 */
public class Conversation {

    private Long id;
    /** Not-null value. */
    private String chatId;
    private String postId;
    private String commentId;
    private String lastMsg;
    private String portrait;
    private String bgUrl;
    private String postContent;
    private String commentContent;
    private String chatSource;
    private String relation;
    private Long timestamp;
    private Long unreadCount;
    private Boolean favorite;

    public Conversation() {
    }

    public Conversation(Long id) {
        this.id = id;
    }

    public Conversation(Long id, String chatId, String postId, String commentId, String lastMsg, String portrait, String bgUrl, String postContent, String commentContent, String chatSource, String relation, Long timestamp, Long unreadCount, Boolean favorite) {
        this.id = id;
        this.chatId = chatId;
        this.postId = postId;
        this.commentId = commentId;
        this.lastMsg = lastMsg;
        this.portrait = portrait;
        this.bgUrl = bgUrl;
        this.postContent = postContent;
        this.commentContent = commentContent;
        this.chatSource = chatSource;
        this.relation = relation;
        this.timestamp = timestamp;
        this.unreadCount = unreadCount;
        this.favorite = favorite;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /** Not-null value. */
    public String getChatId() {
        return chatId;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getLastMsg() {
        return lastMsg;
    }

    public void setLastMsg(String lastMsg) {
        this.lastMsg = lastMsg;
    }

    public String getPortrait() {
        return portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }

    public String getBgUrl() {
        return bgUrl;
    }

    public void setBgUrl(String bgUrl) {
        this.bgUrl = bgUrl;
    }

    public String getPostContent() {
        return postContent;
    }

    public void setPostContent(String postContent) {
        this.postContent = postContent;
    }

    public String getCommentContent() {
        return commentContent;
    }

    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
    }

    public String getChatSource() {
        return chatSource;
    }

    public void setChatSource(String chatSource) {
        this.chatSource = chatSource;
    }

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Long getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(Long unreadCount) {
        this.unreadCount = unreadCount;
    }

    public Boolean getFavorite() {
        return favorite;
    }

    public void setFavorite(Boolean favorite) {
        this.favorite = favorite;
    }

}
