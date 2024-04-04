# quizarius

## Для запуска в dev-моде: 
* Поднять окружение ./src/main/docker/dev/docker-compose.yaml
* Запустить приложение ./gradlew quarkusDev

#### Приложение запускается на порту 8081
#### Swagger: /q/swagger-ui/
#### Пользователь по умолчанию: login: quizarius@itis.ru, password: quizarius
#### Для получения токена отправить логин и пароль на /security/authenticate
#### Токен присылать в заголоке Authorization с префиксом Bearer