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
        return grade + letter;
    }

}
