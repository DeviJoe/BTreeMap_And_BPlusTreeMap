import java.util.AbstractMap;

public class Main {

    public static void main(String[] args) {
        BTreeMap<String, String> tree = new BTreeMap<String, String>(2);

        tree.put("Мама", "ЛЮБИМАЯ!");
        tree.put("Папа", "РОДНОЙ");
        tree.put("Дочь", "Классная");
        tree.put("Сестра", "Милая");
        tree.put("Бабушка", "Добрая");
        tree.put("Дедушка", "В очках");
        tree.put("Жена", "Люблю");
        tree.remove("Папа");

        System.out.println(tree.get("Папа"));
    }
}
