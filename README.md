# B Tree & B+ Tree Map structure

[![Build Status](https://travis-ci.org/joemccann/dillinger.svg?branch=master)](https://travis-ci.org/joemccann/dillinger)

Данная программа реализует методы интерфейса Map (не наследуется от встроенного интерфейса Map)
при помощи структур B и B+ деревьев. Временная сложность алгоритма O(log(n)). 

Реализована в рамках дисциплины "Архитектуры и структуры данных" факультета Компьютерных Наук Воронежского Государственного Университета

Структура документа: 
   -
   ```java
    java (root catalog)
    --- map (package)
    ------ BPlusTreeMap.java
    ------ BTreeMap.java
    ------ IMap.java (interface)
    --- TreantGenerator (package)
    ------ AbstractTreantGenerator.java
    ------ AbstractTreeTreantNode.java
    ------ iTreeMapGenerator (package)
    --------- TreantTreeIMapGenerator.java
    --------- TreeTreantNode.java
    
```
Принцип работы:
-
Пройдемся по структуре, чтобы понять, как тут все работает. Есть два крупных пакета: 
   ```java
map
TreantGenerator
```
В пакете <i>map</i> хранится код двух деревьев + интерфейс, чтобы сразу приступить к работе. Просто создай
экземпляр нужного дерева у себя в проекте и автоматически получишь полностью рабочую <i>Map</i> со всеми преимуществами<br>
В лругой директории хранится генератор кода для фреймворка Treant.js, он позволяет прямо из Java, 
собрав простой обработчик структуры, построить любое дерево. Я уже разработал сам сборщик и 
написал обработчик для структуры IMap - все остальное на Вашей совести.

Использованная литература:
-
| Название | Источник |
| -------- | -------- |
| Wikipedia (B Tree) | [Тык сюда][w1] |
| Wikipedia (B+ Tree) | [Тык сюда][w2] |
| B Tree Visualization | [Тык сюда][vis] |
| Delete Operaton in B Tree | [Тык сюда][del] |
| Статья на Хабре | [Тык сюда][h1] |
| B tree at Database on GitHub | [Тык сюда][git1] |
| B+ tree on C on GitHub | [Тык сюда][git2] |

И еще множество англоязычных и русскоязычных статей, методичка Томского университета с пошаговым описанием 
алгоритма одного из студентов. Некоторые источники затерялись,
сейчас вспомнить уже затруднительно.

[w1]: https://ru.wikipedia.org/wiki/B-%D0%B4%D0%B5%D1%80%D0%B5%D0%B2%D0%BE
[w2]:  https://ru.wikipedia.org/wiki/B%E2%81%BA-%D0%B4%D0%B5%D1%80%D0%B5%D0%B2%D0%BE
[vis]: https://www.cs.usfca.edu/~galles/visualization/BTree.html
[del]: https://www.geeksforgeeks.org/delete-operation-in-b-tree/
[h1]: https://habr.com/ru/post/114154/
[git1]: https://github.com/niteshkumartiwari/B-Plus-Tree
[git2]: https://github.com/parachvte/B-Plus-Tree


Выражаю благодарность 
-
    Лектор: ст.п Соломатин Д.И.
    Практик: Сидоркин А.А.