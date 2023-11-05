package com.example.smartstudy.utilities;

import java.util.HashMap;

public class Constants {

    public static final String KEY_COLLECTION_USERS = "users";
    public static final String KEY_COLLECTION_GROUPS = "groups";
    public static final String KEY_COLLECTION_CHATS = "chats";
    public static final String KEY_COLLECTION_COUPONS= "coupons";

    public static final String KEY_USER_NAME = "userName";
    public static final String KEY_GROUP_NAME = "name";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_COUPON_IDS = "couponIds";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_PREFERENCE_NAME = "chatPreference";
    public static final String KEY_IS_SIGNED_IN = "isSignedIn";
    public static final String KEY_USER_ID = "userId";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_FCM_TOKEN = "fcmToken";
    public static final String KEY_GROUP_ID = "groupId";
    public static final String KEY_MEMBERS = "members";
    public static final String KEY_JOIN_WITH_GROUP_ID = "joinWithId";
    public static final String KEY_CREATED_BY = "createdBy";
    public static final String KEY_CREATED_TIME = "createdTime";
    public static final String KEY_SENDER_ID = "senderId";
    public static final String KEY_MSG = "message";
    public static final String KEY_TIMESTAMP = "timestamp";

    public static final String KEY_SENDER = "sender";
    public static final String KEY_SENDER_NAME = "senderName";
    public static final String KEY_SENDER_IMG = "senderImage";
    public static final String KEY_EVENTS = "events";
    public static final String KEY_TYPE = "type";
    public static final String KEY_SUBJECT = "subject";
    public static final String KEY_DATE = "date";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_MOST_IMPORTANT_INFO = "mostImportantInformation";
    public static final String KEY_CATEGORIES = "fileCategories";
    public static final String KEY_FILES = "files";
    public static final String KEY_QUESTIONS = "questions";
    public static final String KEY_ANSWERSA = "answersA";
    public static final String KEY_ANSWERSB = "answersB";
    public static final String KEY_ANSWERSC = "answersC";
    public static final String KEY_ANSWERSD = "answersD";
    public static final String KEY_CORRECT_ANSWERS = "correctAnswers";
    public static final String KEY_PROMPT = "messages";
    public static final String KEY_AVAILABILITY = "availability";
    public static final String REMOTE_MSG_AUTHORIZATION = "Authorization";
    public static final String REMOTE_MSG_CONTENT_TYPE = "Content-Type";
    public static final String REMOTE_MSG_DATA = "data";
    public static final String REMOTE_MSG_REGISTRATION_IDS = "registration_ids";
    public static final String KEY_ADD_EXAMS_TO_PLAN = "addExamsToPlan";
    public static final String KEY_RESPONSE = "response";
    public static final String KEY_TOPIC = "topic";
    public static final String KEY_LANGUAGE = "language";
    public static final String KEY_POINTS = "points";
    public static final String KEY_COLLECTION_DECKS = "decks";
    public static HashMap<String, String> remoteMsgHeaders = null;
    public static HashMap<String, String> getRemoteMsgHeaders() {
        if (remoteMsgHeaders == null) {
            remoteMsgHeaders = new HashMap<>();
            remoteMsgHeaders.put(REMOTE_MSG_AUTHORIZATION,
                    "key=AAAA79cKFF0:APA91bEYzsycGl0njG5-g2d18hOXXGKqYPxTeS5ZLPZtiOAHZEB_-hD_AzZ0ErIRUPdwbaYmPKSbvRf1iWGzUojZFQF9ivHApCyAN2NE-xHZAEM43T0SfFF16IHF6Ro16-BCI3QR0omf");
            remoteMsgHeaders.put(REMOTE_MSG_CONTENT_TYPE, "application/json");
        }
        return remoteMsgHeaders;
    }
}
