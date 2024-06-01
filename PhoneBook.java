// для корректной работы используйте латинские символы

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class PhoneBook {

    public static void main(String[] args) {
        String fileName = "phone_book.txt";
        Map<String, List<String>> phoneBook = readData(fileName);
        Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);

        while (true) {
            showMainMenu();
            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    displayData(phoneBook);
                    break;
                case "2":
                    addRecord(phoneBook, fileName, scanner);
                    break;
                case "3":
                    editRecord(phoneBook, fileName, scanner);
                    break;
                case "4":
                    deleteRecord(phoneBook, fileName, scanner);
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Неверный выбор. Попробуйте снова.");
            }
        }
    }

    public static Map<String, List<String>> readData(String fileName) {
        Map<String, List<String>> phoneBook = new HashMap<>();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(fileName), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] entry = line.split(";");
                String name = entry[0] + " " + entry[1] + " " + entry[2];
                List<String> phoneNumbers = Arrays.asList(entry[3].split(","));
                phoneBook.put(name, phoneNumbers);
            }
            System.out.println("Данные успешно импортированы.");
        } catch (FileNotFoundException e) {
            System.out.println("Файл не найден. Создание нового файла.");
            System.out.println("Справочник пустой.");
            try {
                new File(fileName).createNewFile();
            } catch (IOException ex) {
                System.out.println("Ошибка создания файла.");
            }
        } catch (IOException e) {
            System.out.println("Ошибка чтения файла.");
        }
        return phoneBook;
    }

    public static void saveData(String fileName, Map<String, List<String>> phoneBook) {
        try (BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(fileName), StandardCharsets.UTF_8))) {
            for (Map.Entry<String, List<String>> entry : phoneBook.entrySet()) {
                String[] nameParts = entry.getKey().split(" ");
                bw.write(String.join(";", nameParts[0], nameParts[1], nameParts[2],
                        String.join(",", entry.getValue())) + "\n");
            }
            System.out.println("Данные успешно сохранены.");
        } catch (IOException e) {
            System.out.println("Ошибка записи файла.");
        }
    }

    public static void displayData(Map<String, List<String>> phoneBook) {
        if (!phoneBook.isEmpty()) {
            List<Map.Entry<String, List<String>>> sortedEntries = new ArrayList<>(phoneBook.entrySet());
            sortedEntries.sort((e1, e2) -> Integer.compare(e2.getValue().size(), e1.getValue().size()));

            System.out.printf("%-5s %-15s %-15s %-15s %-30s\n", "№", "Фамилия", "Имя", "Отчество", "Телефоны");
            System.out.println("--------------------------------------------------------------");
            int counter = 1;
            for (Map.Entry<String, List<String>> entry : sortedEntries) {
                String[] nameParts = entry.getKey().split(" ");
                System.out.printf("%-5d %-15s %-15s %-15s %-30s\n", counter++, nameParts[0], nameParts[1], nameParts[2],
                        String.join(", ", entry.getValue()));
            }
        } else {
            System.out.println("Телефонная книга пуста.");
        }
    }

    public static void addRecord(Map<String, List<String>> phoneBook, String fileName, Scanner scanner) {
        System.out.print("Введите фамилию: ");
        String lastName = scanner.nextLine();
        System.out.print("Введите имя: ");
        String firstName = scanner.nextLine();
        System.out.print("Введите отчество: ");
        String middleName = scanner.nextLine();
        String name = lastName + " " + firstName + " " + middleName;

        List<String> phoneNumbers = phoneBook.getOrDefault(name, new ArrayList<>());

        while (true) {
            System.out.print("Введите номер телефона (или оставьте пустым для завершения): ");
            String phoneNumber = scanner.nextLine();
            if (phoneNumber.isEmpty())
                break;
            phoneNumbers.add(phoneNumber);
        }

        phoneBook.put(name, phoneNumbers);
        saveData(fileName, phoneBook);
    }

    public static void editRecord(Map<String, List<String>> phoneBook, String fileName, Scanner scanner) {
        System.out.println("Для возврата в главное меню введите 0");
        System.out.print("Введите полное имя записи или номер записи для редактирования: ");
        String input = scanner.nextLine();
        if (input.equals("0")) {
            System.out.println("Редактирование отменено.");
            return;
        }

        if (phoneBook.containsKey(input)) {
            editEntry(phoneBook, fileName, input, scanner);
        } else {
            try {
                int index = Integer.parseInt(input) - 1;
                List<Map.Entry<String, List<String>>> entries = new ArrayList<>(phoneBook.entrySet());
                if (index >= 0 && index < entries.size()) {
                    String name = entries.get(index).getKey();
                    editEntry(phoneBook, fileName, name, scanner);
                } else {
                    System.out.println("Запись не найдена.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Некорректный ввод.");
            }
        }
    }

    private static void editEntry(Map<String, List<String>> phoneBook, String fileName, String name, Scanner scanner) {
        List<String> phoneNumbers = new ArrayList<>(phoneBook.get(name)); // Создаем копию списка
        System.out.println("Текущие номера телефона: " + String.join(", ", phoneNumbers));
        System.out.print("Введите новый номер телефона или оставьте пустым: ");
        String newPhoneNumber = scanner.nextLine();
        if (!newPhoneNumber.isEmpty()) {
            phoneNumbers.add(newPhoneNumber);
            phoneBook.put(name, phoneNumbers);
            saveData(fileName, phoneBook);
            System.out.println("Запись \"" + name + "\" отредактирована.");
        } else {
            System.out.println("Редактирование отменено.");
        }
    }

    public static void deleteRecord(Map<String, List<String>> phoneBook, String fileName, Scanner scanner) {
        System.out.println("Внимание!!! Данное действие не обратимо!");
        System.out.println("Для возврата в главное меню введите 0");
        System.out.print("Введите полное имя записи или номер записи для удаления: ");
        String input = scanner.nextLine();
        if (input.equals("0")) {
            System.out.println("Удаление отменено.");
            return;
        }

        if (phoneBook.containsKey(input)) {
            phoneBook.remove(input);
            saveData(fileName, phoneBook);
            System.out.println("Запись \"" + input + "\" удалена из справочника.");
        } else {
            try {
                int index = Integer.parseInt(input) - 1;
                List<Map.Entry<String, List<String>>> entries = new ArrayList<>(phoneBook.entrySet());
                if (index >= 0 && index < entries.size()) {
                    String name = entries.get(index).getKey();
                    phoneBook.remove(name);
                    saveData(fileName, phoneBook);
                    System.out.println("Запись \"" + name + "\" удалена из справочника.");
                } else {
                    System.out.println("Запись не найдена.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Некорректный ввод.");
            }
        }
    }

    public static void showMainMenu() {
        System.out.println("\nГлавное меню:");
        System.out.println("1. Показать все записи");
        System.out.println("2. Добавить запись");
        System.out.println("3. Редактировать запись");
        System.out.println("4. Удалить запись");
        System.out.println("0. Выход");
        System.out.print("Выберите действие: ");
    }
}
