package de.christcoding.smartstudy.models;

import java.io.Serializable;
import java.util.List;

public class User implements Serializable {

        public boolean availability;
        public String userName, email, image, fcmToken;
        public List<String> groupId, couponIds, blockedUsers, blockedBy;
        public int points;
}