package com.utree.eightysix.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.utree.eightysix.dao.Message;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table MESSAGE.
*/
public class MessageDao extends AbstractDao<Message, Long> {

    public static final String TABLENAME = "MESSAGE";

    /**
     * Properties of entity Message.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property ChatId = new Property(1, String.class, "chatId", false, "CHAT_ID");
        public final static Property PostId = new Property(2, String.class, "postId", false, "POST_ID");
        public final static Property CommentId = new Property(3, String.class, "commentId", false, "COMMENT_ID");
        public final static Property MsgId = new Property(4, String.class, "msgId", false, "MSG_ID");
        public final static Property Timestamp = new Property(5, Long.class, "timestamp", false, "TIMESTAMP");
        public final static Property From = new Property(6, String.class, "from", false, "FROM");
        public final static Property Type = new Property(7, Integer.class, "type", false, "TYPE");
        public final static Property Content = new Property(8, String.class, "content", false, "CONTENT");
        public final static Property Status = new Property(9, Integer.class, "status", false, "STATUS");
        public final static Property Read = new Property(10, Boolean.class, "read", false, "READ");
        public final static Property Direction = new Property(11, Integer.class, "direction", false, "DIRECTION");
    };


    public MessageDao(DaoConfig config) {
        super(config);
    }
    
    public MessageDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'MESSAGE' (" + //
                "'_id' INTEGER PRIMARY KEY ," + // 0: id
                "'CHAT_ID' TEXT NOT NULL ," + // 1: chatId
                "'POST_ID' TEXT," + // 2: postId
                "'COMMENT_ID' TEXT," + // 3: commentId
                "'MSG_ID' TEXT," + // 4: msgId
                "'TIMESTAMP' INTEGER," + // 5: timestamp
                "'FROM' TEXT," + // 6: from
                "'TYPE' INTEGER," + // 7: type
                "'CONTENT' TEXT," + // 8: content
                "'STATUS' INTEGER," + // 9: status
                "'READ' INTEGER," + // 10: read
                "'DIRECTION' INTEGER);"); // 11: direction
        // Add Indexes
        db.execSQL("CREATE INDEX " + constraint + "IDX_MESSAGE_CHAT_ID ON MESSAGE" +
                " (CHAT_ID);");
        db.execSQL("CREATE INDEX " + constraint + "IDX_MESSAGE_TIMESTAMP_DESC ON MESSAGE" +
                " (TIMESTAMP);");
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'MESSAGE'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, Message entity) {
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
 
        String commentId = entity.getCommentId();
        if (commentId != null) {
            stmt.bindString(4, commentId);
        }
 
        String msgId = entity.getMsgId();
        if (msgId != null) {
            stmt.bindString(5, msgId);
        }
 
        Long timestamp = entity.getTimestamp();
        if (timestamp != null) {
            stmt.bindLong(6, timestamp);
        }
 
        String from = entity.getFrom();
        if (from != null) {
            stmt.bindString(7, from);
        }
 
        Integer type = entity.getType();
        if (type != null) {
            stmt.bindLong(8, type);
        }
 
        String content = entity.getContent();
        if (content != null) {
            stmt.bindString(9, content);
        }
 
        Integer status = entity.getStatus();
        if (status != null) {
            stmt.bindLong(10, status);
        }
 
        Boolean read = entity.getRead();
        if (read != null) {
            stmt.bindLong(11, read ? 1l: 0l);
        }
 
        Integer direction = entity.getDirection();
        if (direction != null) {
            stmt.bindLong(12, direction);
        }
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public Message readEntity(Cursor cursor, int offset) {
        Message entity = new Message( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getString(offset + 1), // chatId
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // postId
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // commentId
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // msgId
            cursor.isNull(offset + 5) ? null : cursor.getLong(offset + 5), // timestamp
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // from
            cursor.isNull(offset + 7) ? null : cursor.getInt(offset + 7), // type
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // content
            cursor.isNull(offset + 9) ? null : cursor.getInt(offset + 9), // status
            cursor.isNull(offset + 10) ? null : cursor.getShort(offset + 10) != 0, // read
            cursor.isNull(offset + 11) ? null : cursor.getInt(offset + 11) // direction
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, Message entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setChatId(cursor.getString(offset + 1));
        entity.setPostId(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setCommentId(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setMsgId(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setTimestamp(cursor.isNull(offset + 5) ? null : cursor.getLong(offset + 5));
        entity.setFrom(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setType(cursor.isNull(offset + 7) ? null : cursor.getInt(offset + 7));
        entity.setContent(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setStatus(cursor.isNull(offset + 9) ? null : cursor.getInt(offset + 9));
        entity.setRead(cursor.isNull(offset + 10) ? null : cursor.getShort(offset + 10) != 0);
        entity.setDirection(cursor.isNull(offset + 11) ? null : cursor.getInt(offset + 11));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(Message entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(Message entity) {
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
