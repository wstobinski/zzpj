package com.example.transform;

public class PalindromeChecker {

    boolean isPalindrome(String text) {
        return text.equals(text.transform(s -> new StringBuilder(text).reverse().toString()));
    }
}
