# otus-hl-hw

Домашние задания по курсу [Otus](https://otus.ru) [Highload Architect](https://otus.ru/lessons/highloadarchitect/)

## ДЗ №1 Заготовка для социальной сети

Для запуска приложения необходимо выполнить команду

> docker compose -f ./deploy/docker-compose.yaml -p deploy up -d

После запуска происходит разворачивание инфраструктуры (БД + приложение) и автоматического тестирования, через запуск postman коллекции
Результат отработки тестов можно увидеть в html отчёте ./deploy/volumes/postman/newman/otus_hl_hw1-***.html

Для повторного ручного запуска тестов необходимо выполнить команду:
> postman collection run ./deploy/volumes/postman/collections/otus_hl_hw1.postman_collection.json

или:
> newman run ./deploy/volumes/postman/collections/otus_hl_hw1.postman_collection.json

После завершения проверок выполнить команду по удалению инфраструктуры:
> docker compose -f ./deploy/docker-compose.yaml -p deploy down --remove-orphans -v