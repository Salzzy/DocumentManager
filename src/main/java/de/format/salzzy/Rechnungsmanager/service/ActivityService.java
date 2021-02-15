package de.format.salzzy.Rechnungsmanager.service;

import de.format.salzzy.Rechnungsmanager.model.Activity;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface ActivityService {

    /**
     * Gibt alle Activity Objekte in einer Liste zurück
     * @return List<Activity>
     */
    List<Activity> finaAll();

    /**
     * Gibt alle Activity Objekte in einer Liste zurück
     * @param sort Sortiert die Liste
     * @return List<Activity>
     */
    List<Activity> finaAll(Sort sort);

    /**
     * Speichert eine acitivität
     * @param activity neue Activity speichern
     */
    void save(Activity activity);

}
