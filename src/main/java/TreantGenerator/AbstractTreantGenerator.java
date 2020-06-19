package TreantGenerator;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Класс декларирует базовые методы для генерации структурного кода дерева для Treant.js
 * Полученная на выходе строка не нуждается в обработке и сжатии babel
 */
public abstract class AbstractTreantGenerator {

    /**
     * Контейнер для {@code div} блока
     */
    private String container;
    /**
     * Имя переменной, хранящей структуру дерева
     */
    private String treeType;
    /**
     * КОД ГЕНЕРИРУЕТСЯ СЮДА!
     */
    protected String code;
    /**
     * Тип сочленений узлов
     */
    private String connectorsType = "bCurve";
    /**
     * Размер отступов между уровнями
     */
    private Integer levelSeparation = 100;
    /**
     * Размер отступов между узлами
     */
    private Integer siblingSeparation = 70;
    /**
     * Путь к JS файлу
     */
    private final String FILE_PATH;

    public String getContainer() {
        return container;
    }

    public String getTreeType() {
        return treeType;
    }

    public AbstractTreantGenerator(String container, String treeType, String filePath) {
        this.container = container;
        this.treeType = treeType;
        FILE_PATH = filePath;
    }

    public AbstractTreantGenerator(String container, String filePath) {
        this.container = container;
        treeType = "simple_chart_config";
        FILE_PATH = filePath;
    }

    /**
     * Генерация заголовка
     */
    protected void generateHeader() {
        code = "let " + treeType + " = {chart:";
        code += "{";
        code += "container: \"#" + container + "\",";
        code += "connectors: {type: \"" + connectorsType + "\"},";
        code += "levelSeparation: " + levelSeparation.toString() + ", ";
        code += "siblingSeparation: " + siblingSeparation.toString() + ", ";
        code += "}, ";
        code += "nodeStructure: {";
    }

    /**
     * Генерация "подвала"
     */
    protected void generateFooter() {
        code += "}};";
    }

    /**
     * Метод создает JS файл со структурой, если такого файла нет, или перезаписывает старый файл со структурой
     * @return файл со структурой
     */
    protected File createJSFile()  {

        File file = new File(FILE_PATH);

        try {
            if (file.createNewFile()) {
                FileWriter fileWriter = new FileWriter(FILE_PATH, false);
                fileWriter.write(code);
                fileWriter.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return file;
    }

    /**
     * Метод предназначен для переопределения!
     * В этом методе происходит пошаговая "сборка" кода файла и запись в файл
     * @return
     */
    public abstract File generate();

    /**
     * Метод предназначен для переопределения!
     * Метод генерирует всю необходимую информацию для описания узла
     * @param label строка меток
     * @return сгенерированая строка
     */
    protected abstract String generateNodeView(String label);

    /**
     * Метод предназначен для переопределения!
     * Метод рекурсивно генерирует всю структуру потомков узла + вложенные узлы
     * @param nodes лист потомков
     * @return сгенерированная строка
     * @throws Exception
     */
    protected abstract String generateChildrenArray(List<AbstractTreeTreantNode> nodes) throws Exception;

}
