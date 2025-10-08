package com.example.QuoraReactiveApp.adapter;

import com.example.QuoraReactiveApp.models.Question;
import com.example.QuoraReactiveApp.models.QuestionElasticDocument;

public class QuestionElasticDocumentAdapter {

    public static QuestionElasticDocument toEntity(Question question){
       return QuestionElasticDocument.builder()
                .id(question.getId())
                .title(question.getTitle())
                .content(question.getContent())
                .createdById(question.getCreatedById())
                .build();
    }
}
