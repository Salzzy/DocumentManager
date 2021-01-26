package de.format.salzzy.Rechnungsmanager.repository;

import de.format.salzzy.Rechnungsmanager.model.Setting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SettingRepository extends JpaRepository<Setting, Integer> {
}
