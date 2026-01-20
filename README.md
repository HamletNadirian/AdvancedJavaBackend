##### 1. Збірка проектів
Перед запуском контейнерів необхідно зібрати файли, що виконуються (JAR) для обох сервісів.
- Email Service (Gradle): Перейдіть в директорію email-service та виконайте:  
`./gradlew clean buil`
- Backend Service (Maven): Перейдіть до директорії Spring Boot and REST API і виконайте:  
`mvn clean package`
- Порада: Ви також можете використовувати вбудовані інструменти IntelliJ IDEA (вкладки Maven та Gradle у правій частині екрану).
##### 2. Конфігурація Gmail SMTP.
Для коректної роботи розсилки листів:

1. Увімкніть двоетапну верифікацію у вашому Google-акаунті, якщо вона не включена.
2. Перейдіть в App Passwords.
3. Створіть пароль для програми (назвіть його довільно, наприклад Java App).
4. Заміна даних: Змініть файли конфігурації:
   - gamlet.ossmanov@gmail.com — на вашу почту-відправник.
   - gamlet.ossmanov@gmail.com - на вашу пошту-отримувач.
   - Використовуйте згенерований пароль у змінних оточенні / файлі .env. (Для того, щоб знайти всі місця скористайтесь 
пошуком у IntelliJ рядка "хххххххххххххххх" )

##### 3. Запуск інфраструктури (Docker)

- Запустіть усі необхідні компоненти однією командою з кореневої папки проекту:
`docker-compose up -d`

- Сервіси також можна вручну запускати=) Наприклад, для, для elasticsearch ` docker run -d --name elasticsearch -p 9200:9200 -p 9300:9300 -e "discovery.type=single-node" -e "xpack.security.enabled=false" elasticsearch:8.17.10`

#### 4. API и Тестирование.
- Для відправки простого листа з с email-serivce
POST  http://localhost:8081/api/emails/send
  {
  "ownerRef": "Hamlet",
  "emailFrom": "gamlet.ossmanov@gmail.com",
  "emailTo": "gamlet.osmanov@gmail.com",
  "subject": "Test 3",
  "text": "Test email"
  }
  тобто після виконання запиту надходить лист на gamlet.osmanov@gmail.com
  ![Пример получения письма](img/email.png)
- Для додавання сутності №1 и 2
POST  http://localhost:8080/api/producers
{
"name": "Wigram",
"country": "UK"
}

POST  http://localhost:8080/api/movies
  {
  "title": "Sherlock Holmes 2",
  "releaseDate": 2011,
  "producerId": 1,
  "genre": "Sci-Fi",
  "description": "Sherlock Holmes and his sidekick Dr. Watson join forces to outwit and bring down their fiercest adversary, Professor Moriarty",
  "rating": 7.4
  }
тобто після виконання запиту приходить, (умовному адміну), що був доданий фільм.
![Приклад отримання листа після додавання фільму](img/new_movie_added.png)

Також доступний інтерфейс сваггера для виконання операції з сутностями http://localhost:8080/swagger-ui/index.html

Примітка
Якщо вам треба налаштувати або підглянути БД із завдання №2, то ось дані.
`spring.datasource.username=postgres
spring.datasource.password=hamletnadirian
spring.datasource.url=jdbc:postgresql://localhost:5432/movies_db
` посилання на README.md завдання 2,якщо вам потрібно підглянути  https://github.com/HamletNadirian/AdvancedJavaBackend/tree/master/Spring%20Boot%20and%20REST%20API