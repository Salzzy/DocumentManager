package de.format.salzzy.Rechnungsmanager.service;

import de.format.salzzy.Rechnungsmanager.model.Activity;
import de.format.salzzy.Rechnungsmanager.repository.ActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActivityServiceImpl implements ActivityService {

    private final ActivityRepository activityRepository;

    @Autowired
    public ActivityServiceImpl(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    @Override
    public List<Activity> finaAll(Sort sort) {
        return activityRepository.findAll(sort);
    }

    @Override
    public List<Activity> finaAll() {
        return activityRepository.findAll();
    }

    @Override
    public void save(Activity activity) {
        activityRepository.save(activity);
    }
}
