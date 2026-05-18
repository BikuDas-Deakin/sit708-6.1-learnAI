package com.sit708.learningassistant.models;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AttemptDao_Impl implements AttemptDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<QuizAttempt> __insertionAdapterOfQuizAttempt;

  public AttemptDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfQuizAttempt = new EntityInsertionAdapter<QuizAttempt>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `quiz_attempts` (`id`,`username`,`topic`,`totalQuestions`,`correctAnswers`,`timestamp`,`questionsJson`,`selectedAnswersJson`,`correctAnswersJson`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          final QuizAttempt entity) {
        statement.bindLong(1, entity.id);
        if (entity.username == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.username);
        }
        if (entity.topic == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.topic);
        }
        statement.bindLong(4, entity.totalQuestions);
        statement.bindLong(5, entity.correctAnswers);
        statement.bindLong(6, entity.timestamp);
        if (entity.questionsJson == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.questionsJson);
        }
        if (entity.selectedAnswersJson == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, entity.selectedAnswersJson);
        }
        if (entity.correctAnswersJson == null) {
          statement.bindNull(9);
        } else {
          statement.bindString(9, entity.correctAnswersJson);
        }
      }
    };
  }

  @Override
  public void insert(final QuizAttempt attempt) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfQuizAttempt.insert(attempt);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public List<QuizAttempt> getAttemptsForUser(final String username) {
    final String _sql = "SELECT * FROM quiz_attempts WHERE username = ? ORDER BY timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (username == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, username);
    }
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfUsername = CursorUtil.getColumnIndexOrThrow(_cursor, "username");
      final int _cursorIndexOfTopic = CursorUtil.getColumnIndexOrThrow(_cursor, "topic");
      final int _cursorIndexOfTotalQuestions = CursorUtil.getColumnIndexOrThrow(_cursor, "totalQuestions");
      final int _cursorIndexOfCorrectAnswers = CursorUtil.getColumnIndexOrThrow(_cursor, "correctAnswers");
      final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
      final int _cursorIndexOfQuestionsJson = CursorUtil.getColumnIndexOrThrow(_cursor, "questionsJson");
      final int _cursorIndexOfSelectedAnswersJson = CursorUtil.getColumnIndexOrThrow(_cursor, "selectedAnswersJson");
      final int _cursorIndexOfCorrectAnswersJson = CursorUtil.getColumnIndexOrThrow(_cursor, "correctAnswersJson");
      final List<QuizAttempt> _result = new ArrayList<QuizAttempt>(_cursor.getCount());
      while (_cursor.moveToNext()) {
        final QuizAttempt _item;
        _item = new QuizAttempt();
        _item.id = _cursor.getInt(_cursorIndexOfId);
        if (_cursor.isNull(_cursorIndexOfUsername)) {
          _item.username = null;
        } else {
          _item.username = _cursor.getString(_cursorIndexOfUsername);
        }
        if (_cursor.isNull(_cursorIndexOfTopic)) {
          _item.topic = null;
        } else {
          _item.topic = _cursor.getString(_cursorIndexOfTopic);
        }
        _item.totalQuestions = _cursor.getInt(_cursorIndexOfTotalQuestions);
        _item.correctAnswers = _cursor.getInt(_cursorIndexOfCorrectAnswers);
        _item.timestamp = _cursor.getLong(_cursorIndexOfTimestamp);
        if (_cursor.isNull(_cursorIndexOfQuestionsJson)) {
          _item.questionsJson = null;
        } else {
          _item.questionsJson = _cursor.getString(_cursorIndexOfQuestionsJson);
        }
        if (_cursor.isNull(_cursorIndexOfSelectedAnswersJson)) {
          _item.selectedAnswersJson = null;
        } else {
          _item.selectedAnswersJson = _cursor.getString(_cursorIndexOfSelectedAnswersJson);
        }
        if (_cursor.isNull(_cursorIndexOfCorrectAnswersJson)) {
          _item.correctAnswersJson = null;
        } else {
          _item.correctAnswersJson = _cursor.getString(_cursorIndexOfCorrectAnswersJson);
        }
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public int getTotalQuestions(final String username) {
    final String _sql = "SELECT SUM(totalQuestions) FROM quiz_attempts WHERE username = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (username == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, username);
    }
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _result;
      if (_cursor.moveToFirst()) {
        _result = _cursor.getInt(0);
      } else {
        _result = 0;
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public int getTotalCorrect(final String username) {
    final String _sql = "SELECT SUM(correctAnswers) FROM quiz_attempts WHERE username = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (username == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, username);
    }
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _result;
      if (_cursor.moveToFirst()) {
        _result = _cursor.getInt(0);
      } else {
        _result = 0;
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public int getAttemptCount(final String username) {
    final String _sql = "SELECT COUNT(*) FROM quiz_attempts WHERE username = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (username == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, username);
    }
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _result;
      if (_cursor.moveToFirst()) {
        _result = _cursor.getInt(0);
      } else {
        _result = 0;
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
