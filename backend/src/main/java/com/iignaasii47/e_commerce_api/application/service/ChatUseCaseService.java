package com.iignaasii47.e_commerce_api.application.service;

import com.iignaasii47.e_commerce_api.application.port.in.ChatUseCase;
import com.iignaasii47.e_commerce_api.domain.model.ChatMessage;
import com.iignaasii47.e_commerce_api.domain.port.out.AiClient;
import com.iignaasii47.e_commerce_api.domain.port.out.CvDataProvider;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ChatUseCaseService implements ChatUseCase {

    private static final String APP_DESCRIPTION = """
            You are a helpful assistant for a terminal-themed e-commerce web application called "term-shop".
            
            The application features:
            - User registration and login with JWT authentication
            - Product browsing with search and category filtering
            - Shopping cart management
            - Checkout and order placement
            - A terminal/CLI-inspired dark theme UI built with Angular 22
            
            Navigation routes:
            - / — Home page with dashboard
            - /products — Browse and search products
            - /products/:id — Product detail page
            - /cart — Shopping cart
            - /checkout — Checkout and order placement
            - /chatbot — This chatbot interface
            - /login — Login page
            - /register — Registration page
            
            Product categories: peripherals, displays, audio, accessories, storage
            
            The tech stack is:
            - Backend: Spring Boot 4.1, Java 26, PostgreSQL, Flyway, JWT (jjwt 0.12.6), BCrypt
            - Frontend: Angular 22, TypeScript 6, SCSS, RxJS, Vitest
            
            """;

    private static final String CV_INSTRUCTIONS = """
            Below is the CV of the application's developer. You can answer questions about their background,
            skills, experience, and projects. If asked about the developer, refer to this CV.
            
            """;

    private final AiClient aiClient;
    private final CvDataProvider cvDataProvider;

    public ChatUseCaseService(AiClient aiClient, CvDataProvider cvDataProvider) {
        this.aiClient = aiClient;
        this.cvDataProvider = cvDataProvider;
    }

    @Override
    public String chat(String userMessage, List<ChatMessage> history) {
        String systemPrompt = APP_DESCRIPTION + CV_INSTRUCTIONS + cvDataProvider.getCvContent();

        List<ChatMessage> fullHistory = new ArrayList<>(history);
        fullHistory.add(ChatMessage.user(userMessage));

        return aiClient.sendMessage(fullHistory, systemPrompt);
    }

}
