package pl.davidduke.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.davidduke.entities.Person;

@Repository
public interface PersonRepository extends JpaRepository<Person, Integer> {
}