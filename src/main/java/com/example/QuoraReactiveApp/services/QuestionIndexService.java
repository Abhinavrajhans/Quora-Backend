package com.example.QuoraReactiveApp.services;

import com.example.QuoraReactiveApp.models.Question;
import com.example.QuoraReactiveApp.models.QuestionElasticDocument;
import com.example.QuoraReactiveApp.repositories.QuestionDocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QuestionIndexService implements IQuestionIndexService{

    private final QuestionDocumentRepository questionDocumentRepository;

    @Override
    public void createQuestionIndex(Question question) {
        try{
            QuestionElasticDocument document =QuestionElasticDocument.builder()
                    .id(question.getId())
                    .title(question.getTitle())
                    .content(question.getContent())
                    .build();
            questionDocumentRepository.save(document);
            System.out.println("Successfully indexed question: " + question.getId());
        }
        catch (Exception e){
            System.err.println("Failed to index question: " + question.getId() + " - " + e.getMessage());
        }
    }
}
