package com.example.optionalOrElseThrow;

import java.util.Optional;

public class DataService {
    DataProvider dataProvider = new DataProvider();

    String getUsername(int id) {

        Optional<String> usernameOptional = dataProvider.getUsername(id);
        return usernameOptional.orElseThrow();
    }
}
