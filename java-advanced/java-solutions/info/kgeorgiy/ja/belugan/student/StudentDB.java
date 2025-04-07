package info.kgeorgiy.ja.belugan.student;

import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import info.kgeorgiy.java.advanced.student.GroupName;
import info.kgeorgiy.java.advanced.student.Student;
import info.kgeorgiy.java.advanced.student.StudentQuery;


public class StudentDB implements StudentQuery {

    private static final Comparator<Student> STUDENT_COMPARATOR = Comparator
            .comparing(Student::getLastName)
            .thenComparing(Student::getFirstName)
            .thenComparing(Student::getGroup);
    
    private <T> List<T> getStudentsInfo(final Collection<Student> students, Function<Student, T> tFunction) {
        return students.stream()
                .map(tFunction)
                .toList();
    }

    private List<Student> sortStudents(final Collection<Student> students, final Comparator<Student> comparator) {
        return students.stream()
                .sorted(comparator)
                .toList();
    }

    @Override
    public List<String> getFirstNames(final List<Student> students) {
        return getStudentsInfo(students, Student::getFirstName);
    }

    @Override
    public List<String> getLastNames(final List<Student> students) {
        return getStudentsInfo(students, Student::getLastName);
    }

    @Override
    public List<GroupName> getGroups(final List<Student> students) {
        return getStudentsInfo(students, Student::getGroup);
    }

    @Override
    public List<String> getFullNames(final List<Student> students) {
        return getStudentsInfo(students, student -> ("%s %s").formatted(student.getFirstName(), student.getLastName()));
    }

    @Override
    public Set<String> getDistinctFirstNames(final List<Student> students) {
        return students.stream()
                .map(Student::getFirstName)
                .collect(Collectors.toSet());
    }

    @Override
    public String getMaxStudentFirstName(final List<Student> students) {
        return students.stream()
                .max(Student::compareTo)
                .map(Student::getFirstName)
                .orElse("");
    }


    @Override
    public List<Student> sortStudentsById(final Collection<Student> students) {
        return sortStudents(students, Student::compareTo);
    }

    @Override
    public List<Student> sortStudentsByName(final Collection<Student> students) {
        return sortStudents(students, STUDENT_COMPARATOR);

    }
    private <T> Predicate<Student> getStudentsPredicate(final Function<Student, T> tFunction, final T expected) {
        return student -> tFunction
                .apply(student)
                .equals(expected);
    }


    private <T> List<Student> findStudents(final Collection<Student> students,
                                           final Function<Student, T> function,
                                           final T name) {
        return students.stream()
                .filter(getStudentsPredicate(function, name))
                .sorted(STUDENT_COMPARATOR)
                .collect(Collectors.toList());
    }

    @Override
    public List<Student> findStudentsByFirstName(final Collection<Student> students, final String name) {
        return findStudents(students, Student::getFirstName, name);
    }

    @Override
    public List<Student> findStudentsByLastName(final Collection<Student> students, final String name) {
        return findStudents(students, Student::getLastName, name);
    }

    @Override
    public List<Student> findStudentsByGroup(final Collection<Student> students, final GroupName group) {
        return findStudents(students, Student::getGroup, group);
    }

    @Override
    public Map<String, String> findStudentNamesByGroup(final Collection<Student> students, final GroupName group) {
        return students.stream()
                .filter(getStudentsPredicate(Student::getGroup, group))
                .collect(Collectors.toMap(
                        Student::getLastName,
                        Student::getFirstName,
                        BinaryOperator.minBy(Comparator.naturalOrder())
                ));
    }
}
