package com.example.saludable.Interfaces;

import java.util.List;

public interface IFirebaseLoadDone {
    void onFirebaseLoadMaratonDone(List<String> lstEmail);

    void onFirebaseLoadFaile(String message);
}
