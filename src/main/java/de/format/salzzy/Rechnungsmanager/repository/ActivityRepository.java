package de.format.salzzy.Rechnungsmanager.repository;

import de.format.salzzy.Rechnungsmanager.model.Activity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityRepository extends JpaRepository<Activity, Long> {
}
