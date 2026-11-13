# Dodo Pizza Kazakhstan: тапсырыс қабылдау жүйесі

## Жобаның мақсаты
Dodo Pizza Kazakhstan тапсырыстарындағы құрам қателерін азайтуға арналған жеңілдетілген консольдік ақпараттық жүйе (Java 17).

## Зерттелген ұйым
Dodo Pizza: 2011 жылы құрылған халықаралық пиццерия желісі, Қазақстанда 2015 жылдан жұмыс істейді. Толық сипаттама: [docs/research.md](docs/research.md).

## Мүмкіндіктер
- мәзірді `data/menu.csv` файлынан жүктеу;
- тапсырыс жасау, позиция қосу және жою, пицца өлшемін таңдау (25 / 30 / 35 см);
- құрамы бар чек (`Bill`);
- бос тапсырыс қабылданбайды (`EmptyOrderException`);
- асхана кезегі (FIFO), статустар NEW → COOKING → READY → DELIVERING → DONE;
- курьер автоматты тағайындалады (бос курьерлер кезегі);
- тапсырыстар `data/orders.csv` файлына сақталады және қайта іске қосқанда жүктеледі.

## Іске қосу
Талап: JDK 17+ және Maven.

```
mvn compile
mvn exec:java
mvn test
```

IntelliJ IDEA-да: `src/main/java/kz/dodo/app/ConsoleApp.java` файлын ашып, `main` әдісінің жанындағы жасыл үшбұрышты басыңыз.

Бағдарламаны жоба түбірінен іске қосыңыз (`data/` папкасы осы жерде болуы керек).

## Құрылым
```
docs/            research.md, uml/ (use-case, class, sequence, activity, state)
src/main/java/   kz.dodo.model, service, storage, exception, app
src/test/java/   JUnit тесттері
data/            menu.csv, orders.csv
```

## UML диаграммалары
Use Case, Class, Sequence және Activity (проблеманы қалай шешетінімізді көрсетеді) диаграммалары `docs/uml/` папкасында. Қосымша: State диаграммасы.

## Экран суреттері
(Бағдарламаны іске қосып, мәзір, чек және тапсырыс тарихының скриншоттарын осы жерге қосыңыз: `docs/screens/` папкасына салып, мына түрде сілтеме жасаңыз: `![мәзір](docs/screens/menu.png)`)
