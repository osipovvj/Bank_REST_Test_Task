# Bank REST Test Task

REST API сервис для управления банковскими картами пользователей.

## Требования

- Java 17+
- Maven 3.8+
- Docker и Docker Compose
- MySQL 8+

## Запуск приложения

### 1. Клонирование репозитория

```bash
  git clone https://github.com/osipovvj/bank_rest_test_task.git
  cd bank_rest_test_task
```

### 2. Запуск базы данных

```bash
  docker-compose up -d
```

Это запустит MySQL сервер на порту 3306 со следующими параметрами:
- База данных: bank_rest_test_task_db
- Пользователь: effective
- Пароль: mobile

### 3. Запуск приложения

```bash
  mvn spring-boot:run
```

Приложение будет доступно по адресу: http://localhost:8080

## Конфигурация

- JWT токен действителен 24 часа
- Для шифрования номеров карт используется секретный ключ
- Миграции базы данных выполняются через Liquibase

## API Documentation

После запуска приложения документация API будет доступна по адресу: http://localhost:8080/swagger-ui.html