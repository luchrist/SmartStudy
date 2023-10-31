package com.example.smartstudy.models;

import java.io.Serializable;
import java.util.List;

public class User implements Serializable {

        public boolean availability;
        public String userName, email, image, fcmToken;
        public List<String> groupId;
        public int points;
}