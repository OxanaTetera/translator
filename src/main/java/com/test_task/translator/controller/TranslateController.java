package com.test_task.translator.controller;

import com.test_task.translator.DBClass;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.net.URI;
import java.util.*;

@RestController
@RequestMapping("/translate")
public class TranslateController {

    @PostMapping
    public Map<String, Object> callTranslator(@RequestBody Map<String, String> message) {
        // Свойства
        Properties properties = new Properties();
        // Загружаем файл со свойствами
        ClassLoader cl = this.getClass().getClassLoader();
        InputStream applProp = cl.getResourceAsStream("application.properties");
        try {
            properties.load(applProp);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Получаем свойства
        String yandexURI = properties.getProperty("yandexURI");
        String yandexKey = properties.getProperty("yandexKey");
        // Параметры для запроса
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        // Запрос к сервису
        RequestEntity<MultiValueMap<String, String>> request;
        // Ответ от сервиса
        ResponseEntity<String> response;
        RestTemplate restTemplate = new RestTemplate();
        // Разделитель
        String delimiter = " ";
        // Список слов для перевода
        List<String> worldsToTranslate;
        // Объект для разбора ответа сервиса
        JSONObject respBody;
        // Строки для разбора ответа сервиса
        String respString = "";
        String parseText = "";
        // Получаем данные из запроса
        String text = message.get("text");
        String lang = message.get("lang");
        // Объект для работы с данными, получаем из контекста
        ApplicationContext context = new AnnotationConfigApplicationContext(DBClass.class);
        DBClass dbClass = (DBClass) context.getBean("dbClassBean");

        // Результирующий набор ответа сервиса
        Map<String, Object> model = new HashMap<>();

        // Создание таблицы
        dbClass.createTeble();

        //Делим строку для перевода на слова
        worldsToTranslate = Arrays.asList(text.split(delimiter));

        // Заполняем параметры запроса
        params.add("key", yandexKey);
        params.add("lang", lang);

        // Вызываем переводчик для каждого слова
        for (String s : worldsToTranslate) {
            params.add("text", s);
            // Формируем запрос к сервису
            request = RequestEntity.post(URI.create(yandexURI))
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(params);
            // Получаем ответ от сервиса
            response = restTemplate.exchange(request, String.class);

            // Вынимаем часть ответа
            try {
                respBody = new JSONObject(response.getBody());
                parseText = respBody.getJSONArray("text").get(0).toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            // Пишем данные вызова в базу
            try {
                dbClass.writeToDB(params);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Формируем итоговую строку ответа сервиса
            if (respString.isEmpty())
                respString = parseText;
            else
                respString += " " + parseText;

            params.remove("text");
        }

        model.put("text", respString);

        // Читаем данные из базы
        dbClass.readFromDB();

        return model;
    }
}
