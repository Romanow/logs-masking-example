# Masking logs

[![Build project](https://github.com/Romanow/logs-masking-example/actions/workflows/build.yml/badge.svg?branch=master)](https://github.com/Romanow/logs-masking-example/actions/workflows/build.yml)
[![pre-commit](https://img.shields.io/badge/pre--commit-enabled-brightgreen?logo=pre-commit)](https://github.com/pre-commit/pre-commit)

Для маскирования логов используется
плагин [`MaskingConverter`](src/main/kotlin/ru/romanow/logging/filter/MaskingConverter.kt).

Для его подключения используется annotation processor `org.apache.logging.log4j:log4j-core`, который собирает плагины
в [`$buildDir/tmp/kapt3/classes/main/META-INF/org/apache/logging/log4j/core/config/plugins/Log4j2Plugins.dat`](build/tmp/kapt3/classes/main/META-INF/org/apache/logging/log4j/core/config/plugins/Log4j2Plugins.dat).

В шаблоне этот конвертер вызывается как `%mask{ %m }`:

```xml

<PatternLayout pattern="%mask{%d{yyy-MM-dd HH:mm:ss.SSS} [%t] %-5level %logger{36} - %msg%n}"/>
```

Отправка в ELK выполняется в формате JSON через fluent-bit (по адресу localhost:5170), для сборки сообщения
используется `JsonTemplateLayout` (шаблон [layout.json](src/main/resources/logging/layout.json), для маскирования
используется pattern resolver как `%mask{%m}`.

Локальное тестирование:

```shell
$ docker compose up -d

$ ./gradlew bootRun --args='--spring.profiles.active=docker'

$ curl http://localhost:8080/api/v1/send \
  -H 'Content-Type: application/json' \
  -d '{"name": "Alex"}'

$ docker compose logs -f fluent-bit

```

Вывод fluent-bit:

```
fluent-bit  | {"localTime"=>"2024-05-14T20:47:40.589Z", "level"=>"INFO", "text"=>"Initializing Servlet 'dispatcherServlet'", "levelInt"=>6, "loggerName"=>"org.springframework.web.servlet.DispatcherServlet", "threadName"=>"http-nio-8080-exec-1", "appName"=>"log-masking", "appType"=>"JAVA"}]
fluent-bit  | {"localTime"=>"2024-05-14T20:47:40.590Z", "level"=>"INFO", "text"=>"Completed initialization in 1 ms", "levelInt"=>6, "loggerName"=>"org.springframework.web.servlet.DispatcherServlet", "threadName"=>"http-nio-8080-exec-1", "appName"=>"log-masking", "appType"=>"JAVA"}]
fluent-bit  | {"localTime"=>"2024-05-14T20:47:40.620Z", "level"=>"INFO", "text"=>"{"name":"<masked>"}", "levelInt"=>6, "loggerName"=>"ru.romanow.logging.web.LogSender", "threadName"=>"http-nio-8080-exec-1", "appName"=>"log-masking", "appType"=>"JAVA"}]
```
