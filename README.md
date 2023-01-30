# "java-shareit" (backend of web app)

## Description:

Приложение для обмена вещами, которое позволяет:

- Заимствовать вещи,
- Размещать вещи для сдачи в аренду,
- Менять статус бронирования вещи, 
- Cоздавать запросы на отсутствующую вещь.

## Instruction of launch app:

С помощью IntelliJ IDEA (my version - 2022.1)

1. Открываем проект.
2. IntelliJ IDEA сообщит, что "Maven Build Scripts Found", следует нажать "Load".
3. Запускаем команду **mvn clean install**.
4. Запускаем команду **docker-compose up** в терминале IDEA, убедившись, что находимся в той же директории, что и файл "docker-compose.yml", при этом Docker daemon должен быть запущен.
5. Так же можно, загрузить базовую коллекцию ([Ссылка на коллекцию](https://github.com/AlexKlinkov/java-shareit/blob/main/SET_OF_REQUESTS.postman_collection.json)) в Postman и подергать разные ручки, посмотреть как работает приложение.

## Technology stack:

- Java 11.0.15
- Spring Boot 2.7.2
- Maven 4.0.0
- MapStruct 1.5.2.Final
- Lombok 1.18.24
- Httpclient 4.5.13
- ORM Hibernate 5.6.10
- PostgreSQL 14.5
- H2database 2.1.214
- Docker 3.8
- Mockito 4.5.1
- JUnit 5.0
