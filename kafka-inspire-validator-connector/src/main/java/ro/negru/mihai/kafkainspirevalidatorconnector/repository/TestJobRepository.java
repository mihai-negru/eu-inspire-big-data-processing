package ro.negru.mihai.kafkainspirevalidatorconnector.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ro.negru.mihai.kafkainspirevalidatorconnector.entity.TestJob;

@Repository
public interface TestJobRepository extends JpaRepository<TestJob, Long> {
}
