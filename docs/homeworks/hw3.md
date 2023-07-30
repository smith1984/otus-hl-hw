## ДЗ №3 Репликация

## Часть 1 - Асинхронная репликация

В этой части ДЗ будет произведено поднятие реплики в асинхронном режиме, с которой будут читать данные методы по поиску пользователя по идентификатору или имени и фамилии.
Результат представлен в виде графиков потребления ресурсов на master и slave nodes.

### Разворачивание приложения
Для запуска приложения необходимо выполнить команду

> docker compose -f ./deploy/docker-compose.yaml -p deploy up -d

После запуска происходит разворачивание инфраструктуры (БД, приложение, система мониторинга)

### Настройка асинхронной репликации
1. Настройка master node
Создать пользователя для репликации
```
create role replicator with login replication password 'pass';
```
в postgresql.conf необходимо добавить
```
ssl = off
wal_level = replica
max_wal_senders = 4
synchronous_commit = local
```
в pg_hba.conf
```
host    replication     replicator      <172.21.0.0/16 - указать вашу подсеть через которую будет происходить репликация>           md5
```
2. Настройки slave node
   в postgresql.conf необходимо добавить
```
ssl = off
primary_conninfo = 'host=postgres port=5432 user=replicator password=pass application_name=postgres-slave'
```

### Результаты тестирования
#### 1. Без репликации

1.1 Запрос поиска пользователя по ID
![user_master_mon.png](images%2Fhw3%2Fuser_master_mon.png)
![user_master_res.png](images%2Fhw3%2Fuser_master_res.png)

1.2 Запрос поиска пользователя по имени и фамилии
![search_master_mon.png](images%2Fhw3%2Fsearch_master_mon.png)
![search_master_res.png](images%2Fhw3%2Fsearch_master_res.png)

#### 2. С репликацией и запросами на чтения slave node

2.1 Запрос поиска пользователя по ID
![user_slave_mon.png](images%2Fhw3%2Fuser_slave_mon.png)
![user_slave_res_master.png](images%2Fhw3%2Fuser_slave_res_master.png)
![user_slave_res_slave.png](images%2Fhw3%2Fuser_slave_res_slave.png)

2.2 Запрос поиска пользователя по имени и фамилии
![search_slave_mon.png](images%2Fhw3%2Fsearch_slave_mon.png)
![search_slave_res_master.png](images%2Fhw3%2Fsearch_slave_res_master.png)
![search_slave_res_slave.png](images%2Fhw3%2Fsearch_slave_res_slave.png)

### Удаление приложения
После завершения проверок выполнить команду по удалению инфраструктуры:
> docker compose -f ./deploy/docker-compose.yaml -p deploy down --remove-orphans -v

## Часть 2 - Полусинхронная репликация
В этой части ДЗ будет описан процесс создания полусинхронной репликации и проведение эксперимента по потери master и переключение на новый master

#### 1. Подключеам 2-уй slave node
```
#Создаём бэкап на мастере
docker exec -it postgres bash                                    
mkdir backup
pg_basebackup -h postgres -D /backup -U replicator -v -P --wal-method=stream
#Вводим пароль от пользователя replicator

#Копируем бэкап на локальный хост
docker cp postgres:/backup ./deploy/volumes/postgres/data_slave2

# Маркируем базу, что она является slave
touch ./deploy/volumes/postgres/data_slave2/standby.signal 

# Добавляем в конфигурации БД настройку
primary_conninfo = 'host=postgres port=5432 user=replicator password=pass application_name=secondslave'

# запускаем контейнер postgres-slave-2 (параметры контейнера находятся в ./deploy/docker-compose.yaml)

```

#### 2. Настраиваем полусинхронную репликацию на master node
```
#Проверяем текущее состояние репликации
select application_name, sync_state from pg_stat_replication;
# Обе slave nodes находятся в асинхронном режиме
#firstslave,async
#secondslave,async

#Меняем режим репликации в конфигурации на master node
synchronous_commit = remote_write
synchronous_standby_names = 'FIRST 1 (firstslave, secondslave)'

#Применяем конфигурацию и проверяем состояние репликации
select pg_reload_conf();
select application_name, sync_state from pg_stat_replication;
#firstslave,sync
#secondslave,potential
```

#### 3. Эмулируем отключение master node при записи в неё данных
```
# Загружаем нагрузку на master
# Проверяем что данные реплицируется на slave nodes
# Останавливаем master
# Останавливаем нагрузку
# Обе slave node обработали одну и туже последнюю транзакцию 
# 2023-07-30 04:00:22.661 UTC [28] LOG:  waiting for WAL to become available at 0/D091578
```


#### 3. Переключаем один из slave на master
```
#Делаем мастером firstslave
select * from pg_promote();
#Изменяем конфигурацию на firstslave
synchronous_commit = on
synchronous_standby_names = 'ANY 1 (postgres, secondslave)'

#Изменяем конфигурацию на secondslave
primary_conninfo = 'host=postgres-slave port=5432 user=replicator password=pass application_name=secondslave'

#Проверяем статус репликации
select application_name, sync_state from pg_stat_replication;
secondslave,quorum

#Переключаем приложение на запись в новый master и чтение с secondslave
```