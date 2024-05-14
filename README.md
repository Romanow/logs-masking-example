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

fluent-bit | [1] tcp.0: {"localTime"= "level"= "text"= "levelInt"= "loggerName"= "threadName"= "appName"= "appType"= >"2024-05-14T20:47:40.589Z", >"INFO", >"Initializing Servlet 'dispatcherServlet'", >6, >"org.springframework.web.servlet.DispatcherServlet", >"http-nio-8080-exec-1", >"log-masking", >"JAVA"}]
fluent-bit | [2] tcp.0: {"localTime"= "level"= "text"= "levelInt"= "loggerName"= "threadName"= "appName"= "appType"= >"2024-05-14T20:47:40.590Z", >"INFO", >"Completed initialization in 1 ms", >6, >"org.springframework.web.servlet.DispatcherServlet", >"http-nio-8080-exec-1", >"log-masking", >"JAVA"}]
fluent-bit | [3] tcp.0: {"localTime"= "level"= "text"= "levelInt"= "loggerName"= "threadName"= "appName"= "appType"= >"2024-05-14T20:47:40.620Z", >"INFO", >"{"name":" <masked >"}", >6, >"ru.romanow.logging.web.LogSender", >"http-nio-8080-exec-1", >"log-masking", >"JAVA"}]

```
