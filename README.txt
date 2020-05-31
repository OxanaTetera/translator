Для запуска необходима установленная java версии 8 и выше.

Для создания .jar должен быть установлен maven версии не ниже 3.
Выполнить в командной строке
mvn clean install

Затем выполнить
mvn dependency:copy-dependencies

Перенести translator-0.0.1-SNAPSHOT.jar из папки target в папку dependency.

Для запуска открыть терминал по адресу /yandex_translate_v2/target/dependency
Выполнить команду: java -jar translator-0.0.1-SNAPSHOT.jar

Для проверки необходимо запустить браузер Chrome, перейти в режим отладки и выполнить запрос к сервису, например:
fetch('/translate', 
  { 
    method: 'POST', 
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({text: 'Луна спутник земли', lang: 'ru-en' })
  }
).then(result => result.json().then(console.log))

Так же проект можно запустить из среды разработки.
Для открытия через IntelliJ Idea, необходимо перейти в среду разработки и выбрать File->Open.
Выбрать файл /yandex_translate_v2/pom.xml