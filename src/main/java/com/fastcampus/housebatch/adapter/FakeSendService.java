package com.fastcampus.housebatch.adapter;

import org.springframework.stereotype.Service;

/**
 * fileName : FakeSendService
 * author :  KimSangHoon
 * date : 2022/12/04
 */
@Service
public class FakeSendService implements SendService{
    @Override
    public void send(String email, String message) {
        System.out.println("email: " + email + "\nmessage\n" + message);
    }
}
