package com.bslecommerce.springbackend.util.Response;

public class ResponseMaker {
    public static <T> Response<T> successRes(String message, T body) {
        return new Response<>("success", message, body);
    }

    public static <T> Response<T> errorRes(String message) {
        return new Response<>("failure", message, null);
    }

    public static <T> Response<T> errorRes(String message, T body) {
        return new Response<>("failure", message, body);
    }
}
