## ДЗ №2 Производительность индексов

### Разворачивание приложения
Для запуска приложения необходимо выполнить команду

> docker compose -f ./deploy/docker-compose.yaml -p deploy up -d

После запуска происходит разворачивание инфраструктуры (БД, приложение, система мониторинга)
### Результаты нагрузочного тестирования
1. Без индекса
![10_rps_no_index.png](images%2Fhw2%2F10_rps_no_index.png)
![100_rps_no_index.png](images%2Fhw2%2F100_rps_no_index.png)
2. С индексом
![10_rps_index.png](images%2Fhw2%2F10_rps_index.png)
![100_rps_index.png](images%2Fhw2%2F100_rps_index.png)
![1000_rps_index.png](images%2Fhw2%2F1000_rps_index.png)
### Запросы
1. Добавление индекса
```
create extension pg_trgm;
create index if not exists namestrgm_ind on users using gist (first_name gist_trgm_ops, second_name gist_trgm_ops) include (id, age, birthdate, biography, city);
```

2. Explain
```
explain analyze SELECT users.first_name, users.second_name, users.id, users.age, users.birthdate, users.biography, users.city FROM users WHERE (users.first_name LIKE 'Ада%') AND (users.second_name LIKE 'Гуля%') order by id;

Sort  (cost=8.43..8.44 rows=1 width=341) (actual time=0.452..0.453 rows=8 loops=1)
  Sort Key: id
  Sort Method: quicksort  Memory: 28kB
  ->  Index Scan using namestrgm_ind on users  (cost=0.41..8.42 rows=1 width=341) (actual time=0.103..0.442 rows=8 loops=1)
        Index Cond: ((first_name ~~ 'Ада%'::text) AND (second_name ~~ 'Гуля%'::text))
Planning Time: 0.208 ms
Execution Time: 0.469 ms

```

### Выводы
Для выполнения ДЗ выбран gist индекс из extension pg_trgm, так же тестировался комбинированный индекс btree_gist, gist который позволяет индексировать данные для полнотекстового поиска (использование like) по полям first_name, second_name, а btree для получения данных в отсортированном виде по полю id.
Но производительность запросов была на порядок ниже чем gist индекс из extension pg_trgm, в котором сортировка осуществлялась отдельно (см. explain)

### Удаление приложения
После завершения проверок выполнить команду по удалению инфраструктуры:
> docker compose -f ./deploy/docker-compose.yaml -p deploy down --remove-orphans -v