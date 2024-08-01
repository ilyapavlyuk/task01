package ru.pavlyuk.common;

import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) throws IOException {
        List<Team> teams = getTeamsFromTxt();
        List<String> listOfPeople = getPeopleList(teams);
        saveListToTxt(listOfPeople, "результат.txt");
    }

    public static InputStream getUserFile () {
        // Метод просит у пользователя полный путь к файлу и считавает его

        Scanner sc = new Scanner(System.in);
        System.out.println("Введите абсолютный путь до файла:");
        String filePath = sc.nextLine();

        try {
            File file = new File(filePath);
            InputStream is = new FileInputStream(file);
            System.out.println("Файл загружен");
            return is;
        } catch (IOException e) {
            System.out.println("Не удалось загрузить файл");
            return null;
        }
    }

    public static List<Team> getTeamsFromTxt() throws IOException {
        // Читаем файл по пользовательскому пути, обрабатываем исключения, обрабатываем документ
        // Возвращаем словарь с ключом команды и значением в качестве списка её участников

        System.out.println("Пытаюсь загрузить файл входные.txt из папки ресурсов...");
        InputStream is = Main.class.getClassLoader().getResourceAsStream("входные.txt");

        // Если программа не находит файл в папке resources, то возвращает метод задания своего пути к файлу
        if (is == null) {
            System.out.println("Файл не найден");
            is = getUserFile();
        } else {
            System.out.println("Файл загружен успешно!");
        }

        List<Team> teams = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(Objects.requireNonNull(is)))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] split = line.split(",");
                if (split.length != 2) {
                    System.out.println("Неверный формат строки в файле (Формат: ФИО,команда)");
                    return null;
                }
                // Создает экземпляр класса Team
                // Если такой уже есть в списке, то добавляет нового человека в команду
                // Иначе создаёт новую команду с 1 человеком
                Team team = new Team(split[1]);
                if (teams.contains(team)) {
                    teams.get(teams.indexOf(team))
                            .addPerson(new Person(split[0]));
                } else {
                    team.addPerson(new Person(split[0]));
                    teams.add(team);
                }
            }
            is.close();
        } catch (IOException e) {
            System.out.println("Не удалось прочитать файл");
            is.close();
            System.exit(0);
        }

        sortTeamsListByPeopleAmount(teams);
        return teams;
    }

    public static boolean checkPossibleTeams (List<Team> teams) {
        // Проверка на пустой список участников
        // Также проверка, что наибольшее число участников не превосходит на единицу сумму участников других команд
        // При граничном случае выйдет так, что участники одной команды стоят через 1

        int amountParticipants = 0;
        int maxParticipants = 0;
        int currentAmount;


        for (Team team : teams) {
            currentAmount = team.getPersonCount();
            amountParticipants += currentAmount;
            if (currentAmount > maxParticipants) {
                maxParticipants = currentAmount;
            }
        }
        return maxParticipants - 1 <= (amountParticipants - maxParticipants);
    }

    public static List<String> getPeopleList(List<Team> teams) {
        if (!checkPossibleTeams(teams)) {
            System.out.println("Невозможно расположить участников так, чтобы это соответствовало правилам");
            System.exit(0);
        }

        List<String> peopleList = new ArrayList<>();
        Team lastTeam = null;
        Team currentTeam;

        // Реализуемый алгоритм:
        // берем участника из команды с наибольшим числом участников
        // следующий участник будет из обязательно другой команды с наибольшим числом участников
        // если в команде не осталось участников, убираем команду из списков
        while (!teams.isEmpty()) {
            for (Team team : teams) {
                currentTeam = team;
                if (!currentTeam.equals(lastTeam)) {
                    peopleList.add(currentTeam.popTopPerson().name() + "," + currentTeam.getTeamName());
                    lastTeam = currentTeam;
                    if (currentTeam.getPersonCount() == 0) {
                        teams.remove(currentTeam);
                    }
                    break;
                }
            }
            sortTeamsListByPeopleAmount(teams);
        }

        return peopleList;
    }

    public static void sortTeamsListByPeopleAmount (List<Team> teams) {
        // Сортируем список команд по количеству участников в ней от большего к меньшему
        teams.sort(Comparator.comparing(Team::getPersonCount, Comparator.reverseOrder()));
    }

    public static void saveListToTxt(List<String> list, String fileName) {
        // Сохраняем лист в файл

        System.out.println("Сохраняю информацию в файл");
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))) {
            for (String line : list) {
                bw.write(line);
                bw.newLine();
            }
            System.out.println("Успешно сохранено!");
        } catch (IOException e) {
            System.out.println("Не удалось сохранить файл");
            System.exit(0);
        }
    }
}