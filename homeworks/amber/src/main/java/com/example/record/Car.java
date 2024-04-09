package com.example.record;

// TODO: implement here
// change this class to record
// throw IllegalArgumentException when capacity in negative while creating
public record Car(String make, Integer capacity) {

    public Car {
        if (capacity < 0) {
            throw new IllegalArgumentException("Capacity needs to be a non-negative integer!");
        }
    }
}
