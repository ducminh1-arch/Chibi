package com.chibidoll.dressup.data.local;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class SavedLookDao_Impl implements SavedLookDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<SavedLookEntity> __insertionAdapterOfSavedLookEntity;

  private final EntityDeletionOrUpdateAdapter<SavedLookEntity> __deletionAdapterOfSavedLookEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteLookById;

  public SavedLookDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfSavedLookEntity = new EntityInsertionAdapter<SavedLookEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `saved_looks` (`id`,`createdAt`,`equippedItemIdsJson`,`backgroundId`,`thumbnailPath`,`layerColorsJson`,`presetId`) VALUES (nullif(?, 0),?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final SavedLookEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getCreatedAt());
        if (entity.getEquippedItemIdsJson() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getEquippedItemIdsJson());
        }
        if (entity.getBackgroundId() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getBackgroundId());
        }
        if (entity.getThumbnailPath() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getThumbnailPath());
        }
        if (entity.getLayerColorsJson() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getLayerColorsJson());
        }
        if (entity.getPresetId() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getPresetId());
        }
      }
    };
    this.__deletionAdapterOfSavedLookEntity = new EntityDeletionOrUpdateAdapter<SavedLookEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `saved_looks` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final SavedLookEntity entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteLookById = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM saved_looks WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertOrUpdate(final SavedLookEntity look,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfSavedLookEntity.insertAndReturnId(look);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteLook(final SavedLookEntity look,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfSavedLookEntity.handle(look);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteLookById(final long id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteLookById.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteLookById.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<SavedLookEntity>> getAllLooks() {
    final String _sql = "SELECT * FROM saved_looks ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"saved_looks"}, new Callable<List<SavedLookEntity>>() {
      @Override
      @NonNull
      public List<SavedLookEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfEquippedItemIdsJson = CursorUtil.getColumnIndexOrThrow(_cursor, "equippedItemIdsJson");
          final int _cursorIndexOfBackgroundId = CursorUtil.getColumnIndexOrThrow(_cursor, "backgroundId");
          final int _cursorIndexOfThumbnailPath = CursorUtil.getColumnIndexOrThrow(_cursor, "thumbnailPath");
          final int _cursorIndexOfLayerColorsJson = CursorUtil.getColumnIndexOrThrow(_cursor, "layerColorsJson");
          final int _cursorIndexOfPresetId = CursorUtil.getColumnIndexOrThrow(_cursor, "presetId");
          final List<SavedLookEntity> _result = new ArrayList<SavedLookEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final SavedLookEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final String _tmpEquippedItemIdsJson;
            if (_cursor.isNull(_cursorIndexOfEquippedItemIdsJson)) {
              _tmpEquippedItemIdsJson = null;
            } else {
              _tmpEquippedItemIdsJson = _cursor.getString(_cursorIndexOfEquippedItemIdsJson);
            }
            final String _tmpBackgroundId;
            if (_cursor.isNull(_cursorIndexOfBackgroundId)) {
              _tmpBackgroundId = null;
            } else {
              _tmpBackgroundId = _cursor.getString(_cursorIndexOfBackgroundId);
            }
            final String _tmpThumbnailPath;
            if (_cursor.isNull(_cursorIndexOfThumbnailPath)) {
              _tmpThumbnailPath = null;
            } else {
              _tmpThumbnailPath = _cursor.getString(_cursorIndexOfThumbnailPath);
            }
            final String _tmpLayerColorsJson;
            if (_cursor.isNull(_cursorIndexOfLayerColorsJson)) {
              _tmpLayerColorsJson = null;
            } else {
              _tmpLayerColorsJson = _cursor.getString(_cursorIndexOfLayerColorsJson);
            }
            final String _tmpPresetId;
            if (_cursor.isNull(_cursorIndexOfPresetId)) {
              _tmpPresetId = null;
            } else {
              _tmpPresetId = _cursor.getString(_cursorIndexOfPresetId);
            }
            _item = new SavedLookEntity(_tmpId,_tmpCreatedAt,_tmpEquippedItemIdsJson,_tmpBackgroundId,_tmpThumbnailPath,_tmpLayerColorsJson,_tmpPresetId);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getLookById(final long id,
      final Continuation<? super SavedLookEntity> $completion) {
    final String _sql = "SELECT * FROM saved_looks WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<SavedLookEntity>() {
      @Override
      @Nullable
      public SavedLookEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfEquippedItemIdsJson = CursorUtil.getColumnIndexOrThrow(_cursor, "equippedItemIdsJson");
          final int _cursorIndexOfBackgroundId = CursorUtil.getColumnIndexOrThrow(_cursor, "backgroundId");
          final int _cursorIndexOfThumbnailPath = CursorUtil.getColumnIndexOrThrow(_cursor, "thumbnailPath");
          final int _cursorIndexOfLayerColorsJson = CursorUtil.getColumnIndexOrThrow(_cursor, "layerColorsJson");
          final int _cursorIndexOfPresetId = CursorUtil.getColumnIndexOrThrow(_cursor, "presetId");
          final SavedLookEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final String _tmpEquippedItemIdsJson;
            if (_cursor.isNull(_cursorIndexOfEquippedItemIdsJson)) {
              _tmpEquippedItemIdsJson = null;
            } else {
              _tmpEquippedItemIdsJson = _cursor.getString(_cursorIndexOfEquippedItemIdsJson);
            }
            final String _tmpBackgroundId;
            if (_cursor.isNull(_cursorIndexOfBackgroundId)) {
              _tmpBackgroundId = null;
            } else {
              _tmpBackgroundId = _cursor.getString(_cursorIndexOfBackgroundId);
            }
            final String _tmpThumbnailPath;
            if (_cursor.isNull(_cursorIndexOfThumbnailPath)) {
              _tmpThumbnailPath = null;
            } else {
              _tmpThumbnailPath = _cursor.getString(_cursorIndexOfThumbnailPath);
            }
            final String _tmpLayerColorsJson;
            if (_cursor.isNull(_cursorIndexOfLayerColorsJson)) {
              _tmpLayerColorsJson = null;
            } else {
              _tmpLayerColorsJson = _cursor.getString(_cursorIndexOfLayerColorsJson);
            }
            final String _tmpPresetId;
            if (_cursor.isNull(_cursorIndexOfPresetId)) {
              _tmpPresetId = null;
            } else {
              _tmpPresetId = _cursor.getString(_cursorIndexOfPresetId);
            }
            _result = new SavedLookEntity(_tmpId,_tmpCreatedAt,_tmpEquippedItemIdsJson,_tmpBackgroundId,_tmpThumbnailPath,_tmpLayerColorsJson,_tmpPresetId);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
