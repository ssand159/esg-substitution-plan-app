package com.esgsubstitutionplanapp.content;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MyClass {

    private String grade;
    private String letter;

    public String getFullName(){
        String name = grade + letter;
        return name.trim().replace("-", "");
    }

}
