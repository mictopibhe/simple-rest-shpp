package pl.davidduke.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.davidduke.entity.Person;

@Repository
public interface PersonRepository extends JpaRepository<Person, Integer> {
}
