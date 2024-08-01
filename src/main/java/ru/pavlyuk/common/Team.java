package ru.pavlyuk.common;

import java.util.ArrayList;
import java.util.Objects;

// Класс содержит название команды и список с участникам
// Также реализованы методы по добавлению и "доставанию" человека из списка
// Переопределены методы equals и hashcode для корректной отработки метода сортировки с кастомным компоратором
public class Team {
    private final String teamName;
    private final ArrayList<Person> people = new ArrayList<>();

    public Team(String teamName) {
        this.teamName = teamName;
    }

    public String getTeamName() {
        return teamName;
    }

    public void addPerson(Person person) {
        people.add(person);
    }

    public Person popTopPerson() {
        Person person = people.get(0);
        people.remove(0);
        return person;
    }

    public Integer getPersonCount() {
        return people.size();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (!(obj instanceof Team team))
            return false;
        return team.teamName.equals(teamName);

    }

    @Override
    public int hashCode() {
        return Objects.hash(teamName);
    }
}
