[![Build project](https://github.com/Romanow/logs-masking-example/actions/workflows/build.yml/badge.svg?branch=master)](https://github.com/Romanow/logs-masking-example/actions/workflows/build.yml)
[![codecov](https://codecov.io/gh/Romanow/logs-masking-example/branch/master/graph/badge.svg?token=Cckw6pHLh7)](https://codecov.io/gh/Romanow/logs-masking-example)
[![pre-commit](https://img.shields.io/badge/pre--commit-enabled-brightgreen?logo=pre-commit)](https://github.com/pre-commit/pre-commit)

# Masking logs

Для маскирования логов используется
плагин [`MaskingConverter`](log-masking-lib/src/main/kotlin/ru/romanow/logging/filter/MaskingConverter.kt).

Для его подключения используется annotation processor `org.apache.logging.log4j:log4j-core`, который собирает плагины
в [`$buildDir/tmp/kapt3/classes/main/META-INF/org/apache/logging/log4j/core/config/plugins/Log4j2Plugins.dat`](build/tmp/kapt3/classes/main/META-INF/org/apache/logging/log4j/core/config/plugins/Log4j2Plugins.dat).



## Тестирование

```shell
$ docker compose up -d

$ ./gradlew bootRun --args='--spring.profiles.active=docker'

$ curl http://localhost:8080/api/v1/send \
  -H 'Content-Type: application/json' \
  -d '{"lastName": "Alex"}'

$ docker compose logs -f fluent-bit

```

Вывод fluent-bit:

```
fluent-bit  | {"localTime"=>"2024-05-14T20:47:40.589Z", "level"=>"INFO", "text"=>"Initializing Servlet 'dispatcherServlet'", "levelInt"=>6, "loggerName"=>"org.springframework.web.servlet.DispatcherServlet", "threadName"=>"http-nio-8080-exec-1", "appName"=>"log-masking", "appType"=>"JAVA"}]
fluent-bit  | {"localTime"=>"2024-05-14T20:47:40.590Z", "level"=>"INFO", "text"=>"Completed initialization in 1 ms", "levelInt"=>6, "loggerName"=>"org.springframework.web.servlet.DispatcherServlet", "threadName"=>"http-nio-8080-exec-1", "appName"=>"log-masking", "appType"=>"JAVA"}]
fluent-bit  | {"localTime"=>"2024-05-14T20:47:40.620Z", "level"=>"INFO", "text"=>"{"lastName":"<masked>"}", "levelInt"=>6, "loggerName"=>"ru.romanow.logging.web.LogSender", "threadName"=>"http-nio-8080-exec-1", "appName"=>"log-masking", "appType"=>"JAVA"}]
```

## Правила маскирования

Тип поля:

* `email` –> `romanowalex@mail.ru` -> `r**********@mail.ru`
* `firstName` –> не маскируется
* `lastName`, `middleName` –> if (name.length > 7) `Ab*****c` else `A*****`
* `text` –> в зависимости от длины:
    * 1 -> `*`
    * 1..4 -> `1234` -> `1***`
    * 5..9 -> `123456789` -> `1******89` (length * 60%)
    * 10..15 -> `12345678901` -> `12*******01` (length * 60%)
    * 16 + -> `12345678901234567` -> `123***********567` (length * 60%)

Конфигурация:

```yaml
application:
    masking:
        -   field: fullName
            type: FULL_NAME
        -   field: lastName
            type: LAST_NAME
        -   field: email
            type: EMAIL
        -   field: JWT
            type: TEXT
        -   regex: Authorization\s*:\s*(\S+)
            type: TEXT
 ```
