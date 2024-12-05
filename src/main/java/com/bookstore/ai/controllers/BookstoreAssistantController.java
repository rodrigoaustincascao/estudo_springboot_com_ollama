package com.bookstore.ai.controllers;

import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.Map;

@RestController
@RequestMapping("/bookstore")
public class BookstoreAssistantController {
    private final OpenAiChatModel chatClient;
    private final OllamaChatModel chatOllama;

    public BookstoreAssistantController(OpenAiChatModel chatClient, OllamaChatModel chatOllama){
        this.chatClient= chatClient;
        this.chatOllama=chatOllama;
    }

    @GetMapping("/openai/informations")
    public String bookstoreChat(@RequestParam(value = "message", defaultValue = "Quais são os livros best sellers dos últimos anos?") String message){
        return chatClient.call(message);
    }

    @GetMapping("/openai/chat-response-informations")
    public ChatResponse bookstoreChatResponse(@RequestParam(value = "message", defaultValue = "Quais são os livros best sellers dos últimos anos?") String message){
        return chatClient.call(new Prompt(message));
    }

    @GetMapping("/openai/reviews")
    public String bookstoreReview(@RequestParam(value = "book", defaultValue = "Incidente em Antares") String book){
        PromptTemplate promptTemplate = new PromptTemplate("""
                Por favor, forneça-me um breve resumo do livro {book} e também a biografia de seu autor
                """);
        promptTemplate.add("book", book);
        return this.chatClient.call(promptTemplate.create()).getResult().getOutput().getContent();
    }

    @GetMapping("/openai/stream/informations")
    public Flux<String> bookstoreChatStream(@RequestParam(value = "message", defaultValue = "Quais são os livros best sellers dos últimos anos?") String message){
        return chatClient.stream(message);
    }

    @GetMapping("/ollama/generate")
    public Map<String,String> generate(@RequestParam(value = "message", defaultValue = "Conte-me uma piada") String message) {
        return Map.of("generation", chatOllama.call(message));
    }

    @GetMapping("/ollama/generateStream")
    public Flux<ChatResponse> generateStream(@RequestParam(value = "message", defaultValue = "Conte-me uma piada") String message) {
        Prompt prompt = new Prompt(new UserMessage(message));
        return chatOllama.stream(prompt);
    }

}
