package com.utree.eightysix.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.utree.eightysix.dao.Conversation;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table CONVERSATION.
*/
public class ConversationDao extends AbstractDao<Conversation, Long> {

    public static final String TABLENAME = "CONVERSATION";

    /**
     * Properties of entity Conversation.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property ChatId = new Property(1, String.class, "chatId", false, "CHAT_ID");
        public final static Property PostId = new Property(2, String.class, "postId", false, "POST_ID");
        public final static Property PostSource = new Property(3, String.class, "postSource", false, "POST_SOURCE");
        public final static Property PostContent = new Property(4, String.class, "postContent", false, "POST_CONTENT");
        public final static Property CommentId = new Property(5, String.class, "commentId", false, "COMMENT_ID");
        public final static Property LastMsg = new Property(6, String.class, "lastMsg", false, "LAST_MSG");
        public final static Property Portrait = new Property(7, String.class, "portrait", false, "PORTRAIT");
        public final static Property BgUrl = new Property(8, String.class, "bgUrl", false, "BG_URL");
        public final static Property BgColor = new Property(9, String.class, "bgColor", false, "BG_COLOR");
        public final static Property CommentContent = new Property(10, String.class, "commentContent", false, "COMMENT_CONTENT");
        public final static Property Relation = new Property(11, String.class, "relation", false, "RELATION");
        public final static Property Timestamp = new Property(12, Long.class, "timestamp", false, "TIMESTAMP");
        public final static Property UnreadCount = new Property(13, Long.class, "unreadCount", false, "UNREAD_COUNT");
        public final static Property Favorite = new Property(14, Boolean.class, "favorite", false, "FAVORITE");
    };


    public ConversationDao(DaoConfig config) {
        super(config);
    }
    
    public ConversationDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'CONVERSATION' (" + //
                "'_id' INTEGER PRIMARY KEY ," + // 0: id
                "'CHAT_ID' TEXT NOT NULL ," + // 1: chatId
                "'POST_ID' TEXT," + // 2: postId
            "'POST_SOURCE' TEXT," + // 3: postSource
            "'POST_CONTENT' TEXT," + // 4: postContent
            "'COMMENT_ID' TEXT," + // 5: commentId
            "'LAST_MSG' TEXT," + // 6: lastMsg
            "'PORTRAIT' TEXT," + // 7: portrait
            "'BG_URL' TEXT," + // 8: bgUrl
            "'BG_COLOR' TEXT," + // 9: bgColor
            "'COMMENT_CONTENT' TEXT," + // 10: commentContent
            "'RELATION' TEXT," + // 11: relation
            "'TIMESTAMP' INTEGER," + // 12: timestamp
            "'UNREAD_COUNT' INTEGER," + // 13: unreadCount
            "'FAVORITE' INTEGER);"); // 14: favorite
        // Add Indexes
        db.execSQL("CREATE INDEX " + constraint + "IDX_CONVERSATION_TIMESTAMP_DESC ON CONVERSATION" +
                " (TIMESTAMP);");
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'CONVERSATION'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, Conversation entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindString(2, entity.getChatId());
 
        String postId = entity.getPostId();
        if (postId != null) {
            stmt.bindString(3, postId);
        }

        String postSource = entity.getPostSource();
        if (postSource != null) {
            stmt.bindString(4, postSource);
        }

        String postContent = entity.getPostContent();
        if (postContent != null) {
            stmt.bindString(5, postContent);
        }
 
        String commentId = entity.getCommentId();
        if (commentId != null) {
            stmt.bindString(6, commentId);
        }
 
        String lastMsg = entity.getLastMsg();
        if (lastMsg != null) {
            stmt.bindString(7, lastMsg);
        }
 
        String portrait = entity.getPortrait();
        if (portrait != null) {
            stmt.bindString(8, portrait);
        }
 
        String bgUrl = entity.getBgUrl();
        if (bgUrl != null) {
            stmt.bindString(9, bgUrl);
        }

        String bgColor = entity.getBgColor();
        if (bgColor != null) {
            stmt.bindString(10, bgColor);
        }
 
        String commentContent = entity.getCommentContent();
        if (commentContent != null) {
            stmt.bindString(11, commentContent);
        }
 
        String relation = entity.getRelation();
        if (relation != null) {
            stmt.bindString(12, relation);
        }
 
        Long timestamp = entity.getTimestamp();
        if (timestamp != null) {
            stmt.bindLong(13, timestamp);
        }
 
        Long unreadCount = entity.getUnreadCount();
        if (unreadCount != null) {
            stmt.bindLong(14, unreadCount);
        }
 
        Boolean favorite = entity.getFavorite();
        if (favorite != null) {
            stmt.bindLong(15, favorite ? 1l : 0l);
        }
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public Conversation readEntity(Cursor cursor, int offset) {
        Conversation entity = new Conversation( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getString(offset + 1), // chatId
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // postId
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // postSource
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // postContent
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // commentId
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // lastMsg
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // portrait
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // bgUrl
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9), // bgColor
            cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10), // commentContent
            cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11), // relation
            cursor.isNull(offset + 12) ? null : cursor.getLong(offset + 12), // timestamp
            cursor.isNull(offset + 13) ? null : cursor.getLong(offset + 13), // unreadCount
            cursor.isNull(offset + 14) ? null : cursor.getShort(offset + 14) != 0 // favorite
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, Conversation entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setChatId(cursor.getString(offset + 1));
        entity.setPostId(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setPostSource(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setPostContent(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setCommentId(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setLastMsg(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setPortrait(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setBgUrl(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setBgColor(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
        entity.setCommentContent(cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10));
        entity.setRelation(cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11));
        entity.setTimestamp(cursor.isNull(offset + 12) ? null : cursor.getLong(offset + 12));
        entity.setUnreadCount(cursor.isNull(offset + 13) ? null : cursor.getLong(offset + 13));
        entity.setFavorite(cursor.isNull(offset + 14) ? null : cursor.getShort(offset + 14) != 0);
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(Conversation entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(Conversation entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}
