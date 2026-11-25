# Талаптардың орындалуы: талап пен код сәйкестігі

> Жол нөмірлері `java-project.zip` ішіндегі соңғы нұсқаға сәйкес. Егер код өзгерсе, нөмірлер ауысуы мүмкін, сондықтан класс пен әдіс атауына да қараңыз.

## Жалпы кесте

| Талап | Қайда кодта |
|---|---|
| ООП (интерфейс, абстракт класс, мұрагерлік, полиморфизм, enum) | `model/` пакеті |
| Коллекциялар | `Order`, `Menu`, `Kitchen`, `DeliveryService`, `ConsoleApp` |
| Файлдар (CSV) | `storage/CsvStorage.java` |
| Тестілеу (Maven, JUnit) | `pom.xml`, `src/test/java/` (16 тест) |

## 1. ООП

| Түсінік | Қайда | Қалай жұмыс істейді |
|---|---|---|
| **Интерфейс** | `model/Payable.java` (4-жол) | `calculateTotal()` контракты. Оны `Order` (`Order.java:15`) және `Bill` (`Bill.java:7`) іске асырады |
| **Абстракт класс** | `model/MenuItem.java` (4-жол) | Абстракт әдіс `getDescription()` (33-жол). `MenuItem` объектісін тікелей жасау мүмкін емес |
| **Мұрагерлік** | `Pizza.java:4`, `Drink.java:4` | `Pizza extends MenuItem`, `Drink extends MenuItem`. Сондай-ақ `EmptyOrderException` және `WrongItemException` `Exception` классынан мұра алады |
| **Полиморфизм** | `Order.calculateTotal()` (`Order.java:169`, 172-жол) | `List<MenuItem>` ішінде пицца да, сусын да бар, бірақ `item.getPrice()` шақырылғанда әрқайсысы өз есебін орындайды: `Pizza.getPrice()` (`Pizza.java:21`) өлшемге қарай бағаны көбейтеді, ал `MenuItem.getPrice()` (`MenuItem.java:28`) базалық бағаны береді |
| | `Bill.print()` (`Bill.java:25`) | `item.getDescription()` әр түрлі позицияға әр түрлі мәтін қайтарады (`Pizza.java:30`, `Drink.java:19`) |
| **enum** | `model/OrderStatus.java` (4-жол) | NEW, COOKING, READY, DELIVERING, DONE. Статус тек `Order` әдістерімен ретімен өзгереді: `startCooking()` (126), `markReady()` (133), `startDelivery()` (140), `complete()` (157) |
| **Инкапсуляция** | барлық `model/` кластары | өрістер `private`, статусты тек `Order` өзгертеді, `getItems()` өзгертуге болмайтын тізім қайтарады |

**Қорғауда қысқаша:** «Полиморфизм: `Order` тапсырыс құрамын `List<MenuItem>` деп сақтайды және `getPrice()` шақырады. Пицца мен сусын бұл әдісті әртүрлі орындайды, бірақ `Order` кім екенін білмейді».

## 2. Коллекциялар

| Коллекция | Қайда | Не үшін |
|---|---|---|
| `List<MenuItem>` (ArrayList) | `Order.java:17` | тапсырыс құрамы |
| `List<Order>` (ArrayList) | `ConsoleApp.java:35` (`history`) | тапсырыстар тарихы |
| `Set<String>` (HashSet) | `ConsoleApp.java:105` (`busyCouriers`) | жеткізудегі курьерлер аттары, қайталанбайды |
| `Map<Integer, MenuItem>` (HashMap) | `service/Menu.java:12` | мәзір, нөмір бойынша жылдам іздеу |
| `Map<String, Integer>` (HashMap) | `Order.java:103` (`countByDescription`) | қаптама мен тапсырыс құрамын санап салыстыру |
| `Queue<Order>` (LinkedList) | `service/Kitchen.java:11` | асхана кезегі (FIFO) |
| `Queue<Courier>` (LinkedList) | `service/DeliveryService.java:12` | бос курьерлер кезегі |

## 3. Файлдар (CSV)

| Әрекет | Қайда |
|---|---|
| Мәзірді оқу | `CsvStorage.loadMenu()` (`CsvStorage.java:24`), `Files.readAllLines` (26) |
| Мәзірді жазу | `CsvStorage.saveMenu()` (51) |
| Тапсырыстарды жазу | `CsvStorage.saveOrders()` (66), `Files.write` (142) |
| Тапсырыстарды оқу | `CsvStorage.loadOrders()` (83) |
| Файлдар | `data/menu.csv`, `data/orders.csv` |

## 4. Тестілеу

- Maven жобасы: `pom.xml` (JUnit 5, `maven-surefire-plugin`).
- Тесттер саны: **16**.

| Файл | Тест саны | Не тексереді |
|---|---|---|
| `model/OrderTest.java` | 9 | сома, статус тізбегі, қаптаманы тексеру (Гавай ≠ Пепперони), тексерілмеген тапсырысты беруге болмайды |
| `model/PizzaTest.java` | 2 | баға мен өлшем |
| `service/KitchenTest.java` | 2 | бос тапсырыс, FIFO реті |
| `service/DeliveryServiceTest.java` | 3 | курьерді автоматты тағайындау |
